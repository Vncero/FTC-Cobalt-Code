package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "test")
public class AutonomousTest extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor LinearSlide;
    DcMotor CarouselMotor;
    //alternatively,
    // NormalizedColorSensor normColorSensor;
    // ColorSensor colorSensor;
    // DistanceSensor distanceSensor;

    // NormalizedRGBA normRGBA;
    private ElapsedTime runtime = new ElapsedTime();

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    private static final String vuforiaLicenseKey = "AeHIiIj/////AAABmfU5ZY97Xk1Zu4akumUUm8JgsnypstBzT4OX7y1ZGLg4yuFGeCRDcHHXzsOkB2meJE3IdkM2mpq08BW54IWLTydXQ9PSFDSaXOQdFIcXePB+tLwRomI5lKbYAOxK16YjxwuKtJB43Ia5F1QwNZ+zjCO/VZWmcZlhyeVH0sKFxu1zHBgfw7xwnoNWww1U95lxhFspXtoAcvOH138ndfOZ5HlzXFnlLqaBG0esOXCJyowFvrJIvQ5pr3DW7NZBDNRRHfOr2TKWTRO9li+JIqI29640MI0Zrl/7RhcezrH3Gl+nIOYyKzbsmPDzcQVmokcAp5z4E6uZCiozbmhTBRdakDgKbXrXCJMWbMc8AUUTPshY";

    TFObjectDetector tfObjectDetector;

    private static final String TFOD_MODEL_ASSET = "FreightFenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    @Override
    public void runOpMode(){
        // normColorSensor = hardwareMap.get(NormalizedColorSensor.class, "normColorSensor");
        // colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        // distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

        initVuforia();

        initTensorFlow();

        waitForStart();

        // normRGBA = normColorSensor.getNormalizedColors();

        // telemetry.addData("normRed", normRGBA.red);
        // telemetry.addData("normGreen", normRGBA.green);
        // telemetry.addData("normBlue", normRGBA.blue);
        // telemetry.addData("normAlpha", normRGBA.alpha);

        telemetry.update();

        if (tfObjectDetector != null) {
            tfObjectDetector.activate();
        }

        List<Recognition> updatedRecognitions = tfObjectDetector.getUpdatedRecognitions();
        telemetry.update();

        ArrayList<Recognition> duckRecognitions = new ArrayList<Recognition>();

        ArrayList<Recognition> nonDuckRecognitions = new ArrayList<Recognition>();
        int i = 0;
        if (updatedRecognitions != null) {
            for (Recognition recognition : updatedRecognitions) {
                i++;
                if (recognition.getLabel().equals(LABELS[1])) {
                    duckRecognitions.add(recognition);
                    telemetry.addData("Confidence of recognition " +
                            updatedRecognitions.indexOf(recognition) +  " (cube)", recognition.getConfidence());
                } else {
                    nonDuckRecognitions.add(recognition); //nonDuckRecognitions is likely only Marker recognitions: tfod model only has Duck and Marker
                    telemetry.addData("Confidence of recognition " +
                            updatedRecognitions.indexOf(recognition) + " (noncube)", recognition.getConfidence());
                }
            }

            // telemetry.addData("Cubes Detected", duckRecognitions.size());
            // telemetry.addData("Non-Cubes Detected", nonDuckRecognitions.size());
            telemetry.addLine("" + i);
            telemetry.update();

            for (Recognition recognition : duckRecognitions) { //theoretically, there should only be one duck recognized but keep this in case
                double frameWidth = recognition.getImageWidth(); //this should get the width of the frame from camera
                double regionWidth = frameWidth / 3; //divide frame width into three sections of this width
                //therefore, left is ~ 0 < recognition.getBottom() < regionWidth, middle ~ regionWidth < recognition.getBottom() < 2 * regionWidth
                //and right is 2 * regionWidth < recognition.getBottom() < frameWidth (3 * regionWidth)
                if (inRange(0, recognition.getBottom(), regionWidth)) { // use bottom to avoid bias in coordinate
                    //duck is on left, leftmost barcode position means lowest level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData("Duck " + duckRecognitions.indexOf(recognition), "left");
                }

                if (inRange(2 * regionWidth, recognition.getBottom(), frameWidth)) {
                    //duck is on right, rightmost barcode position means highest level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData("Duck " + duckRecognitions.indexOf(recognition), "right");
                } else {
                    //duck is in middle, middle barcode position means middle level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData("Duck " + duckRecognitions.indexOf(recognition), "middle");
                }
                telemetry.update();
            }
        }

        telemetry.update();

        if (tfObjectDetector != null) {
            tfObjectDetector.deactivate();
            tfObjectDetector.shutdown();
        }

        // //put rest of autonomous below
        // sleep(5000);
    }

    public boolean inRange (double min, double value, double max) {
        return min <= value & value <= max;
    }

    public void initVuforia() { //the two monitors may conflict, remove cameraMonitorViewId if necessary
        // int cameraMonitorViewId = hardwareMap.appContext.getResources()
        //         .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        // add camera monitor view id if not works
        VuforiaLocalizer.Parameters vuParams = new VuforiaLocalizer.Parameters();

        vuParams.vuforiaLicenseKey = vuforiaLicenseKey;
        vuParams.cameraName = hardwareMap.get(WebcamName.class, "vuCam");

        vuforia = ClassFactory.getInstance().createVuforia(vuParams);
    }

    public void initTensorFlow () {
        int tfMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfParams = new TFObjectDetector.Parameters(tfMonitorViewId);
        tfObjectDetector = ClassFactory.getInstance().createTFObjectDetector(tfParams, vuforia);

        tfObjectDetector.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

