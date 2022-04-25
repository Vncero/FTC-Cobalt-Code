package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.R;

import com.acmerobotics.dashboard.FtcDashboard;

import java.util.ArrayList;
import java.util.List;

/*
 * This sample demonstrates how to stream frames from Vuforia to the dashboard. Make sure to fill in
 * your Vuforia key below and select the 'Camera' preset on top right of the dashboard. This sample
 * also works for UVCs with slight adjustments.
 */
@Autonomous (name = "TensorflowWebcam")
public class TensorflowWebcam extends LinearOpMode {
    /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
     * the following 4 detectable objects
     *  0: Ball,
     *  1: Cube,
     *  2: Duck,
     *  3: Marker (duck location tape marker)
     *
     *  Two additional model assets are available which only contain a subset of the objects:
     *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
     *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };
    private static final String VUFORIA_KEY =
            "AeHIiIj/////AAABmfU5ZY97Xk1Zu4akumUUm8JgsnypstBzT4OX7y1ZGLg4yuFGeCRDcHHXzsOkB2meJE3IdkM2mpq08BW54IWLTydXQ9PSFDSaXOQdFIcXePB+tLwRomI5lKbYAOxK16YjxwuKtJB43Ia5F1QwNZ+zjCO/VZWmcZlhyeVH0sKFxu1zHBgfw7xwnoNWww1U95lxhFspXtoAcvOH138ndfOZ5HlzXFnlLqaBG0esOXCJyowFvrJIvQ5pr3DW7NZBDNRRHfOr2TKWTRO9li+JIqI29640MI0Zrl/7RhcezrH3Gl+nIOYyKzbsmPDzcQVmokcAp5z4E6uZCiozbmhTBRdakDgKbXrXCJMWbMc8AUUTPshY";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() throws InterruptedException {
        // gives Vuforia more time to exit before the watchdog notices
        msStuckDetectStop = 2500;
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);

        initTfod();

        if (tfod != null) {
            tfod.activate();
            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0/9.0);
        }

        waitForStart();

        //this is sample code to test Tensorflow
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            if (recognition.getLabel().equals(LABELS[2])) {
                                telemetry.addData("Duck detected at recognition", i);
                            }
                            i++;
                        }
                        telemetry.update();
                    }
                }
            }
        }

        //this is custom code for duck detection, this logic needs reworking
        //depending on what gets consistently detected, also this will not work for custom element lol
//        if (opModeIsActive()) {
//            if (tfod != null) {
//                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
//                if (updatedRecognitions != null) {
//                    ArrayList<Float> Duck = new ArrayList<Float>(2);
//                    ArrayList<Float> Marker1 = null; //janky solution
//                    ArrayList<Float> Marker2 = new ArrayList<>(2);
//
//                    for (Recognition recognition : updatedRecognitions) {
//                        if (recognition.getLabel().equals(LABELS[3])) {
//                            if (Marker1 != null) {
//                                Marker2.add(0, recognition.getConfidence());
//                                Marker2.add(1, (recognition.getLeft() + recognition.getRight()) / 2);
//                            } else {
//                                Marker1 = new ArrayList<>(2);
//                                Marker1.add(0, recognition.getConfidence());
//                                Marker1.add(1, (recognition.getLeft() + recognition.getRight()) / 2);
//                            }
//                        } else if (recognition.getLabel().equals(LABELS[2])) {
//                            //duck detected
//                            Duck.add(0, recognition.getConfidence());
//                            Duck.add(1, (recognition.getLeft() + recognition.getRight()) / 2);
//                        }
//                    }
//
//                    float m1 = Marker1.get(1);
//                    float m2 = Marker2.get(1);
//                    float d = Duck.get(1);
//                    String pos = "middle";
//                    String level = "middle"
//
//                    //figure out relation of duck and markers
//                    if (inRange(d, m1, m2)
//                            || inRange(d, m2, m1)) {
//                        //left
//                        pos = "left";
//                        level = "bottom";
//                    } else if (inRange(m1, m2, d)
//                            || inRange(m2, m1, d)) {
//                        //right
//                        pos = "right";
//                        level = "top"
//                    } else {
//                        //middle
//                    }
//
//                    telemetry.addData("duck pos", pos);
//                    telemetry.addData("target level", level);
//                    telemetry.update();
//                }
//            }
//        }

        if (tfod != null) {
            tfod.deactivate();
            tfod.shutdown();
        }

//        https://gist.github.com/5484Enderbots/772584ae3fc53fda99c0f8f51dc1a9f9 for a closeable vuforia
//        note: vuforia will close when the opmode ends, the above is for closing it earlier
    }

    public boolean inRange(Float min, Float val, Float max) {
        return (min < val && val < max);
    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "vuCam");
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;

        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}