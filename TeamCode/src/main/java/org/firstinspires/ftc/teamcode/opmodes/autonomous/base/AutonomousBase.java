package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.threads.RobotThread;
import org.firstinspires.ftc.teamcode.util.BitUtils;

public abstract class AutonomousBase extends LinearOpMode {
    protected Robot robot;
    protected RobotThread robotThread;

    private final int activations;

    public AutonomousBase() {
        this(0);
    }

    public AutonomousBase(int activations) {
        this.activations = activations;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(telemetry, hardwareMap);

        robotThread = new RobotThread(robot, this);
        if (BitUtils.isBitIn(activations, Activation.UPDATE_ANGLE)) robotThread.start();
        setup();
        waitForStart();
        runAuto();
//        if (BitUtils.isBitIn(activations, Activation.UPDATE_ANGLE)) robotThread.interrupt(); // nvm don't need this
    }

    public abstract void runAuto() throws InterruptedException;

    public abstract void setup() throws InterruptedException;

    // hopefully, static works
    // TODO: check if this works cuz jason really wants it to work
    public static final class Activation {
        /* note:
            int a = 0x0000001;
            int b = 0x0000010;
            int c = 0x0000100;
            ((a | b | c) & a) == a // true
            ((a | b) & c) == c // false
            ((a | b | c) & (b | c)) == (b | c) // true
            ((a | b) & (b | c)) == (b | c) // false
         */
        // THESE ARE MASKS
        public static final int UPDATE_ANGLE = 0b0000001; // leave it like this, so it's easier on the eyes
    }
}
