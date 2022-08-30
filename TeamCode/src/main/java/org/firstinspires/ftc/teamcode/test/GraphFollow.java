package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot;

@Autonomous(name="GraphFollow")
public class GraphFollow extends LinearOpMode {
    Robot r;

    @Override
    public void runOpMode() {
        r = new Robot(telemetry, hardwareMap);
        waitForStart();

        double inc = 0.5;

        double past_x = -1;
        double past_y = eq(past_x);

        double x = past_x + inc;
        double y = past_y + eq(past_x);

        double max = 0.5;

        while (opModeIsActive()) {
            double deltaY = y - past_y;
            double deltaX = x - past_x;

            past_x = x;
            past_y = y;

            x += inc;
            y = eq(x);

            double[] powers = {(deltaY + deltaX), (-deltaY + deltaX), (deltaY - deltaX), (-deltaY - deltaX)};

            double highest = powers[0];

            for (int i = 1; i < powers.length; i++) {
                if (Math.abs(powers[i]) > Math.abs(highest)) highest = powers[i];
            }

            telemetry.addData("highest", highest);

            for (int i = 0; i < powers.length; i++) {
                powers[i] *= (max/Math.abs(highest)); // clamps it between [-1 and 1]
            }

            r.frontLeft.setPower(powers[0]);
            r.frontRight.setPower(powers[1]);
            r.backLeft.setPower(powers[2]);
            r.backRight.setPower(powers[3]);

            telemetry.addData("powers[0]", powers[0]);
            telemetry.addData("powers[1]", powers[1]);
            telemetry.addData("powers[2]", powers[2]);
            telemetry.addData("powers[3]", powers[3]);

            telemetry.update();

            sleep(500);
        }
    }

    public double eq(double x) {
        return Math.pow(2d, x - 0.8d) - 1;
    }
}
