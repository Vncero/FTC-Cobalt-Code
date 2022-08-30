package org.firstinspires.ftc.teamcode.threads;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class RobotThread extends Thread {
    private Robot robot;
    private LinearOpMode auto;

    public boolean cameraOpenRequested;

    public RobotThread(Robot robot, LinearOpMode auto) {
        this.auto = auto;
        this.robot = robot;
    }

    public void run() {
        while (auto.opModeIsActive()) {
            robot.updateHeading();
//            r.telemetry.addLine("global angles: " + r.globalAngle * 180d / Math.PI);
//            r.telemetry.update();
            if (cameraOpenRequested) {
                openCamera();
                this.cameraOpenRequested = false;
            }
        }
    }

    private void openCamera() {
        int cameraMonitorViewId = auto.hardwareMap
                .appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id",
                        auto.hardwareMap
                                .appContext
                                .getPackageName());
        WebcamName wN = auto.hardwareMap.get(WebcamName.class, "Camera 1");

        robot.webcam.showFpsMeterOnViewport(true);
        robot.webcam.setMillisecondsPermissionTimeout(1500);
        
        attemptCameraOpen(wN, R.id.cameraMonitorViewId);
        if (!robot.isCameraOpen()) attemptCameraOpen(wN, cameraMonitorViewId);
    }

    private void attemptCameraOpen (WebcamName wN, int cameraMonitorViewId) {
        robot.webcam = OpenCvCameraFactory
                .getInstance()
                .createWebcam(wN, cameraMonitorViewId);

        robot.webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                robot.webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                robot.toggleCameraOpen();
            }

            @Override
            public void onError(int errorCode) {
                robot.telemetryData("error", errorCode);
                robot.telemetryUpdate();
            }
        });

    }

    public void requestCameraClose() {
        robot.webcam.stopStreaming();
        robot.webcam.closeCameraDeviceAsync(() -> robot.toggleCameraOpen());
    }
}