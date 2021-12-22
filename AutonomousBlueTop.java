package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.

@Autonomous(name="AutonomousBlueTop")
public class AutonomousBlueTop extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    CRServo CarouselTest;

    private ElapsedTime runtime = new ElapsedTime();
/* TODO
  move forward
  strafe left
  turn

  step #1 get duck on carousel
    1a Maybe get random duck

  */

    final String side = "left";
    final double DISTANCE_PER_SECOND = 104.25;
    final double DEGREES_PER_SECOND = 350.0; // approximated


    @Override
    public void runOpMode(){
        waitForStart();

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        // CarouselTest = hardwareMap.get(CRServo.class, "CarouselServo");
        // CarouselTest.setPower(0.5);

        Forward(-0.2);
        sleep(300);
        Stop();

        StrafeLeft(1);
        sleep(1005);

        Stop();
        sleep(1000);

        Forward(-0.5);
        sleep(600);

        StrafeLeft(0.4);
        sleep(800);

        Stop();

        // StrafeRight(1);
        // sleep(200);

        // Stop();
        // sleep(100);

        // StrafeRight(0.2);
        // sleep(500);

        // Stop();
        // sleep(100);

        // Forward(0.5);
        // sleep(400);
    }

    public void EncodersCode() throws InterruptedException{
        waitForStart();

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");

        // AT LEAST GET IT TO PARK

        int y = (int) Ticks(15.0);

        encoderReset();

        FrontLeft.setTargetPosition(y);
        FrontRight.setTargetPosition(-y);
        BackLeft.setTargetPosition(y);
        BackRight.setTargetPosition(-y);

        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        Forward(1.0);

        while (FrontLeft.isBusy() && FrontRight.isBusy() && BackLeft.isBusy() && BackRight.isBusy()) {}

        Stop();

        FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

        y = (int) Ticks(5.0);

        FrontLeft.setTargetPosition(y);
        FrontRight.setTargetPosition(-y);
        BackLeft.setTargetPosition(y);
        BackRight.setTargetPosition(-y);

        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        Forward(0.5);

        while (FrontLeft.isBusy() && FrontRight.isBusy() && BackLeft.isBusy() && BackRight.isBusy()) {}

        Stop();
    }

    public static double Ticks (double inches) {
        double diameter = 3.5;

        double circumference = Math.PI * diameter;

        double inchesPerTick = circumference / 1440;

        return inches / inchesPerTick;
    }

    public void encoderReset() {
        FrontLeft.setMode(DcMotor.RunMode.RESET_ENCODERS);
        FrontRight.setMode(DcMotor.RunMode.RESET_ENCODERS);
        BackLeft.setMode(DcMotor.RunMode.RESET_ENCODERS);
        BackRight.setMode(DcMotor.RunMode.RESET_ENCODERS);

        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }


    public void TestAutonomous() {
        /*
        This is for testing our autonomous
         */
        //same color up different color down;
        /*
        Check TODO
         */

        // NOTE: flip powers when side is "right"

        // step 1: turn right 90 degree (TODO: find power equal to 90 degree)
        // step 2: go forward the amount that will reach carousel
        // step 3: spin that carousel and get the point
        // step 4: strafeleft for some power
        //moves 104.25 inches per second at full power

        // step one: find counts per inches
        // step two: code

        // CarouselTest.setPower(1);
        // sleep(1000);
        // StopCarouselServo();
    }

    public void TestingPower(){
        // First test 90 degree
        /*
        at full power, the robot turns approximately 350 degrees
        350: 1
        350 * 90 / 350
         */

        double power = 1;


        TurnLeft(1);
        sleep(1000);
        stop();
    }

    public void StrafeLeft (double Power) {
        FrontLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);
    }

    public void StrafeRight (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnLeft (double Power) {
        // both left sides go forward
        // both right sides go backwards
        // this makes the robot turn left and stationary

        FrontLeft.setPower(-Power);
        BackLeft.setPower(-Power);

        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnRight (double Power) {
        // both right sides go forward
        // both left sides go backwards

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);

        FrontRight.setPower(Power);
        BackRight.setPower(Power);
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);
    }

    public void StopCarouselServo () {
        // CarouselTest.setPower(0);
    }

    public void Forward (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void Spin_Carousel(double Power){

    }

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setMotorTargets (int motorTarget) {
        FrontLeft.setTargetPosition(motorTarget);
        FrontRight.setTargetPosition(motorTarget);
        BackLeft.setTargetPosition(motorTarget);
        BackRight.setTargetPosition(motorTarget);
    }

}
