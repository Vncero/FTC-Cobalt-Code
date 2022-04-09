package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;

public class RobotCommand extends Thread {
    public Robot r;
    public LinearOpMode auto;

    public RobotCommand(Robot r, LinearOpMode auto) {
        this.auto = auto;
        this.r = r;
    }

    public void run() {
        while (auto.opModeIsActive()) {
            r.updateGlobalAngle();
//            r.telemetry.addLine("global angle: " + r.globalAngle);
//            r.telemetry.update();
        }
    }
}
