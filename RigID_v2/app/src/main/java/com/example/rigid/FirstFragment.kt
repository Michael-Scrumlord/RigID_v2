package com.example.rigid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.rigid.databinding.FragmentFirstBinding
import com.google.cloud.vision.v1.*
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

private const val REQUEST_CAMERA_PERMISSION = 1

class FirstFragment : Fragment(), View.OnClickListener {

    companion object {
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }

    private lateinit var captureImageFab: Button
    private lateinit var inputImageView: ImageView
    private lateinit var imgSampleOne: ImageView
    private lateinit var imgSampleTwo: ImageView
    private lateinit var tvPlaceholder: TextView
    private lateinit var guitarModel: TextView
    private lateinit var currentPhotoPath: String

    private var _binding: FragmentFirstBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        EasyPermissions.requestPermissions(
            this,
            "The camera permission is needed to take pictures.",
            REQUEST_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )

        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        captureImageFab = view.findViewById(R.id.captureImageFab)
        inputImageView = view.findViewById(R.id.imageView)

        imgSampleOne = view.findViewById(R.id.imgSampleOne)
        imgSampleTwo = view.findViewById(R.id.imgSampleTwo)
        tvPlaceholder = view.findViewById(R.id.tvPlaceholder)
        guitarModel = view.findViewById(R.id.guitarDescription)

