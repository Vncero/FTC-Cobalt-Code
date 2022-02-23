package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="AutonomousBlueBottom")
public class AutonomousBlueBottom extends AutonomousBaseBottom {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor CarouselMotor;
    DcMotor LinearSlide;
    CRServo Intake;
    Servo LSExtensionServo;

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

        super.setMult(-1);

        telemetry.addLine("wsg daddy");
        telemetry.update();

        super.__start();
    }
}
