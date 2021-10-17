package com.geekbrains;

import java.io.Serializable;


public class FileRequest extends Command implements Serializable {
    private final String msg;

    public FileRequest(String msg) {
        super(CommandType.FILE_REQUEST);
        this.msg = msg;
    }

    //Передает имя файла (когда из сервера просим файл с таким именем)
    /**
     * @return имя выбранного файла или дирректории
     */
    public String getName() {
        return msg;
    }
}
