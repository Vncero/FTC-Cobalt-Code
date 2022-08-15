package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous (name = "(camera) eocv testing")
public class Camera extends LinearOpMode {
    Robot r;
    BarcodePipeline bP;

    @Override
    public void runOpMode () {
        r = new Robot(telemetry, hardwareMap);
        //possibly put this into robot as a separate camera setup method
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
        OpenCvWebcam camera = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, R.id.cameraMonitorViewId);
        camera.setMillisecondsPermissionTimeout(1500);
        camera.setPipeline(bP);

        FtcDashboard
                .getInstance()
                .startCameraStream(camera, 30);

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
        waitForStart();

        while (opModeIsActive() && !gamepad1.x) {
        } //suspicious

        if (bP.getBarcode(this) != BarcodePipeline.Barcode.LEFT) {
            r.setLinearSlidePosition(bP.getBarcode(this) == BarcodePipeline.Barcode.RIGHT
                    ? r.theoreticalFullExtension : r.theoreticalMiddleExtension);
        }

        camera.closeCameraDeviceAsync(() -> {

//                camera.stopStreaming();
        });
        //continue auto
    }
}
