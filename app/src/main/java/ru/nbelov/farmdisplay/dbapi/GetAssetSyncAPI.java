package ru.nbelov.farmdisplay.dbapi;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class GetAssetSyncAPI {
    private static final String TAG = "GetAssetSyncAPI";

    public static byte[] getAsset(String name) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        InputStream blobStream = null;

        try {
            // Используем новый метод getConnection() вместо getDataSource()
            connection = DBController.getConnection();

            // Используем PreparedStatement для защиты от SQL-инъекций
            String sql = "SELECT MBDISPLAY_BLOB_DATA FROM organiz.t_mbdisplay_blob " +
                    "WHERE MBDISPLAY_BLOB_FILE_NAME = ?";

            stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);

            Log.i(TAG, "Запрос ресурса: " + name);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Читаем BLOB как InputStream
                blobStream = rs.getBinaryStream("MBDISPLAY_BLOB_DATA");

                if (blobStream == null) {
                    Log.w(TAG, "Ресурс '" + name + "' найден, но BLOB пустой (NULL)");
                    return null;
                }

                // Читаем поток в массив байтов
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] dataChunk = new byte[4096];
                int bytesRead;

                while ((bytesRead = blobStream.read(dataChunk)) != -1) {
                    buffer.write(dataChunk, 0, bytesRead);
                }

                byte[] data = buffer.toByteArray();

                Log.i(TAG, "Ресурс '" + name + "' успешно загружен. Размер: " + data.length + " байт");
                return data;

            } else {
                Log.w(TAG, "Ресурс '" + name + "' не найден в БД");
                return null;
            }

        } catch (Exception ex) {
            Log.e(TAG, "Ошибка для ресурса '" + name + "': " + ex.getMessage());
            return null;
        } finally {
            // Закрываем ресурсы в правильном порядке
            closeQuietly(blobStream);
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(connection);
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.w(TAG, "Ошибка при закрытии ресурса: " + e.getMessage());
            }
        }
    }
}

/*package ru.nbelov.farmdisplay.dbapi;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetAssetSyncAPI {
    public static byte[] getAsset(String name) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = DBController.getDataSource().getConnection();

            // Используем PreparedStatement для защиты от SQL-инъекций
            String sql = "SELECT MBDISPLAY_BLOB_DATA FROM organiz.t_mbdisplay_blob WHERE MBDISPLAY_BLOB_FILE_NAME = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);

            Log.i("GetAssetSyncAPI", "Запрос ресурса: " + sql);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Для Oracle BLOB может потребоваться особый подход
                byte[] data = rs.getBytes("MBDISPLAY_BLOB_DATA");

                if (data != null) {
                    Log.i("GetAssetSyncAPI", "Ресурс '" + name + "' найден. Размер: " + data.length + " байт.");
                    return data;
                } else {
                    Log.w("GetAssetSyncAPI", "Ресурс '" + name + "' найден, но данные NULL");
                    return null;
                }
            } else {
                Log.w("GetAssetSyncAPI", "Ресурс '" + name + "' не найден в базе данных.");
                return null;
            }

        } catch (SQLException ex) {
            Log.e("GetAssetSyncAPI", "Ошибка при получении ресурса '" + name + "': " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            // Закрываем ресурсы вручную в правильном порядке
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                Log.e("GetAssetSyncAPI", "Ошибка при закрытии ResultSet: " + e.getMessage());
            }

            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                Log.e("GetAssetSyncAPI", "Ошибка при закрытии Statement: " + e.getMessage());
            }

            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Log.e("GetAssetSyncAPI", "Ошибка при закрытии Connection: " + e.getMessage());
            }
        }
    }
}

/*package ru.nbelov.farmdisplay.dbapi;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class GetAssetSyncAPI {
    public static byte[] getAsset(String name) {
        try {
            try (Connection connection = DBController.getDataSource().getConnection();) {
                try (Statement stmt = connection.createStatement();) {
                    StringBuffer stringBuffer = new StringBuffer();
                    Log.i("TT","select MBDISPLAY_BLOB_DATA from organiz.t_mbdisplay_blob  t where t.MBDISPLAY_BLOB_FILE_NAME = '" + name + "'");
                    ResultSet rs = stmt.executeQuery("select MBDISPLAY_BLOB_DATA from organiz.t_mbdisplay_blob  t where t.MBDISPLAY_BLOB_FILE_NAME = '" + name + "'");
                    if (rs.next()) {
                        Log.i("GetAssetSyncAPI", "Ресурс '" + name + " найден в базе данных. Размер: " + rs.getBytes(1).length + " байт.");
                        return rs.getBytes(1);
                    }else {
                    // Строк в результате запроса нет, ресурс не найден в БД.
                    Log.w("GetAssetSyncAPI", "Ресурс '" + name + "' не найден в базе данных.");
                    return new byte[0]; // Возвращаем массив нулевой длины.
                    }
                }
            }
        } catch (SQLException ex) {
            Log.e("GetAssetSyncAPI", ex.getLocalizedMessage());
            ex.printStackTrace();
            return "".getBytes();
        }
    }
}
*/