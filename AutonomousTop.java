package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "cobaltAutonomousOne")
public class AutonomousTop extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;

/* TODO
  move forward
  strafe left
  turn

  step #1 get duck on carousel
    1a Maybe get random duck

  */

    final String side = "left";

    @Override
    public void runOpMode() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");

    }

    public void TestAutonomous() {
    /*
        This is for testing our autonomous
         */

        /*
        Check TODO
         */

        // NOTE: flip powers when side is "right"

        // step 1: turn right 90 degree (TODO: find power equal to 90 degree)
        // step 2: go forward the amount that will reach carousel
        // step 3: spin that carousel and get the point
        // step 4: strafeleft for some power

        encoderReset();

        stop();
        sleep(30000);

    }

    public void StrafeRight (double Power) {
        FrontLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);
    }

    public void StrafeLeft (double Power) {
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

   public void Forward (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    public void Backward (double Power) {
        FrontLeft.setPower(Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(-Power);
    }

    //encoders
    // TODO: get number of ticks, figure out the circumference of wheel, divide circumference by ticks to get 1 inch per # ticks
    //28 ticks in a revolution of 5203s
    //3.5 inches diameter
    public void encoderReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    public static double Ticks (double inches) {
        double diameter = 3.5;
        double circumference = Math.PI * diameter;

        double ticksPerInch = circumference / diameter;

        return ticksPerInch * inches;

    }


}
