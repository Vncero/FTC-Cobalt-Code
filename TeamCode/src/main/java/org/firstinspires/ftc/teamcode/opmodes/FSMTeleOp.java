package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;

import java.util.ArrayList;

@TeleOp (name = "FSMTeleOp")
public class FSMTeleOp extends OpMode {
    Robot r;
    LinearSlideStates linearSlideState;

    //implement cancelling into different presets
    ArrayList<Boolean> liftButtons = new ArrayList<>(3);
    boolean pressed;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);
        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlideState = LinearSlideStates.IDLE;
        liftButtons.add(gamepad2.a);
        liftButtons.add(gamepad2.b);
        liftButtons.add(gamepad2.y);

        r.LSExtensionServo.setPosition(1);
        r.vertical.setPosition(0);
        r.horizontal.setPosition(0.4);
    }

    @Override
    public void loop() {

        double c = gamepad1.left_stick_x * (gamepad1.left_bumper ? 0.3 : 0.9);
        double x = gamepad1.right_stick_x * (gamepad1.left_bumper ? 0.3 : 0.9);
        double y = -gamepad1.left_stick_y * (gamepad1.left_bumper ? 0.3 : 0.9);

        r.FrontLeft.setPower(y+x+c);
        r.FrontRight.setPower(-y+x+c);
        r.BackLeft.setPower(y+x-c);
        r.BackRight.setPower(-y+x-c);

        switch (linearSlideState) {
            case IDLE:
                if (liftButtons.get(0)) {

                }
                break;
            case MOVING:
                //make 100 a position buffer
                if (r.LinearSlide.getCurrentPosition() - 100 >= r.LinearSlide.getTargetPosition()) {
                    r.LinearSlide.setPower(0); //cut power to avoid snapping
                }
                break;
        }
    }

    public enum LinearSlideStates {
        IDLE,
        MOVING
    }
}
