package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Robot;

//TODO: tune measuredWheelCircumference & theoreticalRadius
//measuredWheelCircumference: either remeasure mecanums or have robot drive 537.7 ticks and measure
//theoreticalRadius: empirically tune by having robot motorArcLength(90) until accurate


@TeleOp (name = "Tuner")
public class Tuner extends OpMode {
    private Robot robot;
    private TunerState state;

    private Servo servo;
    private double pos = 0;

    private boolean turned = false;
    private int i = 0; //exists purely for changing delta
    private double[] deltas = {1, 0.1, 0.01, 0.001};
    private double delta = deltas[i];

    private boolean run = false;

    @Override
    public void init() {
        robot = new Robot(telemetry, hardwareMap);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("Press Y for Servo");
        telemetry.addLine("Press X for EXTENSION");
        telemetry.addLine("Press B for Radius");
        telemetry.addLine("Press A for Circumference");

        state = gamepad1.a ? TunerState.CIRCUMFERENCE :
                gamepad1.b ? TunerState.RADIUS : gamepad1.y ? TunerState.SERVO : gamepad1.x ? TunerState.EXTENSION : TunerState.UNSELECTED;

        telemetry.update();
    }

    @Override
    public void loop() {
        switch (state) {
            case SERVO:
                telemetry.addLine("Press Y for ExtensionServo");
                telemetry.addLine("Press B for horizontal");
                telemetry.addLine("Press A for vertical");
                servo = gamepad1.y
                        ? robot.lsExtensionServo
                        : gamepad1.b
                        ? robot.horizontal
                        : gamepad1.a
                        ? robot.vertical
                        : null;

                if (gamepad1.dpad_left || gamepad1.dpad_right)
                    i = gamepad1.dpad_left ? i == 0 ? 3 : i-- : i == 3 ? 0 : i++;
                delta = deltas[i];

                if (gamepad1.dpad_up || gamepad1.dpad_down) {
                    pos += gamepad1.dpad_up ? delta : -delta;
                }

                if (servo != null) servo.setPosition(pos);
                break;
            case RADIUS:
                if (!turned) {
                    telemetry.addLine("Turning 90 degrees CCW");
                    telemetry.update();

                    robot.setMotorTargets(robot.motorArcLength(90), false, Robot.Drive.TURN_LEFT);
                    robot.drive(0.5);
                    turned = true;
                }

                if (gamepad1.dpad_left || gamepad1.dpad_right)
                    i = gamepad1.dpad_left
                        ? i == 0
                            ? 3
                            : i--
                        : i == 3
                            ? 0
                            : i++;

                if (gamepad1.dpad_up || gamepad1.dpad_down) {
                    Robot.theoreticalRadius += gamepad1.dpad_up ? delta : -delta;
                }
                break;
            case CIRCUMFERENCE:
                if (!run) {
                    robot.setMotorTargets(Robot.ticksInARotation, true, Robot.Drive.FORWARD);
                    robot.drive(0.3);
                    run = true;
                }

                telemetry.addLine("Measure the distance the robot traveled at 0.3 power");
                telemetry.addLine("It represents the circumference of the wheels");
                telemetry.update();
                break;
            case EXTENSION:
                if (gamepad1.dpad_up || gamepad1.dpad_down) {
                    robot.linearSlide.setPower(gamepad1.dpad_up ? 0.1 : -0.1);
                }

                if (gamepad2.right_bumper || gamepad2.right_trigger > 0) {
                    robot.lsExtensionServo.setPosition(gamepad2.right_bumper ? 0 : 1);
                }

                telemetry.addData("Linear slide position", robot.linearSlide.getCurrentPosition());
                telemetry.update();
                break;
            case UNSELECTED:
                telemetry.addLine("Press Y for Servo");
                telemetry.addLine("Press X for EXTENSION");
                telemetry.addLine("Press B for Radius");
                telemetry.addLine("Press A for Circumference");

                state = gamepad1.a ? TunerState.CIRCUMFERENCE :
                        gamepad1.b ? TunerState.RADIUS :
                                gamepad1.y ? TunerState.SERVO :
                                        gamepad1.x ? TunerState.EXTENSION : TunerState.UNSELECTED;

                telemetry.update();
                break;
        }

        telemetry.addData("delta", delta);
        telemetry.addData("pos", pos);
        telemetry.addData("theoreticalRadius", robot.theoreticalRadius);
        telemetry.addLine("Press left bumper for the menu");
        if (gamepad1.left_bumper) state = TunerState.UNSELECTED;
        //        telemetry.update();
    }

    public enum TunerState {
        SERVO,
        RADIUS,
        CIRCUMFERENCE,
        EXTENSION,
        UNSELECTED
    }
}