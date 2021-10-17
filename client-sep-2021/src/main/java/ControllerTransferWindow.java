import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    public ListView<String> clientList;
    public ListView<String> serverList;
    public TextField clientPath;
    public TextField serverPath;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Path currentDir;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fillClientView();
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            //По двойному нажатию на папку
            clientList.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String item = clientList.getSelectionModel().getSelectedItem();
                    Path newPath = currentDir.resolve(item);
                    if(Files.isDirectory(newPath)){
                        currentDir = newPath;
                        try{
                            fillClientView();
                        } catch (IOException exception){
                            log.error("Ошибка обновления списка файлов" , exception);
                        }
                    }
                }
            });

            serverList.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String item = clientList.getSelectionModel().getSelectedItem();
                    try {
                        os.writeObject(new PathInRequest(item));
                        os.flush();
                    } catch (IOException exception ){
                        log.error("Ошибка отправки запроса на сервер" , exception);
                    }
                }
            });


            Thread daemon = new Thread(() -> {
                try {
                    while (true) {
                        Command msg = (Command) is.readObject();
                        log.debug("Ответ сервера, на получение файла, команда "+ msg.getType());
                        // TODO: 23.09.2021 Разработка системы команд
                        switch (msg.getType()) {
                            //Ответ сервера, на получение файла
                            case FILE_REQUEST:
                                String text = ((FileRequest)msg).getName();
                                log.debug("received: {}" + text);
                                Platform.runLater(()-> clientPath.setText(text));
                                break;

                            case LIST_RESPONSE:
                                //Обновляем файловую структуру сервера
                                fillServerView(((ListResponse)msg).getList());
                                break;

                            case PATH_RESPONSE:
                                String path = ((PathResponse)msg).getPath();
                                Platform.runLater(()-> serverPath.setText(path) );
                                break;

                            case FILE_MESSAGE:
                                FileMessage fileMessage = (FileMessage) msg;
                                Files.write(currentDir.resolve(fileMessage.getName()) , fileMessage.getBytes());
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

    /**
     * Метод заполняет listView клиента
     */
    private void fillClientView() throws IOException {
        currentDir = Paths.get("client-sep-2021" , "root");
        clientPath.setText(currentDir.toString());
        clientList.getItems().clear();
        clientList.getItems().addAll(
                Files.list(Paths.get(ROOT_DIR))
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Метод заполняет listView сервера
     * @param fileNames - список имен файлов
     */
    private void fillServerView(List<String> fileNames){
        Platform.runLater(()-> {
            serverList.getItems().clear();
            serverList.getItems().addAll(fileNames);
        });
    }

    /**
     * Событие по нажатию на кнопку UP сервера
     */
    @FXML
    private void serverPathUp() {
        try {
            os.writeObject(new PathUpRequest());
            os.flush();
        }catch ( IOException e ){
            log.error("Ошибка отправки запроса PathUpRequest на сервер. " , e);
        }
    }

    /**
     * Событие по нажатию на кнопку UP клиента
     */
    @FXML
    private void clientPathUp() {
        try {
            currentDir = currentDir.getParent();
            clientPath.setText(currentDir.toString());
            fillClientView();
        }catch ( IOException e ){
            log.error("Не удалось обновить listView клиента " , e);
        }
    }

    /**
     * Событие по нажатию на кнопку загрузки файла с сервера
     */
    @FXML
    private void download (){
        try {
            String fileName = serverList.getSelectionModel().getSelectedItem();
            os.writeObject(new FileRequest(fileName));
            os.flush();
        } catch (IOException e){
            log.error("Не удалось отправить запрос для загрузки файла с сервера FileRequest" , e);
        }
    }

    /**
     * Событие по нажатию на кнопку загрузки файла на сервер
     */
    @FXML
    private void sendFile() {
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

    /**
     * Событие по нажатию на кнопку обновления структуры сервера
     */
    @FXML
    private void reload (){
        try {
            os.writeObject(new ListRequest());
            os.flush();
            log.debug("Запрос на обновление отправлен");
        }catch (IOException e){
            log.error("Ошибка отправки запроса ls" , e);
        }

    }
}
