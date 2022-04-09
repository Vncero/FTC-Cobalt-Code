package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public abstract class AutonomousBase extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;

    final double up = 0.15d;
    // final double middle = 0.45d;
    final double bottom = 0.8d;

    public void setDriveTrain(DcMotor FrontLeft, DcMotor FrontRight, DcMotor BackLeft, DcMotor BackRight) {
        this.FrontLeft = FrontLeft;
        this.FrontRight = FrontRight;
        this.BackLeft = BackLeft;
        this.BackRight = BackRight;
    }
    public Servo setLSExtensionServo(Servo LSExtensionServo) {
        LSExtensionServo.scaleRange(up, bottom);
        return LSExtensionServo;
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
