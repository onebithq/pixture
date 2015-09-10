# PIXTURE
Android wrapper library for picking and cropping picture, from camera or gallery. It uses [android-cropimage](https://github.com/lvillani/android-cropimage) for cropping under the hood (might change in future releases).

# Usage

Please use type-safe Intent builder. See `demo` module for examples.

```
File file = ...; 
Intent intent = Pixture.askPictureSource() // or fromGallery() or fromCamera
                .setCropAspectX(3)
                .setCropAspectY(4)
                .setCropWidth(300)
                .setCropHeight(400)
                .saveLocation(file)
                .createIntent(this);

startActivityForResult(intent, REQUEST_PICTURE);
```

The result Uri is provided in the activity result:

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK && requestCode == REQUEST_PICTURE) {
        picture.setImageURI(data.getData());
    }
}
```

