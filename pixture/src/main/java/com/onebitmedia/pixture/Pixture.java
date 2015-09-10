package com.onebitmedia.pixture;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.File;

public class Pixture {
    public static final String TAG = "Pixture";
    public static final String EXTRA_CONFIG = "pixture_config";

    public static ConfigBuilder fromGallery() {
        return new ConfigBuilder().setSource(Source.GALLERY);
    }

    public static ConfigBuilder fromCamera() {
        return new ConfigBuilder().setSource(Source.CAMERA);
    }

    public static ConfigBuilder askPictureSource() {
        return new ConfigBuilder().setSource(Source.ASK);
    }

    public static class Config implements Parcelable {

        public static final Parcelable.Creator<Config> CREATOR = new Parcelable.Creator<Config>() {
            public Config createFromParcel(Parcel source) {
                return new Config(source);
            }

            public Config[] newArray(int size) {
                return new Config[size];
            }
        };

        private Source source;
        @Nullable private File saveLocation;
        private boolean cropRequired;
        private int cropAspectX, cropAspectY;
        private int cropWidth, cropHeight;
        private boolean scaleUpIfNeeded;

        public Config() {
        }

        protected Config(Parcel in) {
            int tmpSource = in.readInt();
            this.source = tmpSource == -1 ? null : Source.values()[tmpSource];
            this.saveLocation = (File) in.readSerializable();
            this.cropRequired = in.readByte() != 0;
            this.cropAspectX = in.readInt();
            this.cropAspectY = in.readInt();
            this.cropWidth = in.readInt();
            this.cropHeight = in.readInt();
            this.scaleUpIfNeeded = in.readByte() != 0;
        }

        public Source getSource() {
            return source;
        }

        @Nullable
        public File getSaveLocation() {
            return saveLocation;
        }

        public boolean isCropRequired() {
            return cropRequired;
        }

        public int getCropAspectX() {
            return cropAspectX;
        }

        public int getCropAspectY() {
            return cropAspectY;
        }

        public int getCropWidth() {
            return cropWidth;
        }

        public int getCropHeight() {
            return cropHeight;
        }

        public boolean isScaleUpIfNeeded() {
            return scaleUpIfNeeded;
        }

        public Intent createIntent(Context context) {
            Intent intent = new Intent(context, PixtureActivity.class);
            intent.putExtra(EXTRA_CONFIG, this);
            return intent;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.source == null ? -1 : this.source.ordinal());
            dest.writeSerializable(this.saveLocation);
            dest.writeByte(cropRequired ? (byte) 1 : (byte) 0);
            dest.writeInt(this.cropAspectX);
            dest.writeInt(this.cropAspectY);
            dest.writeInt(this.cropWidth);
            dest.writeInt(this.cropHeight);
            dest.writeByte(scaleUpIfNeeded ? (byte) 1 : (byte) 0);
        }
    }

    public static class ConfigBuilder {
        private final Config config;

        public ConfigBuilder(Config config) {
            this();
            this.config.source = config.source;
            this.config.saveLocation = config.saveLocation;
            this.config.cropRequired = config.cropRequired;
            this.config.cropAspectX = config.cropAspectX;
            this.config.cropAspectY = config.cropAspectY;
            this.config.cropWidth = config.cropWidth;
            this.config.cropHeight = config.cropHeight;
            this.config.scaleUpIfNeeded = config.scaleUpIfNeeded;
        }

        public ConfigBuilder() {
            this.config = new Config();
        }

        public ConfigBuilder setSource(Source source) {
            config.source = source;
            return this;
        }

        public ConfigBuilder setSaveLocation(@Nullable File saveLocation) {
            config.saveLocation = saveLocation;
            return this;
        }

        public ConfigBuilder setCropRequired(boolean cropRequired) {
            config.cropRequired = cropRequired;
            return this;
        }

        public ConfigBuilder setCropAspectX(int cropAspectX) {
            config.cropAspectX = cropAspectX;
            config.cropRequired = true;
            return this;
        }

        public ConfigBuilder setCropAspectY(int cropAspectY) {
            config.cropAspectY = cropAspectY;
            config.cropRequired = true;
            return this;
        }

        public ConfigBuilder setCropWidth(int cropWidth) {
            config.cropWidth = cropWidth;
            config.cropRequired = true;
            return this;
        }

        public ConfigBuilder setCropHeight(int cropHeight) {
            config.cropHeight = cropHeight;
            config.cropRequired = true;
            return this;
        }

        public ConfigBuilder setScaleUpIfNeeded(boolean scaleUpIfNeeded) {
            config.scaleUpIfNeeded = scaleUpIfNeeded;
            return this;
        }

        public Config build() {
            return config;
        }

        public Intent createIntent(Context context) {
            return build().createIntent(context);
        }
    }
}
