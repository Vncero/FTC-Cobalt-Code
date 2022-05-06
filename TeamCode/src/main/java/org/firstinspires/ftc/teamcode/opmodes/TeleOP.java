package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Robot;

@TeleOp
public class TeleOP extends OpMode {
    private Robot r;
    private int _level = 1;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);

        telemetry.addData("Full LS Extension", r.theoreticalFullExtension);
        telemetry.addData("Middle LS Extension", r.theoreticalMiddleExtension);
        telemetry.addData("Ground LS Extension", r.theoreticalGroundExtension);

        telemetry.addLine("Press right on the dpad if on red");
        telemetry.addLine("Press start if on blue");

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        r.LSExtensionServo.setPosition(1);
        r.vertical.setPosition(0);
        r.horizontal.setPosition(0.4);
    }

    @Override
    public void init_loop () {
        if (gamepad1.dpad_right) {
            r.CarouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            //reverse motor, now turns CW - blue, dpad_right
        } if (gamepad1.dpad_left) {
            r.CarouselMotor.setDirection(DcMotorSimple.Direction.FORWARD);
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

        double c = gamepad1.left_stick_x * (gamepad1.left_bumper ? 0.3 : 0.9);
        double x = gamepad1.right_stick_x * (gamepad1.left_bumper ? 0.3 : 0.9);
        double y = -gamepad1.left_stick_y * (gamepad1.left_bumper ? 0.3 : 0.9);

//        c *= -1;
//        x *= -1;
//        y *= -1;

//        // DO NOT D ELETE idk why tf it flips it wasnt like this last time someone did
//        // something, but im too lazy to go through each method rn
//        // oh shit it might fuck up during auto
//        // if it does then we have to change everything
//        // can you reverse motor directions????????????
//        // if you can that would save a lot of time
//        // not tryna go through each method and change it one by one
//        // thats disgusting

        r.FrontLeft.setPower(y+x+c);
        r.FrontRight.setPower(-y+x+c);
        r.BackLeft.setPower(y+x-c);
        r.BackRight.setPower(-y+x-c);

        if (gamepad1.dpad_right || gamepad1.dpad_left) {
            r.CarouselMotor.setPower(gamepad1.dpad_right ? 0.8 : -0.8); // CHANGED: from .95 to .8 <- Henry Zhang
        } else r.CarouselMotor.setPower(0);

        if (gamepad2.dpad_up || gamepad2.dpad_down) {
            r.Intake.setPower(gamepad2.dpad_up ? 1 : -1);
        } else r.Intake.setPower(0);

        double verticalIncrement = gamepad2.left_stick_y;
        double horizontalIncrement = gamepad2.right_stick_x;
        double scale = 0.00075d;

        if (horizontalIncrement > 0) horizontalIncrement = 1;
        else if (horizontalIncrement < 0) horizontalIncrement = -1;

        if (verticalIncrement > 0) verticalIncrement = 1;
        else if (verticalIncrement < 0) verticalIncrement = -1;

        double vert_pos = r.vertical.getPosition();
        double hor_pos = r.horizontal.getPosition();

        if (!gamepad2.left_bumper) {
            verticalIncrement = 0;
            horizontalIncrement = 0;
        }

        if (gamepad2.left_trigger > 0) horizontalIncrement *= 0.05;

        // pos flipped - higher vert pos -> lower position vert
        if ((vert_pos > 0 && verticalIncrement < 0) ||
                (vert_pos < 1 && verticalIncrement > 0)) {
            telemetry.addLine("vertical servo position: " + vert_pos);
            r.vertical.setPosition(vert_pos + (verticalIncrement * scale));
        }
        if ((hor_pos > 0 && horizontalIncrement < 0) ||
                (hor_pos < 1 && horizontalIncrement > 0)) {
            telemetry.addLine("horizontal servo position: " + hor_pos);
            r.horizontal.setPosition(hor_pos + (horizontalIncrement * scale));
        }

         telemetry.update();

        if (gamepad2.right_bumper) {
            if (gamepad2.left_bumper) {
                r.TurretBottom.setPower(r.extenderPower);
                r.TurretTop.setPower(r.extenderPower);
            }else if (_level != 1) {
                //0
                r.LSExtensionServo.setPosition(1);
            }
        } else if (gamepad2.right_trigger > 0) {
            if (gamepad2.left_bumper) {
                r.TurretBottom.setPower(-r.extenderPower);
                r.TurretTop.setPower(-r.extenderPower);
            }  else if (_level != 1) {
                //180
                r.LSExtensionServo.setPosition(0);
            }
        } else {
            r.TurretBottom.setPower(0);
            r.TurretTop.setPower(0);
//            r.LSExtensionServo.setPosition(r.LSExtensionServo.getPosition());
        }

        if (gamepad2.y) {
            lsLevelSet(3);
            r.LSExtensionServo.setPosition(0);
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