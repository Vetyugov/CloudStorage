package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;

public class FileRequest extends Command implements Serializable {
    private final String msg;

    public FileRequest(String msg) throws IOException {
        super(CommandType.FILE_REQUEST);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
