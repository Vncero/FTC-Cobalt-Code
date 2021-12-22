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
    DcMotor LinearSlide;
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
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");

        LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        LinearSlide.setTargetPosition((int) Math.floor(0.24 * 537.7));

        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Motor are position: " + LinearSlide.getCurrentPosition(), null);
    }
}
