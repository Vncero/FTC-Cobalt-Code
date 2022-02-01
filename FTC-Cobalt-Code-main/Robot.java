package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;

public class Robot {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor LinearSlide;

    public void hardwareMap(HardwareMap hardwareMap) {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeftFrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeftBackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRightFrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRightBackRight");
    }

    public void StrafeRight (double Power) {

        FrontLeft.setPower(-Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void StrafeLeft (double Power) {

        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(Power);
    }

    public void TurnLeft (double Power) {
        // both left sides go forward
        // both right sides go backwards
        // this makes the robot turn left and stationary

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnRight (double Power) {
        // both right sides go forward
        // both left sides go backwards

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void LinearSlideSet(int ticks) {
        LinearSlide.setTargetPosition(ticks);

        while (LinearSlide.isBusy()) {}

        LinearSlide.setPower(0);
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);
    }
}