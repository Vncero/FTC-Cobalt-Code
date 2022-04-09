package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ThreadPool;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@Autonomous(name="threading test")
public class ThreadTestAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot r = new Robot(telemetry, hardwareMap);
        RobotCommand command = new RobotCommand(r, this);

        waitForStart();

        command.start();

        while (opModeIsActive()) {

        }
        command.interrupt();
    }
}