        captureImageFab.setOnClickListener(this)
        imgSampleOne.setOnClickListener(this)
        imgSampleTwo.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
            resultCode == Activity.RESULT_OK
        ) {
            setViewAndDetect(getCapturedImage())
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if the camera permission has been granted
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The camera permission has been granted, you can now access the camera
        } else {
            // The camera permission has been denied, you cannot access the camera
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureImageFab -> {
                try {
                    dispatchTakePictureIntent()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }

            R.id.imgSampleOne -> {
                setViewAndDetect(getSampleImage(R.drawable.demo_img1))
            }

            R.id.imgSampleTwo -> {
                setViewAndDetect(getSampleImage(R.drawable.demo_img2))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun runObjectDetection(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        //val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        val localModel = LocalModel.Builder()
            .setAssetFilePath("object_labeler.tflite")
            .build()
        val options =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableClassification()
                .enableMultipleObjects()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(5)
                .build()

        val objectDetector: ObjectDetector = ObjectDetection.getClient(options)

        //val objectDetector2 = ObjectDetectionProcessor.getClient(options)

        objectDetector.process(image).addOnSuccessListener { results ->
            debugPrint(results)

            // Parse ML Kit's DetectedObject and create corresponding visualization data
            val detectedObjects = results
                .filter { it.labels.isNotEmpty() && it.labels.first().text == "Guitar" }
                .map {
                    var text = "Unidentified"

                    // Shows the top confident detection result if it exists
                    if (it.labels.isNotEmpty()) {
                        val firstLabel = it.labels.first()
                        text = "${firstLabel.text}, ${firstLabel.confidence.times(100).toInt()}%"
                    }

                    BoxWithText(it.boundingBox, text)
                }

            // Draw the detection result on the input bitmap
            val visualizedResult = drawDetectionResult(bitmap, detectedObjects)
            // Show the detection result on the app screen
            inputImageView.setImageBitmap(visualizedResult)

            inputImageView.setOnTouchListener { _, event ->

                val x = event.x.toInt()
                val y = event.y.toInt()

                // Find the object that the user clicked on
                val clickedObject = detectedObjects.firstOrNull { it.box.contains(x, y) }

                if (clickedObject != null) {
                    // Crop the image so that just the selected object is visible
                    val croppedVisualizedResult = Bitmap.createBitmap(
                        visualizedResult,
                        clickedObject.box.left,
                        clickedObject.box.top,
                        clickedObject.box.width(),
                        clickedObject.box.height()
                    )

                    // Show the cropped image on the app screen
                    val croppedVisualizedResultRotated = rotateImage(croppedVisualizedResult, 90f)
                    inputImageView.setImageBitmap(croppedVisualizedResult)

                    val apiKey = ""

                    // Convert the cropped image to a filepath
                    //var croppedVisualizedResultFile = File(context?.cacheDir, "cropped_image.jpg")
                    val tempFile = File.createTempFile("cropped_image", ".jpg")
                    croppedVisualizedResultRotated.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(tempFile))
                    roboInference(tempFile, apiKey)
                }

                // Return true if the listener has consumed the event, false otherwise
                true
            }

        }.addOnFailureListener {
            Log.e(TAG, it.message.toString())
        }
    }

    private fun debugPrint(detectedObjects: List<DetectedObject>) {
        for (detectedObject in detectedObjects) {
            detectedObject.boundingBox
            detectedObject.trackingId
        }
        detectedObjects.forEachIndexed { index, detectedObject ->
            val box = detectedObject.boundingBox

            Log.d("Detection", "Detected object: $index")
            Log.d("ID", " trackingId: ${detectedObject.trackingId}")
            Log.d("Box", " boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")
            detectedObject.labels.forEach {
                Log.d("Category", " categories: ${it.text}")
                Log.d("Confidence", " confidence: ${it.confidence}")
            }
        }
    }

    private fun setViewAndDetect(bitmap: Bitmap) {
        // Display the captured image
        inputImageView.setImageBitmap(bitmap)
        tvPlaceholder.visibility = View.INVISIBLE

        // Run object detection and display the result
        runObjectDetection(bitmap)
    }

    // Decodes and crops the captured image from camera.

    private fun getCapturedImage(): Bitmap {
        // Get the dimensions of the View
        val targetW: Int = inputImageView.width
        val targetH: Int = inputImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }

            else -> {
                bitmap
            }
        }
    }

    /**
     * Get image form drawable and convert to bitmap.
     */
    private fun getSampleImage(drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, drawable, BitmapFactory.Options().apply {
            inMutable = true
        })
    }

    /**
     * Rotate the given bitmap.
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return with(File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )) {
            currentPhotoPath = absolutePath
            this
        }
    }
    //Open a camera app to take photo.
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val packageManager: PackageManager = requireContext().packageManager
            Log.d("Line 264", "dispatchTakePictureIntent - right before that line.")
            takePictureIntent.resolveActivity(packageManager)?.also {
                Log.d("Line 265", "dispatchTakePictureIntent - made it this far.")
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.rigid.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun roboInference(file: File, API_KEY: String) {
        Log.d("Robo Inference", "Robo Inferring")
        // Base 64 Encode
        val encodedFile: String
        val fileInputStreamReader = FileInputStream(file)
        val bytes = ByteArray(file.length().toInt())
        fileInputStreamReader.read(bytes)
        encodedFile = String(Base64.getEncoder().encode(bytes), StandardCharsets.US_ASCII)
        val MODEL_ENDPOINT = "guitar_detector-xvzkb/4"//"dataset/v" // Set model endpoint (Found in Dataset URL)

        // Construct the URL
        val uploadURL =
            "https://detect.roboflow.com/$MODEL_ENDPOINT?api_key=$API_KEY&name=cropped_image.jpg"

        Log.d("Upload URL", uploadURL)
        CoroutineScope(Dispatchers.IO).launch {
            // Http Request
            var connection: HttpURLConnection? = null
            try {
                // Configure connection to URL
                Log.d("RoboInference", "Robo Inference")
                val url = URL(uploadURL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                connection.setRequestProperty(
                    "Content-Length",
                    encodedFile.toByteArray().size.toString()
                )
                connection.setRequestProperty("Content-Language", "en-US")
                connection.useCaches = false
                connection.doOutput = true

                //Send request
                val wr = DataOutputStream(
                    connection.outputStream
                )
                wr.writeBytes(encodedFile)
                wr.close()

                // Get Response
                val stream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(stream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    println(line)
                    Log.d("For Class Demo Purposes", line.toString())
                    guitarModel.text=line
                    reader.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detection: List<BoxWithText>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val draw = Paint()
        draw.textAlign = Paint.Align.LEFT

        detection.forEach {
            draw.color = Color.RED
            draw.strokeWidth = 8F
            draw.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, draw)

            val tagSize = Rect(0, 0, 0, 0)

            draw.style = Paint.Style.FILL_AND_STROKE
            draw.color = Color.YELLOW
            draw.strokeWidth = 2F

            draw.textSize = MAX_FONT_SIZE
            draw.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = draw.textSize * box.width() / tagSize.width()

            if (fontSize < draw.textSize) draw.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), draw
            )
        }
        return outputBitmap
    }


    data class BoxWithText(val box: Rect, val text: String)

}