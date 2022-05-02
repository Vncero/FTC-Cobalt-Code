package org.firstinspires.ftc.teamcode.util;

public class BitUtils {
    public static boolean isBitIn(int whole, int check) {
        return (whole & check) == check;
    }
}
