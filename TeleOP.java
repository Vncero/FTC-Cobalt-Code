package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class TeleOP extends OpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor LinearSlide;
    DcMotor CarouselMotor;
    CRServo Intake;
    Servo LSExtensionServo;
    Servo LSReleaseServo;
    TouchSensor LinearSlideTopSensor;

    final int LINEAR_SLIDE_AT_BOTTOM_LIMIT = 1;
    final int LINEAR_SLIDE_AT_TOP_LIMIT = 2;

    final int LINEAR_SLIDE_CURRENT_STATE = 0;

    final int LS_TOP_LIMIT = (int) (6 * 537.7);
    final int LS_BOT_LIMIT = 0;
    // approx 5 inches per one rotation
    //approximately 3 rotations-"2cm"

    //TODO: convert 2cm to inches, pass into ticks method, get a tick amount for 2cm in inches, multiply tick amount per rotation by 3, subtract 2cm from rotation,
    //2cm ~ 0.787402 inches, 0.00929886553 / 0.787402 inches = ticks for 2cm (0.011809552856614936), 3*537.7 ~ 1613.1
    // 1613.1 - 0.00732194531 ~ ticks for full extension if not we're f'd
    // theoretically, we get an approximate number of ticks for the full thing
    final double ticksInARotation = 537.7;


    final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(0.787402));

    boolean STOP_LS = false;

    int upwardsTickCount = 0;
    int downwardsTickCount = 0;

    boolean LINEAR_SLIDE_AT_LIMIT = false; // set to true when LINEAR_SLIDE_LIMIT at either LINEAR_SLIDE_AT_BOTTOM_LIMIT or LINEAR_SLIDE_AT_TOP_LIMIT

    //CRServo BucketTurner;top linearSlide servo is normal, not technically Math.pi radians
    //TouchSensor LinearSlideLimiterTop;
    //TouchSensor LinearSlideLimiterBottom;

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "IntakeServo");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        LinearSlideTopSensor = hardwareMap.get(TouchSensor.class, "LinearSlideTopTouchSensor");
        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo")
        LSReleaseServo = hardwareMap.get(Servo.class, "LSReleaseServo");

        positionLSDefault("down");

        LSReleaseServo.setPosition(0);

        LinearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //setPower now sets speed, not directly power into it

    }

