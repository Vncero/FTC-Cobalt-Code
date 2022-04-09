package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutonomousBaseBottom extends LinearOpMode {

    Robot r;
    private ElapsedTime runtime = new ElapsedTime();

    RobotCommand command;

    int mult = 1;

    /* a lot of notes
    the objective - get radius of turning circle
    when the r turns, the edges of the wheel hit points that make up its turning circle
    relating the turning circle to the r, we can deduce that the diameter of this circle can be found by
    finding the diagonal measure between wheels, like r.FrontLeft and r.BackRight.
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
        r = new Robot(telemetry, hardwareMap);
        command = new RobotCommand(r, this);
        command.start();
        waitForStart();

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.LinearSlide.setTargetPosition((int) r.theoreticalFullExtension);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.LinearSlide.setPower(0.75);
        while (r.LinearSlide.isBusy()) {}
        r.LinearSlide.setPower(0);

        r.LSExtensionServo.setPosition(r.up);

        sleep(100);

        r.setMotorTargets(mult * (34), Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        sleep(100);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.1);

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.2);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        r.correctAngle();

        sleep(100);

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

        r.setMotorTargets(mult * 43, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);

        r.correctAngle();

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
        r.Stop();

        r.CarouselMotor.setPower(0);

        r.correctAngle();

        sleep(100);

        if (mult == 1) {
            r.Stop();
        }

        r.LSExtensionServo.setPosition(r.bottom);
        sleep(1000);
        r.setLinearSlidePosition(0);
    }
}

