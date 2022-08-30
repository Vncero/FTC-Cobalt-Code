package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseTop;

@Autonomous(name="AutonomousRedTop")
public class AutonomousRedTop extends AutonomousBaseTop {
    @Override
    public void setup() {
        setRed();
    }
}
