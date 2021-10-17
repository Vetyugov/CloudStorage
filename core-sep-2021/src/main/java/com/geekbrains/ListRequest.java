package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;

//Запрос на сервер для получения списка файлов на сервере
public class ListRequest extends Command implements Serializable {

    public ListRequest() throws IOException {
        super(CommandType.LIST_REQUEST);
    }
}
