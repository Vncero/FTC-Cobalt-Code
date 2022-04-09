package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@Autonomous(name="AutonomousRedBottom")
public class AutonomousRedBottom extends AutonomousBaseBottom {
    @Override
    public void runOpMode() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "Intake");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        LSExtensionServo = hardwareMap.get(Servo.class, "LSExtensionServo");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");

        super.setComponents(FrontLeft, FrontRight, BackLeft, BackRight, Intake, LinearSlide, LSExtensionServo, CarouselMotor);

//        super.setMult(-1); red side has no mult changed

        telemetry.addLine("wsg daddy");
        telemetry.update();

        super.__start();
    }
}
