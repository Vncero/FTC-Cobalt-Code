package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import com.qualcomm.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.external.tfod.Recognition;

import android.app.Activity;

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
    NormalizedColorSensor normColorSensor;
    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    NormalizedRGBA normRGBA;
    private ElapsedTime runtime = new ElapsedTime();

    Activity activity
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    private static final String vuforiaLicenseKey = " -- YOUR NEW VUFORIA KEY GOES HERE  --- ";

    TFObjectDetector tfObjectDetector;

    /* TODO: write codes for color sen., distance sen., and camera
    use colorSensor to gauge what colors appear, look for yellow
    use distance sensor to somehow figure out
  */
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
        telemetry.addData("normRed", normRGBA.green);
        telemetry.addData("normRed", normRGBA.blue);
        telemetry.addData("normRed", normRGBA.alpha);

        if (opModeIsActive()) {
            /** Activate TensorFlow Object Detection. */
            if (tfObjectDetector != null) {
                tfObjectDetector.activate();
            }

            while (opModeIsActive()) {
                if (tfObjectDetector != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfObjectDetector.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Left");
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Right");
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                }
                            }
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfObjectDetector != null) {
            tfObjectDetector.deactivate();
            tfObjectDetector.shutdown();
        }
    }

    public void initVuforia() {
        VuforiaLocalizer.Parameters vuParams = new VuforiaLocalizer.Parameters();

        vuParams.vuforiaLicenseKey = vuforiaLicenseKey;
        vuParams.cameraName = hardwareMap.get(WebcamName.class, "vuCam");

        vuforia = ClassFactory.getInstance().createVuforia(vuParams);
    }

    public void initTensorFlow () {
        int tfMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("tfMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfParams = new TFObjectDetector.Parameters(tfMonitorViewId);
        tfObjectDetector = ClassFactory.getInstance().createTFObjectDetector(tfParams, vuforia);

    }
}

