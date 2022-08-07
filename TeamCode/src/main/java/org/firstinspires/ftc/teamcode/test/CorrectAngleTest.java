package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

@Autonomous(name="CorrectAngleTest")
public class CorrectAngleTest extends LinearOpMode {
    Robot r;

    @Override
    public void runOpMode() {
        r = new Robot(telemetry, hardwareMap);
        RobotThread thread = new RobotThread(r, this);
        thread.start();
        waitForStart();
        r.setMotorTargets(r.motorArcLength(90), Robot.Drive.TURN_LEFT);
        r.drive(0.5);
        r.correctAngle(0.5, this);
        sleep(1000);
    }
}
