package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.threads.RobotThread;


@Autonomous(name="threading test")
public class ThreadTestAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot r = new Robot(telemetry, hardwareMap);
        RobotThread command = new RobotThread(r, this);

        waitForStart();

        command.start();

        while (opModeIsActive()) {

        }
        command.interrupt();
    }
}
