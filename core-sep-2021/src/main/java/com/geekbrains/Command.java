package com.geekbrains;

import java.io.Serializable;

public class Command implements Serializable {

    CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }
}
