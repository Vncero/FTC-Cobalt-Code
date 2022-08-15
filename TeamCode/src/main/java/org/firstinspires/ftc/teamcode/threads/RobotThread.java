package org.firstinspires.ftc.teamcode.threads;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class RobotThread extends Thread {
    public Robot r;
    public LinearOpMode auto;

    public boolean cameraOpenRequested;

    public RobotThread(Robot r, LinearOpMode auto) {
        this.auto = auto;
        this.r = r;
    }

    public void run() {
        while (auto.opModeIsActive()) {
            r.updateHeading();
//            r.telemetry.addLine("global angles: " + r.globalAngle * 180d / Math.PI);
//            r.telemetry.update();
            if (cameraOpenRequested) {
                openCamera();
                this.cameraOpenRequested = false;
            }
        }
    }

    public void openCamera() {
        int cameraMonitorViewId = auto.hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id",
                        auto.hardwareMap
                                .appContext
                                .getPackageName());
        WebcamName wN = auto.hardwareMap.get(WebcamName.class, "Camera 1");

        r.webcam.showFpsMeterOnViewport(true);
        r.webcam.setMillisecondsPermissionTimeout(1500);
        
        attemptCameraOpen(wN, R.id.cameraMonitorViewId);
        if (!r.cameraIsOpen) attemptCameraOpen(wN, cameraMonitorViewId);
    }

    private void attemptCameraOpen (WebcamName wN, int cameraMonitorViewId) {
        r.webcam = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, cameraMonitorViewId);

        r.webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                r.webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                r.cameraIsOpen = true;
            }

            @Override
            public void onError(int errorCode) {
                r.cameraIsOpen = false;
                r.telemetry.addData("error", errorCode);
                r.telemetry.update();
            }
        });

    }

    public void requestCameraClose() {
        r.webcam.stopStreaming();
        r.webcam.closeCameraDeviceAsync(() -> r.cameraIsOpen = false);
    }

}
