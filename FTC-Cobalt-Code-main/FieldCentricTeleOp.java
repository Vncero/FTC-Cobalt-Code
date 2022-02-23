package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name="TEST FIELD CENTRIC SHIT")
public class FieldCentricTeleOp extends OpMode {
    public Robot r;

    @Override
    public void init() {
        r = new Robot();
        r.hardwareMap(hardwareMap);

    }

    @Override
    public void loop() {
        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        r.updateGlobalAngle();

        c = Math.floor(c * 1000);
        c /= 1000;

        y = Math.floor(y * 1000);
        y /= 1000;

        telemetry.addLine("the global angle is " + r.globalAngle);

        telemetry.addLine("DA OG X: " + c);
        telemetry.addLine("DA OG Y: " + y);

        double angle = r.globalAngle * Math.PI / 180d;

        double _c = c * Math.cos(angle) - y * Math.sin(angle);
        double _y = c * Math.sin(angle) + y * Math.cos(angle);

        c = _c;
        y = _y;

        c = Math.floor(c * 1000);
        c /= 1000;

        y = Math.floor(y * 1000);
        y /= 1000;

        telemetry.addLine("DA NEW X: " + c);
        telemetry.addLine("DA NEW Y: " + y);

        telemetry.update();
    }
}
