package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Robot;

@TeleOp (name = "FSMTeleOp")
public class FSMTeleOp extends OpMode {
    private Robot robot;
    private Gamepad prevGamepad1, prevGamepad2, currGamepad1, currGamepad2;
    private LinearSlideStates linearSlideState;
    final double buffer = 100;

    @Override
    public void init() {
        robot = new Robot(telemetry, hardwareMap);
        robot.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlideState = LinearSlideStates.IDLE;

        currGamepad1 = new Gamepad();
        currGamepad2 = new Gamepad();

        prevGamepad1 = new Gamepad();
        prevGamepad2 = new Gamepad();

        robot.lsExtensionServo.setPosition(1);
        robot.vertical.setPosition(0);
        robot.horizontal.setPosition(0.4);
    }

    @Override
    public void loop() {
        try {
            prevGamepad1.copy(currGamepad1);
            prevGamepad2.copy(currGamepad2);

            currGamepad1.copy(gamepad1);
            currGamepad2.copy(gamepad2);
        } catch (RobotCoreException e) {}

        robot.driveRobotCentric(currGamepad1);
//        robot.driveFieldCentric(currGamepad1);

        switch (linearSlideState) {
            case IDLE: 
                checkLSInput();
                break;
            case MOVING:
                if (Math.abs(robot.linearSlide.getTargetPosition() - robot.linearSlide.getCurrentPosition()) <= this.buffer) {
                    robot.linearSlide.setPower(0);
                    this.linearSlideState = LinearSlideStates.IDLE;
                }
                checkLSInput();
                break;
            case HOMING:
                telemetry.addLine("HOMING: input is cut (this may be changed after testing)");
                telemetry.addData("Linear Slide Position", robot.linearSlide.getCurrentPosition());
                if (robot.linearSlideHome.isPressed()) {
                    robot.linearSlide.setPower(0);
                    robot.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    this.linearSlideState = LinearSlideStates.IDLE;
                } else robot.linearSlide.setPower(-0.8);
                break;
        }
//        telemetry.update(); // supposedly unnecessary according to gm0
    }

    private void checkLSInput() {
        // what if ternary?
        if (currGamepad2.y && !prevGamepad2.y) {
            robot.linearSlide.setTargetPosition((int) Robot.LinearSlidePosition.theoreticalFullExtension);
            this.linearSlideState = LinearSlideStates.MOVING;
        } else if (currGamepad2.b && !prevGamepad2.b) {
            robot.linearSlide.setTargetPosition((int) Robot.LinearSlidePosition.theoreticalMiddleExtension);
            this.linearSlideState = LinearSlideStates.MOVING;
        } else if (currGamepad2.a && !prevGamepad2.a) {
            robot.linearSlide.setTargetPosition(0);
            this.linearSlideState = LinearSlideStates.MOVING;
        } else if (currGamepad2.x && !prevGamepad2.x) {
            this.linearSlideState = LinearSlideStates.HOMING;
        }
    }

    public enum LinearSlideStates {
        IDLE,
        MOVING,
        HOMING
    }
}