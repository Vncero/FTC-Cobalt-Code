package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TestPipeline extends OpenCvPipeline {
    public double scaleFactor = 1.2;
    Mat resizedMat = new Mat();

    @Override
    public Mat processFrame (Mat input) {
        Imgproc.resize(input, resizedMat, new Size(), scaleFactor, scaleFactor, Imgproc.INTER_LINEAR);
        Rect rect = new Rect(
                new Point(0 * scaleFactor, 0 * scaleFactor),
                new Point(100 * scaleFactor,100 * scaleFactor));
        Imgproc.rectangle(resizedMat, rect, new Scalar(0, 255, 0));

        return resizedMat;
    }
}
