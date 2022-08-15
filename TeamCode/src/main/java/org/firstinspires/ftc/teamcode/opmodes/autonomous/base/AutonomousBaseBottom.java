package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

public abstract class AutonomousBaseBottom extends AutonomousBase {

    BarcodePipeline bP;
    BarcodePipeline.Barcode barcode;
    private ElapsedTime runtime = new ElapsedTime();

    int mult = 1;

    int top = 0;
    int bottom = 1;

    public AutonomousBaseBottom() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void runAuto() {
        //setup robot, barcode reading, and thread for imu
        bP = new BarcodePipeline(telemetry);
        rThread.cameraOpenRequested = true; //theoretically triggers attempts to open camera
        if (r.cameraIsOpen) r.webcam.setPipeline(bP);

        //ensure extension is at lowest
        r.LSExtensionServo.setPosition(1);

        waitForStart();

        double blueSideStrafe = 8;

        //strafe to center robot for barcode
        // mult == -1 means blue side
        r.setMotorTargets(mult * (mult == -1 ? blueSideStrafe: 0), Robot.Drive.STRAFE_RIGHT);
        r.drive(mult == -1 ? 0.5: 0);

        barcode = r.cameraIsOpen ? bP.getBarcode(this) : bP.randomRead();
        if (r.cameraIsOpen) rThread.requestCameraClose();

        //flip
        r.LSExtensionServo.setPosition(top);

        sleep(700);

        double forwardInches = 20.5;
        int leftTakeAway = 0; // take away this value when barcode left

        //if ground level, get the move extension up and linear slide back down
        if (barcode == BarcodePipeline.Barcode.LEFT) {
            // add shit here right now it dont matter
//            forwardInches -= leftTakeAway;
//            sleep(300);
//            r.setLinearSlidePosition(r.theoreticalGroundExtension);
        }

        //adjust for middle level of hub
        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
            r.setLinearSlidePosition(r.theoreticalGroundExtension + r.ticksInARotation / 4.0);
        }

        //strafe to center with shipping hub
        r.setMotorTargets(mult * (mult == -1 ? 34 - blueSideStrafe : 34), Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        sleep(100);

        //correct against the wall
        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.3);
        r.correctAngle(0.1, this);

        //drive up to shipping hub
        // forward inches 16 if middle or left, else 16 - leftTakeAway
        r.setMotorTargets(forwardInches, Robot.Drive.FORWARD);
        r.drive(0.5);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        r.correctAngle(0.1, this);

        sleep(100);

        r.Intake.setPower(1);

        sleep(1500);

        r.Intake.setPower(0);

        r.setMotorTargets(19 - leftTakeAway, Robot.Drive.BACKWARD);
        r.drive(0.3);

        // HERE

        sleep(100);

        r.setMotorTargets(mult * 49, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);

        r.correctAngle(0.1, this);

        r.setMotorTargets(10, Robot.Drive.FORWARD);
        r.drive(0.3);

        sleep(100);

        r.setMotorTargets(mult * (14.5), Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);
        // right now the robot is next to carousel, a diagonal from the carousel

        r.setMotorTargets(3, Robot.Drive.BACKWARD);
        r.drive(0.3);

        if (mult == 1) { // red side
            r.setMotorTargets(3, Robot.Drive.BACKWARD);
            r.drive(0.3);

        } else {
            r.setMotorTargets(r.motorArcLength(45), Robot.Drive.TURN_LEFT);
            r.drive(0.5);
            r.setMotorTargets(1.5 , Robot.Drive.BACKWARD);
            r.drive(0.5);
        }

        r.CarouselMotor.setPower(mult * 0.5);

        sleep(5000);
        r.Stop();

        r.CarouselMotor.setPower(0);

        r.correctAngle(0.1, this);

        sleep(100);

        r.LSExtensionServo.setPosition(1);
        sleep(750);

        r.setMotorTargets(25, Robot.Drive.FORWARD);

        r.drive(0.5);

        r.setLinearSlidePosition(0);
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

