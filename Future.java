package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class Future  extends OpMode{
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor LinearSlide;
    CRServo Intake;
    Servo BucketTurner;
    TouchSensor LinearSlideLimiterTop;
    TouchSensor LinearSlideLimiterBottom;
    boolean MasterMethodRunning;

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        LinearSlideLimiterTop = hardwareMap.get(TouchSensor.class, "LinearSlideLimiterTop");
        LinearSlideLimiterBottom = hardwareMap.get(TouchSensor.class, "LinearSlideLimiterBottom");
        Intake = hardwareMap.get(CRServo.class, "Intake");
        BucketTurner = hardwareMap.get(Servo.class,"Bucket Turner");
        //BucketTurner.setPosition(0);
        MasterMethodRunning = false;
    }

    @Override
    public void loop (){
        double y = -gamepad1.left_stick_y;


//        if (! gamepad1.x && BucketTurner.getPosition() != 0) {
//            BucketTurner.setPosition(0);
//        }
//
//        // next step touch sensor to stop robot when reaches certain height
//        if (gamepad1.b && ! LinearSlideLimiterTop.isPressed() && ! LinearSlideLimiterBottom.isPressed()) {
//            LinearSlide.setPower(0.001);
//
//            return; // robot cannot move while aiming linear slides
//        }
//
//        if (gamepad1.a) {
//            Intake.setPower(1);
//
//            return;
//        }
//        if (gamepad1.x){
//            BucketTurner.setPosition(0.3);//percentage
//            return;
//        }

//        if (MasterMethodRunning) {
//            return;
//        }

        if (gamepad1.left_bumper && gamepad1.right_bumper) {
            // MasterMethodRunning = true; // set this to true so player can't move robot

            LinearSlide.setPower(0.075);

            wait(2000);

            LinearSlide.setPower(0);

            //servo pos 0 = tilted to catch cargo from tunnel Math.pi/4
            // servo pos 1 = tilted to release cargo 3(Math.pi)/4
            BucketTurner.setDirection(Servo.Direction.FORWARD);

            BucketTurner.setPosition(1);

            wait(1000);

            BucketTurner.setPosition(0);

        }

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;

        y *= 0.9;
        c *= 0.9;
        //x *= 0.9;

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(x-y+c);
        BackLeft.setPower(x+y-c);
        BackRight.setPower(x-y-c);

        // listen for a button, and aim the linear slide
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        }

        catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
