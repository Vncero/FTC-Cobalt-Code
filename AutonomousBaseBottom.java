package org.firstinspires.ftc.teamcode;

// DO NOT RUN THIS - THIS IS MEANT TO BE BASE OBJECT

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.bosch.BNO055IMU;

public class AutonomousBaseBottom extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor CarouselMotor;
    DcMotor LinearSlide;

    public Robot r = new Robot(telemetry, hardwareMap);
    private ElapsedTime runtime = new ElapsedTime();

    final double ticksInARotation = 537.7;
    final double theoreticalRadius = 10.9;
    final double diameter = 5.75;
    final double theoreticalMiddleExtension =  LinearSlideTicks(5.5);
    final double theoreticalGroundExtension = LinearSlideTicks(3);
    final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(5));
    double globalAngle;

    CRServo Intake;
    Servo LSExtensionServo;

    int mult = 1;

    /* a lot of notes
    the objective - get radius of turning circle
    when the r turns, the edges of the wheel hit points that make up its turning circle
    relating the turning circle to the r, we can deduce that the diameter of this circle can be found by
    finding the diagonal measure between wheels, like FrontLeft and BackRight.
    without being able to directly measure, we can estimate this diagonal by figuring out the r's dimensions
    we know that it fits between the barrier gap, meaning the width is at max 13.68in, use 13.65in
    for length, the r fits in the 2ft by 2ft squares, but the size limit is 18, so use 17in
    now we have two measures for pythagorean theorem
    13.65^2 + 17^2 = c^2
    186.3225 + 289 = c^2
    475.3225 = c^2
    sqrt(475.3225) ~ 21.8in diameter
    21.8 / 2 = 10.9in radius
    * */

    /* previous turn circle radius estimate
    the r must be within 18*18*18
    therefore, the circle it rotates has diameter 18 at max
    18 / 2 = 9
    previously, I made the judgment that the circle it rotates needs the same area as the 18*18 square
    however, this would create a circle larger than the square
    anyway, here's the math
    18*18 = 324 sq. in., max area of the circle
    324 >= Math.PI * Math.pow(r, 2)
    324 / Math.PI ~ 103.13240312354819
    103.13240312354819 >~ Math.pow(r, 2)
    Math.sqrt(103.13240312354819) ~ 10.155
    10.155 >~ r
    20.310 >~ d
    round diameter down a little to 20, then circumference is about 62.83185
    */

    @Override
    public void runOpMode() {
//        __start();

    }

    public void setMult(int mult) {
        this.mult = mult;
    }

    public void __start(){
        r.hardwareMap(hardwareMap);

        this.FrontLeft = r.FrontLeft;
        this.BackLeft = r.BackLeft;
        this.FrontRight = r.FrontRight;
        this.BackRight = r.BackRight;

        waitForStart();

        LinearSlide = r.LinearSlide;

        LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LinearSlide.setTargetPosition((int) theoreticalFullExtension);
        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LinearSlide.setPower(0.75);
        while (LinearSlide.isBusy()) {}
        LinearSlide.setPower(0);

        r.LSExtensionServo.setPosition(r.up);

        sleep(100);

        r.setMotorTargets(mult * (29), Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        sleep(100);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.1);

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.2);
        sleep(200);

        r.Intake.setPower(0.75);
        sleep(1000);

        telemetry.addLine("i am here, the intake is set power to: " + r.Intake.getPower());
        telemetry.update();

        r.Intake.setPower(0);

        r.setMotorTargets(22, Robot.Drive.BACKWARD);
        r.drive(0.15);

        sleep(200);

        r.setMotorTargets(3, Robot.Drive.FORWARD);
        r.drive(0.3);

        sleep(500);

        r.setMotorTargets(mult * 38, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);

        // for whatever reason, the robot moves BACKWARDS when strafing.
        // move backwards again to ensure that the robot will face forward when turning

        r.setMotorTargets(4.5, Robot.Drive.BACKWARD);
        r.drive(0.1);

        r.setMotorTargets(12.55, Robot.Drive.FORWARD);
        r.drive(0.15);

        sleep(100);

        r.setMotorTargets(mult * (15.5), Robot.Drive.STRAFE_LEFT);
        r.drive(0.15);

        if (mult == 1) { // red side
            telemetry.update();
            r.setMotorTargets(r.motorArcLength(55), Robot.Drive.TURN_RIGHT);
            r.drive(0.3);

            r.setMotorTargets(4, Robot.Drive.STRAFE_RIGHT);
            r.drive(0.2);

            r.setMotorTargets(3, Robot.Drive.BACKWARD);
            r.drive(0.1);
        }

        else {
            r.setMotorTargets(mult * (5), Robot.Drive.STRAFE_LEFT);
            r.drive(0.15);
            r.setMotorTargets(7.8, Robot.Drive.BACKWARD);
            r.drive(0.05);
        }

        r.CarouselMotor.setPower(mult * 0.5);

        if (mult == 1) { // red
            r.TurnRight(0.01);
            sleep(2000);
            r.Stop();
            sleep(3000);
        }

        else {
            sleep(5000);
        }
        Stop();

        r.CarouselMotor.setPower(0);

        if (mult == 1) {
            telemetry.addLine("hello i am here turning the robot back");
            telemetry.update();
            r.setMotorTargets(4, Robot.Drive.STRAFE_LEFT);
            r.drive(0.2);
            r.setMotorTargets(r.motorArcLength(55), Robot.Drive.TURN_LEFT);
            r.drive(0.3);

            r.setMotorTargets(3, Robot.Drive.FORWARD);
            r.drive(0.3);

            r.setMotorTargets(7, Robot.Drive.STRAFE_LEFT);
            r.drive(0.15);

            r.setMotorTargets(15.5, Robot.Drive.FORWARD);
        }

        else {
            r.setMotorTargets(17.5, Robot.Drive.FORWARD);
        }

        r.drive(0.3);

        sleep(100);

        if (mult == 1) {
//            r.setMotorTargets(5, Robot.Drive.STRAFE_LEFT);
//            r.drive(0.2);
//            r.setMotorTargets(5, Robot.Drive.FORWARD);
//            r.drive(0.5);

            Stop();
        }

        r.LSExtensionServo.setPosition(r.bottom);
        sleep(1000);
        r.setLinearSlidePosition(0);
    }

    public enum Drive {
        FORWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STRAFE_LEFT,
        STRAFE_RIGHT
    }

    public int LinearSlideTicks(double inches) {
        double diameter = 1.5;

        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !
        // original measurement was 5in
        //alt circumference ~ 4.75in.
        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick

        return (int) Math.floor(inches / inchesPerTick);
    }

    public double motorArcLength (int theta) {
        double rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalRadius; //returns S, the arc length
        /* old notes
        all the turning math is done on the assumption that driving a distance as a line
        is the same as driving that distance around a circumference
        as in, the turning motion does not counteract movement along the circumference
        and if all 4 wheels drive for 10 inches, then if half the wheels drive opposite to start turning,
        they would still drive 10 inches, just along the circumference of their rotation
        this is likely not true, but I cannot find math online and can't really model it either
        to correct much, just do testing
        */
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
        FrontLeft.setPower(-Power);
        BackLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);
    }

    public void TurnRight (double Power) {
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

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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

    public void setMotorTargets (int inches, Drive drive) {
        encoderMotorReset();

        int target = (int) r.motorTicks(inches);

        switch (drive) {
            case FORWARD:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(-target);
                break;
            case TURN_LEFT:
                FrontLeft.setTargetPosition(-target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(-target);
                break;
            case TURN_RIGHT:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(target);
                break;
            case STRAFE_LEFT:
                FrontLeft.setTargetPosition(-target);
                FrontRight.setTargetPosition(-target);
                BackLeft.setTargetPosition(target);
                BackRight.setTargetPosition(target);
                break;
            case STRAFE_RIGHT:
                FrontLeft.setTargetPosition(target);
                FrontRight.setTargetPosition(target);
                BackLeft.setTargetPosition(-target);
                BackRight.setTargetPosition(-target);
                break;
        }

        runMotorEncoders();

    }
}
