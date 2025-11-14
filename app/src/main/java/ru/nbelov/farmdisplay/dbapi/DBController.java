package ru.nbelov.farmdisplay.dbapi;

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
