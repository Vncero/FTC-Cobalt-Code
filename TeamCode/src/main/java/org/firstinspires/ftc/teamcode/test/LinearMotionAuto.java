package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="LinearMotionAuto")
public class LinearMotionAuto extends LinearOpMode {
    double FPS = 2;

    @Override
    public void runOpMode() {
        double x = 0;
        while (opModeIsActive()) {
            double y = eq(x);

            r.FrontLeft.setPower(y+x);
            r.FrontRight.setPower(-y+x);
            r.BackLeft.setPower(y-x);
            r.BackRight.setPower(-y-x);

            sleep(1000 / FPS);

            x += 0.5;
        }
    }

    public double eq(int x) {
        double y = 1d - Math.pow(2d, -x);
        return y;
    }
}
