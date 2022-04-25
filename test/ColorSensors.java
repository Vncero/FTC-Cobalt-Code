package org.firstinspires.ftc.teamcode.test;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Robot;

import java.util.Locale;

@TeleOp(name = "Sensor: REVColorDistance", group = "Sensor")
public class ColorSensors extends LinearOpMode {

    ColorSensor leftColor, rightColor;
    DistanceSensor leftDistance, rightDistance;

    Robot r;

    @Override
    public void runOpMode() {

        r = new Robot(telemetry,hardwareMap);

        // get a reference to the color sensors.
        leftColor = hardwareMap.get(ColorSensor.class, "leftColorDist");
        rightColor = hardwareMap.get(ColorSensor.class, "rightColorDist");
        leftColor.enableLed(true);
        rightColor.enableLed(true);

        // get a reference to the distance sensors that share the same name.
        leftDistance = hardwareMap.get(DistanceSensor.class, "leftColorDist");
        rightDistance = hardwareMap.get(DistanceSensor.class, "rightColorDist");

        // arrays to hold color sensor values
        float[] lHsv = {0F, 0F, 0F};
        float[] rHsv = {0F, 0F, 0F};
        final float[] lValues = lHsv;
        final float[] rValues = rHsv;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attenuate the measured values.
        final double SCALE_FACTOR = 255;

        // wait for the start button to be pressed.
        waitForStart();

        //34.25 is distance from drive team area to near edge of marker
        //robot is ~17in length-wise
        r.setMotorTargets(17.25, Robot.Drive.FORWARD);
        r.drive(0.5);

        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        Color.RGBToHSV((int) (leftColor.red() * SCALE_FACTOR),
                (int) (leftColor.green() * SCALE_FACTOR),
                (int) (leftColor.blue() * SCALE_FACTOR),
                lHsv);
        Color.RGBToHSV((int) (rightColor.red() * SCALE_FACTOR),
                (int) (rightColor.green() * SCALE_FACTOR),
                (int) (rightColor.blue() * SCALE_FACTOR),
                rHsv);

        // send the info back to driver station using telemetry function.
        telemetry.addData("Left Distance (cm)",
                String.format(Locale.US, "%.02f", leftDistance.getDistance(DistanceUnit.CM)));
        telemetry.addData("Left Alpha", leftColor.alpha());
        telemetry.addData("Left Red", leftColor.red());
        telemetry.addData("Left Green", leftColor.green());
        telemetry.addData("Left Blue", leftColor.blue());
        telemetry.addData("Left Hue", lValues[0]);

        telemetry.addData("Right Distance (cm)",
                String.format(Locale.US, "%.02f", rightDistance.getDistance(DistanceUnit.CM)));
        telemetry.addData("Right Alpha", rightColor.alpha());
        telemetry.addData("Right Red", rightColor.red());
        telemetry.addData("Right Green", rightColor.green());
        telemetry.addData("Right Blue", rightColor.blue());
        telemetry.addData("Right Hue", rValues[0]);

        String duckPos = "middle";
        String level = "middle";
        double target = r.theoreticalMiddleExtension;

        if (hueIsYellow(lValues[0])) {
            duckPos = "left";
            level = "bottom";
            target = 0;
        } else if (hueIsYellow(rValues[0])) {
            duckPos = "right";
            level = "top";
            target = r.theoreticalFullExtension;
        }

        telemetry.addData("Position Detected", duckPos);
        telemetry.addData("Level", level);

        telemetry.update();
        r.setMotorTargets(12, Robot.Drive.STRAFE_RIGHT);
        r.drive(0.5);
        r.correctAngle();
        r.setLinearSlidePosition(target);
        r.LSExtensionServo.setPosition(Robot.LSExtensionServoPosition.TOP);
        sleep(500);
        r.Intake.setPower(1);
        sleep(1000);
        r.Intake.setPower(0);

        telemetry.addLine("did it even work?");
        telemetry.update();
    }

    public boolean hueIsYellow (float hue) {
        return inRange(55, hue, 65);
        //min could possibly drop to 45
    }

    public boolean inRange (double min, double value, double max) {
        return min <= value && value <= max;
    }

    //below is code from the original sample that could be used to test color sensors
//
//        // get a reference to the RelativeLayout so we can change the background
//        // color of the Robot Controller app to match the hue detected by the RGB sensor.
//        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
//        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);
//
//        // change the background color to match the color detected by the RGB sensor.
//        // pass a reference to the hue, saturation, and value array as an argument
//        // to the HSVToColor method.
//        relativeLayout.post(new Runnable() {
//            public void run() {
//                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
//            }
//        });
//
//        // Set the panel back to the default color
//        relativeLayout.post(new Runnable() {
//            public void run() {
//                relativeLayout.setBackgroundColor(Color.WHITE);
//            }
//        });
}
