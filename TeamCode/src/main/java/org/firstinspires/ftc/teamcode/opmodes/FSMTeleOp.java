package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;

@TeleOp (name = "FSMTeleOp")
public class FSMTeleOp extends OpMode {
    Robot r;

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);
    }

    @Override
    public void loop() {

    }
}
