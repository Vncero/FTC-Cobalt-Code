package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

public abstract class AutonomousBaseBottom extends AutonomousBase {

    Robot r;
    private ElapsedTime runtime = new ElapsedTime();

    int mult = 1;

    public AutonomousBaseBottom() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void runAuto() {
        r = new Robot(telemetry, hardwareMap);
        RobotThread thread = new RobotThread(r, this);
        thread.start();
        waitForStart();

        //strafe to center robot for barcode
        r.setMotorTargets(mult * 6.0, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.5);

        //read barcode
        // this loops until the barcode value is given back
        BarcodePipeline.Barcode barcode = bP.getBarcode();

        r.closeWebcam();

        //move linear slides based on barcode
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

        //drive up to shipping hub
        r.setMotorTargets(15, Robot.Drive.FORWARD);
        r.drive(0.3);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        r.correctAngle(0.1, this);

        sleep(100);

        // push out the cargo within our intake
        r.Intake.setPower(1);
        sleep(1000);
        r.Intake.setPower(0);
        
        // move backwards towards the wall
        r.setMotorTargets(19, Robot.Drive.BACKWARD);
        r.drive(0.3);

        sleep(500);

        // move towards the carousel
        r.setMotorTargets(mult * 43, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);
        
        // correct angle so that the robot faces foward
        r.correctAngle(0.1, this);

        // move forward to be diagonal (top left or right) from the carousel
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
            // turn 45 degrees to the left, move backwards 8 inches
            r.setMotorTargets(r.motorArcLength(45), Robot.Drive.TURN_LEFT);
            r.drive(0.5);
            r.setMotorTargets(8, Robot.Drive.BACKWARD);
            r.drive(0.5);
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

