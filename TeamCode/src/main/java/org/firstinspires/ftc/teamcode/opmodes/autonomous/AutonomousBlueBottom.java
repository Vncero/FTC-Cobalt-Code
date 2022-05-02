package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseBottom;

@Autonomous(name="AutonomousBlueBottom")
public class AutonomousBlueBottom extends AutonomousBaseBottom {

    @Override
    public void setup() {
        super.setMult(Multiplier.BLUE);
    }
}
