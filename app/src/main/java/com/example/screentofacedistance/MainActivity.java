package com.example.screentofacedistance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import static android.Manifest.permission.CAMERA;
import static com.example.screentofacedistance.CameraPreview.getCameraInstance;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import androidx.core.app.ActivityCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.content.ContextCompat;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends AppCompatActivity implements IMessageActionListener {

    private Camera usedCamera;
    private String[] neededPermissions = new String[]{CAMERA};
    private CameraPreview usedCameraPreview;
    TextView distanceView;
    ProgressBar pB;

    private final static DecimalFormat dF = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Application");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        boolean result = checkPermission();
        if (result) {
            usedCamera = getCameraInstance();
            usedCameraPreview = new CameraPreview(this, usedCamera);
            FrameLayout p = (FrameLayout) findViewById(R.id.camera_preview);
            p.addView(usedCameraPreview);
            distanceView = (TextView) findViewById(R.id.currentDistance);
            pB = (ProgressBar) findViewById(R.id.pbProgress);
            pB.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageActionProducer.get().registration((IMessageActionListener) this);
        usedCamera = getCameraInstance();
        usedCameraPreview.setCamera(usedCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageActionProducer.get().unregistration((IMessageActionListener) this);
        resetCam();
    }

    private void resetCam() {
        if (usedCamera != null) {
            usedCamera.stopPreview();
            usedCamera.setPreviewCallback(null);
            usedCamera.release();
        }
    }

    public void pressedCalibrate(final View v) {

        usedCameraPreview.operate();

    }

    public void pressedReset(final View v) {
        distanceView.setText("Click calibrate to calculate distance");
        usedCameraPreview.reset();
    }

    public void changeDistanceOnScreen(final Measurement dist) {
        String str = String.valueOf(dist.distanceToFaceGet()).substring(0, 4);

        if (str.equals("-0.1") || str.equals(("-0.2"))){
            distanceView.setText("Calculating Distance");
            pB.setVisibility(View.VISIBLE);
        }
        else {
            pB.setVisibility(View.GONE);
            distanceView.setText(dF.format(dist.distanceToFaceGet()) + " cm");

            distanceView.setTextColor(Color.parseColor(getColorOnDistance(dist.distanceToFaceGet())));
            float fontRatio = dist.distanceToFaceGet() / 29.7f;
            distanceView.setTextSize(fontRatio * 20);
        }


    }

    public String getColorOnDistance(float f) {
        Log.i("CHECK", String.valueOf(f));
        if(f < 23f)
            return "#FF0000";
        return "#0000FF";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "This permission is required", Toast.LENGTH_LONG).show();
                    checkPermission();
                    return;
                }
            }

            usedCamera = getCameraInstance();

            usedCameraPreview = new CameraPreview(this, usedCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(usedCameraPreview);

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkPermission() {

        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission);
            }
        }

        if (!permissionsNotGranted.isEmpty()) {
            boolean shouldShowAlert = false;

            for (String permission : permissionsNotGranted) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            }

            if (shouldShowAlert) {
                showPermissionAlert(permissionsNotGranted.toArray(new String[0]));
            } else {
                requestPermissions(permissionsNotGranted.toArray(new String[0]));
            }
            return false;
        }
        return true;
    }

    private void showPermissionAlert(final String[] permissions) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission Required");
        alertBuilder.setMessage("Camera permission is required to move forward.");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();

    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 1001);
    }

    @Override
    public void onMessage(final int messageID, final Object message) {

        switch (messageID) {

            case MessageActionProducer.MEASUREMENT_STEP:
                changeDistanceOnScreen((Measurement) message);
                break;

            default:
                break;
        }

    }


}
