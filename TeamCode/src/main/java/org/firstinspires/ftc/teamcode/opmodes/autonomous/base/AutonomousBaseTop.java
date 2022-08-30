package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;

public class AutonomousBaseTop extends AutonomousBase {
    private int mult = 1;

    protected AutonomousBaseTop() {
        super(Activation.UPDATE_ANGLE);
    }

    @Override
    public void setup() throws InterruptedException {}

    @Override
    public void runAuto() throws InterruptedException {
        robotThread.cameraOpenRequested = true; //theoretically triggers attempts to open camera
        if (robot.isCameraOpen()) robot.webcam.setPipeline(barcodePipeline);

        robot.lsExtensionServo.setPosition(1); // ensure extension at lowest
        robot.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        waitForStart();
    //      robot.CarouselMotor.setPower(1);
        // get to the correct position to read barcode
    //      robot.setMotorTargets(mult * 3.0, Robot.Drive.STRAFE_LEFT);
    //      robot.drive(0.5);

        BarcodePipeline.Barcode barcode = robot.isCameraOpen() ? barcodePipeline.getBarcode(this) : barcodePipeline.randomRead();
        if (robot.isCameraOpen()) robotThread.requestCameraClose();

        robot.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        switch (barcode) {
            case LEFT:
            case MIDDLE:
                robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalMiddleExtension);
                break;
            case RIGHT:
                robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalFullExtension);
                break;
        }

        robot.lsExtensionServo.setPosition(0);

        double forward = 25;

        robot.setMotorTargets(mult * 24, false, Robot.Drive.STRAFE_LEFT);
        robot.drive(0.5);

        sleep(1000); // wait for extension servo to go up

        robot.setMotorTargets(forward, false, Robot.Drive.FORWARD);
        robot.drive(0.15);

        sleep(200);
        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
            robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalGroundExtension + Robot.ticksInARotation / 4.0);
        }
        if (barcode == BarcodePipeline.Barcode.LEFT) {

        }

        robot.intake.setPower(1);

        sleep(2000);

        robot.intake.setPower(0);

        sleep(100);

        robot.setMotorTargets(forward, false, Robot.Drive.BACKWARD);
        robot.drive(0.5);

        sleep(100);

        robot.lsExtensionServo.setPosition(1);

        sleep(100);

        robot.setLinearSlidePosition((int) Robot.LinearSlidePosition.theoreticalMiddleExtension);
        robot.setMotorTargets(mult * 34, false, Robot.Drive.STRAFE_RIGHT);
        robot.drive(0.3);

        robot.setMotorTargets(1, false, Robot.Drive.BACKWARD);
        robot.drive(0.05);

        robot.setMotorTargets(mult * 30, false, Robot.Drive.STRAFE_RIGHT);
        robot.drive(0.5);

        robot.setLinearSlidePosition(0);
        robot.setMotorTargets(20, false, Robot.Drive.FORWARD);
        robot.drive(0.5);

        robot.carouselMotor.setPower(0);
    }

    public void setBlue() {
        mult = -1;
    }

    public void setRed() {
        mult = 1;
    }
}
