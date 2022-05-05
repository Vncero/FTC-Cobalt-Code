package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SampleThresholdPipeline extends OpenCvPipeline {

    BarcodePipeline.Barcode b = null;
    double threshold = 0.4;
    /*
     * These are our variables that will be
     * modifiable from the variable tuner.
     *
     * Scalars in OpenCV are generally used to
     * represent color. So our values in the
     * lower and upper Scalars here represent
     * the Y, Cr and Cb values respectively.
     *
     * YCbCr, like most color spaces, range
     * from 0-255, so we default to those
     * min and max values here for now, meaning
     * that all pixels will be shown.
     */
    public Scalar upper = new Scalar(255, 255, 255);
    public Scalar lower = new Scalar(0, 0, 0);

    /*
     * A good practice when typing EOCV pipelines is
     * declaring the Mats you will use here at the top
     * of your pipeline, to reuse the same buffers every
     * time. This removes the need to call mat.release()
     * with every Mat you create on the processFrame method,
     * and therefore, reducing the possibility of getting a
     * memory leak and causing the app to crash due to an
     * "Out of Memory" error.
     */
    private Mat ycrcbMat       = new Mat();
    private Mat binaryMat      = new Mat();
    private Mat maskedInputMat = new Mat();

    public Rect leftROI = new Rect(
            new Point(0, 0),
            new Point(10, 10)
    );
    public Rect rightROI = new Rect(
            new Point(20, 0),
            new Point(30, 10)
    );

    @Override
    public Mat processFrame(Mat input) {
        /*
         * Converts our input mat from RGB to YCrCb.
         * EOCV ALWAYS returns RGB mats, so you'd
         * always convert from RGB to the color
         * space you want to use.
         *
         * Takes our "input" mat as an input, and outputs
         * to a separate Mat buffer "ycrcbMat"
         */
        Imgproc.cvtColor(input, ycrcbMat, Imgproc.COLOR_RGB2HSV);

        /*
         * This is where our thresholding actually happens.
         * Takes our "ycrcbMat" as input and outputs a "binary"
         * Mat to "binaryMat" of the same size as our input.
         * "Discards" all the pixels outside the bounds specified
         * by the scalars above (and modifiable with EOCV-Sim's
         * live variable tuner.)
         *
         * Binary meaning that we have either a 0 or 255 value
         * for every pixel.
         *
         * 0 represents our pixels that were outside the bounds
         * 255 represents our pixels that are inside the bounds
         */
        Core.inRange(ycrcbMat, lower, upper, binaryMat);
//        split the image into left and right sides*/
        Mat lROI = binaryMat.submat(leftROI);
        Mat rROI = binaryMat.submat(rightROI);

        //finds the percentage of white on left and right
        double l = Core.sumElems(lROI).val[0] / leftROI.area() / 255;
        double r = Core.sumElems(rROI).val[0] / rightROI.area() / 255;

        /*
         * Release the reusable Mat so that old data doesn't
         * affect the next step in the current processing
         */
        maskedInputMat.release();
        lROI.release();
        rROI.release();

        /*
         * Now, with our binary Mat, we perform a "bitwise and"
         * to our input image, meaning that we will perform a mask
         * which will include the pixels from our input Mat which
         * are "255" in our binary Mat (meaning that they're inside
         * the range) and will discard any other pixel outside the
         * range (RGB 0, 0, 0. All discarded pixels will be black)
         */

        //determine if the element is there (threshold can probably be lowered)
        boolean lElement = l > threshold;
        boolean rElement = r > threshold;

        //ternary operators to determine Barcode
        b = lElement ? BarcodePipeline.Barcode.LEFT : rElement ? BarcodePipeline.Barcode.RIGHT : BarcodePipeline.Barcode.MIDDLE;

        Core.bitwise_and(input, input, maskedInputMat, binaryMat);

        /*
         * The Mat returned from this method is the
         * one displayed on the viewport.
         *
         * To visualize our threshold, we'll return
         * the "masked input mat" which shows the
         * pixel from the input Mat that were inside
         * the threshold range.
         */
        Scalar barcodePosition = new Scalar(0, 255, 0);
        Scalar empty = new Scalar(255, 0, 0);

        //draw the rectangles and color based on the barcode position
        Imgproc.rectangle(maskedInputMat, leftROI, b ==  BarcodePipeline.Barcode.LEFT ? barcodePosition : empty);
        Imgproc.rectangle(maskedInputMat, rightROI, b == BarcodePipeline.Barcode.RIGHT ? barcodePosition : empty);

        return maskedInputMat;
    }

}