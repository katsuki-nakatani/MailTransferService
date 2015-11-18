package com.miruker.lib.mailtransferservice;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public
@Data
class LabelResult implements Parcelable {

    public static final int RESULT_ERROR = -1;
    public static final int RESULT_SUCCESS = 1;
    private int result_code;
    private List<String> labels;
    private String message;

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result_code);
        dest.writeStringList(labels);
        dest.writeString(message);
    }

    public static final Parcelable.Creator<LabelResult> CREATOR = new Creator<LabelResult>() {

        @Override
        public LabelResult[] newArray(int size) {
            return new LabelResult[size];
        }

        @Override
        public LabelResult createFromParcel(Parcel source) {
            return new LabelResult(source);
        }
    };

    private LabelResult(Parcel parcel) {
        result_code = parcel.readInt();
        labels = new ArrayList<>();
        parcel.readStringList(labels);
        message = parcel.readString();
    }

    public LabelResult(int result_code, List<String> labels, String message) {
        super();
        this.result_code = result_code;
        this.labels = labels;
        this.message = message;
    }


}
