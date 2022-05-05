package org.firstinspires.ftc.teamcode.threads;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Robot;

public class RobotThread extends Thread {
    public Robot r;
    public LinearOpMode auto;

    public RobotThread(Robot r, LinearOpMode auto) {
        this.auto = auto;
        this.r = r;
    }

    public void run() {
        while (auto.opModeIsActive()) {
            r.updateHeading();
        }
    }
}
