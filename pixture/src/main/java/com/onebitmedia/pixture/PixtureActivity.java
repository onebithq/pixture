package com.onebitmedia.pixture;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.onebitmedia.pixture.util.FileUtils;
import com.onebitmedia.pixture.util.StorageUtils;

import java.io.File;

public class PixtureActivity extends Activity {


    private static final int REQUEST_CAMERA = 36001;
    private static final int REQUEST_GALLERY = 36002;
    private static final int REQUEST_CROP = 36003;

    private Uri mCameraUri;
    private Uri mCroppedResultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Pixture.Config config = getConfig();

        switch (config.getSource()) {
            case CAMERA:
                requestCamera(config.getSaveLocation());
                break;

            case GALLERY:
                requestGallery();
                break;

            default:
                String[] items = new String[]{Source.GALLERY.getLabel(), Source.CAMERA.getLabel()};
                new AlertDialog.Builder(this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    requestGallery();
                                } else {
                                    requestCamera(config.getSaveLocation());
                                }
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                cancel();
                            }
                        })
                        .show();
                break;
        }
    }

    public void requestCamera(@Nullable File saveLocation) {
        if (saveLocation == null) {
            mCameraUri = StorageUtils.createOutputMediaFileUri(StorageUtils.MEDIA_TYPE_IMAGE);
        } else {
            mCameraUri = Uri.fromFile(saveLocation);
        }

        if (mCameraUri == null) {
            toast("Cannot open storage for taking photo");
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    public void requestGallery() {
        d("Requesting image from Gallery");
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d("PhotoDialogActivity received activity result: " + requestCode);

        Pixture.Config config = getConfig();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY:
                    d("Receiving result from Gallery %s", data);
                    if (config.isCropRequired()) {
                        requestCroppedImage(data.getData());
                    } else {
                        finishWithResult(data.getData());
                    }
                    break;

                case REQUEST_CAMERA:
                    d("Receiving result from Camera");
                    if (config.isCropRequired()) {
                        requestCroppedImage(mCameraUri);
                    } else {
                        finishWithResult(mCameraUri);
                    }
                    break;

                case REQUEST_CROP:
                    d("Receiving result from image cropper");
                    if (isFileExist(mCroppedResultUri)) {
                        finishWithResult(mCroppedResultUri);
                    } else {
                        e("Cropped file not found: %s", mCroppedResultUri);
                        toast("Error cropping photo.");
                    }
                    break;
            }
        }
    }

    private void requestCroppedImage(Uri uri) {
        d("Requesting cropped image");
        if (isFileExist(uri)) {
            Pixture.Config config = getConfig();

            if (config.getSaveLocation() == null) {
                File resultFile = StorageUtils.createOutputMediaFile(StorageUtils.MEDIA_TYPE_IMAGE);
                mCroppedResultUri = Uri.fromFile(resultFile);
            } else {
                mCroppedResultUri = Uri.fromFile(config.getSaveLocation());
            }

            Intent crop = new CropImageIntentBuilder(
                    config.getCropAspectX(),
                    config.getCropAspectY(),
                    config.getCropWidth(),
                    config.getCropHeight(),
                    mCroppedResultUri)

                    .setSourceImage(uri)
                    .setOutputFormat("JPEG")
                    .setScale(true) // TODO add to config
                    .setScaleUpIfNeeded(config.isScaleUpIfNeeded())
                    .setOutputQuality(80) // TODO add to config
                    .setDoFaceDetection(true)
                    .getIntent(this);

            startActivityForResult(crop, REQUEST_CROP);
        } else {
            e("Source photo file not found: %s", uri);
            toast("Failed to load photo.");
        }
    }

    private void cancel() {
        finish();
    }

    private void finishWithResult(@Nullable Uri uri) {
        Intent resultData = new Intent();
        resultData.setData(uri);
        setResult(RESULT_OK, resultData);
        finish();
    }

    private boolean isFileExist(Uri uri) {
        File file = FileUtils.getFile(this, uri);
        if (file == null || !file.exists()) {
            e("Not exist: Uri %s -- Path %s", uri, file);
            return false;
        } else {
            return true;
        }
    }


    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void v(String message, Object... args) {
        Log.v(Pixture.TAG, String.format(message, args));
    }

    private void d(String message, Object... args) {
        Log.d(Pixture.TAG, String.format(message, args));
    }

    private void e(String message, Object... args) {
        Log.e(Pixture.TAG, String.format(message, args));
    }

    public Pixture.Config getConfig() {
        Intent intent = getIntent();

        if (intent.hasExtra(Pixture.EXTRA_CONFIG)) {
            return intent.getParcelableExtra(Pixture.EXTRA_CONFIG);
        } else {
            return new Pixture.Config();
        }
    }
}
