package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;

public class AutonomousBaseTop extends AutonomousBase {
    Robot r;
    BarcodePipeline bP;

    int mult = 1;

    int top = 0;
    int bottom = 1;

    @Override
    public void runAuto() throws InterruptedException {
        bP = new BarcodePipeline(telemetry, this);
        rThread.cameraOpenRequested = true; //theoretically triggers attempts to open camera
        if (r.cameraIsOpen) r.webcam.setPipeline(bP);

        r.LSExtensionServo.setPosition(1); // ensure extension at lowest
        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        waitForStart();
//        r.CarouselMotor.setPower(1);
        // get to the correct position to read barcode
//        r.setMotorTargets(mult * 3.0, Robot.Drive.STRAFE_LEFT);
//        r.drive(0.5);

        barcode = r.cameraIsOpen ? bP.getBarcode() : bP.randomRead();
        if (r.cameraIsOpen) rThread.requestCameraClose();

        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        switch (barcode) {
            case LEFT:
            case MIDDLE:
                r.setLinearSlidePosition(r.theoreticalMiddleExtension);
                break;
            case RIGHT:
                r.setLinearSlidePosition(r.theoreticalFullExtension);
                break;
        }

        r.LSExtensionServo.setPosition(0);

        double forward = 25;

        r.setMotorTargets(mult * 24, Robot.Drive.STRAFE_LEFT);
        r.drive(0.5);

        sleep(1000); // wait for extension servo to go up

        r.setMotorTargets(forward, Robot.Drive.FORWARD);
        r.drive(0.15);

        sleep(200);
        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
            r.setLinearSlidePosition(r.theoreticalGroundExtension + r.ticksInARotation / 4.0);
        }
        if (barcode == BarcodePipeline.Barcode.LEFT) {

        }

        r.Intake.setPower(1);

        sleep(2000);

        r.Intake.setPower(0);

        sleep(100);

        r.setMotorTargets(forward, Robot.Drive.BACKWARD);
        r.drive(0.5);

        sleep(100);

        r.LSExtensionServo.setPosition(bottom);

        sleep(100);

        r.setLinearSlidePosition((int) r.theoreticalMiddleExtension);
        r.setMotorTargets(mult * 34, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.3);

        r.setMotorTargets(1, Robot.Drive.BACKWARD);
        r.drive(0.05);

        r.setMotorTargets(mult * 30, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.5);

        r.setLinearSlidePosition(0);
        r.setMotorTargets(20, Robot.Drive.FORWARD);
        r.drive(0.5);

        r.CarouselMotor.setPower(0);
    }

    public void setBlue() {
        mult = -1;
    }

    public void setRed() {
        mult = 1;
    }
}
