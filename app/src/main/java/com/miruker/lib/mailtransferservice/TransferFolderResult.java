package com.miruker.lib.mailtransferservice;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public
@Data
class TransferFolderResult implements Parcelable {

    public static final int RESULT_ERROR = -1;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_WARNING = 2;
    public static final int RESULT_CANCEL = 3;
    private int result_code;
    private int totalCount;
    private int successCount;
    private List<String> errList;
    private int errorCount;
    private String message;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result_code);
        dest.writeInt(totalCount);
        dest.writeInt(successCount);
        dest.writeInt(errorCount);
        dest.writeString(message);
        dest.writeStringList(errList);
    }

    public static final Parcelable.Creator<TransferFolderResult> CREATOR = new Creator<TransferFolderResult>() {

        @Override
        public TransferFolderResult[] newArray(int size) {
            return new TransferFolderResult[size];
        }

        @Override
        public TransferFolderResult createFromParcel(Parcel source) {
            return new TransferFolderResult(source);
        }
    };

    private TransferFolderResult(Parcel parcel) {
        result_code = parcel.readInt();
        totalCount = parcel.readInt();
        successCount = parcel.readInt();
        errorCount = parcel.readInt();
        message = parcel.readString();
        errList = new ArrayList<String>();
        parcel.readStringList(errList);
    }

    public TransferFolderResult(int result_code, int totalCount,
                                int successCount, int errorCount, String message, List<String> list) {
        super();
        this.result_code = result_code;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.message = message;
        this.errList = list;
    }
}
