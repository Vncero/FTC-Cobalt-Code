package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.external.tfod.Recognition;

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
    NormalizedColorSensor normColorSensor;
    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    NormalizedRGBA normRGBA;
    private ElapsedTime runtime = new ElapsedTime();

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    private static final String vuforiaLicenseKey = " -- YOUR NEW VUFORIA KEY GOES HERE  --- ";

    TFObjectDetector tfObjectDetector;

    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_DM.tflite";
    private static final String[] LABELS = {
            "Duck",
            "Marker"
    };


    @Override
    public void runOpMode(){
        normColorSensor = hardwareMap.get(NormalizedColorSensor.class, "normColorSensor");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTensorFlow();
        }
        else {
            telemetry.addLine("Failed to create TFObjectDetector.");
            telemetry.update()
        }

        waitForStart();

        normRGBA = normColorSensor.getNormalizedColors();

        telemetry.addData("normRed", normRGBA.red);
        telemetry.addData("normGreen", normRGBA.green);
        telemetry.addData("normBlue", normRGBA.blue);
        telemetry.addData("normAlpha", normRGBA.alpha);

        telemetry.update();

        if (tfObjectDetector != null) {
            tfObjectDetector.activate();
        }

        List<Recognition> updatedRecognitions = tfObjectDetector.getUpdatedRecognitions();

        List<Recognition> duckRecognitions;

        List<Recognition> nonDuckRecognitions;

        if (updatedRecognitions != null) {
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel() == LABELS[0]) {
                    duckRecognitions.add(recognition);
                    telemetry.addData("Confidence of recognition " +
                            Integer.toString(updatedRecognitions.indexOf(recognition)) +  " (duck)", recognition.getConfidence());
                } else {
                    nonDuckRecognitions.add(recognition); //nonDuckRecognitions is likely only Marker recognitions: tfod model only has Duck and Marker
                    telemetry.addData("Confidence of recognition " +
                            Integer.toString(updatedRecognitions.indexOf(recognition)) + " (nonDuck)", recognition.getConfidence());
                }

            }

            telemetry.addData("Ducks Detected", duckRecognitions.size());
            telemetry.addData("Non-Ducks Detected", nonDuckRecognitions.size());
            telemetry.update();

            for (Recognition recognition : duckRecognitions) { //theoretically, there should only be one duck recognized but keep this in case
                double frameWidth = recognition.getImageWidth(); //this should get the width of the frame from camera
                double regionWidth = frameWidth / 3; //divide frame width into three sections of this width
                //therefore, left is ~ 0 < recognition.getBottom() < regionWidth, middle ~ regionWidth < recognition.getBottom() < 2 * regionWidth
                //and right is 2 * regionWidth < recognition.getBottom() < frameWidth (3 * regionWidth)
                if (inRange(0, recognition.getBottom(), regionWidth)) { // use bottom to avoid bias in coordinate
                    //duck is on left, leftmost barcode position means lowest level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData(("Duck " + Integer.toString(duckRecognitions.indexOf(recognition)), "left");
                }

                if (inRange(2 * regionWidth, recognition.getBottom, frameWidth)) {
                    //duck is on right, rightmost barcode position means highest level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData("Duck " + Integer.toString(duckRecognitions.indexOf(recognition)), "right");
                } else {
                    //duck is in middle, middle barcode position means middle level based on Freight Frenzy video, check how barcode is read
                    telemetry.addData("Duck " + Integer.toString(duckRecognitions.indexOf(recognition)), "middle");
                }
                telemetry.update();
            }
        }

        if (tfObjectDetector != null) {
            tfObjectDetector.deactivate();
            tfObjectDetector.shutdown();
        }

        //put rest of autonomous below

    }

    public boolean inRange (int min, int value, int max) {
        boolean inRange = (min < value & value < max) ? true : false;
        return inRange;
    }

    public void initVuforia() { //the two monitors may conflict, remove cameraMonitorViewId if necessary
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuParams = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        vuParams.vuforiaLicenseKey = vuforiaLicenseKey;
        vuParams.cameraName = hardwareMap.get(WebcamName.class, "vuCam");

        vuforia = ClassFactory.getInstance().createVuforia(vuParams);
    }

    public void initTensorFlow () {
        int tfMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("tfMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfParams = new TFObjectDetector.Parameters(tfMonitorViewId);
        tfObjectDetector = ClassFactory.getInstance().createTFObjectDetector(tfParams, vuforia);

        tfObjectDetector.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

