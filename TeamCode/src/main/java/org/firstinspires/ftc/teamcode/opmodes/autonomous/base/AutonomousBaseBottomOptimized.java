package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
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
        bP = new BarcodePipeline(telemetry);
        r.setupWebcam(hardwareMap);
        r.webcam.setPipeline(bP);
    }

    @Override
    public void runAuto() {
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


    }
}
