package com.geekbrains;

//Команда, говорящая о том, что необходимо попасть внутрь дирректории
public class PathInRequest extends Command{
    private final String dir;

    public PathInRequest(String dir) {
        super(CommandType.PATH_IN_REQUEST);
        this.dir = dir;
    }
    public String getDir(){
        return dir;
    }
}
