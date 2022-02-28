package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

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
    public Robot r;

    //approximately 3 rotations - "2cm"
    //2cm ~ 0.787402 inches, 0.00929886553 / 0.787402 inches = ticks for 2cm (0.011809552856614936), 3*537.7 ~ 1613.1
    // 1613.1 - 0.00732194531 ~ ticks for full extension if not we're f'd
    // theoretically, we get an approximate number of ticks for the full thing

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

    public int _level = 1;

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "Intake");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");

        telemetry.addData("Full LS Extension", theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", theoreticalGroundExtension);

        telemetry.addLine("Press right on the dpad if on red");
        telemetry.addLine("Press start if on blue");

        // need to clear it first - set to 0
        encoderLSReset();
        runWithoutEncoders();

        telemetry.addLine("DURING INIT - SET TARGET POSITION TO " + LinearSlide.getTargetPosition());

        telemetry.update();

        r = new Robot(telemetry, hardwareMap);
        r.hardwareMap(hardwareMap);
    }

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

        r.updateHeading();
        telemetry.addData("updatedHeading Z", r.currentHeadingZ);
        telemetry.addData("lastHeading Z", r.lastHeadingZ);

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        telemetry.addLine("past horizontal: " + c);
        telemetry.addLine("past vertical: " + y);

        double angle = r.getExternalHeading();
        telemetry.addData("external heading (with offset)", angle);

//        double _c = c * Math.cos(angle) - y * Math.sin(angle);
//        double _y = c * Math.sin(angle) + y * Math.cos(angle);
//
//        c = _c;
//        y = _y;

        telemetry.addData("calculated horizontal", c);
        telemetry.addData("calculated vertical", y);

        /* May or may not need brandon-gong's normalizer
        to normalize the calculated values and not the angle
        // You may need to multiply some of these by -1 to invert direction of
        // the motor.  This is not an issue with the calculations themselves.
        double[] speeds = {
            (drive + strafe + twist),
            (drive - strafe - twist),
            (drive - strafe + twist),
            (drive + strafe - twist)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for(int i = 0; i < speeds.length; i++) {
            if ( max < Math.abs(speeds[i]) ) max = Math.abs(speeds[i]);
        }

        // If and only if the maximum is outside of the range we want it to be,
        // normalize all the other speeds based on the given speed value.
        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
        }

        // apply the calculated values to the motors.
        front_left.setPower(speeds[0]);
        front_right.setPower(speeds[1]);
        back_left.setPower(speeds[2]);
        back_right.setPower(speeds[3]);
        * */

        if (gamepad1.left_bumper) {
            y *= 0.3;
            c *= 0.3;
            x *= 0.3;
        } else {
            y *= 0.9;
            c *= 0.9;
            x *= 0.9;
        }

        telemetry.addData("leftS_y", y);
        telemetry.addData("leftS_x", c);
        telemetry.addData("rightS_x", x);
        telemetry.update();

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(-y+x+c);
        BackLeft.setPower(y+x-c);
        BackRight.setPower(-y+x-c);

//        telemetry.addData("FrontLeft power", FrontLeft.getPower());
//        telemetry.addData("FrontRight power", FrontRight.getPower());
//        telemetry.addData("BackLeft power", BackLeft.getPower());
//        telemetry.addData("BackRight power", BackRight.getPower());
//
//        telemetry.addData("FrontLeft pos", FrontLeft.getCurrentPosition());
//        telemetry.addData("FrontRight pos", FrontRight.getCurrentPosition());
//        telemetry.addData("BackLeft pos", BackLeft.getCurrentPosition());
//        telemetry.addData("BackRight pos", BackRight.getCurrentPosition());

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
        if (_level != 1) {
            if (gamepad2.right_bumper) {
                //0
                LSExtensionServo.setPosition(0.15);
            }

            else if (gamepad2.right_trigger >= 0.5d) {
                //180
                LSExtensionServo.setPosition(0.85);
            }
        }

        if (gamepad2.y) {
            lsLevelSet(3);
            LSExtensionServo.setPosition(0.15);
        }

        if (gamepad2.b) {
            lsLevelSet(2);
            // telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        }

        if (gamepad2.a) {
            lsLevelSet(1);
            telemetry.addData("Ground LS Extension", 0);
        }

        r.lastHeading = r.currentHeading;
        r.lastHeadingZ = r.currentHeadingZ;

        telemetry.update();
    }

    public void lsLevelSet (int level) { //method to move ls to preset levels
        this._level = level;
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

    public void runWithoutEncoders() {
        FrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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