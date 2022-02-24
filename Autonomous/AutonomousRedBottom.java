package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousBase;
import org.firstinspires.ftc.teamcode.Utilities;

@Autonomous(name="AutonomousRedBottom")
public class AutonomousRedBottom extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor CarouselMotor;
    DcMotor LinearSlide;
    CRServo Intake;
    Servo LSExtensionServo;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode(){

        AutonomousBase autoBase = new AutonomousBase() {
            Robot robot = new Robot();
//            robot.hardwareMap(hardwareMap);
        };

        waitForStart();

        LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LinearSlide.setTargetPosition((int) Utilities.theoreticalMiddleExtension);
        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LinearSlide.setPower(0.5);
        while (LinearSlide.isBusy()) {}
        LinearSlide.setPower(0);

        sleep(100);

        // strafe along the wall
        autoBase.StrafeRight(0.4);
        sleep(1500);
        autoBase.Stop();
        sleep(100);

        // go back more to ensure robot is facing perfectly perpendicular to the wall
        autoBase.Forward(-0.1);
        sleep(200);
//        Stop();

        // now, the robot is facing shipping hub, with its back to the wall. 

        // forward towards the shipping hub
//        Forward(0.45);
        sleep(500);
//        Stop();
        sleep(100);

        // raise linear slides to highest position
        LinearSlide.setTargetPosition((int) Utilities.theoreticalFullExtension);
        LinearSlide.setPower(0.5);
        while (LinearSlide.isBusy()) {}
        LinearSlide.setPower(0);

        // extend fully with extension servo
        LSExtensionServo.setPosition(0.15);
        sleep(1000);

//        Stop();
        sleep(500);

        Intake.setPower(1);
        sleep(1000);
        Intake.setPower(0);

//        Forward(-0.25);
        sleep(1500);
//        Stop();

        LSExtensionServo.setPosition(0.8);
        sleep(1000);

        LinearSlide.setTargetPosition((int) Utilities.theoreticalMiddleExtension);
        LinearSlide.setPower(0.5);
        while (LinearSlide.isBusy()) {}
        LinearSlide.setPower(0);

//        StrafeLeft(0.5);
        sleep(1900);
//        Stop();

        // right now robot is around a foot away from the carousel, with its back to
        // the wall, and the carousel to its left. go forward, turn right a bit, go
        // backwards, turn carousel, go forward, turn right a bit more, strafe right
        // at low power. now you are parallel to the wall, with the front of
        // the robot facing the warehouse. now simply move forward.

        // this is all in the perspective of red bottom

        sleep(200);

//        Forward(0.3);
        sleep(500);
//        Stop();

//        TurnRight(0.25);
        sleep(550);
//        Stop();

//        Forward(-0.3);
        sleep(150);
//        Forward(-0.15);
        sleep(600);
//        Stop();

//        TurnRight(0.1);
        sleep(500);
//        Stop();

        CarouselMotor.setPower(0.55);
        sleep(6000);
        CarouselMotor.setPower(0);

        autoBase.StrafeLeft(0.5);
        sleep(1600);

        autoBase.Forward(0.25);
        sleep(240);

        // right now, the robot is facing diagonally from the carousel, with the motor touching the disk

        // int iterations = 0;

        // Forward(0.5);
        // sleep(300);

        // StrafeRight(0.25);
        // sleep(6000);

        // Forward(1);
        // sleep(1500);

        LinearSlide.setTargetPosition(0);
        LinearSlide.setPower(0.7);

        while (LinearSlide.isBusy()) {}

        autoBase.Stop();
    }
}