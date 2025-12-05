/*package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;
import android.os.Handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBController {

    public static String VERSION = "1.0";

    public static void destroy() {
        try {
            dataSource.close();
            threadPool.shutdown();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static interface OnErrorResponse {
        void execute(String errorMessage);
    }

    public static interface InternalMessage {
        void execute();
    }

    public static void executeInMainThread(Context ctx, final InternalMessage internalMessage) {
        Handler mainHandler = new Handler(ctx.getMainLooper());
        Runnable myRunnable = () -> internalMessage.execute();
        mainHandler.post(myRunnable);
    }

    public static interface DBInstruction {

        void execute(Connection connection) throws SQLException;
    }

    public static interface DBCallback {

        void execute(SQLException ex);
    }

    private static BasicDataSource dataSource = null;
    private static ExecutorService threadPool = null;

    public static BasicDataSource getDataSource() {
        return dataSource;
    }

    public static void init(int poolNum, String url, String login, String password) {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
        }
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(poolNum);
        }
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUsername(login);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
    }

    public static void executeInDb(Context ctx, DBInstruction instruction, DBCallback errorCallback) {
        threadPool.execute(new DBThreadExecutor(ctx, dataSource, instruction, errorCallback));
    }
}
*/

package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBController {
    private static final String TAG = "DBController";

    public static String VERSION = "1.0";

    // Храним параметры подключения вместо BasicDataSource
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;

    private static ExecutorService threadPool = null;

    public static void destroy() {
        try {
            // Больше не нужно закрывать dataSource
            if (threadPool != null) {
                threadPool.shutdown();
                Log.i(TAG, "Пул потоков остановлен");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Ошибка при остановке пула потоков: " + ex.getMessage());
        }
    }

    public interface OnErrorResponse {
        void execute(String errorMessage);
    }

    public interface InternalMessage {
        void execute();
    }

    public static void executeInMainThread(Context ctx, final InternalMessage internalMessage) {
        Handler mainHandler = new Handler(ctx.getMainLooper());
        Runnable myRunnable = internalMessage::execute;
        mainHandler.post(myRunnable);
    }

    public interface DBInstruction {
        void execute(Connection connection) throws SQLException;
    }

    public interface DBCallback {
        void execute(SQLException ex);
    }

    // Удаляем старый метод getDataSource()

    public static void init(int poolNum, String url, String login, String password) {
        dbUrl = url;
        dbUsername = login;
        dbPassword = password;

        if (threadPool == null) {
            // Ограничиваем размер пула для Android
            int actualPoolSize = Math.min(poolNum, 10);
            threadPool = Executors.newFixedThreadPool(actualPoolSize);
            Log.i(TAG, "Пул потоков инициализирован с размером: " + actualPoolSize);
        }

        try {
            // Регистрируем драйвер Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Log.i(TAG, "Oracle JDBC драйвер успешно зарегистрирован");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Не удалось зарегистрировать Oracle JDBC драйвер: " + e.getMessage());
        }
    }

    // НОВЫЙ МЕТОД: Получение соединения с БД
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // ВАЖНО: Устанавливаем параметры для Oracle BLOB
            connection.setAutoCommit(true); // Для запросов только для чтения

            return connection;
        } catch (SQLException e) {
            Log.e(TAG, "Ошибка при подключении к БД: " + e.getMessage());
            throw e;
        }
    }

    public static void executeInDb(Context ctx, DBInstruction instruction, DBCallback errorCallback) {
        threadPool.execute(() -> {
            Connection connection = null;
            try {
                connection = getConnection();
                instruction.execute(connection);
            } catch (SQLException ex) {
                Log.e(TAG, "Ошибка выполнения в БД: " + ex.getMessage());
                if (errorCallback != null) {
                    executeInMainThread(ctx, () -> errorCallback.execute(ex));
                }
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.w(TAG, "Ошибка при закрытии соединения: " + e.getMessage());
                    }
                }
            }
        });
    }
}