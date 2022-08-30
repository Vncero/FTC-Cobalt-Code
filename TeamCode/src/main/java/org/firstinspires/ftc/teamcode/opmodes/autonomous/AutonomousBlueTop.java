package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseTop;

@Autonomous(name="AutonomousBlueTop")
public class AutonomousBlueTop extends AutonomousBaseTop {
    @Override
    public void setup() {
        setBlue();
    }
}
