package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Robot {
    public DcMotor FrontLeft, BackLeft,
            FrontRight, BackRight,
            CarouselMotor, LinearSlide;
    public CRServo Intake, TurretTop, TurretBottom;
    public Servo LSExtensionServo, horizontal, vertical;
    public Telemetry telemetry;
    public BNO055IMU imu;
    public OpenCvWebcam webcam;

    //headings
    Orientation currentOrientation, lastOrientation;

    //angles
    public double lastHeading = 0.0;
    public double headingOffset;
    private double rawHeading = 0.0;
    private double currentHeading = 0.0;

    public final double ticksInARotation = 537.7;
    public double theoreticalRadius = 11.1;

    final double TAU = Math.PI * 2;

    public final double extenderPower = 0.8d;

    final double measuredWheelCircumference = Math.PI * 3.9d;

    public final double theoreticalMiddleExtension = LinearSlideTicks(5.5);
    public final double theoreticalGroundExtension = LinearSlideTicks(0.2);
    public final double theoreticalFullExtension = (3 * ticksInARotation) - LinearSlideTicks(5);

    // vertical, higher value you go, the lower you get
    public final double verticalMin = 0.5;
    public final double verticalMax = 0.85;

    public final double horizontalMin = 0.1378d;
    public final double horizontalMax = 0.5388d;

    public Robot (Telemetry telemetry, HardwareMap hardwareMap) {
         this.telemetry = telemetry;
         hardwareMap(hardwareMap);
    }

    public void hardwareMap(HardwareMap hardwareMap) {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");

        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");

        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");
        horizontal = hardwareMap.get(Servo.class, "horizontal");
        vertical = hardwareMap.get(Servo.class, "vertical");

        Intake = hardwareMap.get(CRServo.class, "Intake");
        TurretTop = hardwareMap.get(CRServo.class, "TurretTop");
        TurretBottom = hardwareMap.get(CRServo.class, "TurretBottom");

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        FrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        FrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        BackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRight.setDirection(DcMotorSimple.Direction.REVERSE);

        TurretTop.setDirection(DcMotorSimple.Direction.REVERSE);
        LSExtensionServo.scaleRange(LSExtensionServoPosition.TOP, LSExtensionServoPosition.BOTTOM);
        //0 is top, 1 is bottom

        vertical.scaleRange(verticalMin, verticalMax);
        horizontal.scaleRange(horizontalMin, horizontalMax);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu.initialize(parameters);

        currentOrientation = imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        lastOrientation = currentOrientation;

        headingOffset = currentOrientation.firstAngle;
        updateHeading();
    }

    public void setupWebcam(HardwareMap hardwareMap) {
        int cameraMonitorViewId = hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id",
                        hardwareMap
                                .appContext
                                .getPackageName());
        WebcamName wN = hardwareMap.get(WebcamName.class, "Camera 1");
        webcam = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, cameraMonitorViewId);
        FtcDashboard
                .getInstance()
                .startCameraStream(webcam, 30);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {}
        });

        webcam.showFpsMeterOnViewport(true);
    }

    public void closeWebcam() {
        webcam.closeCameraDeviceAsync(new OpenCvCamera.AsyncCameraCloseListener() {
            @Override
            public void onClose() {
                webcam.stopStreaming();
            }
        });
    }

    public void correctAngle(double power, LinearOpMode auto) {
        auto.sleep(500);
        telemetry.addLine("correcting angle: " + currentHeading);
        telemetry.update();
        setMotorTargets(motorArcLength(-currentHeading * 180.0 / Math.PI), Robot.Drive.TURN_LEFT);
        telemetry.addLine("correcting angle: " + currentHeading);
        drive(power);
    }

    public void TurnRight (double Power) {
        // both right sides go forward
        // both left sides go backwards

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(Power);
        BackRight.setPower(Power);
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
            case DIAG_NW:
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(target);
                break;
            case DIAG_NE:
                FrontLeft.setTargetPosition(target);
                BackRight.setTargetPosition(-target);
                break;
            case DIAG_SW:
                FrontLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(target);
                break;
            case DIAG_SE:
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(-target);
                break;
        }

        runMotorEncoders();
    }

    public int LinearSlideTicks(double inches) {
        double diameter = 1.5;

        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !
        // original measurement was 5in
        //alt circumference ~ 4.75in.
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

    public double normDelta (double delta) {
        double modifiedAngleDelta = normalizeAngle(delta);

        if (modifiedAngleDelta > Math.PI) {
            modifiedAngleDelta -= TAU;
        }

        return modifiedAngleDelta;
    }

    public double getHeading() {
        return normalizeAngle(rawHeading + headingOffset);
    }

    public void updateHeading() {
        lastOrientation = currentOrientation;
        currentOrientation = imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.ZXY, AngleUnit.RADIANS);

        rawHeading = currentOrientation.firstAngle;

        double delta = normDelta(currentOrientation.firstAngle - lastOrientation.firstAngle);

        currentHeading = getHeading() + delta;
    }

    public static final class LSExtensionServoPosition {
        public static final double TOP = 0.109d;
        public static final double BOTTOM = 0.773;
        // public static final double middle = 0.45d;
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
}