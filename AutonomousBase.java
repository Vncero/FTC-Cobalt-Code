package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class AutonomousBase extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;

    public void setDriveTrain(DcMotor FrontLeft, DcMotor FrontRight, DcMotor BackLeft, DcMotor BackRight) {
        this.FrontLeft = FrontLeft;
        this.FrontRight = FrontRight;
        this.BackLeft = BackLeft;
        this.BackRight = BackRight;
    }

    public void Forward (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void StrafeLeft (double Power) {
        FrontLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);
    }

    public void StrafeRight (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnLeft (double Power) {
        FrontLeft.setPower(-Power);
        BackLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnRight (double Power) {
        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackRight.setPower(Power);
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);
    }
}
