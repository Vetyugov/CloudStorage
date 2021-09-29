package com.geekbrains.netty;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.geekbrains.Command;
import com.geekbrains.FileMessage;
import com.geekbrains.FileRequest;
import com.geekbrains.ListRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private static final Path ROOT = Paths.get("server-sep-2021", "root");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
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
            case LIST_RESPONSE:
                log.info("Получили запрос на обновление...");
                ctx.writeAndFlush(new ListRequest(getFilesInfo()));
                break;
        }

    }
    private List<String> getFilesInfo() throws Exception {
        String list = Files.list(ROOT)
                .map(this::resolveFileType)
                .collect(Collectors.joining("\n")) + "\n";
        list = list.trim();
        String[] array = list.split("\n");
        return new ArrayList<String>(Arrays.asList(array));


    }
    private String resolveFileType(Path path) {
        if (Files.isDirectory(path)) {
            return String.format("%s\t%s", path.getFileName().toString(), "[DIR]");
        } else {
            return String.format("%s\t%s", path.getFileName().toString(), "[FILE]");
        }
    }
}
