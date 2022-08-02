package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;

import java.util.ArrayList;

@TeleOp (name = "FSMTeleOp")
public class FSMTeleOp extends OpMode {
    Robot r;
    Gamepad prevGamepad1, prevGamepad2, currGamepad1, currGamepad2;
    LinearSlideStates linearSlideState;

    final double buffer = 100;
    boolean pressed;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);
        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlideState = LinearSlideStates.IDLE;

        currGamepad1 = new Gamepad();
        currGamepad2 = new Gamepad();

        prevGamepad1 = new Gamepad();
        prevGamepad2 = new Gamepad();

        r.LSExtensionServo.setPosition(1);
        r.vertical.setPosition(0);
        r.horizontal.setPosition(0.4);
    }

    @Override
    public void loop() {
        try {
            prevGamepad1.copy(currGamepad1);
            prevGamepad2.copy(currGamepad2);

            currGamepad1.copy(gamepad1);
            currGamepad2.copy(gamepad2);
        } catch (RobotCoreException e) {}


        double c = currGamepad1.left_stick_x * (currGamepad1.left_bumper ? 0.3 : 0.9);
        double x = currGamepad1.right_stick_x * (currGamepad1.left_bumper ? 0.3 : 0.9);
        double y = -currGamepad1.left_stick_y * (currGamepad1.left_bumper ? 0.3 : 0.9);

        r.FrontLeft.setPower(y+x+c);
        r.FrontRight.setPower(-y+x+c);
        r.BackLeft.setPower(y+x-c);
        r.BackRight.setPower(-y+x-c);

        switch (linearSlideState) {
            case IDLE:

                break;
            case MOVING:
                if (Math.abs(r.LinearSlide.getTargetPosition() - r.LinearSlide.getCurrentPosition()) <= this.buffer) r.LinearSlide.setPower(0);
                this.linearSlideState = LinearSlideStates.IDLE;
                break;
        }
    }

    public enum LinearSlideStates {
        IDLE,
        MOVING
    }
}