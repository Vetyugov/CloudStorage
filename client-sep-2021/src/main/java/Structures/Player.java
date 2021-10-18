package Structures;

import lombok.Getter;

@Getter
public class Player {

    private String login;
    private String path;

    public Player(String login, String path) {
        this.login = login;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Player{" +
                "login='" + login + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
