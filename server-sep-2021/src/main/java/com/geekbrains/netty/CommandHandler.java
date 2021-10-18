package com.geekbrains.netty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.geekbrains.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private static Path currentPath;

    private static final Path ROOT = Paths.get("server-sep-2021", "root");


    public CommandHandler() throws IOException {
        //TODO Сюда ещё добавить userName - его получаем как ответ от сервиса авторизации
        currentPath = Paths.get("server-sep-2021", "root");
        if(!Files.exists(currentPath)){
            Files.createDirectory(currentPath);
        }
    }

    //Когда мы подключились, сразу отправляем 2 команды (где мы сейчас на сервере и список файлов на сервере)
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListResponse(currentPath));
        ctx.writeAndFlush(new PathResponse((currentPath.toString())));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        log.debug("Получена команда" + cmd.getType());
        // TODO: 23.09.2021 Разработка системы команд
        switch (cmd.getType()) {
            case FILE_MESSAGE:
                log.info("Получили запрос на сохранение файла...");
                Files.write(
                    ROOT.resolve(((FileMessage)cmd).getName()),
                        ((FileMessage)cmd).getBytes()
                );
                log.info("сохранили файл");
                ctx.writeAndFlush(new FileRequest("OK"));
                break;
            case LIST_REQUEST:
                log.info("Получили запрос на обновление...");
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;
            case FILE_REQUEST:

                FileRequest fileRequest = (FileRequest) cmd;
                log.info("Запрос: Перевести текущий файл в Path: " + fileRequest.getName()  +" ==" + currentPath.toString());
                ctx.writeAndFlush( new FileMessage(currentPath.resolve(fileRequest.getName())) );
                break;
            case PATH_UP_REQUEST:
                log.info("Запрос: Перейти на папку выше");
                if (currentPath.getParent()!= null){
                    currentPath = currentPath.getParent();
                }
                ctx.writeAndFlush(new PathResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;
            case PATH_IN_REQUEST:
                PathInRequest request = (PathInRequest) cmd;
                //Новый path достаем из дирректории внутри request
                Path newPath = currentPath.resolve(request.getDir());
                //Если это папка - передаем обновленные данные на клиент
                if (Files.isDirectory(newPath)){
                    currentPath = newPath;
                    ctx.writeAndFlush(new PathResponse(currentPath.toString()));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                }
                break;
        }

    }
    private String resolveFileType(Path path) {
        if (Files.isDirectory(path)) {
            return String.format("%s\t%s", path.getFileName().toString(), "[DIR]");
        } else {
            return String.format("%s\t%s", path.getFileName().toString(), "[FILE]");
        }
    }
}
