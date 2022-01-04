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
    //TouchSensor LinearSlideLimiterTop;
    //TouchSensor LinearSlideLimiterBottom;

    final int LINEAR_SLIDE_AT_BOTTOM_LIMIT = 1;
    final int LINEAR_SLIDE_AT_TOP_LIMIT = 2;

    final int LINEAR_SLIDE_CURRENT_STATE = 0;

    final int LS_TOP_LIMIT = (int) (6 * 537.7);
    final int LS_BOT_LIMIT = 0;
    //
    boolean STOP_LS = false;

    int upwardsTickCount = 0;
    int downwardsTickCount = 0;

    boolean LINEAR_SLIDE_AT_LIMIT = false; // set to true when LINEAR_SLIDE_LIMIT at either LINEAR_SLIDE_AT_BOTTOM_LIMIT or LINEAR_SLIDE_AT_TOP_LIMIT

    //approximately 3 rotations - "2cm"
    //2cm ~ 0.787402 inches, 0.00929886553 / 0.787402 inches = ticks for 2cm (0.011809552856614936), 3*537.7 ~ 1613.1
    // 1613.1 - 0.00732194531 ~ ticks for full extension if not we're f'd
    // theoretically, we get an approximate number of ticks for the full thing

    final double ticksInARotation = 537.7;

    final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(0.787402));
    // official information says 3.1 rotations apparently
    //https://www.gobilda.com/low-side-cascading-kit-two-stage-376mm-travel/
    //top of the alliance shipping hub is 14.7, assuming the above is the correct slides, it reaches 14.8
    //so alternate fullExtension to use is LinearSlideTicks(14.7);

    final double theoreticalMiddleExtension =  LinearSlideTicks(8.5);
    /*alliance shipping hub middle level top edge is 8.5 inches up,
    assuming that the extension servo will cover the remaining height to dump freight in*/

    final double theoreticalGroundExtension = LinearSlideTicks(3);
    //if the ext doesn't already reach bottom level, use this

    boolean directionSet = false;

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "IntakeServo");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        // LinearSlideTopSensor = hardwareMap.get(TouchSensor.class, "LinearSlideTopTouchSensor");
        //LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");
        //LSReleaseServo = hardwareMap.get(Servo.class, "LSReleaseServo");

        //LSReleaseServo.setPosition(0.5);
        //either uncomment this or the one in intakeFreight()

        // while (!gamepad1.dpad_left || !gamepad1.dpad_right) {
        //     //carousel is a motor, default is CCW - red, dpad_left
        //     //if on red, press left to pass through
        // }

        // if (gamepad1.dpad_right) {
        //     CarouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        //     //reverse motor, now turns CW - blue, dpad_right
        // }
    }

    //if the init while doesn't work, use this init_loop()
    @Override
    public void init_loop () {
        telemetry.addLine("Press right on the dpad if on red");
        telemetry.update();
        if (gamepad1.dpad_right) {
            CarouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            telemetry.addLine("Changed carousel direction to CW");
            telemetry.update();

            //reverse motor, now turns CW - blue, dpad_right
        }

    }

    @Override
    public void loop () {
        /* button config
         *   gamepad1: movement on joysticks, carousel dir set (dpad) and activate (button)
         *   gamepad2: bucket on dpad up and down, x intake, y top LS, b mid LS, a bot LS
         * */

        if (gamepad1.b) {
            CarouselMotor.setPower(1);
        }

        else CarouselMotor.setPower(0);


        if (gamepad2.dpad_up) { //or left and right, depending what makes sense
            LSReleaseServo.setPosition(0);
            //theoretically, the way the intake method is designed, 0 is higher up than 0.5
        }

        if (gamepad2.dpad_down) {
            LSReleaseServo.setPosition(0.5);
        }

        if (gamepad2.x) {
            intakeFreight();
        }

        //LS level presets
        if (gamepad2.y) {
            lsLevelSet(3);
        }

        if (gamepad2.b) {
            lsLevelSet(2);
        }

        if (gamepad2.a) {
            lsLevelSet(1);
        }

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

        y *= 0.9;
        c *= 0.9;
        x *= 0.9;

        //make a default percentage to read at the whole game

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(-y+x+c);
        BackLeft.setPower(y+x-c);
        BackRight.setPower(-y+x-c);

    }

    public void intakeFreight () { //intake method, theoretically completely loopable
        //rel begins 0.5 after releasing: b pressed
        //LSReleaseServo.setPosition(0.5);
        lsLevelSet(1);
        LSExtensionServo.setPosition(0);
        Intake.setPower(1);
        lsLevelSet(3);
        Intake.setPower(0);
        LSExtensionServo.setPosition(0.5);
        LSReleaseServo.setPosition(0); //if necessary, tweak this to 0.25 in case bucket drops freight
        LSExtensionServo.setPosition(1); //if rel is tweaked to 0.25, find a point of ext to return rel to 0
        //rel can now go to 0.5 after pressing b
    }

    public void lsLevelSet (int level) { //method to move ls to preset levels

        if (level == 1) { //ground
            LinearSlide.setTargetPosition(0);

            LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            LinearSlide.setPower(0.5);

            while (LinearSlide.isBusy()) {
                telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                telemetry.update();


            }
            LinearSlide.setPower(0);

        }

        if (level == 2) { //middle
            LinearSlide.setTargetPosition((int) theoreticalMiddleExtension);

            LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            LinearSlide.setPower(0.5);

            while (LinearSlide.isBusy()) {
                telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                telemetry.update();


            }
            LinearSlide.setPower(0);
        }

        if (level == 3) { //top
            LinearSlide.setTargetPosition((int) theoreticalFullExtension);

            LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            LinearSlide.setPower(0.5);

            while (LinearSlide.isBusy()) {
                telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
                telemetry.update();


            }
            LinearSlide.setPower(0);
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

    public int LinearSlideTicks(double inches) {
        double diameter = 1.5;

        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !
        // original measurment was 5in
        //alt circumference ~ 4.75in.
        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick

        return (int) Math.floor(inches / inchesPerTick);
    }

    public double MotorTicks (double inches) {
        double diameter = 3.75;

        double circumference = Math.PI * diameter; // in inches

        double inchesPerTick = circumference / 537.7; // approx 0.0204492733635192 inches per tick

        return inches / inchesPerTick;
    }
}
