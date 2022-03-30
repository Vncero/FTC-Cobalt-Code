package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="AutonomousBlueBottom")
public class AutonomousBlueBottom extends AutonomousBaseBottom {
    @Override
    public void runOpMode() {

        super.setMult(-1);

        super.__start();
    }
}
