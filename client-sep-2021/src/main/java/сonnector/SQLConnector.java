package сonnector;

import Structures.Player;
import javafx.print.PageLayout;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

import static сonnector.Configs.*;

@Slf4j
public class SQLConnector {
    private Connection conn = null;

    private Connection getConn() throws ClassNotFoundException, SQLException {
        if (conn != null) {
            return conn;
        }
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        log.debug("Запрос подключения к БД = " + connectionString);
        try {
            conn = DriverManager.getConnection(connectionString, user, pwd);
        } catch (Exception e) {
            log.error("Не удалось подключиться к БД. Настройте соединение.", e);
        }
        return conn;
    }

    /**
     * етод подключается к БД и сохраняет туда нового пользователя
     * @param login логин нового пользователя
     * @param pwd пароль нового пользователя
     * @param file путь к папке нового пользователя на сервере
     * @return true - если пользователь зарегистрирован, false - если пользователь не зарегистрирован
     */
    public boolean setNewUser(String login, String pwd, String file){
        try {
            Connection conn = getConn();
//            PreparedStatement stat = conn.prepareStatement("INSERT INTO cloud_users (login, `password`, `file` ) VALUES (?, ?, ?);");
//            stat.setString(1, login);
//            stat.setString(2, pwd);
//            stat.setString(3, file);
//            log.debug("Параметры запроса : " + login +", "+ pwd +" ," +file);

            Statement stat = conn.createStatement();
            String SQL = "INSERT INTO cloud_users (login, `password`, `file` ) VALUES ('"+login+"', '"+pwd+"', '"+file+"');";

            int rs = stat.executeUpdate(SQL);
            if(rs != 0){
                log.info("Новый пользователь успешно добавлен");
                stat.close();
            } else {
                stat.close();
                throw new Exception();
            }
        } catch (Exception e){
            log.error("Ошибка добавления нового пользователя" , e);
            return false;
        }
        return true;
    }

    /**
     * Метод проверяет есть ли пользователь с таким логином и паролем
     * @param login введенный логин
     * @param pwd введенный пароль
     * @return player - если пользователь найден, null - если пользователь не найден
     */
    public Player checkUser(String login, String pwd){
        Player player;
        try {
            Connection conn = getConn();
            Statement stat = conn.createStatement();
            String SQL = "SELECT id, file FROM cloud_users WHERE `login` = '"+login+"' AND `password` = '"+pwd+"';";
            ResultSet rs = stat.executeQuery(SQL);
            if(rs.next()){
                player = new Player(login, rs.getString("file"));
                rs.close();
                stat.close();
            } else {
                rs.close();
                stat.close();
                throw new Exception();
            }
        }catch (Exception e){
            log.error("Ошибка проверки пользователя", e);
            return null;
        }
        log.debug("Возвращаем " + player);
        return player;
    }

}
