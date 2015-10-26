package com.miruker.lib.mailtransferservice;

import lombok.Data;

public
@Data
class TransferMessageResult {
    public TransferMessageResult(int result_code, String message) {
        super();
        this.result_code = result_code;
        this.message = message;
    }

    public static final int RESULT_ERROR = -1;
    public static final int RESULT_SUCCESS = 1;
    private int result_code;
    private String message;


}
