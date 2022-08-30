package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import org.openftc.easyopencv.OpenCvPipeline;

public class FreightPipeline extends OpenCvPipeline {

    /*TODO: figure out how to isolate freight, both cubes and balls
    * find the closest freight, travel toward it until the intake gets it
    * very complicated to figure out how it will algorithmically get freight
    * after getting it, go to alliance shipping hub, cycle
    * use elapsed time to track when it stops cycling in a fsm while loop
    */

    Mat mat = new Mat();

    @Override
    public Mat processFrame (Mat input) {
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        return input;
    }
}
