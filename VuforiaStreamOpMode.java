package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

/*
 * This sample demonstrates how to stream frames from Vuforia to the dashboard. Make sure to fill in
 * your Vuforia key below and select the 'Camera' preset on top right of the dashboard. This sample
 * also works for UVCs with slight adjustments.
 */
@Autonomous
public class VuforiaStreamOpMode extends LinearOpMode {

    public static final String VUFORIA_LICENSE_KEY = "AeHIiIj/////AAABmfU5ZY97Xk1Zu4akumUUm8JgsnypstBzT4OX7y1ZGLg4yuFGeCRDcHHXzsOkB2meJE3IdkM2mpq08BW54IWLTydXQ9PSFDSaXOQdFIcXePB+tLwRomI5lKbYAOxK16YjxwuKtJB43Ia5F1QwNZ+zjCO/VZWmcZlhyeVH0sKFxu1zHBgfw7xwnoNWww1U95lxhFspXtoAcvOH138ndfOZ5HlzXFnlLqaBG0esOXCJyowFvrJIvQ5pr3DW7NZBDNRRHfOr2TKWTRO9li+JIqI29640MI0Zrl/7RhcezrH3Gl+nIOYyKzbsmPDzcQVmokcAp5z4E6uZCiozbmhTBRdakDgKbXrXCJMWbMc8AUUTPshY";

    @Override
    public void runOpMode() throws InterruptedException {
        // gives Vuforia more time to exit before the watchdog notices
        msStuckDetectStop = 2500;

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters();
        vuforiaParams.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        vuforiaParams.secondsUsbPermissionTimeout = msStuckDetectStop;
        vuforiaParams.cameraName = hardwareMap.get(WebcamName.class, "Camera 1");
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(vuforiaParams);

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);

        waitForStart();

        while (opModeIsActive());
    }
}