//    @Override
//    public void loop () {
//        if (LinearSlideTopSensor.isPressed())  {
//            Intake.setPower(1);
//        }
//
//        else {
//            Intake.setPower(0);
//        }
//    }

    @Override
    public void loop () {

        if (gamepad1.b) { //turns LSReleaseServo opp pos
            if ((int) LSReleaseServo.getPosition() == 0) {
                LSReleaseServo.setPosition(1);
            }

            if ((int) LSReleaseServo.getPosition() == 1) {
                LSReleaseServo.setPosition(0);
            }
        }

        if (gamepad1.a) { //LS reset if up/down pressed, otherwise does intake
            if (gamepad1.dpad_up) {
                LinearSlide.setTargetPosition(theoreticalFullExtension);

                LinearSlide.setMode(DcMotor.Mode.RUN_TO_POSITION);

                LinearSlide.setPower(0.5);

                while (LinearSlide.isBusy()) {
                    telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                    telemetry.update();

                    idle();
                }
                LinearSlide.setPower(0);
            }

            if (gamepad1.dpad_down) {
                LinearSlide.setTargetPosition(0);

                LinearSlide.setMode(DcMotor.Mode.RUN_TO_POSITION);

                LinearSlide.setPower(0.5);

                while (LinearSlide.isBusy()) {
                    telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                    telemetry.update();

                    idle();
                }
                LinearSlide.setPower(0);
            }
            else  {
                intakeCargo();
            }
        }

        if (gamepad1.dpad_left) { //turns carousel, adjust so carousel turns left relative to robot back
            CarouselMotor.setPower(1);

            telemetry.addData("Set Carousel Servo to 1" , null);
            telemetry.update();
        }

        if (gamepad1.dpad_right) { //turns carousel, adjust so carousel turns right relative to robot back
            CarouselMotor.setPower(-1);

            telemetry.addData("Set Carousel Servo to -1" , null);
            telemetry.update();
        }

        else {
            CarouselServo.setPower(0);

            telemetry.addData("Set Carousel Servo to 0", null);
            telemetry.update();
        }

        if (gamepad1.dpad_up && LinearSlide.getCurrentPosition() < (int) theoreticalFullExtension) {
            LinearSlide.setPower(0.1); //LS moves up if not at top
        }

        if (gamepad1.dpad_down && LinearSlide.getCurrentPosition() > 0) { //LS moves down if not at bottom
            LinearSlide.setDirection(DcMotorSimple.Direction.REVERSE);
            LinearSlide.setPower(0.1);
        }

        else {
            LinearSlide.setDirection(DcMotorSimple.Direction.FORWARD)
            LinearSlide.setPower(0);
            telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
        }

//
//        if (gamepad1.dpad_up && !STOP_LS) {
//            LinearSlide.setPower(0.01);
//
//            if (LinearSlide.getCurrentPosition() == LS_TOP_LIMIT - 300) {
//                STOP_LS = true;
//            }
//
//            else {
//                STOP_LS = false;
//            }
//        }
//
//        else if (gamepad1.dpad_down && !STOP_LS) {
//            LinearSlide.setPower(-0.01);
//
//            if (LinearSlide.getCurrentPosition() == LS_BOT_LIMIT + 300) {
//                STOP_LS = true;
//            }
//
//            else {
//                STOP_LS = false;
//            }
//        }
//
//        else {
//            LinearSlide.setPower(0);
//        }

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        if (gamepad1.y) {
            y *= 0.25;
            c *= 0.25;
            x *= 0.25;
        }

        if (gamepad1.x) {
            y *= 1;
            c *= 1;
            x *= 1;
        }

        else {
            y *= 0.9;
            c *= 0.9;
            x *= 0.9;
        }

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(-y+x+c);
        BackLeft.setPower(y+x-c);
        BackRight.setPower(-y+x-c);

        // listen for a button, and aim the linear slide

    }

    public void intakeCargo () { //intake method
        LSReleaseServo.setPosition(0);
        positionLSDefault("down");
        LSExtensionServo.setPosition(0);
        Intake.setPower(1);
        LSExtensionServo.setPosition(1);
        Intake.setPower(0);
        positionLSDefault("up");
    }

    public void positionLSDefault (String direction) { //method to default LS to top or bottom
        if (direction == "up") {
            LinearSlide.setTargetPosition(theoreticalFullExtension);

            LinearSlide.setMode(DcMotor.Mode.RUN_TO_POSITION);

            LinearSlide.setPower(0.5);

            while (LinearSlide.isBusy()) {
                telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                telemetry.update();

                idle();
            }
            LinearSlide.setPower(0);
        }

        if (direction == "down") {
            LinearSlide.setTargetPosition(0);

            LinearSlide.setMode(DcMotor.Mode.RUN_TO_POSITION);

            LinearSlide.setPower(0.5);

            while (LinearSlide.isBusy()) {
                telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                telemetry.update();

                idle();
            }
            LinearSlide.setPower(0);

        }

        else {
            break;
        }

    }

    public void encoderLSReset() {
        LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runLSEncoder() {
        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setMotorTargets (int motorTarget) {
        FrontLeft.setTargetPosition(motorTarget);
        FrontRight.setTargetPosition(motorTarget);
        BackLeft.setTargetPosition(motorTarget);
        BackRight.setTargetPosition(motorTarget);
    }

    public void runMotorEncoders () {
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public static double LinearSlideTicks(double inches) {

        double circumference = 5.0; // might be wrong if it is then we're FUCKED !

        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick

        return inches / inchesPerTick;
    }

    public static double MotorTicks (double inches) {
        double diameter = 3.75; // in inches

        double circumference = Math.PI * diameter; // in inches

        double inchesPerTick = circumference / 537.7; // approx 0.0204492733635192 inches per tick

        return inches / inchesPerTick;
    }

}
