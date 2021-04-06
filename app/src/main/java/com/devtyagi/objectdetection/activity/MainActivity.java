package com.devtyagi.objectdetection.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devtyagi.objectdetection.databinding.ActivityMainBinding;
import com.devtyagi.objectdetection.util.Draw;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ObjectDetectorOptions options;
    ObjectDetector objectDetector;

//    CustomObjectDetectorOptions customObjectDetectorOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        options = new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                        .enableClassification()
                        .build();

//        LocalModel localModel =
//                new LocalModel.Builder()
//                        .setAssetFilePath("ssd_mobilenet_v1_1_default_1.tflite")
//                        .build();

//        customObjectDetectorOptions =
//                new CustomObjectDetectorOptions.Builder(localModel)
//                        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
//                        .enableClassification()
//                        .setClassificationConfidenceThreshold(0.5f)
//                        .setMaxPerObjectLabelCount(3)
//                        .build();

        objectDetector = ObjectDetection.getClient(options);

        Dexter.withContext(this)
                .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        makeToast("Permissions Required!");
                    }
                }).check();

    }

    private void makeToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void setupCamera() {
        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getInputImageFromFrame(frame));
            }
        });
    }

    private void processImage(InputImage inputImage) {
        objectDetector.process(inputImage)
                .addOnSuccessListener(detectedObjects -> processResults(detectedObjects))
                .addOnFailureListener(e -> makeToast("Some Error Occurred"));
    }

    private void processResults(List<DetectedObject> detectedObjects) {
        for(DetectedObject i : detectedObjects) {
            if(binding.parentLayout.getChildCount() > 1) {
                binding.parentLayout.removeViewAt(1);
            }
            Rect boundingBox = i.getBoundingBox();
            //Log.d("MainActivity", "processResults: " + i.getLabels().toString());
            String text = "Undefined";
            if(i.getLabels().size() != 0) {
                text = i.getLabels().get(0).getText();
            }
            Draw element = new Draw(this, boundingBox, text);
            binding.parentLayout.addView(element);
        }
    }

    private InputImage getInputImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        return InputImage.fromByteArray(data, frame.getSize().getWidth(), frame.getSize().getHeight(), frame.getRotation(), InputImage.IMAGE_FORMAT_NV21);
    }

}