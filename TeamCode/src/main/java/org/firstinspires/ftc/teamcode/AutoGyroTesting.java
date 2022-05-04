package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;


@Autonomous(name="servo testing")
public class AutoGyroTesting extends LinearOpMode {
    CRServo small;
    CRServo big;

    @Override
    public void runOpMode () {
//        small = hardwareMap.get(CRServo.class, "small");
        big = hardwareMap.get(CRServo.class, "big");
        big.setPower(0.01);
    }

}
