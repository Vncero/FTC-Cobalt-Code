package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseTop;

@Autonomous(name="AutonomousRedTop")
public class AutonomousRedTop extends AutonomousBaseTop {
    @Override
    public void setup() {
        setRed();
    }
}
