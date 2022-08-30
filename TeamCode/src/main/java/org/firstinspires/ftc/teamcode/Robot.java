package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import org.openftc.easyopencv.OpenCvWebcam;

public class Robot {
    // all of the hardware is directly accessed
    public DcMotor frontLeft, backLeft,
                   frontRight, backRight,
                   carouselMotor, linearSlide;
    public TouchSensor linearSlideHome;
    public CRServo intake, turretTop, turretBottom;
    public Servo lsExtensionServo, horizontal, vertical;

    private Telemetry telemetry;

    // camera fields
    public OpenCvWebcam webcam;
    private boolean cameraIsOpen = false;

    // imu heading fields
    private BNO055IMU imu;
    private Orientation currentOrientation, lastOrientation; // orientations
    private double headingOffset, lastHeading, currentHeading = 0.0; // headings

    // drivetrain constants
    public static final double ticksInARotation = 537.7; // of wheels & linear slide
    public static double theoreticalRadius = 11.1; // of robot center to corner of wheel

    private static final double measuredWheelCircumference = 14.5625;

    public Robot (Telemetry telemetry, HardwareMap hardwareMap) {
         this.telemetry = telemetry;
         hardwareMap(hardwareMap);
    }

    private void hardwareMap(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backRight = hardwareMap.get(DcMotor.class, "BackRight");

        linearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        carouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");

        lsExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");
        horizontal = hardwareMap.get(Servo.class, "horizontal");
        vertical = hardwareMap.get(Servo.class, "vertical");

        intake = hardwareMap.get(CRServo.class, "Intake");
        turretTop = hardwareMap.get(CRServo.class, "TurretTop");
        turretBottom = hardwareMap.get(CRServo.class, "TurretBottom");

        linearSlideHome = hardwareMap.get(TouchSensor.class, "lsHome");

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        turretTop.setDirection(DcMotorSimple.Direction.REVERSE);

        //0 is top, 1 is bottom
        lsExtensionServo.scaleRange(LSExtensionServoPosition.TOP, LSExtensionServoPosition.BOTTOM);

        vertical.scaleRange(CappingMechanismConstants.VERTICAL_MIN, CappingMechanismConstants.VERTICAL_MAX);
        horizontal.scaleRange(CappingMechanismConstants.VERTICAL_MIN, CappingMechanismConstants.VERTICAL_MAX);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        this.imu.initialize(parameters);
        this.headingOffset = -this.imu.getAngularOrientation().firstAngle;
        this.updateHeading();
    }

    // robot constants
    public static final class LinearSlidePosition {
        public static final double theoreticalMiddleExtension = linearSlideTicks(5.5);
        public static final double theoreticalGroundExtension = linearSlideTicks(0.2);
        public static final double theoreticalFullExtension = (3 * ticksInARotation) - linearSlideTicks(5);
    }

    // most servo position constants are probably out of tune
    public static final class LSExtensionServoPosition {
        public static final double TOP = 0.109d;
        public static final double BOTTOM = 0.773;
        // public static final double MIDDLE = 0.45d;
    }

    public static final class CappingMechanismConstants {
        public static final double EXTENDER_POWER = 0.8d;

        // vertical, higher value you go, the lower you get
        private static final double VERTICAL_MIN = 0.5;
        private static final double VERTICAL_MAX = 0.85;

        private static final double HORIZONTAL_MIN = 0.1378d;
        private static final double HORIZONTAL_MAX = 0.5388d;
    }

    public enum Drive {
        FORWARD,
        BACKWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STRAFE_LEFT,
        STRAFE_RIGHT,
        DIAG_NW,
        DIAG_NE,
        DIAG_SW,
        DIAG_SE
    }

    // linearSlide utils
    public static int linearSlideTicks(double inches) {
        double diameter = 1.5;
        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !
        // original measurement was 5in, alt circumference ~ 4.75in.
        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick
        return (int) Math.floor(inches / inchesPerTick);
    }

    public void setLinearSlidePosition(double ticks) {
        this.linearSlide.setTargetPosition((int) ticks);
        this.linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.linearSlide.setPower(0.9);
        while (this.linearSlide.isBusy()) {}
        this.linearSlide.setPower(0);
    }

    // telemetry wrapper methods
    public void telemetryData(String caption, Object value) {
        this.telemetry.addData(caption, value);
    }

    public void telemetryLine(String caption) {
        this.telemetry.addLine(caption);
    }

    public void telemetryUpdate() {
        this.telemetry.update();
    }

    // camera
    public void toggleCameraOpen() {
        this.cameraIsOpen = !this.cameraIsOpen;
    }

    public boolean isCameraOpen() {
        return this.cameraIsOpen;
    }

    // imu heading
    public void updateHeading() {
        this.lastOrientation = this.currentOrientation;
        this.lastHeading = this.currentHeading;

        this.currentOrientation = this.imu.getAngularOrientation();
        this.currentHeading = -this.currentOrientation.firstAngle; // apparently the imu is clockwise positive
//        this.currentHeading = -(this.currentOrientation.firstAngle - this.headingOffset); // account for offset if relevant
    }

    public double getHeading() {
        return this.currentHeading;
    }

    //  this relies on a working heading reading
    public void correctAngle(double power, LinearOpMode auto) {
        auto.sleep(500);
        telemetry.addLine("correcting angle: " + currentHeading);
        telemetry.update();
        setMotorTargets(motorArcLength(-currentHeading * 180.0 / Math.PI), false, Robot.Drive.TURN_LEFT);
        telemetry.addLine("correcting angle: " + currentHeading);
        this.drive(power);
    }

