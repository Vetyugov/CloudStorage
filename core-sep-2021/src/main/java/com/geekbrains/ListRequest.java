package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;


/**
 * Сообщение от сервера со структурой дирректории
 */
public class ListRequest extends Command implements Serializable {
    private final List<String> list;

    public ListRequest(List<String> list) throws IOException {
        super(CommandType.LIST_REQUEST);
        this.list = list;
    }
    public List<String> getList() {
        return list;
    }
}
