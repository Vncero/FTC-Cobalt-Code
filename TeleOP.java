package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class TeleOP extends OpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor LinearSlide;
    CRServo CarouselServo;
    CRServo Intake;
    TouchSensor LinearSlideTopSensor;

    final int LINEAR_SLIDE_AT_BOTTOM_LIMIT = 1;
    final int LINEAR_SLIDE_AT_TOP_LIMIT = 2;

    final int LINEAR_SLIDE_CURRENT_STATE = 0;

    boolean LINEAR_SLIDE_AT_LIMIT = false; // set to true when LINEAR_SLIDE_LIMIT at either LINEAR_SLIDE_AT_BOTTOM_LIMIT or LINEAR_SLIDE_AT_TOP_LIMIT

    //CRServo BucketTurner;top linearSlide servo is normal, not technically Math.pi radians
    //TouchSensor LinearSlideLimiterTop;
    //TouchSensor LinearSlideLimiterBottom;

    @Override
    public void init() {
        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "IntakeServo");
        CarouselServo = hardwareMap.get(CRServo.class, "CarouselServo");
        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
        LinearSlideTopSensor = hardwareMap.get(TouchSensor.class, "LinearSlideTopTouchSensor");
//        LinearSlide = hardwareMap.get(DcMotor.class, "LinearSlide");
//        CarouselTest = hardwareMap.get(CRServo.class, "CarouselServo");
        //IntakeWheel = hardwareMap.get(CRServo.class, "IntakeWheel");
        //BucketTurner = hardwareMap.get(CRServo.class, "BucketTurner");
    }

//    @Override
//    public void loop () {
//        if (LinearSlideTopSensor.isPressed())  {
//            Intake.setPower(1);
//        }
//
//        else {
//            Intake.setPower(0);
//        }
//    }

    @Override
    public void loop () {
        if (gamepad1.b) {//if held, intake constantly goes 1-0
            Intake.setPower(1);
        }

        else {
            Intake.setPower(0);
        }

        if (gamepad1.a) {//still sets 1-0 repeatedly
            CarouselServo.setPower(1);
            // CarouselInMotion = true;

            telemetry.addData("Set Carousel Servo to 1" , null);
            telemetry.update();
        }

        else {
            CarouselServo.setPower(0);

            telemetry.addData("set Carousel Servo to 0", null);
            telemetry.update();
        }

        if (gamepad1.left_bumper) {
            // approx 5 inches per one rotation
            //approximately 3 rotations-"2cm"

            //TODO: convert 2cm to inches, pass into ticks method, get a tick amount for 2cm in inches, multiply tick amount per rotation by 3, subtract 2cm from rotation,
            //2cm ~ 0.787402 inches, 0.00929886553 * 0.787402 = ticks for 2cm (0.00732194531), 3*537.7 ~ 1613.1
            // 1613.1 - 0.00732194531 ~ ticks for full extension if not we're f'd
            // theoretically, we get an approximate number of ticks for the full thing
            double theoreticalFullExtension = 1613.1 - 0.00732194531;
            
            encoderMotorReset();
            
            setMotorTargets((int)theoreticalFullExtension);
            
            FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            
            FrontLeft.setPower(0.25);
            FrontRight.setPower(0.25);
            BackLeft.setPower(0.25);
            BackRight.setPower(0.25);
            //LinearSlide.setPower(0.01);
        }

        else {
            LinearSlide.setPower(0);

        }

        double c = gamepad1.left_stick_x;
        double x = gamepad1.right_stick_x;
        double y = -gamepad1.left_stick_y;

        y *= 0.9;
        c *= 0.9;
        //x *= 0.9;

//        FrontLeft.setPower(y+x+c);
//        FrontRight.setPower(x-y+c);
//        BackLeft.setPower(x+y-c);
//        BackRight.setPower(x-y-c);

        FrontLeft.setPower(y+x+c);
        FrontRight.setPower(-y+x+c);
        BackLeft.setPower(y+x-c);
        BackRight.setPower(-y+x-c);

        // listen for a button, and aim the linear slide

    }

    public void encoderLSReset() {
        LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


    }
    
    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    
    public void setMotorTargets (int motorTarget) {
        FrontLeft.setTargetPosition(motorTarget);
        FrontRight.setTargetPosition(motorTarget);
        BackLeft.setTargetPosition(motorTarget);
        BackRight.setTargetPosition(motorTarget);
    }

    public static double LinearSlideTicks(double inches) {

        double circumference = 5.0; // might be wrong if it is then we're FUCKED !

        double ticksPerInch = circumference / 537.7;//approx 0.00929886553 ticks

        return ticksPerInch * inches;
    }

    public static double MotorTicks (double inches) {
        double diameter = 3.75; // in inches

        double circumference = Math.PI * diameter; // in inches

        double ticksPerInch = circumference / 537.7; // ticks per a single inch

        return ticksPerInch * inches;

    }

}