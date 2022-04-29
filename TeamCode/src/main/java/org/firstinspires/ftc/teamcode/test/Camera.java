package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pipelines.BarcodePipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous (name = "(camera) eocv testing")
public class Camera extends LinearOpMode {
    Robot r;
    BarcodePipeline bP;

    @Override
    public void runOpMode () {
        //possibly put this into robot as a separate camera setup method
        r = new Robot(telemetry, hardwareMap);
        bP = new BarcodePipeline(telemetry);
        int cameraMonitorViewId = hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName wN = hardwareMap.get(WebcamName.class, "Camera");
        OpenCvCamera camera = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, cameraMonitorViewId);
        camera.setPipeline(bP);

        FtcDashboard
                .getInstance()
                .startCameraStream(camera, 0);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(320, 180, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        waitForStart();

        while (opModeIsActive() && !gamepad1.x) {
        } //suspicious

        if (bP.getBarcode() != BarcodePipeline.Barcode.LEFT) {
            r.setLinearSlidePosition(bP.getBarcode() == BarcodePipeline.Barcode.RIGHT
                    ? r.theoreticalFullExtension : r.theoreticalMiddleExtension);
        }
        //continue auto
    }
}
