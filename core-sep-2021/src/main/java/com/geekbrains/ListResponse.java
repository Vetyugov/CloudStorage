package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;

//Запрос на сервер для получения списка файлов на сервере
public class ListResponse extends Command implements Serializable {

    public ListResponse() throws IOException {
        super(CommandType.LIST_RESPONSE);
    }
}
