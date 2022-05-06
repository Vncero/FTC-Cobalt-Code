package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="AutonomousBlueTop")
public class AutonomousBlueTop extends LinearOpMode {
    Robot r;

    @Override
    public void runOpMode(){

        r = new Robot(telemetry, hardwareMap);

        waitForStart();

        r.setMotorTargets(20, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.2);
        r.setMotorTargets(0.5, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.setLinearSlidePosition((int) r.theoreticalFullExtension);
        r.LSExtensionServo.setPosition(Robot.LSExtensionServoPosition.TOP);

        sleep(1000); // wait for extension servo to go up

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.15);

        sleep(200);

        r.Intake.setPower(0.75);
        sleep(1000);
        r.Intake.setPower(0);

        sleep(100);

        r.setMotorTargets(16, Robot.Drive.BACKWARD);
        r.drive(0.2);

        sleep(100);

        r.setMotorTargets(2, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.LSExtensionServo.setPosition(Robot.LSExtensionServoPosition.BOTTOM);

        sleep(100);

        r.setLinearSlidePosition((int) r.theoreticalMiddleExtension);
        r.setMotorTargets(30, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.setMotorTargets(30, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        r.setLinearSlidePosition(0);
        r.setMotorTargets(12, Robot.Drive.FORWARD);
        r.drive(0.4);
    }

    public void NoEncodersFullAutonomous() {

    }

    public enum Drive {
        FORWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STRAFE_LEFT,
        STRAFE_RIGHT
    }

}
