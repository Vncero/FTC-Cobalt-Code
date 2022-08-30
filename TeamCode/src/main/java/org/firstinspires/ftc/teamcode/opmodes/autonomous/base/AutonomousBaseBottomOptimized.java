package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;

@Autonomous (name = "AutoBaseBottomOptimized")
public class AutonomousBaseBottomOptimized extends AutonomousBase {

    private int mult = 1;

    @Override
    public void setup() {
        robotThread.cameraOpenRequested = true;
        if (robot.isCameraOpen()) robot.webcam.setPipeline(barcodePipeline);
    }

    @Override
    public void runAuto() {
        robot.lsExtensionServo.setPosition(1);

        waitForStart();

        robot.setMotorTargets(mult * 6.0, false, Robot.Drive.STRAFE_RIGHT);
        robot.drive(0.5);

        BarcodePipeline.Barcode barcode = barcodePipeline.getBarcode(this);

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

        if (barcode == BarcodePipeline.Barcode.LEFT) robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalGroundExtension);

        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
            sleep(700);
            robot.setLinearSlidePosition(Robot.LinearSlidePosition.theoreticalGroundExtension + Robot.ticksInARotation / 2.0);
        }

        robot.setMotorTargets(mult * (34 - 6), false, Robot.Drive.STRAFE_RIGHT);
        robot.drive(0.3);

        sleep(100);

        robot.setMotorTargets(1, false, Robot.Drive.BACKWARD);
        robot.drive(0.3);

        robot.setMotorTargets(16, false, Robot.Drive.FORWARD);
        robot.drive(0.3);
        sleep(200);

        // aroudn here, for some reason, the robot turns
        // use the global angle to turn the robot back

        robot.correctAngle(0.1, this);

        sleep(100);

        robot.intake.setPower(1);
        sleep(1000);

        robot.intake.setPower(0);

        robot.setMotorTargets(19, false, Robot.Drive.BACKWARD);
        robot.drive(0.3);
    }
}
