package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.firstinspires.ftc.teamcode.threads.RobotThread;

public abstract class AutonomousBaseBottom extends AutonomousBase {

    Robot r;
    BarcodePipeline bP;

    private ElapsedTime runtime = new ElapsedTime();

    int mult = 1;
    double lastCalledAngle; //store the current heading (normalized, convert to deg) to correct to

    public AutonomousBaseBottom() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void setup() {
        r = new Robot(telemetry, hardwareMap);
        bP = new BarcodePipeline(telemetry);
        r.setupWebcam(hardwareMap);
        r.webcam.setPipeline(bP);
    }

    @Override
    public void runAuto() {
        r = new Robot(telemetry, hardwareMap);
        RobotThread thread = new RobotThread(r, this);
        thread.start();
        waitForStart();

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        switch (bP.getBarcode()) {
            case LEFT:
            case MIDDLE:
                r.setLinearSlidePosition(r.theoreticalMiddleExtension);
                break;
            case RIGHT:
                r.setLinearSlidePosition(r.theoreticalFullExtension);
                break;
        }

        r.LSExtensionServo.setPosition(0);

        if (bP.getBarcode() == BarcodePipeline.Barcode.LEFT) r.setLinearSlidePosition(r.theoreticalGroundExtension);

        r.setMotorTargets(mult * (34), Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        sleep(100);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.3);

        r.setMotorTargets(16, Robot.Drive.FORWARD);
        r.drive(0.3);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        r.correctAngle(0.1, this);

        sleep(100);

        r.Intake.setPower(0.75);
        sleep(1000);

        r.Intake.setPower(0);

        r.setMotorTargets(22, Robot.Drive.BACKWARD);
        r.drive(0.3);

        sleep(200);

        r.setMotorTargets(3, Robot.Drive.FORWARD);
        r.drive(0.3);

        sleep(500);

        r.setMotorTargets(mult * 43, Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        sleep(100);

        r.correctAngle(0.1, this);

        // for whatever reason, the robot moves BACKWARDS when strafing.
        // move backwards again to ensure that the robot will face forward when turning

        r.setMotorTargets(4.5, Robot.Drive.BACKWARD);
        r.drive(0.3);

        r.setMotorTargets(12.55, Robot.Drive.FORWARD);
        r.drive(0.3);

        sleep(100);

        r.setMotorTargets(mult * (15.5), Robot.Drive.STRAFE_LEFT);
        r.drive(0.3);

        // right now the robot is next to carousel, a diagonal from the carousel

        if (mult == 1) { // red side
            telemetry.update();
            r.setMotorTargets(r.motorArcLength(55), Robot.Drive.TURN_RIGHT);
            r.drive(0.3);
            //save this heading
            lastCalledAngle = r.getHeading() * 180 / Math.PI;

            r.setMotorTargets(4, Robot.Drive.STRAFE_RIGHT);
            r.drive(0.3);

            r.setMotorTargets(3, Robot.Drive.BACKWARD);
            r.drive(0.3);
        }

        else {
            r.setMotorTargets(mult * (5), Robot.Drive.STRAFE_LEFT);
            r.drive(0.3);
            r.setMotorTargets(7.8, Robot.Drive.BACKWARD);
            r.drive(0.1);
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

        r.correctAngle(0.1, this);

        sleep(100);

        r.LSExtensionServo.setPosition(0);
        sleep(1000);

        r.setMotorTargets(12, Robot.Drive.FORWARD);
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

