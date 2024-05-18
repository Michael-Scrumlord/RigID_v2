// Provided by Google ML-Kit - https://cloud.google.com/ml-kit

package com.example.rigid

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.example.rigid.ImageAnalyzer
import java.io.IOException

class ObjectDetectionProcessor(private val view: GraphicOverlay) :
    ImageAnalyzer<List<DetectedObject>>() {

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()
    private val detector = ObjectDetection.getClient(options)

    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Object Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<DetectedObject>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        results.forEach {
            val labelGraphic = ObjectGraphic(graphicOverlay, it, rect)
            graphicOverlay.add(labelGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Object detection failed.$e")
    }

    companion object {
        private const val TAG = "ObjectProcessor"
    }

}