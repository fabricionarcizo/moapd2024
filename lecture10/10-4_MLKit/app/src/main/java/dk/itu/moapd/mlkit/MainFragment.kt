/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.mlkit

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dk.itu.moapd.mlkit.databinding.FragmentMainBinding
import kotlin.math.min

/**
 * A fragment to display the main screen of the app.
 */
class MainFragment : Fragment(), View.OnClickListener {

    /**
     * A general-purpose data class to store detection result for visualization.
     */
    data class BoxWithText(val box: Rect, val text: String)

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentMainBinding? = null

    /**
     * This property is only valid between `onCreateView()` and `onDestroyView()` methods.
     */
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    /**
     * The URI of latest taken photo using the camera intent.
     */
    private lateinit var capturedImageUri: Uri

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val MAX_FONT_SIZE = 96F
    }

    /**
     * This object launches a new activity and receives back some result data.
     */
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        galleryResult(result)
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default `View` can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns null.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a `View` from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define the UI behavior using lambda expressions.
        binding.buttonCapture.setOnClickListener { onClick(it) }
    }

    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    override fun onClick(view: View) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Check if there's a camera app available to handle the intent.
        cameraIntent.resolveActivity(requireActivity().packageManager)?.let { _ ->

            // Create an implicit intent to capture photos.
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, "ML Kit")
            }

            // Insert a new image into the MediaStore and get its URI.
            requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
                capturedImageUri = uri
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                // Launch the camera intent.
                cameraLauncher.launch(cameraIntent)
            }
        }
    }

    /**
     * When the second activity finishes (i.e., the take photo intent), it returns a result to this
     * activity. If the user selects an image correctly, we can get a reference of the selected
     * image and send it to the object detection method.
     *
     * @param result A container for an activity result as obtained form `onActivityResult()`.
     */
    private fun galleryResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            // Get the captured image as a `Bitmap`.
            getCapturedImage()?.let { image ->
                // Display capture image.
                binding.imageView.setImageBitmap(image)

                // Run through ODT and display result.
                runObjectDetection(image)
            }
        }
    }

    /**
     * Get the latest captured image as a `Bitmap`.
     *
     * @return The bitmap of latest captured image.
     */
    private fun getCapturedImage(): Bitmap? {
        val bitmap = InputImage.fromFilePath(
            requireContext(), capturedImageUri).bitmapInternal ?: return null

        val viewWidth = binding.imageView.width.toFloat()
        val viewHeight = binding.imageView.height.toFloat()
        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        val scaleFactor = min(imageWidth / viewWidth, imageHeight / viewHeight)
        val scaledWidth = viewWidth * scaleFactor
        val scaledHeight = viewHeight * scaleFactor

        val left = (imageWidth - scaledWidth) / 2
        val top = (imageHeight - scaledHeight) / 2

        return Bitmap.createBitmap(
            bitmap, left.toInt(), top.toInt(), scaledWidth.toInt(), scaledHeight.toInt())
    }

    /**
     * ML Kit Object Detection function.
     *
     * @param bitmap The input image with objects to be detected.
     */
    private fun runObjectDetection(bitmap: Bitmap) {
        // Step 1: Create ML Kit's InputImage object.
        val image = InputImage.fromBitmap(bitmap, 0)

        // Step 2: Acquire detector object.
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        // Step 3: Feed given image to detector and setup callback.
        objectDetector.process(image)
            .addOnSuccessListener { results ->
                // Parse ML Kit's DetectedObject and create corresponding visualization data.
                val detectedObjects = results.map { obj ->
                    val text = obj.labels.firstOrNull()?.let { label ->
                        "${label.text}, ${label.confidence.times(100).toInt()}%"
                    } ?: "Unknown"
                    BoxWithText(obj.boundingBox, text)
                }
                // Draw the detection result on the input bitmap.
                val visualizedResult = drawDetectionResult(bitmap, detectedObjects)
                // Show the detection result on the app screen.
                binding.imageView.setImageBitmap(visualizedResult)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, e.message.toString())
            }
    }

    /**
     * Draw bounding boxes around objects together with the object's name.
     *
     * @param bitmap The input image to detect objects.
     * @param detectionResults A list of objects detected in the input image.
     *
     * @return An image with the detected objects around rectangles.
     */
    private fun drawDetectionResult(bitmap: Bitmap, detectionResults: List<BoxWithText>): Bitmap {

        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {

            // Draw bounding box.
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // Calculate the right font size.
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // Adjust the font size so texts are inside the bounding box.
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )

        }
        return outputBitmap
    }

}
