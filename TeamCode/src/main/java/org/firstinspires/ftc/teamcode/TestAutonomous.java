package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBase;
import org.firstinspires.ftc.teamcode.opmodes.autonomous.base.AutonomousBaseBottom;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

@Autonomous(name="TestAutonomous")
public class TestAutonomous extends LinearOpMode {
    Servo horizontal;
    @Override
    public void runOpMode() {
        waitForStart();

        horizontal = hardwareMap.get(Servo.class, "horizontal");
        horizontal.setPosition(0.5);

        while (opModeIsActive()) {

        }
    }
}