    // drivetrain utils
    private static int motorTicks (double inches) {
//        double circumference = Math.PI * diameter;
        double inchesPerTick = measuredWheelCircumference / ticksInARotation; // approx 0.0204492733635192 inch
        return (int) Math.floor(inches / inchesPerTick);
    }

    public static double motorArcLength (double theta) {
        double rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalRadius; //returns S, the arc length
    }

    // TeleOp drives, probably could be more efficient
    public void driveRobotCentric(Gamepad gamepad1) {
        this.useDriveEquation(gamepad1, gamepad1.right_stick_x, gamepad1.left_stick_y, gamepad1.left_stick_x);
    }

    public void driveFieldCentric(Gamepad gamepad1) {
        double heading = this.getHeading();
        double rotX = gamepad1.right_stick_x * Math.cos(heading) - gamepad1.left_stick_y * Math.sin(heading);
        double rotY = gamepad1.right_stick_x * Math.sin(heading) + gamepad1.left_stick_y * Math.cos(heading);
        this.useDriveEquation(gamepad1, rotX, rotY, gamepad1.left_stick_x);
    }

    private void useDriveEquation(Gamepad gamepad1, double x, double y, double c) {
        c = Math.pow(c * (gamepad1.left_bumper ? 0.3 : gamepad1.right_bumper ? 1.0 : 0.9), 3);
        x = Math.pow(x * (gamepad1.left_bumper ? 0.3 : gamepad1.right_bumper ? 1.0 : 0.9), 3);
        y = Math.pow(-y * (gamepad1.left_bumper ? 0.3 : gamepad1.right_bumper ? 1.0 : 0.9), 3);

        double denominator = Math.max(Math.abs(c) + Math.abs(x) + Math.abs(y), 1.0);

        this.frontLeft.setPower((y + x + c) / denominator);
        this.frontRight.setPower((-y + x + c) / denominator);
        this.backLeft.setPower((y + x - c) / denominator);
        this.backRight.setPower((-y + x - c) / denominator);
    }

    // encoder based system
    private void resetMotorEncoders() {
        this.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runMotorEncoders () {
        this.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void waitForMotorEncoders () {
        while (this.frontLeft.isBusy() && this.frontRight.isBusy() && this.backLeft.isBusy() && this.backRight.isBusy()) {}
        this.Stop();
    }

    // setMotorTargets() and drive() are the only methods necessary to drive with encoders
    public void setMotorTargets (double inchesOrTicks, boolean useTicks, Drive drive) {
        this.resetMotorEncoders();

        int target = useTicks ? (int) inchesOrTicks : motorTicks(inchesOrTicks);

        switch (drive) {
            case FORWARD:
                this.frontLeft.setTargetPosition(target);
                this.frontRight.setTargetPosition(-target);
                this.backLeft.setTargetPosition(target);
                this.backRight.setTargetPosition(-target);
                break;
            case BACKWARD:
                this.frontLeft.setTargetPosition(-target);
                this.frontRight.setTargetPosition(target);
                this.backLeft.setTargetPosition(-target);
                this.backRight.setTargetPosition(target);
                break;
            case TURN_LEFT:
                this.frontLeft.setTargetPosition(-target);
                this.frontRight.setTargetPosition(-target);
                this.backLeft.setTargetPosition(-target);
                this.backRight.setTargetPosition(-target);
                break;
            case TURN_RIGHT:
                this.frontLeft.setTargetPosition(target);
                this.frontRight.setTargetPosition(target);
                this.backLeft.setTargetPosition(target);
                this.backRight.setTargetPosition(target);
                break;
            case STRAFE_LEFT:
                this.frontLeft.setTargetPosition(-target);
                this.frontRight.setTargetPosition(-target);
                this.backLeft.setTargetPosition(target);
                this.backRight.setTargetPosition(target);
                break;
            case STRAFE_RIGHT:
                this.frontLeft.setTargetPosition(target);
                this.frontRight.setTargetPosition(target);
                this.backLeft.setTargetPosition(-target);
                this.backRight.setTargetPosition(-target);
                break;
            case DIAG_NW:
                this.frontRight.setTargetPosition(-target);
                this.backLeft.setTargetPosition(target);
                break;
            case DIAG_NE:
                this.frontLeft.setTargetPosition(target);
                this.backRight.setTargetPosition(-target);
                break;
            case DIAG_SW:
                this.frontLeft.setTargetPosition(-target);
                this.backRight.setTargetPosition(target);
                break;
            case DIAG_SE:
                this.frontRight.setTargetPosition(target);
                this.backLeft.setTargetPosition(-target);
                break;
        }

        this.runMotorEncoders();
    }

    public void drive(double power) {
        this.frontLeft.setPower(power);
        this.frontRight.setPower(power);
        this.backLeft.setPower(power);
        this.backRight.setPower(power);

        this.waitForMotorEncoders();
        this.Stop();
    }

    // ancient, power & timing based drive
    // TurnRight() and Stop() are very old
    public void TurnRight(double Power) {
        // both right sides go forward
        // both left sides go backwards

        frontLeft.setPower(Power);
        backLeft.setPower(Power);

        frontRight.setPower(Power);
        backRight.setPower(Power);
    }

    public void Stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}