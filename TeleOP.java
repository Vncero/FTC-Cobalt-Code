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
        r = new Robot(telemetry, hardwareMap);

        telemetry.addData("Full LS Extension", theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", theoreticalGroundExtension);

        telemetry.addLine("Press right on the dpad if on red");
        telemetry.addLine("Press start if on blue");

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addLine("DURING INIT - SET TARGET POSITION TO " + r.LinearSlide.getTargetPosition());
        telemetry.update();
    }

    @Override
    public void init_loop () {
        if (gamepad1.dpad_right) {
            r.CarouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            telemetry.clear();

            telemetry.addLine("Changed carousel direction to CW (red)");
            telemetry.update();

            //reverse motor, now turns CW - blue, dpad_right
        } if (gamepad1.dpad_left) {
            r.CarouselMotor.setDirection(DcMotorSimple.Direction.FORWARD);
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

        telemetry.addLine("past horizontal: " + c);
        telemetry.addLine("past vertical: " + y);

//        double _c = c * Math.cos(angle) - y * Math.sin(angle);
//        double _y = c * Math.sin(angle) + y * Math.cos(angle);
//
//        c = _c;
//        y = _y;

        telemetry.addData("calculated horizontal", c);
        telemetry.addData("calculated vertical", y);

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

        r.FrontLeft.setPower(y+x+c);
        r.FrontRight.setPower(-y+x+c);
        r.BackLeft.setPower(y+x-c);
        r.BackRight.setPower(-y+x-c);

        if (gamepad1.dpad_right) {
            r.CarouselMotor.setPower(1);
        }

        else if (gamepad1.dpad_left) {
            r.CarouselMotor.setPower(-1);
        }

        else r.CarouselMotor.setPower(0);

        if (gamepad2.dpad_up) {
            r.Intake.setPower(1);
        }

        else if (gamepad2.dpad_down) {
            r.Intake.setPower(-1);
        }

        else r.Intake.setPower(0);

        if (gamepad1.y) {
            r.LinearSlide.setPower(0.1);
            telemetry.addLine("tu madre soy dora");
            telemetry.update();
        }

        else if (gamepad1.a) {
            r.LinearSlide.setPower(-0.1);
            telemetry.addLine("tu madre soy no dora");
            telemetry.update();
        }

        else {
            r.LinearSlide.setPower(0);
            telemetry.addLine("tu madre soy deeznuts");
            telemetry.update();
        }

        if (_level != 1) {
            if (gamepad2.right_bumper) {
                //0
                r.LSExtensionServo.setPosition(0.15);
            }

            else if (gamepad2.right_trigger >= 0.5d) {
                //180
                r.LSExtensionServo.setPosition(0.85);
            }
        } else {
            r.LSExtensionServo.setPosition(r.LSExtensionServo.getPosition());
        }

        if (gamepad2.y) {
            lsLevelSet(3);
            r.LSExtensionServo.setPosition(0.15);
        }

        if (gamepad2.b) {
            lsLevelSet(2);
            // telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        }

        if (gamepad2.a) {
            lsLevelSet(1);
            telemetry.addData("Ground LS Extension", 0);
        }

        telemetry.update();
    }

//    @Override
//    public void stop () {
//        lsLevelSet(1);
//    }

    public void lsLevelSet (int level) { //method to move ls to preset levels
        this._level = level;

        if (level == 1) { //ground
            r.LinearSlide.setTargetPosition(0);
            //Set the Linear Slide's target to the lowest, 0
        }

        if (level == 2) { //middle
            r.LinearSlide.setTargetPosition((int) theoreticalMiddleExtension);
        }

        if (level == 3) { //top
            r.LinearSlide.setTargetPosition((int) theoreticalFullExtension);
        }

        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //set the motor to run toward the position

        r.LinearSlide.setPower(0.75); //set power to the motor

        waitForLinearSlide(); //while loop for the encoders to run through

        r.LinearSlide.setPower(0); //cut the encoder power
    }

    public void waitForLinearSlide() {
        while (r.LinearSlide.isBusy()) {
            r.LinearSlide.setPower(-0.25);
            telemetry.addData("Linear Slide at position", r.LinearSlide.getCurrentPosition());
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

            r.FrontLeft.setPower(y+x+c);
            r.FrontRight.setPower(-y+x+c);
            r.BackLeft.setPower(y+x-c);
            r.BackRight.setPower(-y+x-c);
        }
    }

    public void encoderLSReset() {
        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runLSEncoder() {
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void encoderMotorReset() {
        r.FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setMotorTargets (int motorTarget) {
        r.FrontLeft.setTargetPosition(motorTarget);
        r.FrontRight.setTargetPosition(motorTarget);
        r.BackLeft.setTargetPosition(motorTarget);
        r.BackRight.setTargetPosition(motorTarget);
    }

    public void runMotorEncoders () {
        r.FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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