package ru.nbelov.farmdisplay.dbapi;

import android.util.Log;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class BasicDataSource implements DataSource {

    private int loginTimeout = 0;
    private PrintWriter printWriter = null;
    String driverClassName;
    String login;
    String password;
    String url;
    String validationQuery;
    Connection connection;
    boolean isBusy = false;

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUsername(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void close() throws SQLException {
        connection.close();
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        Locale.setDefault(Locale.ENGLISH);
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, login, password);
    }

    private boolean validateConnection() {
        try {
            Statement stmt = connection.createStatement();
            StringBuffer stringBuffer = new StringBuffer();
            ResultSet rs = stmt.executeQuery(validationQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            rsmd.getColumnCount();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = createConnection();
                if (!validateConnection()) {
                    Log.i("DataSource", "connection is invalid");
                    return null;
                }
                return connection;
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        if (!validateConnection()) {
            try {
                connection.close();
            } catch (SQLException ex) {

            }
            connection = null;
            return getConnection();
        }

        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.printWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = loginTimeout;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
