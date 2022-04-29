package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import org.openftc.easyopencv.OpenCvPipeline;

public class FreightPipeline extends OpenCvPipeline {

    /*TODO: figure out how to isolate freight, both cubes and balls
    * find the closest freight, travel toward it until the intake gets it
    * very complicated to figure out how it will algorithmically get freight
    * after getting it, go to alliance shipping hub, cycle
    * use elapsed time to track when it stops cycling in a fsm while loop
    */

    Mat mat = new Mat();
    public Rect leftROI = new Rect(
            new Point(0, 0),
            new Point(106.67d, 150)
    );
    public Rect rightROI = new Rect(
            new Point(213.334d, 0),
            new Point(106.67d, 150)
    );

    @Override
    public Mat processFrame (Mat input) {
//        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        Imgproc.rectangle(mat, leftROI, new Scalar(0, 0, 0));
        Imgproc.rectangle(mat, rightROI, new Scalar(0, 0, 0));
        return mat;
    }
}
