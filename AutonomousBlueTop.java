package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="AutonomousBlueTop")
public class AutonomousBlueTop extends LinearOpMode {
    Robot r;

    private ElapsedTime runtime = new ElapsedTime();

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

        r = new Robot(telemetry, hardwareMap);

        waitForStart();

        r.setMotorTargets(20, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.2);
        r.setMotorTargets(0.5, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.setLinearSlidePosition((int) r.theoreticalFullExtension);
        r.LSExtensionServo.setPosition(Robot.LSExtensionServoPosition.TOP);

        sleep(1000); // wait for extension servo to go up

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.15);

        sleep(200);

        r.Intake.setPower(0.75);
        sleep(1000);
        r.Intake.setPower(0);

        sleep(100);

        r.setMotorTargets(16, Robot.Drive.BACKWARD);
        r.drive(0.2);

        sleep(100);

        r.setMotorTargets(2, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.LSExtensionServo.setPosition(Robot.LSExtensionServoPosition.BOTTOM);

        sleep(100);

        r.setLinearSlidePosition((int) r.theoreticalMiddleExtension);
        r.setMotorTargets(30, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.setMotorTargets(30, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        r.setLinearSlidePosition(0);
        r.setMotorTargets(12, Robot.Drive.FORWARD);
        r.drive(0.4);
    }

    public void NoEncodersFullAutonomous() {

    }

    public enum Drive {
        FORWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STRAFE_LEFT,
        STRAFE_RIGHT
    }

}
