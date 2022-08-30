package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;

//this is eventually supposed to test field-centric if imu ever gets setup well enough
@TeleOp
public class Future extends OpMode {
    public Robot r;

    final double ticksInARotation = 537.7;

    // final double theoreticalFullExtension = (3 * ticksInARotation) - (r.LinearSlideTicks(0.787402));
    final double theoreticalFullExtension = (3 * ticksInARotation) - (Robot.linearSlideTicks(5));
    final double theoreticalMiddleExtension =  Robot.linearSlideTicks(5.5);
    final double theoreticalGroundExtension = Robot.linearSlideTicks(3);

    public int _level = 1;

    @Override
    public void init() {
        telemetry.addData("Full LS Extension", theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", theoreticalGroundExtension);

        telemetry.addLine("Press right on the dpad if on red");
        telemetry.addLine("Press start if on blue");

        // need to clear it first - set to 0

        telemetry.addLine("DURING INIT - SET TARGET POSITION TO " + r.linearSlide.getTargetPosition());

        telemetry.update();

        r = new Robot(telemetry, hardwareMap);
    }

    @Override
    public void init_loop () {
        if (gamepad1.dpad_right) {
            r.carouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            telemetry.clear();

            telemetry.addLine("Changed r.carousel direction to CW (red)");
            telemetry.update();

            //reverse motor, now turns CW - blue, dpad_right
        } if (gamepad1.dpad_left) {
            r.carouselMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            telemetry.clear();

            telemetry.addLine("Changed r.carousel direction to CCW (blue)");
            telemetry.update();
        }
    }

    @Override
    public void loop () {

        /* button config
         *   gamepad1: movement on joysticks, r.carousel dir set (dpad) and activate (button)
         *   gamepad2:  x r.intake, y top LS, b mid LS, a bot LS
         * */

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        telemetry.addLine("past horizontal: " + c);
        telemetry.addLine("past vertical: " + y);

//        double angle = r.getExternalHeading();
//        telemetry.addData("external heading (with offset)", angle);

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

        r.frontLeft.setPower(y+x+c);
        r.frontRight.setPower(-y+x+c);
        r.backLeft.setPower(y+x-c);
        r.backRight.setPower(-y+x-c);


        if (gamepad1.dpad_right) {
            r.carouselMotor.setPower(1);
        }

        else if (gamepad1.dpad_left) {
            r.carouselMotor.setPower(-1);
        }

        else r.carouselMotor.setPower(0);

        if (gamepad2.dpad_up) {
            r.intake.setPower(1);
            telemetry.update();
        }

        else if (gamepad2.dpad_down) {
            r.intake.setPower(-1);
        }

        else r.intake.setPower(0);
        if (_level != 1) {
            if (gamepad2.right_bumper) {
                //0
                r.lsExtensionServo.setPosition(0.15);
            }

            else if (gamepad2.right_trigger >= 0.5d) {
                //180
                r.lsExtensionServo.setPosition(0.85);
            }
        }

        if (gamepad2.y) {
            lsLevelSet(3);
            r.lsExtensionServo.setPosition(0.15);
        }

        if (gamepad2.b) {
            lsLevelSet(2);
        }

        if (gamepad2.a) {
            lsLevelSet(1);
            telemetry.addData("Ground LS Extension", 0);
        }

        telemetry.update();
    }

    public void lsLevelSet (int level) { //method to move ls to preset levels
        this._level = level;
        if (level == 1) { //ground

            r.linearSlide.setTargetPosition(0);
            //Set the Linear Slide's target to the lowest, 0

            r.linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //set the motor to run toward the position

            r.linearSlide.setPower(0.75); //set power to the motor

            waitForLinearSlide(); //while loop for the encoders to run through

            r.linearSlide.setPower(0); //cut the encoder power
        }

        if (level == 2) { //middle
            telemetry.addLine("target position before: " + r.linearSlide.getTargetPosition());
            r.linearSlide.setTargetPosition((int) theoreticalMiddleExtension);

            // telemetry.addLine("middle extension is " + theoreticalMiddleExtension);

            r.linearSlide.setPower(0.75);

            telemetry.addLine("the target position should be " + theoreticalMiddleExtension + " , and is: " + r.linearSlide.getTargetPosition());

            waitForLinearSlide();

            r.linearSlide.setPower(0);
        }

        if (level == 3) { //top

            r.linearSlide.setTargetPosition((int) theoreticalFullExtension);

            r.linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            r.linearSlide.setPower(0.75);

            waitForLinearSlide();

            r.linearSlide.setPower(0);
        }
    }

    public void waitForLinearSlide() {
        while (r.linearSlide.isBusy()) {
            telemetry.addData("Linear Slide at position", r.linearSlide.getCurrentPosition());
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

            r.frontLeft.setPower(y+x+c);
            r.frontRight.setPower(-y+x+c);
            r.backLeft.setPower(y+x-c);
            r.backRight.setPower(-y+x-c);
        }
    }

}
