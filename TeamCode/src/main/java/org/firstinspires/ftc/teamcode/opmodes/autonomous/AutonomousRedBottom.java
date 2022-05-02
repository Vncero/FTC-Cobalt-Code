package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseBottom;

@Autonomous(name="AutonomousRedBottom")
public class AutonomousRedBottom extends AutonomousBaseBottom {

    @Override
    public void setup() {
        super.setMult(Multiplier.RED); // just to be more verbose
    }
}
