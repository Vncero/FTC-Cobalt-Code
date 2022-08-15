package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.firstinspires.ftc.teamcode.threads.RobotThread;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous (name = "AutoBaseBottomOptimized")
public class AutonomousBaseBottomOptimized extends AutonomousBase {
    Robot r;
    BarcodePipeline bP;

    public int mult = 1;

    @Override
    public void setup() {
        r = new Robot(telemetry, hardwareMap);
        bP = new BarcodePipeline(telemetry, this);
        r.setupWebcam(hardwareMap);
        r.webcam.setPipeline(bP);
    }

    @Override
    public void runAuto() {
        r = new Robot(telemetry, hardwareMap);
        bP = new BarcodePipeline(telemetry, this);
        r.setupWebcam(hardwareMap);
        r.webcam.setPipeline(bP);
        RobotThread thread = new RobotThread(r, this);
        thread.start();

        r.LSExtensionServo.setPosition(1);

        waitForStart();

        r.setMotorTargets(mult * 6.0, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.5);

        BarcodePipeline.Barcode barcode = bP.getBarcode();

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

        if (barcode == BarcodePipeline.Barcode.LEFT) r.setLinearSlidePosition(r.theoreticalGroundExtension);

        if (barcode == BarcodePipeline.Barcode.MIDDLE) {
            sleep(700);
            r.setLinearSlidePosition(r.theoreticalGroundExtension + r.ticksInARotation / 2.0);
        }

        r.setMotorTargets(mult * (34 - 6), Robot.Drive.STRAFE_RIGHT);
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

        r.Intake.setPower(1);
        sleep(1000);

        r.Intake.setPower(0);

        r.setMotorTargets(19, Robot.Drive.BACKWARD);
        r.drive(0.3);
    }
}
