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
    DcMotor CarouselMotor;

    private ElapsedTime runtime = new ElapsedTime();

    final double ticksInARotation = 537.7;
    final double theoreticalMaxRadius = 9;

    final double DISTANCE_PER_SECOND = 104.25;
    final double DEGREES_PER_SECOND = 350.0; // approximated


    @Override
    public void runOpMode(){

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");

        waitForStart();

        Forward(6, 0.5);
        StrafeRight(54, 0.5);
        CarouselMotor.setPower(1);
        sleep(1000); //possibly figure out precise number of rotations to get duck off, then do encoders for it
        CarouselMotor.setPower(0);
        TurnLeft(motorArcLength(90), 0.5); //motorArcLength() returns an inch amount that is passed to motorTicks()
        Forward(6, 0.5);
        StrafeLeft(9, 0.5);
        Forward(72, 1);

//        Forward(-0.2);
//        sleep(300);
//        Stop();
//
//        StrafeLeft(1);
//        sleep(1005);
//
//        Stop();
//        sleep(1000);
//
//        Forward(-0.5);
//        sleep(600);
//
//        StrafeLeft(0.4);
//        sleep(800);
//
//        Stop();

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

    public static double motorArcLength (int theta) {
        int rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalMaxRadius; //isolates S, arc length
    	/*
    	all the turning math is done on the assumption that driving a distance as a line
    	is the same as driving that distance around a circumference
    	as in, the turning motion does not counteract movement along the circumference
    	and if all 4 wheels drive for 10 inches, then if half the wheels drive opposite to start turning,
    	they would still drive 10 inches, just along the circumference of their rotation
    	this is likely not true, but I cannot find math online and can't really model it either
    	to correct much, just do testing
    	*/
    }

    public static double motorTicks (double inches) {
        double diameter = 3.5;

        double circumference = Math.PI * diameter;

        double inchesPerTick = circumference / ticksInARotation;

        return inches / inchesPerTick;
    }

    public static double LinearSlideTicks(double inches) {

        double circumference = 5.0; // might be wrong if it is then we're FUCKED !

        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 ticks

        return inches / inchesPerTick;
    }

    public void StrafeLeft (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders()

        FrontLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void StrafeRight (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders()

        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(-Power);

        waitForMotorEncoders();
    }

    public void TurnLeft (double inches, double Power) {
        // both left sides go forward
        // both right sides go backwards
        // this makes the robot turn left and stationary

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders()

        FrontLeft.setPower(-Power);
        BackLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);

        waitForMotorEncoders();
    }

    public void TurnRight (double inches, double Power) {
        // both right sides go forward
        // both left sides go backwards

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders()

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void Forward (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders()

        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);

        waitForMotorEncoders();
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);

        encoderMotorReset();
    }

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    public void setMotorTargets (int motorTarget) {
        FrontLeft.setTargetPosition(motorTarget);
        FrontRight.setTargetPosition(motorTarget);
        BackLeft.setTargetPosition(motorTarget);
        BackRight.setTargetPosition(motorTarget);
    }

    public void runMotorEncoders () {
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void waitForMotorEncoders () {
        while (FrontLeft.isBusy() && FrontRight.isBusy() && BackLeft.isBusy() && BackRight.isBusy()) {
            idle();
        }

        Stop();
    }
}
