package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

//TODO: add Side enum to class and constructor, then pass robot's Side in for pipeline
public class Robot {
    public DcMotor FrontLeft, BackLeft,
            FrontRight, BackRight,
            CarouselMotor, LinearSlide;
    public CRServo Intake, big, small;
    public Servo LSExtensionServo, horizontal, vertical;
    Telemetry telemetry;
    public BNO055IMU imu;

    //headings
    Orientation currentHeading, lastAngle;

    //angles
    double globalAngle;

//    public final double diameter = 5.75;
    public final double ticksInARotation = 537.7;
    public final double theoreticalRadius = 10.9;

    final double measuredWheelCircumference = Math.PI * 3.9d;
    final double TAU = Math.PI * 2;

    public final double extenderPower = 0.8d;

    public final double theoreticalMiddleExtension =  LinearSlideTicks(5.5);
    public final double theoreticalGroundExtension = LinearSlideTicks(0.2);
    public final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(5));

    public Robot (Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        hardwareMap(hardwareMap);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu.initialize(parameters);

        currentHeading = imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        lastAngle = currentHeading;
    }

    public void hardwareMap(HardwareMap hardwareMap) {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");
        Intake = hardwareMap.get(CRServo.class, "Intake");
        big = hardwareMap.get(CRServo.class, "big");
        small = hardwareMap.get(CRServo.class, "small");
        small.setDirection(CRServo.Direction.REVERSE);

        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");
        horizontal = hardwareMap.get(Servo.class, "horizontal");
        vertical = hardwareMap.get(Servo.class, "vertical");

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

    }

    public void Forward(double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void StrafeRight (double Power) {
        FrontLeft.setPower(-Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void StrafeLeft (double Power) {

        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(Power);
    }

    public void TurnLeft (double Power) {
        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void correctAngle() {
        telemetry.addLine("correcting angle: " + globalAngle);
        telemetry.update();
        setMotorTargets(motorArcLength(-globalAngle), Robot.Drive.TURN_LEFT);
        drive(0.5);
    }

    public void TurnRight (double Power) {
        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);
    }

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runMotorEncoders () {
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void runUsingEncoders () {
        FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void waitForMotorEncoders () {
        while (FrontLeft.isBusy() && FrontRight.isBusy()
                && BackLeft.isBusy() && BackRight.isBusy()) {}

        Stop();
    }

    public int motorTicks (double inches) {
//        double circumference = Math.PI * diameter;

        double circumference = measuredWheelCircumference;

        double inchesPerTick = circumference / ticksInARotation; // approx 0.0204492733635192 inch

        return (int) Math.floor(inches / inchesPerTick);
    }

    public void drive(double power) {
        FrontLeft.setPower(power);
        FrontRight.setPower(power);
        BackLeft.setPower(power);
        BackRight.setPower(power);

        waitForMotorEncoders();
        Stop();
    }

    public void setMotorTargets (double inches, Drive drive) {
        encoderMotorReset();

        int target = motorTicks(inches);

        switch (drive) {
            case FORWARD:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(-target);
                break;
            case BACKWARD:
                FrontLeft.setTargetPosition(-target);
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(target);
                break;
            case TURN_LEFT:
                FrontLeft.setTargetPosition(-target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(-target);
                break;
            case TURN_RIGHT:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(target);
                break;
            case STRAFE_LEFT:
                FrontLeft.setTargetPosition(-target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(target);
                break;
            case STRAFE_RIGHT:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(-target);
                break;
        }

        runMotorEncoders();
    }

    public int LinearSlideTicks(double inches) {
        double diameter = 1.5;

        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !

        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick

        return (int) Math.floor(inches / inchesPerTick);
    }

    public void setLinearSlidePosition(double ticks) {
        LinearSlide.setTargetPosition((int) ticks);
        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        LinearSlide.setPower(0.9);
        while (LinearSlide.isBusy()) {}
        LinearSlide.setPower(0);
    }

    public double motorArcLength (double theta) {
        double rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalRadius; //returns S, the arc length
    }

    public double normalizeAngle (double angle) {
        double modifiedAngle = angle % TAU;

        modifiedAngle = (modifiedAngle + TAU) % TAU;

        return modifiedAngle;
    }

    public double normalizeDelta (double angleDelta) {
        double modifiedAngleDelta = normalizeAngle(angleDelta);

        if (modifiedAngleDelta > Math.PI) {
            modifiedAngleDelta -= TAU;
        }

        return modifiedAngleDelta;
    }

    public void updateGlobalAngle() {
        Orientation currentOrientation = imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);

        double deltaAngle = currentOrientation.firstAngle - lastAngle.firstAngle;

        globalAngle += normalizeDelta(deltaAngle);

        lastAngle = currentOrientation;
    }

    public static final class LSExtensionServoPosition {
        public static final double TOP = 0.099d;
        public static final double BOTTOM = 0.760d;
        // public static final double middle = 0.45d; this is so out of tune
    }

    public enum Drive {
        FORWARD,
        BACKWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STRAFE_LEFT,
        STRAFE_RIGHT
    }

}