package com.example.ayush;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.ayush.deepmodel.DetectionResult;
import com.example.ayush.deepmodel.MobileNetObjDetector;
import com.example.ayush.customview.OverlayView;
import com.example.ayush.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.speech.tts.TextToSpeech;

public class MainActivity extends CameraActivity implements OnImageAvailableListener {
    private static final String LOGGING_TAG = MainActivity.class.getName();

    final Handler handler = new Handler(Looper.getMainLooper());
    private int previewWidth = 0;
    private int previewHeight = 0;
    private MobileNetObjDetector objectDetector;
    private Bitmap imageBitmapForModel = null;
    private Bitmap rgbBitmapForCameraImage = null;
    private boolean computing = false;
    private Matrix imageTransformMatrix;
    private TTS tts;
    private OverlayView overlayView;
    String text;
    List<String> list = new ArrayList<String>();

    @Override
    public void onPreviewSizeChosen(final Size previewSize, final int rotation) {
        float TEXT_SIZE_DIP = 10;
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        tts = new TTS(this, Locale.ENGLISH);

        try {
            objectDetector = MobileNetObjDetector.create(getAssets());
            Log.i(LOGGING_TAG, "Model Initiated successfully.");
            Toast.makeText(getApplicationContext(), "MobileNetObjDetector created", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            e.printStackTrace();
            Log.i(LOGGING_TAG, "Model Initiated successfully.");
            Toast.makeText(getApplicationContext(), "MobileNetObjDetector could not be created", Toast.LENGTH_SHORT).show();
            finish();
        }
        overlayView = findViewById(R.id.overlay);

        final int screenOrientation = getWindowManager().getDefaultDisplay().getRotation();
        //Sensor orientation: 90, Screen orientation: 0
        Integer sensorOrientation = rotation + screenOrientation;
        Log.i(LOGGING_TAG, String.format("Camera rotation: %d, Screen orientation: %d, Sensor orientation: %d",
                rotation, screenOrientation, sensorOrientation));

        previewWidth = previewSize.getWidth();
        previewHeight = previewSize.getHeight();
        Log.i(LOGGING_TAG, "preview rotation: " + previewSize + rotation);
        Log.i(LOGGING_TAG, "preview height: " + previewHeight);
        // create empty bitmap
        int MODEL_IMAGE_INPUT_SIZE = 300;
        imageBitmapForModel = Bitmap.createBitmap(MODEL_IMAGE_INPUT_SIZE, MODEL_IMAGE_INPUT_SIZE, Config.ARGB_8888);
        rgbBitmapForCameraImage = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        imageTransformMatrix = ImageUtils.getTransformationMatrix(previewWidth, previewHeight,
                MODEL_IMAGE_INPUT_SIZE, MODEL_IMAGE_INPUT_SIZE, sensorOrientation,true);
        imageTransformMatrix.invert(new Matrix());
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image imageFromCamera = null;
        try {
            imageFromCamera = reader.acquireLatestImage();
            if (imageFromCamera == null) {
                return;
            }
            if (computing) {
                imageFromCamera.close();
                return;
            }
            computing = true;
            preprocessImageForModel(imageFromCamera);
            imageFromCamera.close();
        } catch (final Exception ex) {
            if (imageFromCamera != null) {
                imageFromCamera.close();
            }
            Log.e(LOGGING_TAG, ex.getMessage());
        }

        runInBackground(() -> {
            Log.i(LOGGING_TAG, "imageBitmapForModel: " + imageBitmapForModel);
            final List<DetectionResult> results = objectDetector.detectObjects(imageBitmapForModel);
            overlayView.setResults(results);

Float c =results.get(0).getConfidence();
            if(c > 0.65){
                String title = results.get(0).getTitle();

                if(!list.contains(title)){
                    list.add(title);
                    text += ", ";
                    text += results.get(0).getTitle();
                }
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i(LOGGING_TAG, results.get(0).toString());
//                        String title = results.get(0).getTitle();
//                        tts.speak(title);
//                    }},1000);


            }

//            if(results.size() > 0) {
//                for(int ix = 1 ; ix < results.size() - 1; ix++) {
//                    text += ", ";
//                    text += results.get(ix).getTitle();
//                }
//            }

            requestRender();
            computing = false;
        });
    }

    private void preprocessImageForModel(final Image imageFromCamera) {
        rgbBitmapForCameraImage.setPixels(ImageUtils.convertYUVToARGB(imageFromCamera, previewWidth, previewHeight),
                0, previewWidth, 0, 0, previewWidth, previewHeight);

        new Canvas(imageBitmapForModel).drawBitmap(rgbBitmapForCameraImage, imageTransformMatrix, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (objectDetector != null) {
            tts.speak(text);
            Log.i(LOGGING_TAG, "objectDetector"+text);

            objectDetector.close();
        }
    }
}
