package com.geekbrains;

//Команда, с помощью которой сервер передаемт на клиент тот Path, в котором он сейачс находится
public class PathResponse extends Command {
    private final String path;

    public PathResponse(String path){
        super(CommandType.PATH_RESPONSE);
        this.path = path;
    }
    public String getPath (){
        return path;
    }
}
