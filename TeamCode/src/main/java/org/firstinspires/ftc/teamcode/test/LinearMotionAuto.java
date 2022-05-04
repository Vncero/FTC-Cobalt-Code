package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="LinearMotionAuto")
public class LinearMotionAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        double x = 1;
        double y = eq(x);

        r.FrontLeft.setPower(y+x);
        r.FrontRight.setPower(-y+x);
        r.BackLeft.setPower(y-x);
        r.BackRight.setPower(-y-c);

        sleep(1000);
    }

    public double eq(int x) {
        double y = 0.5 * x;
    }
}
