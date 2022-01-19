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
    CRServo LSReleaseServo;

    //approximately 3 rotations - "2cm"
    //2cm ~ 0.787402 inches, 0.00929886553 / 0.787402 inches = ticks for 2cm (0.011809552856614936), 3*537.7 ~ 1613.1
    // 1613.1 - 0.00732194531 ~ ticks for full extension if not we're f'd
    // theoretically, we get an approximate number of ticks for the full thing
 
    final double topExtensionPosition = 0.8;

    final double ticksInARotation = 537.7;

    // final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(0.787402));
    final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(5));
    
    // official information says 3.1 rotations apparently
    //https://www.gobilda.com/low-side-cascading-kit-two-stage-376mm-travel/
    //top of the alliance shipping hub is 14.7, assuming the above is the correct slides, it reaches 14.8
    //so alternate fullExtension to use is LinearSlideTicks(14.7);

    final double theoreticalMiddleExtension =  LinearSlideTicks(5.5);
    /*alliance shipping hub middle level top edge is 8.5 inches up,
    assuming that the extension servo will cover the remaining height to dump freight in*/

    final double theoreticalGroundExtension = LinearSlideTicks(3);
    //if the ext doesn't already reach bottom level, use this

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "Intake");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        // LinearSlideTopSensor = hardwareMap.get(TouchSensor.class, "LinearSlideTopTouchSensor");
        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");

        // LSExtensionServo.setPosition(0);
        // IMPORTANT: 0.93 is top position, 0.25 is bottom position


        // LSExtensionServo.setPosition(0);
        // IMPORTANT: 0.93 is top position, 0.25 is bottom position
        LSExtensionServo.setPosition(0.25);
        
        telemetry.addData("Full LS Extension", theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", theoreticalGroundExtension);

        telemetry.addLine("Press right on the dpad if on red");
        telemetry.addLine("Press start if on blue");

        // need to clear it first - set to 0
        encoderLSReset();

        telemetry.addLine("DURING INIT - SET TARGET POSITION TO " + LinearSlide.getTargetPosition());

        telemetry.update();
        
        encoderLSReset();
        LinearSlide.setTargetPosition(0); // need to clear it first - set to 0
        telemetry.addLine("DURING INIT - SET TARGET POSITION TO " + LinearSlide.getTargetPosition());
        
        telemetry.update();
    }

    //if the init while doesn't work, use this init_loop()
    @Override
    public void init_loop () {
        if (gamepad1.dpad_right) {
            CarouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            telemetry.clear();

            telemetry.addLine("Changed carousel direction to CW (red)");
            telemetry.update();

            //reverse motor, now turns CW - blue, dpad_right
        } if (gamepad1.dpad_left) {
            CarouselMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            telemetry.clear();

            telemetry.addLine("Changed carousel direction to CCW (blue)");
            telemetry.update();
        }

    }

    @Override
    public void loop () {

        /* button config
         *   gamepad1: movement on joysticks, carousel dir set (dpad) and activate (button)
         *   gamepad2:  x intake, y top LS, b mid LS, a bot LS
         * */

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        if (gamepad1.left_bumper) {
            y *= 0.3;
            c *= 0.3;
            x *= 0.3;
        } else {
            y *= 0.9;
            c *= 0.9;
            x *= 0.9;
        }

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(-y+x+c);
        BackLeft.setPower(y+x-c);
        BackRight.setPower(-y+x-c);

        if (gamepad1.dpad_right) {
            CarouselMotor.setPower(1);
        }

        else if (gamepad1.dpad_left) {
            CarouselMotor.setPower(-1);
        }

        else CarouselMotor.setPower(0);

        if (gamepad2.dpad_up) {
            Intake.setPower(1);
            telemetry.update();
        }

        else if (gamepad2.dpad_down) {
            Intake.setPower(-1);
        }

        else Intake.setPower(0);

        if (gamepad2.left_bumper) {
            LSExtensionServo.setPosition(0.15);
        }

        else if (gamepad2.right_bumper) {
            LSExtensionServo.setPosition(0.45);
        }

        else if (gamepad2.right_trigger == 1) {
            //180
            LSExtensionServo.setPosition(0.8);
        }

        if (gamepad2.y) { // top extension
            lsLevelSet(3);
            LSExtensionServo.setPosition(0.15);
        }

        if (gamepad2.b) { // middle extension
            lsLevelSet(2);
            LSExtensionServo.setPosition(0.45);
        }

        if (gamepad2.a) { // ground extension
            lsLevelSet(1);
        }

        //make a default percentage to read at the whole game

        telemetry.update();
    }

    public void lsLevelSet (int level) { //method to move ls to preset levels

        if (level == 1) { //ground

            LinearSlide.setTargetPosition(0);
            //Set the Linear Slide's target to the lowest, 0

            LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //set the motor to run toward the position

            LinearSlide.setPower(0.75); //set power to the motor

            waitForLinearSlide(); //while loop for the encoders to run through

            LinearSlide.setPower(0); //cut the encoder power
        }

        if (level == 2) { //middle
            telemetry.addLine("target position before: " + LinearSlide.getTargetPosition());
            LinearSlide.setTargetPosition((int) theoreticalMiddleExtension);

            // telemetry.addLine("middle extension is " + theoreticalMiddleExtension);

            LinearSlide.setPower(0.75);

            telemetry.addLine("the target position should be " + theoreticalMiddleExtension + " , and is: " + LinearSlide.getTargetPosition());

            waitForLinearSlide();

            LinearSlide.setPower(0);
        }

        if (level == 3) { //top

            LinearSlide.setTargetPosition((int) theoreticalFullExtension);

            LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            LinearSlide.setPower(0.75);

            waitForLinearSlide();

            LinearSlide.setPower(0);
        }
    }

    public void waitForLinearSlide() {
        while (LinearSlide.isBusy()) {
            telemetry.addData("Linear Slide at position", LinearSlide.getCurrentPosition());
            telemetry.update();

            double c = gamepad1.left_stick_x;
            double x = gamepad1.right_stick_x;
            double y = -gamepad1.left_stick_y;

            if (gamepad1.left_bumper) {
                y *= 0.3;
                c *= 0.3;
                x *= 0.3;
            } else {
                y *= 0.9;
                c *= 0.9;
                x *= 0.9;
            }

            FrontLeft.setPower(y+x+c);
            FrontRight.setPower(-y+x+c);
            BackLeft.setPower(y+x-c);
            BackRight.setPower(-y+x-c);
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
        // original measurement was 5in
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
