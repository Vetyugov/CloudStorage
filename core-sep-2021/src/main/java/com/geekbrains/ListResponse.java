package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Сообщение от сервера со структурой дирректории
 */
public class ListResponse extends Command implements Serializable {
    private final List<String> list;

    public ListResponse(Path path) throws IOException {
        super(CommandType.LIST_RESPONSE);
        list = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

    }
    public List<String> getList() {
        return list;
    }
}
