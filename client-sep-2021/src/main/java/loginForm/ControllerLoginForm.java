package loginForm;

import Structures.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import сonnector.SQLConnector;

import java.io.IOException;

@Slf4j
public class ControllerLoginForm {
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Label logger;

    private final SQLConnector sqlConnector = new SQLConnector();

    private Player player;

    public Player getPlayer() {
        return player;
    }

    @FXML
    private void initialize(){

    }

    /**
     * Метод по нажатию на кнопку "Войти"
     * Проверяет, есть ли такой пользователь в бд, если есть - переход на экран transferWindow
     */
    @FXML
    private void onButtonClickedLogin(){
        String loginText = login.getText();
        player = sqlConnector.checkUser(loginText, password.getText());
        if (player != null){
            logger.setText("Добро пожаловать");
            log.info("Пользователь "+loginText+" найден");
            loadTransferWindow();
        }else {
            logger.setText("Не верный логин или пароль");
            log.debug("Не верный логин или пароль");
        }
    }

    /**
     * Метод по нажатию на кнопку "Зарегистрироваться"
     * Метод проверяет, нет ли пользоваеля с таким логинов в БД, если нет, то создает нового пользователя
     */
    @FXML
    private void onButtonClickedReg(){
        String loginText = login.getText();
        String path = loginText+"_path";
        if (sqlConnector.setNewUser(loginText, password.getText(), path) ){
            logger.setText("Новый пользователь зарегистрирован");
            log.info("Новый пользователь зарегистрирован");
        }else {
            logger.setText("Ошибка добавления нового пользователя");
            log.debug("Ошибка добавления нового пользователя");
        }
    }

    private void loadTransferWindow(){
        try {
            //Закрываем текущее окно
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/transferWindow/transferWindow.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage Stage = new Stage();
            Stage.initModality(Modality.APPLICATION_MODAL);
            Stage.setTitle("Облачное хранилище");
            Stage.setScene(new Scene(root1));
            Stage.setResizable(true);
            Stage.show();
        } catch (IOException e) {
            log.error("Не удаётся загрузить страницу transferWindow.fxml ", e);
        }
    }
}
