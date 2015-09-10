package com.onebitmedia.pixture.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.onebitmedia.pixture.Pixture;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PICTURE = 1001;

    @Bind(R.id.picture) ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.pick)
    public void pickPicture() {
        Intent intent = Pixture.askPictureSource()
                .setCropAspectX(3)
                .setCropAspectY(4)
                .createIntent(this);

        startActivityForResult(intent, REQUEST_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_PICTURE) {
            picture.setImageURI(data.getData());
        }
    }
}
