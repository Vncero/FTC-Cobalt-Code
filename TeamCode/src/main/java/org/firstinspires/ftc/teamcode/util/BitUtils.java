package org.firstinspires.ftc.teamcode.opmodes.autonomous.base;

public class BitUtils {
    public static boolean isBitIn(int whole, int check) {
        return (whole & check) == check; 
    }
}
