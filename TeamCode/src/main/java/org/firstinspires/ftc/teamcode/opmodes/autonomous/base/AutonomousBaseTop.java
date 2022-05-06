package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

public abstract class AutonomousBaseTop extends AutonomousBase {
    Robot r;

    int mult = 1;

    int top = 0;
    int bottom = 1;

    @Override
    public void runOpMode() {
        r = new Robot(telemetry, hardwareMap);

        waitForStart();

        r.setMotorTargets(mult * 20, Robot.Drive.STRAFE_LEFT);
        r.drive(0.2);
        r.setMotorTargets(0.5, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        r.setLinearSlidePosition((int) r.theoreticalFullExtension);
        r.LSExtensionServo.setPosition(top);

        sleep(1000);

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.15);

        sleep(200);

        r.Intake.setPower(1);
        sleep(1000);
        r.Intake.setPower(0);

        sleep(100);

        r.setMotorTargets(16, Robot.Drive.BACKWARD);
        r.drive(0.2);

        sleep(100);

        r.LSExtensionServo.setPosition(bottom);

        sleep(100);

        r.setLinearSlidePosition((int) r.theoreticalMiddleExtension);
        r.setMotorTargets(mult * 60, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        r.setLinearSlidePosition(0);
        r.setMotorTargets(12, Robot.Drive.FORWARD);
        r.drive(0.4);

        r.setMotorTargets(mult * r.motorArcLength(15), Robot.Drive.TURN_LEFT);
        r.drive(0.2);

        r.setMotorTargets(3.5, Robot.Drive.FORWARD);
        r.drive(0.2);
    }
}