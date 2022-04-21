package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.threads.RobotThread;

import java.lang.Thread;

@Autonomous(name="AutonomousCorrectAngleTest")
public class AutonomousCorrectAngleTest extends LinearOpMode {
    RobotThread command;
    Robot r;


    @Override
    public void runOpMode() {
        r = new Robot(telemetry, hardwareMap);
        command = new RobotThread(r, this);
        command.start();
        waitForStart();

        r.setMotorTargets(r.motorArcLength(90), Robot.Drive.TURN_RIGHT);
        r.drive(0.5);
        r.Stop();
        sleep(200);
        r.correctAngle(0.1, this);

//        r.vertical.setPosition(0);
//        telemetry.addLine("going to 0 position...");
//        telemetry.update();
//        sleep(2000);
//        r.vertical.setPosition(1);
//        telemetry.addLine("going to 1 position...");
//        telemetry.update();
//        sleep(2000);
    }
}