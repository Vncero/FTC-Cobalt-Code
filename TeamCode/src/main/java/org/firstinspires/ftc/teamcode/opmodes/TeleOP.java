package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;

@TeleOp
public class TeleOP extends OpMode {
    private Robot robot;
    private int _level = 1;

    @Override
    public void init() {
        robot = new Robot(telemetry, hardwareMap);

        robot.telemetryData("Full LS Extension", Robot.LinearSlidePosition.theoreticalFullExtension);
        robot.telemetryData("Middle LS Extension", Robot.LinearSlidePosition.theoreticalMiddleExtension);
        robot.telemetryData("Ground LS Extension", Robot.LinearSlidePosition.theoreticalGroundExtension);

        robot.telemetryLine("Press right on the dpad if on red");
        robot.telemetryLine("Press start if on blue");

        robot.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.lsExtensionServo.setPosition(1);
        robot.vertical.setPosition(0);
        robot.horizontal.setPosition(0.4);
    }

    @Override
    public void init_loop() {
        if (gamepad1.dpad_right) {
            robot.carouselMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            //reverse motor, now turns CW - blue, dpad_right
        } else if (gamepad1.dpad_left) {
            robot.carouselMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        }
    }

    @Override
    public void loop() {

        /* button config
         *   gamepad1: movement on joysticks, carousel dir set (dpad) and activate (button)
         *   gamepad2:  x intake, y top LS, b mid LS, a bot LS,
         *          (left_bumper: dpad_up measuring tape out, dpad_down measuring tape in)
         *          left_stick_y vertical, right_stick_x horizontal
         * */

        robot.driveRobotCentric(gamepad1);

        robot.carouselMotor.setPower(
                (gamepad1.dpad_right || gamepad1.dpad_left)
                        ? gamepad1.dpad_right
                            ? 0.95
                            : -0.95
                        : 0
        );

        double power = gamepad2.left_trigger > 0 ? 0.5 : 1;
        robot.intake.setPower(
                (gamepad2.dpad_up || gamepad2.dpad_down)
                        ? gamepad2.dpad_up
                            ? power
                            : -power
                        : 0
        );

        double verticalDelta = gamepad2.left_bumper ? Math.signum(gamepad2.left_stick_y) : 0;
        double horizontalDelta = gamepad2.left_bumper ? Math.signum(gamepad2.right_stick_x) : 0;
        double scale = 0.00075;

        double vert_pos = robot.vertical.getPosition();
        double hor_pos = robot.horizontal.getPosition();

        telemetry.addLine("vertical servo position: " + vert_pos);
        telemetry.addLine("horizontal servo position: " + hor_pos);

        // pos flipped - higher vert pos -> lower position vert
        if ((vert_pos > 0 && verticalDelta < 0)
                || (vert_pos < 1 && verticalDelta > 0))
            robot.vertical.setPosition(vert_pos + (verticalDelta * scale));

        if ((hor_pos > 0 && horizontalDelta < 0)
                || (hor_pos < 1 && horizontalDelta > 0))
            robot.horizontal.setPosition(hor_pos + (horizontalDelta * scale * (gamepad2.left_trigger > 0 ? 0.5d : 1d)));

        if (gamepad2.right_bumper) {
            if (gamepad2.left_bumper) {
                robot.turretBottom.setPower(Robot.CappingMechanismConstants.EXTENDER_POWER);
                robot.turretTop.setPower(Robot.CappingMechanismConstants.EXTENDER_POWER);
            } else if (_level != 1) robot.lsExtensionServo.setPosition(0); // 0
        } else if (gamepad2.right_trigger > 0) {
            if (gamepad2.left_bumper) {
                robot.turretBottom.setPower(-Robot.CappingMechanismConstants.EXTENDER_POWER);
                robot.turretTop.setPower(-Robot.CappingMechanismConstants.EXTENDER_POWER);
            } else if (_level != 1) {
                robot.lsExtensionServo.setPosition(1); //180
            } else {
                robot.turretBottom.setPower(0);
                robot.turretTop.setPower(0);
            }

            if (gamepad2.y) {
                setLSLevel(3);
                robot.lsExtensionServo.setPosition(0);
            }

            if (gamepad2.b) setLSLevel(2);

            if (gamepad2.a) setLSLevel(1);

            telemetry.update();
        }
    }
    private void setLSLevel (int level){ //method to move ls to preset levels
        this._level = level;
        int target = 0;

        if (level == 2) { //middle
            target = (int) Robot.LinearSlidePosition.theoreticalMiddleExtension;
        } else if (level == 3) { //top
            target = (int) Robot.LinearSlidePosition.theoreticalFullExtension;
        }

        robot.linearSlide.setTargetPosition(target);
        robot.linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.linearSlide.setPower(0.75);
        waitForLinearSlide();
        robot.linearSlide.setPower(0);
    }

    private void waitForLinearSlide() {
        while (robot.linearSlide.isBusy()) {
            robot.telemetryData("Linear Slide at position", robot.linearSlide.getCurrentPosition());
            robot.driveRobotCentric(gamepad1);
        }
    }
}