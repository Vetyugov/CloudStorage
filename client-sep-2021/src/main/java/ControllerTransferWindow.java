import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import com.geekbrains.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ControllerTransferWindow implements Initializable {

    private static final String ROOT_DIR = "client-sep-2021/root";
//    private static byte[] buffer = new byte[1024];
    public ListView<String> clientList;
    public TextField input;
    public ListView<String> serverList;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;


    @FXML
    private void sendFile() throws IOException {
        try {
            String fileName = clientList.getSelectionModel().getSelectedItem();
            log.debug("Отправляю файл" + fileName);
            Path file = Paths.get(ROOT_DIR, fileName);
            os.writeObject(new FileMessage(file));
            os.flush();
            log.debug("Файл отправлен");
        } catch (IOException e){
            log.error("Ошибка отправки файла", e);
        }

    }

    //Обновить структуру хранилища
    @FXML
    private void reload (){
        try {
            os.writeObject(new ListResponse());
            os.flush();
            log.debug("Запрос на обновление отправлен");
        }catch (IOException e){
            log.error("Ошибка отправки запроса ls");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fillFilesInCurrentDir();
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread daemon = new Thread(() -> {
                try {
                    while (true) {
                        Command msg = (Command) is.readObject();
                        log.debug("Ответ сервера, на получение файла, команда "+ msg.getType());
                        // TODO: 23.09.2021 Разработка системы команд
                        switch (msg.getType()) {
                            //Ответ сервера, на получение файла
                            case FILE_REQUEST:
                                String text = ((FileRequest)msg).getMsg();
                                log.debug("received: {}" + text);
                                Platform.runLater(()-> input.setText(text));
                                break;
                            case LIST_REQUEST:
                                ObservableList<String> listOfFiles = FXCollections.observableList( ((ListRequest)msg).getList());
                                serverList.setItems(listOfFiles);
                                log.debug("received: {}" + listOfFiles.toString());
                                break;

                        }
                    }
                } catch (Exception e) {
                    log.error("exception while read from input stream");
                }
            });
            daemon.setDaemon(true);
            daemon.start();
        } catch (IOException ioException) {
            log.error("e=", ioException);
        }
    }

    private void fillFilesInCurrentDir() throws IOException {
        clientList.getItems().clear();
        clientList.getItems().addAll(
                Files.list(Paths.get(ROOT_DIR))
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toList())
        );
        clientList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = clientList.getSelectionModel().getSelectedItem();
                input.setText(item);
            }
        });
    }
}
