package com.some.notes.Audio.pickvideo.beans;

import android.os.Parcel;
import android.os.Parcelable;

    public class PdfFile extends BaseFile implements Parcelable {
        private long duration;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(getId());
            dest.writeString(getName());
            dest.writeString(getPath());
            dest.writeLong(getSize());
            dest.writeString(getBucketId());
            dest.writeString(getBucketName());
            dest.writeLong(getDate());
            dest.writeByte((byte) (isSelected() ? 1 : 0));
            dest.writeLong(getDuration());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PdfFile> CREATOR = new Creator<PdfFile>() {
            @Override
            public PdfFile[] newArray(int size) {
                return new PdfFile[size];
            }

            @Override
            public PdfFile createFromParcel(Parcel in) {
                PdfFile file = new PdfFile();
                file.setId(in.readLong());
                file.setName(in.readString());
                file.setPath(in.readString());
                file.setSize(in.readLong());
                file.setBucketId(in.readString());
                file.setBucketName(in.readString());
                file.setDate(in.readLong());
                file.setSelected(in.readByte() != 0);
                file.setDuration(in.readLong());
                return file;
            }
        };

}
