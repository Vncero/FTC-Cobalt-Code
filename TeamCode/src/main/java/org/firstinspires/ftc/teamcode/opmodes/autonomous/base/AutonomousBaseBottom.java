package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;

public abstract class AutonomousBaseBottom extends AutonomousBase {
    private int mult = 1;

    protected AutonomousBaseBottom() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void runAuto() {
        //setup robot, barcode reading, and thread for imu
        robotThread.cameraOpenRequested = true; //theoretically triggers attempts to open camera
        if (robot.isCameraOpen()) robot.webcam.setPipeline(barcodePipeline);

        //ensure extension is at lowest
        robot.lsExtensionServo.setPosition(1);

        waitForStart();

        double blueSideStrafe = 8;

        //strafe to center robot for barcode
        // mult == -1 means blue side
        robot.setMotorTargets(mult * (mult == -1 ? blueSideStrafe: 0), false, Robot.Drive.STRAFE_RIGHT);
        robot.drive(mult == -1 ? 0.5: 0);

        BarcodePipeline.Barcode barcode = robot.isCameraOpen() ? barcodePipeline.getBarcode(this) : barcodePipeline.randomRead();
        if (robot.isCameraOpen()) robotThread.requestCameraClose();

        //flip
        robot.lsExtensionServo.setPosition(0);

        sleep(700);

        double forwardInches = 20.5;
        int leftTakeAway = 0; // take away this value when barcode left

        //if ground level, get the move extension up and linear slide back down
        if (barcode == BarcodePipeline.Barcode.LEFT) {
            // add shit here right now it dont matter
//            forwardInches -= leftTakeAway;
//            sleep(300);
    //        robot.setLinearSlidePosition(r.theoreticalGroundExtension);
        }

        //adjust for middle level of hub
        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
        robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalGroundExtension + Robot.ticksInARotation / 4.0);
        }

        //strafe to center with shipping hub
    robot.setMotorTargets(mult * (mult == -1 ? 34 - blueSideStrafe : 34), false, Robot.Drive.STRAFE_RIGHT);
    robot.drive(0.3);

        sleep(100);

        //correct against the wall
    robot.setMotorTargets(1, false, Robot.Drive.BACKWARD);
    robot.drive(0.3);
    robot.correctAngle(0.1, this);

        //drive up to shipping hub
        // forward inches 16 if middle or left, else 16 - leftTakeAway
    robot.setMotorTargets(forwardInches, false, Robot.Drive.FORWARD);
    robot.drive(0.5);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

    robot.correctAngle(0.1, this);

        sleep(100);

    robot.intake.setPower(1);

        sleep(1500);

    robot.intake.setPower(0);

    robot.setMotorTargets(19 - leftTakeAway, false, Robot.Drive.BACKWARD);
    robot.drive(0.3);

        // HERE

        sleep(100);

    robot.setMotorTargets(mult * 49, false, Robot.Drive.STRAFE_LEFT);
    robot.drive(0.3);
    sleep(100);

    robot.correctAngle(0.1, this);

    robot.setMotorTargets(10, false, Robot.Drive.FORWARD);
    robot.drive(0.3);

        sleep(100);

    robot.setMotorTargets(mult * (14.5), false, Robot.Drive.STRAFE_LEFT);
    robot.drive(0.3);
        // right now the robot is next to carousel, a diagonal from the carousel

    robot.setMotorTargets(3, false, Robot.Drive.BACKWARD);
    robot.drive(0.3);

        if (mult == 1) { // red side
            robot.setMotorTargets(3, false, Robot.Drive.BACKWARD);
            robot.drive(0.3);
        } else {
            robot.setMotorTargets(Robot.motorArcLength(45), false, Robot.Drive.TURN_LEFT);
            robot.drive(0.5);
            robot.setMotorTargets(1.5 , false, Robot.Drive.BACKWARD);
            robot.drive(0.5);
        }

        robot.carouselMotor.setPower(mult * 0.5);
        sleep(5000);

        robot.Stop();

        robot.carouselMotor.setPower(0);

        robot.correctAngle(0.1, this);

        sleep(100);

        robot.lsExtensionServo.setPosition(1);
        sleep(750);

        robot.setMotorTargets(25, false, Robot.Drive.FORWARD);
        robot.drive(0.5);

        robot.setLinearSlidePosition(0);
    }

    protected void setMult(Multiplier mult) {
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

