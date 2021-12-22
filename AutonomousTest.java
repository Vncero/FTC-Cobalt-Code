package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.

@Autonomous(name = "test")
public class AutonomousTest extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    CRServo CarouselTest;
    Servo OtherServo;

    private ElapsedTime runtime = new ElapsedTime();
/* TODO
  move forward
  strafe left
  turn

  step #1 get duck on carousel
    1a Maybe get random duck

  */


    @Override
    public void runOpMode(){
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        CarouselTest = hardwareMap.get(CRServo.class, "CarouselServo");
        CarouselTest.setPower(0.5);

        sleep(3000);
    }
}
