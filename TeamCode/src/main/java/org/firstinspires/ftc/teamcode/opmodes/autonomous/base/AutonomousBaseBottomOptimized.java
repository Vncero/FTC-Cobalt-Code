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

    private OpenCvCamera camera;
    private BarcodePipeline bP;

    public int mult = 1;

    @Override
    public void setup() {
        r = new Robot(telemetry, hardwareMap);
        setupCamera();
    }

    @Override
    public void runAuto() {
//        BarcodePipeline pipeline = new BarcodePipeline(telemetry);
//        r.setCameraPipeline(pipeline);

        switch (bP.getBarcode()) {
            case LEFT:
                r.setLinearSlidePosition(r.theoreticalMiddleExtension);
                r.LSExtensionServo.setPosition(1);
                r.setLinearSlidePosition(r.theoreticalGroundExtension);
                break;
            case MIDDLE:
                r.setLinearSlidePosition(r.theoreticalMiddleExtension);
                break;
            case RIGHT:
                r.setLinearSlidePosition(r.theoreticalFullExtension);
                break;
        }

        while (opModeIsActive()) {}
    }

    public void setupCamera() {
        bP = new BarcodePipeline(telemetry);
        int cameraMonitorViewId = hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id",
                        hardwareMap
                                .appContext
                                .getPackageName());
        WebcamName wN = hardwareMap.get(WebcamName.class, "Camera 1");
        camera = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, cameraMonitorViewId);

        FtcDashboard
                .getInstance()
                .startCameraStream(camera, 30);

        camera.setPipeline(bP);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        camera.showFpsMeterOnViewport(true);
    }
}
