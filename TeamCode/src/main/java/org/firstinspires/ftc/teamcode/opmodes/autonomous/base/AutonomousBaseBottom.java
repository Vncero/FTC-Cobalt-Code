package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

public abstract class AutonomousBaseBottom extends AutonomousBase {

    Robot r;
    private ElapsedTime runtime = new ElapsedTime();

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

    public AutonomousBaseBottom() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void runAuto() {
        r = new Robot(telemetry, hardwareMap);
        RobotThread thread = new RobotThread(r, this);
        thread.start();
        waitForStart();

<<<<<<< HEAD
        //strafe to center robot for barcode
        r.setMotorTargets(mult * 6.0, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.5);

        //read barcode
        // this loops until the barcode value is given back
        BarcodePipeline.Barcode barcode = bP.getBarcode();

        r.closeWebcam();

        //move linear slides based on barcode
=======
>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        r.LinearSlide.setTargetPosition((int) r.theoreticalFullExtension);
        r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        r.LinearSlide.setPower(0.75);
        while (r.LinearSlide.isBusy()) {}
        r.LinearSlide.setPower(0);

        r.LSExtensionServo.setPosition(1);

        sleep(100);

        r.setMotorTargets(mult * (34), Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        sleep(100);

<<<<<<< HEAD
        //drive up to shipping hub
        r.setMotorTargets(15, Robot.Drive.FORWARD);
=======
        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.3);

        r.setMotorTargets(16, Robot.Drive.FORWARD);
>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        r.drive(0.3);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        r.correctAngle(0.1, this);

        sleep(100);

<<<<<<< HEAD
        // push out the cargo within our intake
        r.Intake.setPower(1);
=======
        r.Intake.setPower(0.75);
>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        sleep(1000);
        r.Intake.setPower(0);
<<<<<<< HEAD
        
        // move backwards towards the wall
        r.setMotorTargets(19, Robot.Drive.BACKWARD);
        r.drive(0.3);

=======

        r.setMotorTargets(22, Robot.Drive.BACKWARD);
        r.drive(0.3);

        sleep(200);

        r.setMotorTargets(3, Robot.Drive.FORWARD);
        r.drive(0.3);

>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        sleep(500);

        // move towards the carousel
        r.setMotorTargets(mult * 43, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);
        
        // correct angle so that the robot faces foward
        r.correctAngle(0.1, this);

<<<<<<< HEAD
        // move forward to be diagonal (top left or right) from the carousel
=======
        // for whatever reason, the robot moves BACKWARDS when strafing.
        // move backwards again to ensure that the robot will face forward when turning

        r.setMotorTargets(4.5, Robot.Drive.BACKWARD);
        r.drive(0.3);

>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        r.setMotorTargets(12.55, Robot.Drive.FORWARD);
        r.drive(0.3);

        sleep(100);

        // right now the robot is next to carousel, a diagonal from the carousel

        // TODO: MAKE SURE THE IF STATEMENT CHAIN BELOW WILL PUSH THE CAROUSEL MOTOR DIRECTLY ON THE CAROUSEL
        if (mult == 1) { // red side
            // strafe left 15.5 inches
            r.setMotorTargets(15.5, Robot.Drive.STRAFE_LEFT);
            r.drive(0.3);
            
            // turn to the right by 55 degrees (the carousel motor should be directly facing the carousel disk)

            // TODO: TEST HERE
            r.setMotorTargets(r.motorArcLength(55), Robot.Drive.TURN_RIGHT);
            r.drive(0.3);
            
            // strafe right by 4 inches, then move 3 inches backwards towards the carousel
            r.setMotorTargets(4, Robot.Drive.STRAFE_RIGHT);
            r.drive(0.3);
            r.setMotorTargets(3, Robot.Drive.BACKWARD);
            r.drive(0.3);
        }

        else {
<<<<<<< HEAD
            // turn 45 degrees to the left, move backwards 8 inches
            r.setMotorTargets(r.motorArcLength(45), Robot.Drive.TURN_LEFT);
            r.drive(0.5);
            r.setMotorTargets(8, Robot.Drive.BACKWARD);
            r.drive(0.5);
=======
            r.setMotorTargets(mult * (5), Robot.Drive.STRAFE_LEFT);
            r.drive(0.3);
            r.setMotorTargets(7.8, Robot.Drive.BACKWARD);
            r.drive(0.1);
>>>>>>> 8476fcdb941f5ea30f69dad6b7fed9b54878be64
        }

        // by the end of that, the robot should be oriented in a way where the carousel motor is touching the carousel disk
        // turn the carousel for 5 seconds, direction is accorded to the mult
        r.CarouselMotor.setPower(mult * 0.5);
        sleep(5000);
        
        // stop the carousel and correct the angle (face fowards again)
        r.CarouselMotor.setPower(0);
        r.correctAngle(0.1, this);
        
        sleep(100);

        // move BUCKET TURNER!! back to bottom position, wait for it to move to the bottom
        r.LSExtensionServo.setPosition(0);
        sleep(500);

        // move foward to the parking spot
        r.setMotorTargets(12, Robot.Drive.FORWARD);
        r.drive(0.5);

        // set linear slides back to 0
        r.setLinearSlidePosition(0);
        
        /*
        Now, the robot has read barcode and dropped the freight at the correct level, moved to carousel and dropped the duck, and parked at warehouse, which give us
        [VINCENT INSERT VALUE HERE] points
        */
        
        // now a nice, simple, beautiful final touch to flex on our opponents and make them think they are doomed...
        while (opModeIsActive()) {
            r.TurnRight(0.5);
        }
    }


    public void setMult(Multiplier mult) {
        this.mult = mult.multiplier;
    }

    public enum Multiplier {
        BLUE(-1),
        RED(1);
        public final int multiplier;

        Multiplier(int multiplier) {
            this.multiplier = multiplier;
        }
    }
}

