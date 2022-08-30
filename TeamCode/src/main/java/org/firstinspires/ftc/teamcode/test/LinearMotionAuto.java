package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Robot;

@Autonomous(name="LinearMotionAuto")
public class LinearMotionAuto extends LinearOpMode {
    double FPS = 2;

    Robot r;

    @Override
    public void runOpMode() {
        double x = 0;
        while (opModeIsActive()) {
            double y = eq(x);

            r.frontLeft.setPower(y+x);
            r.frontRight.setPower(-y+x);
            r.backLeft.setPower(y-x);
            r.backRight.setPower(-y-x);

            sleep((long) (1000 / FPS));

            x += 0.5;
        }
    }

    public double eq(double x) {
        double y = 1d - Math.pow(2d, -x);
        return y;
    }
}
