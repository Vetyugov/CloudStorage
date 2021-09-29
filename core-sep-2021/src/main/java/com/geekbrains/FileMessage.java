package com.geekbrains;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends Command implements Serializable {

    private final String name;
    private final byte[] bytes;

    public FileMessage(Path path) throws IOException {
        super(CommandType.FILE_MESSAGE);
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
