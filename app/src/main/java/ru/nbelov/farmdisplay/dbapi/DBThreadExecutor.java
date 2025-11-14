package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;

import java.sql.Connection;
import java.sql.SQLException;

public class DBThreadExecutor implements Runnable {
    private final BasicDataSource dataSource;
    private final DBController.DBInstruction instruction;
    private final DBController.DBCallback errorCallback;
    private final Context ctx;

    public DBThreadExecutor(Context ctx, BasicDataSource dataSource, DBController.DBInstruction instruction, DBController.DBCallback errorCallback) {
        this.dataSource = dataSource;
        this.instruction = instruction;
        this.errorCallback = errorCallback;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try {
            try (Connection conn = dataSource.getConnection();) {
                instruction.execute(conn);
            }
        } catch (SQLException ex) {
            if (errorCallback != null) {
                DBController.executeInMainThread(ctx, () -> {
                    errorCallback.execute(ex);
                });
            }
            ex.printStackTrace();
        }
    }
}
