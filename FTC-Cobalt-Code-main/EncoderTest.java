package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (name = "encoderTest")
public class EncoderTest extends LinearOpMode {

    final double ticksInARotation = 537.7;
    final double diameter = 5.75;

    public Robot r = new Robot(telemetry, hardwareMap);

    @Override
    public void runOpMode () {
        r.hardwareMap(hardwareMap);
        telemetry.addLine("hardware mapped");
        telemetry.update();

        waitForStart();

        r.FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        r.setMotorTargets(25, Robot.Drive.FORWARD);
        telemetry.addData("Front Left target", r.FrontLeft.getTargetPosition());
        telemetry.addData("Front right target", r.FrontRight.getTargetPosition());
        telemetry.addData("Back left target", r.BackLeft.getTargetPosition());
        telemetry.addData("Back right target", r.BackRight.getTargetPosition());
        telemetry.update();

        r.drive(0.15);

        sleep(1000);

        r.setLinearSlidePosition(r.theoreticalFullExtension);

//        r.setMotorTargets(14, Robot.Drive.FORWARD);
//        r.drive(0.75);
//
//        r.LSExtensionServo.setPosition(0.15);
//
//        r.Intake.setPower(1);
//        sleep(1000);
//        r.Intake.setPower(0);
//
//        r.LSExtensionServo.setPosition(0.8);
//
//        r.setLinearSlidePosition(0);
//
//        r.setMotorTargets(14, Robot.Drive.BACKWARD);
//        r.drive(0.75);
//
//        r.setMotorTargets(48.5, Robot.Drive.STRAFE_LEFT);
//        r.drive(0.75);
//
//        r.setMotorTargets(14.25, Robot.Drive.FORWARD);
//        r.drive(0.75);
//
//        r.setMotorTargets(r.motorArcLength(60d), Robot.Drive.TURN_RIGHT);
//        r.drive(0.75);
//
//        r.setMotorTargets(6, Robot.Drive.BACKWARD);
//        r.drive(0.5);
//
//        r.CarouselMotor.setPower(-0.5);
//        sleep(3000);
//        r.CarouselMotor.setPower(0);
//
//        r.setMotorTargets(6, Robot.Drive.FORWARD);
//        r.drive(0.75);
//
//        r.setMotorTargets(r.motorArcLength(60d), Robot.Drive.TURN_LEFT);
//        r.drive(0.75);
//
//        r.setMotorTargets(4, Robot.Drive.STRAFE_LEFT);
//        r.drive(0.75);
//
//        r.setMotorTargets(19, Robot.Drive.FORWARD);
//        r.drive(0.75);
    }
}
