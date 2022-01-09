package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.

@Autonomous(name="AutonomousRedBottom")
public class AutonomousBottom extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor CarouselMotor;

    private ElapsedTime runtime = new ElapsedTime();

    final double ticksInARotation = 537.7;
    final double theoreticalRadius = 10.9;
    /* a lot of notes
    the objective - get radius of turning circle
    when the robot turns, the edges of the wheel hit points that make up its turning circle
    relating the turning circle to the robot, we can deduce that the diameter of this circle can be found by
    finding the diagonal measure between wheels, like FrontLeft and BackRight.
    without being able to directly measure, we can estimate this diagonal by figuring out the robot's dimensions
    we know that it fits between the barrier gap, meaning the width is at max 13.68in, use 13.65in
    for length, the robot fits in the 2ft by 2ft squares, but the size limit is 18, so use 17in
    now we have two measures for pythagorean theorem
    13.65^2 + 17^2 = c^2
    186.3225 + 289 = c^2
    475.3225 = c^2
    sqrt(475.3225) ~ 21.8in diameter
    21.8 / 2 = 10.9in radius
    * */

    /* previous turn circle radius estimate
     the robot must be within 18*18*18
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

        StrafeLeft(6, 0.5);
        TurnRight(motorArcLength(90), 0.5); //motorArcLength() returns an inch amount that is passed to motorTicks()
        CarouselMotor.setPower(1);
        sleep(1000); //possibly figure out precise number of rotations to get duck off, then do encoders for it
        CarouselMotor.setPower(0);
        Forward(6, 0.5);
        StrafeRight(9, 0.5);
        Forward(72, 1);

//        Forward(0.55);
//        sleep(675);
//
//        Stop();
//
//        StrafeLeft(0.5);
//        sleep(1100);
//        // Stop();
//
//        Stop();
//
//        Forward(-0.3);
//        sleep(100);
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

    public int motorTicks (double inches) {
        double diameter = 3.5;

        double circumference = Math.PI * diameter;

        double inchesPerTick = circumference / ticksInARotation; // approx 0.0204492733635192 inch

        return (int) Math.floor(inches / inchesPerTick);
    }

    public double linearSlideTicks(double inches) {
        //copy changes in these measurments from TeleOp
        double circumference = 5.0; // might be wrong if it is then we're FUCKED !

        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inch

        return inches / inchesPerTick;
    }

    public void StrafeLeft (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();

        FrontLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void StrafeRight (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();

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

        runMotorEncoders();

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

        runMotorEncoders();

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void Forward (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();

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
