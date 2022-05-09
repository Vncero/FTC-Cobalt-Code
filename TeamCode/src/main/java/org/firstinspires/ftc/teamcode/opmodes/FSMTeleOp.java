package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;

@TeleOp (name = "FSMTeleOp")
public class FSMTeleOp extends OpMode {
    Robot r;
    LinearSlideStates linearSlideState;
    double startingPosition;
    boolean[] liftBtns = {gamepad2.a, gamepad2.b, gamepad2.y};

    @Override
    public void init() {
        r = new Robot(telemetry, hardwareMap);
//        r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlideState = LinearSlideStates.HOMING;

        r.LSExtensionServo.setPosition(1);
        r.vertical.setPosition(0);
        r.horizontal.setPosition(0.4);
    }

    @Override
    public void loop() {

        double c = gamepad1.left_stick_x * (gamepad1.left_bumper ? 0.3 :
                gamepad1.right_bumper ? 1.0 : 0.9);
        double x = gamepad1.right_stick_x * (gamepad1.left_bumper ? 0.3 :
                gamepad1.right_bumper ? 1.0 : 0.9);
        double y = -gamepad1.left_stick_y * (gamepad1.left_bumper ? 0.3 :
                gamepad1.right_bumper ? 1.0 : 0.9);

        r.FrontLeft.setPower(y+x+c);
        r.FrontRight.setPower(-y+x+c);
        r.BackLeft.setPower(y+x-c);
        r.BackRight.setPower(-y+x-c);

        telemetry.addData("Linear Slides Position", r.LinearSlide.getCurrentPosition());

        if (gamepad2.x) linearSlideState = LinearSlideStates.HOMING; //allow for homing at any point

        switch (linearSlideState) {
            case IDLE:
                r.LinearSlide.setTargetPosition((int) (liftBtns[0] ? 0 :
                        liftBtns[1] ? r.theoreticalMiddleExtension :
                                liftBtns[2] ? r.theoreticalFullExtension :
                                        r.LinearSlide.getCurrentPosition()));
                if (r.LinearSlide.getTargetPosition() != r.LinearSlide.getCurrentPosition()) {
                    startingPosition = r.LinearSlide.getCurrentPosition();
                    r.LinearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    r.LinearSlide.setPower(0.5);
                    linearSlideState = LinearSlideStates.MOVING;
                }
                break;
            case MOVING:
                //make 100 a position buffer
                if ((startingPosition > r.LinearSlide.getTargetPosition() //moving down
                        && r.LinearSlide.getCurrentPosition() - 100 <= r.LinearSlide.getTargetPosition()) //position drops below target
                        || (startingPosition < r.LinearSlide.getTargetPosition() //moving up
                        && r.LinearSlide.getCurrentPosition() + 100 >= r.LinearSlide.getTargetPosition())) { //position is above target
                    r.LinearSlide.setPower(0); //cut power to avoid snapping
                }

                //make into individual if statements later if wanted
                r.LinearSlide.setTargetPosition((int) (liftBtns[0] ? 0 :
                        liftBtns[1] ? r.theoreticalMiddleExtension :
                                liftBtns[2] ? r.theoreticalFullExtension :
                                        r.LinearSlide.getTargetPosition()));
                break;
            case HOMING:
                r.LinearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                r.LinearSlide.setPower(!r.linearSlidesHome.isPressed() ? -0.5 : 0);
                if (r.linearSlidesHome.isPressed()) {
//                  r.LinearSlide.setPower(0); //use in case ternary fails to trigger
                    r.LinearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    linearSlideState = LinearSlideStates.IDLE;
                }
                break;
        }

        telemetry.update();
    }

    public enum LinearSlideStates {
        IDLE,
        MOVING,
        HOMING
    }
}
