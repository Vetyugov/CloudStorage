package сonnector;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Configs {

    /**
     * Адрес хоста
     */
    protected static String dbHost = "127.0.0.1";
    /**
     * Адрес порта
     */
    protected static String dbPort = "3306";
    /**
     * Название БД
     */
    protected static String dbName = "cloud_storage";

    /**
     * Логин для подключения к БД
     */
    protected static String user = "root";

    /**
     * Пароль для подключения в БД
     */
    protected static String pwd = "123";
}

