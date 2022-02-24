package org.firstinspires.ftc.teamcode;

public class Utilities {
    public static final double ticksInARotation = 537.7;
    public static final double theoreticalRadius = 10.9;

    public static final double theoreticalMiddleExtension =  LinearSlideTicks(5.5);
    public static final double theoreticalGroundExtension = LinearSlideTicks(3);
    public static final double theoreticalFullExtension = (3 * ticksInARotation) - (LinearSlideTicks(5));

    public static int LinearSlideTicks(double inches) {
        double diameter = 1.5;

        double circumference = diameter * Math.PI; // might be wrong if it is then we're FUCKED !
        // original measurement was 5in
        //alt circumference ~ 4.75in.
        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 inches per tick

        return (int) Math.floor(inches / inchesPerTick);
    }

    public static int MotorTicks (double inches) {
        double diameter = 3.75;

        double circumference = Math.PI * diameter; // in inches

        double inchesPerTick = circumference / 537.7; // approx 0.0204492733635192 inches per tick

        return  (int) Math.floor(inches / inchesPerTick);
    }

    public double motorArcLength (int theta) {
        double rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalRadius; //returns S, the arc length
    }
        /* old notes
        all the turning math is done on the assumption that driving a distance as a line
        is the same as driving that distance around a circumference
        as in, the turning motion does not counteract movement along the circumference
        and if all 4 wheels drive for 10 inches, then if half the wheels drive opposite to start turning,
        they would still drive 10 inches, just along the circumference of their rotation
        this is likely not true, but I cannot find math online and can't really model it either
        to correct much, just do testing

        a lot of notes
        the objective - get radius of turning circle
        when the robot turns, the edges of the wheel hit points that make up its turning circle
        relating the turning circle to the robot, we can deduce that the diameter of this circle can be found by
        finding the diagonal measure between wheels, like FrontLeft and BackRight.
        without being able to directly measure, we can estimate this diagonal by figuring out the robot's dimensions
        we know that it fits between the barrier gap, meaning the width is at max 13.68in, use 13.65in
        for length, the robot fits in the 2ft by 2ft squares, but the size limit is 18, so use 17in
        now we have two measures for pythagorean theorem
        13.65^2 + 17^2 = c^2
        186.3225 + 289 = c^2
        475.3225 = c^2
        sqrt(475.3225) ~ 21.8in diameter
        21.8 / 2 = 10.9in radius

        previous turn circle radius estimate
        the robot must be within 18*18*18
        therefore, the circle it rotates has diameter 18 at max
        18 / 2 = 9
        previously, I made the judgment that the circle it rotates needs the same area as the 18*18 square
        however, this would create a circle larger than the square
        anyway, here's the math
        18*18 = 324 sq. in., max area of the circle
        324 >= Math.PI * Math.pow(r, 2)
        324 / Math.PI ~ 103.13240312354819
        103.13240312354819 >~ Math.pow(r, 2)
        Math.sqrt(103.13240312354819) ~ 10.155
        10.155 >~ r
        20.310 >~ d
        round diameter down a little to 20, then circumference is about 62.83185
        */
}
