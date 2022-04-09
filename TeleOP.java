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

    public int _level = 1;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);

        telemetry.addData("Full LS Extension", r.theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", r.theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", r.theoreticalGroundExtension);

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
         *   gamepad2:  x intake, y top LS, b mid LS, a bot LS,
         *          (left_bumper: dpad_up measuring tape out, dpad_down measuring tape in)
         *          left_stick_y vertical, right_stick_x horizontal
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
            r.CarouselMotor.setPower(0.95);
        } else if (gamepad1.dpad_left) {
            r.CarouselMotor.setPower(-0.95);
        } else r.CarouselMotor.setPower(0);

        if (gamepad2.dpad_up) {
            if (gamepad2.left_bumper) {
                r.big.setPower(r.extenderPower);
                r.small.setPower(r.extenderPower);
            } else {
                r.Intake.setPower(1);
            }
        } else if (gamepad2.dpad_down) {
            if (gamepad2.left_bumper) {
                r.big.setPower(-r.extenderPower);
                r.small.setPower(-r.extenderPower);
            } else {
                r.Intake.setPower(-1);
            }
        } else {
            r.Intake.setPower(0);
            r.big.setPower(0);
            r.small.setPower(0);
        }

        double verticalIncrement = gamepad2.left_stick_y;
        double horizontalIncrement = gamepad2.right_stick_x;
        double scale = 1000;

//
//        if (!gamepad2.x) {
//            verticalIncrement = 0;
//            horizontalIncrement = 0;
//        }
//
//        else {
//            telemetry.addLine("increment not zero");
//            telemetry.addLine("v: " + verticalIncrement);
//            telemetry.addLine("h: " + horizontalIncrement);
//            telemetry.update();
//        }

        if ((r.vertical.getPosition() > 0 && verticalIncrement < 0) ||
                (r.vertical.getPosition() < 1 && verticalIncrement > 0)) {
            telemetry.addLine("found data input");
            r.vertical.setPosition(r.vertical.getPosition() + (verticalIncrement / scale));
        }
        if ((r.horizontal.getPosition() > 0 && horizontalIncrement < 0) ||
                (r.horizontal.getPosition() < 1 && horizontalIncrement > 0)) {
            r.horizontal.setPosition(r.horizontal.getPosition() + (horizontalIncrement / scale));
            telemetry.addLine("found data input");
        }

         telemetry.update();

        if (_level != 1) {
            if (gamepad2.right_bumper) {
                //0
                r.LSExtensionServo.setPosition(r.up);
            } else if (gamepad2.right_trigger >= 0.5d) {
                //180
                r.LSExtensionServo.setPosition(r.bottom);
            }
        } else {
            r.LSExtensionServo.setPosition(r.LSExtensionServo.getPosition());
        }

        if (gamepad2.y) {
            lsLevelSet(3);
            r.LSExtensionServo.setPosition(r.up);
        }

        if (gamepad2.b) {
            lsLevelSet(2);
        }

        if (gamepad2.a) {
            lsLevelSet(1);
        }

        telemetry.update();
    }

    public void lsLevelSet (int level) { //method to move ls to preset levels
        this._level = level;
        int target = 0;

        if (level == 2) { //middle
            target = (int) r.theoreticalMiddleExtension;
        } else if (level == 3) { //top
            target = (int) r.theoreticalFullExtension;
        }

        r.LinearSlide.setTargetPosition(target);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.LinearSlide.setPower(0.75);
        waitForLinearSlide();
        r.LinearSlide.setPower(0);

    }

    public void waitForLinearSlide() {
        while (r.LinearSlide.isBusy()) {
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
}