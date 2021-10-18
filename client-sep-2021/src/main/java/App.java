import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/loginForm/loginForm.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
