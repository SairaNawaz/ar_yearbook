package com.shliama.augmentedvideotutorial

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import java.util.concurrent.CompletableFuture

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
class AugmentedImageNode(context: Context) : AnchorNode() {

    // The augmented image represented by this node.
    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */
    // If any of the models are not loaded, then recurse when all are loaded.
    // Set the anchor based on the center of the image.
    // Make the 4 corner nodes.
    // Upper left corner.
    // Upper right corner.
    // Lower right corner.
    // Lower left corner.
    var image: AugmentedImage? = null
        set(image) {
            field = image
            if (!videoRenderable!!.isDone || !borderRenderable!!.isDone || !facebookRenderable!!.isDone
                || !callRenderable!!.isDone || !messageRenderable!!.isDone
            ) {
                CompletableFuture.allOf(
                    videoRenderable,
                    borderRenderable,
                    facebookRenderable,
                    callRenderable,
                    messageRenderable
                )
                    .thenAccept { aVoid: Void -> this@AugmentedImageNode.image = image }
                    .exceptionally { throwable ->
                        Log.e(TAG, "Exception loading", throwable)
                        null
                    }
            }
            anchor = image?.createAnchor(image.getCenterPose())
            localScale = Vector3(
                1.0f*image!!.extentX, // width
                1.0f,
                1.0f*image!!.extentZ
            ) // height
            localPosition = Vector3(
                0.0f*image!!.extentX, // width
                0.0f,
                0.0f*image!!.extentZ
            )
            border.localScale = Vector3(
                1.95f* image!!.extentX, // width
                1.0f,
                1.3f* image!!.extentZ
            )

            border.localPosition = Vector3(
                0.0f* image!!.extentX, // width
                0.0f,
                0.0f* image!!.extentZ
            )
            facebook.localScale = Vector3(
                0.6f,0.1f,0.6f
            )

            facebook.localPosition = Vector3(
                0.325f, 0.0f, -0.2f
            )

            facebook.setOnTapListener { hitTestResult, motionEvent ->
                val uri = Uri.parse("https://mail.google.com/mail")
                val intent = Intent(Intent.ACTION_VIEW, uri)
              //  context?.startActivity(intent)
            }




            message.localScale = Vector3(
                0.6f,0.1f,0.6f
            )

            message.localPosition = Vector3(
                0.325f, 0.0f, 0.0f
            )

            message.setOnTapListener { hitTestResult, motionEvent ->
                val uri = Uri.parse("https://www.instagram.com/dream_ar_/")
                val intent = Intent(Intent.ACTION_VIEW, uri)
               // context?.startActivity(intent)
            }
            call.localScale = Vector3(
                0.6f,0.1f,0.6f
            )

            call.localPosition = Vector3(
                0.325f , 0.0f, 0.2f
            )

            call.setOnTapListener { hitTestResult, motionEvent ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:8800211079")
             //   context?.startActivity(intent)
            }
        }

    init {
        // Upon construction, start loading the models for the corners of the frame.
        if (videoRenderable == null) {
            //   mediaPlayer = MediaPlayer()
            // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
            // it can display an ExternalTexture.
            videoRenderable = ModelRenderable.builder()
                .setSource(context, R.raw.augmented_video_model)
                .build()

            borderRenderable = ModelRenderable.builder()
                .setSource(context, Uri.parse("Border.sfb"))
                .build()

            facebookRenderable = ModelRenderable.builder()
                .setSource(context, Uri.parse("facebook_Button.sfb"))
                .build()

            callRenderable = ModelRenderable.builder()
                .setSource(context, Uri.parse("Call_Button.sfb"))
                .build()

            messageRenderable = ModelRenderable.builder()
                .setSource(context, Uri.parse("Message_Button.sfb"))
                .build()

            border = Node().apply {
                setParent(this)
            }

            facebook = Node().apply {
                setParent(border)
            }

            message = Node().apply {
                setParent(border)
            }

            call = Node().apply {
                setParent(border)
            }
        }
    }

    companion object {

        private val TAG = "AugmentedImageNode"

        // Models of the 4 corners.  We use completable futures here to simplify
        // the error handling and asynchronous loading.  The loading is started with the
        // first construction of an instance, and then used when the image is set.
        private lateinit var border: Node
        private lateinit var facebook: Node
        private lateinit var call: Node
        private lateinit var message: Node

        private var videoRenderable: CompletableFuture<ModelRenderable>? = null
        private var facebookRenderable: CompletableFuture<ModelRenderable>? = null
        private var callRenderable: CompletableFuture<ModelRenderable>? = null
        private var borderRenderable: CompletableFuture<ModelRenderable>? = null
        private var messageRenderable: CompletableFuture<ModelRenderable>? = null
    }
}
