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
    Robot r;
    TunerStates state;

    Servo servo;
    double pos = 0;

    boolean turned = false;
    int i = 0; //exists purely for changing delta
    double[] deltas = {1, 0.1, 0.01, 0.001};
    double delta = deltas[i];

    boolean run = false;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("Press Y for Servo");
        telemetry.addLine("Press B for Radius");
        telemetry.addLine("Press A for Circumference");

        state = gamepad1.a ? TunerStates.CIRCUMFERENCE :
                gamepad1.b ? TunerStates.RADIUS : gamepad1.y ? TunerStates.SERVO : TunerStates.UNSELECTED;

        telemetry.update();
    }

    @Override
    public void loop() {
        switch (state) {
            case SERVO:

                telemetry.addLine("Press Y for ExtensionServo");
                telemetry.addLine("Press B for horizontal");
                telemetry.addLine("Press A for vertical");
                servo = gamepad1.y ? r.LSExtensionServo : gamepad1.b ? r.horizontal : gamepad1.a ? r.vertical : null;

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

                    r.setMotorTargets(r.motorArcLength(90), Robot.Drive.TURN_LEFT);
                    r.drive(0.5);
                    turned = true;
                }

                if (gamepad1.dpad_left || gamepad1.dpad_right)
                    i = gamepad1.dpad_left ? i == 0 ? 3 : i-- : i == 3 ? 0 : i++;

                if (gamepad1.dpad_up || gamepad1.dpad_down) {
                    r.theoreticalRadius += gamepad1.dpad_up ? delta : -delta;
                }
                break;
            case CIRCUMFERENCE:
                if (!run) {
                    r.encoderMotorReset();

                    r.FrontLeft.setTargetPosition((int) r.ticksInARotation);
                    r.FrontRight.setTargetPosition((int) -r.ticksInARotation);
                    r.BackLeft.setTargetPosition((int) r.ticksInARotation);
                    r.BackRight.setTargetPosition((int) -r.ticksInARotation);

                    r.runMotorEncoders();

                    //low power to reduce momentum
                    r.FrontLeft.setPower(0.3);
                    r.FrontRight.setPower(0.3);
                    r.BackLeft.setPower(0.3);
                    r.BackRight.setPower(0.3);

                    r.waitForMotorEncoders();
                    r.Stop();
                    run = true;
                }

                telemetry.addLine("Measure the distance the robot traveled at 0.3 power");
                telemetry.addLine("It represents the circumference of the wheels");
                telemetry.update();
                break;
            case UNSELECTED:
                telemetry.addLine("Press Y for Servo");
                telemetry.addLine("Press B for Radius");
                telemetry.addLine("Press A for Circumference");

                state = gamepad1.a ? TunerStates.CIRCUMFERENCE :
                        gamepad1.b ? TunerStates.RADIUS : gamepad1.y ? TunerStates.SERVO : TunerStates.UNSELECTED;

                telemetry.update();
                break;
        }

        telemetry.addData("delta", delta);
        telemetry.addData("pos", pos);
        telemetry.addData("theoreticalRadius", r.theoreticalRadius);
        telemetry.addLine("Press X for the menu");
        telemetry.update();
        if (gamepad1.x) state = TunerStates.UNSELECTED;

    }

    public enum TunerStates {
        SERVO,
        RADIUS,
        CIRCUMFERENCE,
        UNSELECTED
    }
}