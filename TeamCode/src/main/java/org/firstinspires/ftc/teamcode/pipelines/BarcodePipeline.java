package org.firstinspires.ftc.teamcode.pipelines;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.openftc.easyopencv.OpenCvPipeline;

public class BarcodePipeline extends OpenCvPipeline {

    //TODO: test all of this, make sure it works somewhat

    Telemetry t;
    private Barcode b = null;

    Mat mat = new Mat();
    private Mat maskedInputMat = new Mat();
//
//    public Rect leftROI = new Rect(
//            new Point(0, 120),
//            new Point(640.0 / 3.0, 360)
//    );
//    public Rect rightROI = new Rect(
//            new Point(2 * 640.0 / 3.0, 120),
//            new Point(640, 360)
//    );


    public Rect leftROI = new Rect(
            new Point(0, 0),
            new Point(106.67d, 150)
    );
    public Rect rightROI = new Rect(
            new Point(213.334d, 0),
            new Point(106.67d, 150)
    ); //last point should be x: ~320 but the sim is janky

//    public Scalar upper = new Scalar(125, 255, 190);
//    public Scalar lower = new Scalar(110, 25, 0);
//    public Scalar upper = new Scalar(245 / 2.0, 125, 255);
//    public Scalar lower = new Scalar(206 / 2.0, 0, 255);
    public Scalar upper = new Scalar(255, 255, 255);
    public Scalar lower = new Scalar(85, 125, 0);
    //H(200, 250/255) - from a color picker (divide by 2)
    //H(217/206, 234/245) - second values were under odd lighting (divide by 2)
    //S,V(100, 255)

    public BarcodePipeline (Telemetry t) {
        this.t = t;
    }

    @Override
    public Mat processFrame(Mat input) {
        //convert image to hsv, easier colorspace for processing
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

        //these roughly represent yellow, for exclusive duck detection
//        upper = new Scalar(65, 100, 100);
//        lower = new Scalar(60, 50, 70);

        Core.inRange(mat, lower, upper, mat);
        /*everything that falls into the range of blue specified by lower and upper
          turns into white, everything else turns into black

        split the image into left and right sides*/
        Mat lROI = mat.submat(leftROI);
        Mat rROI = mat.submat(rightROI);

        //finds the percentage of white on left and right
        double l = Core.sumElems(lROI).val[0] / leftROI.area() / 255;
        double r = Core.sumElems(rROI).val[0] / rightROI.area() / 255;

        t.addData("left percentage", Math.round(l * 100) + "%");
        t.addData("right percentage", Math.round(r * 100) + "%");

        //make sure to release the submatrices after using them
        lROI.release();
        rROI.release();

        //determine if the element is there (threshold can probably be lowered)
        boolean lElement = l > r;
        boolean rElement = r > l;

        //ternary operators to determine Barcode
        b = lElement ? Barcode.LEFT : rElement ? Barcode.RIGHT : Barcode.MIDDLE;

        t.addData("Barcode: ", b.toString());

        //convert back to rgb to visually show Barcode determination
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

        //green for detected position, red for not detected
        Scalar barcodePosition = new Scalar(0, 255, 0);
        Scalar empty = new Scalar(255, 0, 0);

        //draw the rectangles and color based on the barcode position
        Imgproc.rectangle(mat, leftROI, b ==  Barcode.LEFT ? barcodePosition : empty);
        Imgproc.rectangle(mat, rightROI, b == Barcode.RIGHT ? barcodePosition : empty);

        t.update();

//        Core.bitwise_and(input, input, maskedInputMat, mat);

        return mat;
    }

    public Barcode getBarcode () {
        while (b == null) {}
        return b;
    }

    public enum Barcode {
        LEFT,
        MIDDLE,
        RIGHT
    }
}
