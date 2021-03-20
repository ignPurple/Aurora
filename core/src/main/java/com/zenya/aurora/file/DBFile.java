package com.zenya.aurora.file;

import com.zenya.aurora.Aurora;

import java.io.File;
import java.sql.*;

public class DBFile extends StorageFile {
    public DBFile(String fileName) {
        this(Aurora.getInstance().getDataFolder().getPath(), fileName);
    }

    public DBFile(String directory, String fileName) {
        this(directory, fileName, null, false);
    }

    public DBFile(String directory, String fileName, Integer fileVersion, boolean resetFile) {
        super(directory, fileName, fileVersion, resetFile);

        if(!file.exists()) {
            this.createTables();
        }
    }

    private static Connection connect() {
        String url = "jdbc:sqlite:" + Aurora.getInstance().getDataFolder() + File.separator + "database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void sendStatement(String sql) {
        sendPreparedStatement(sql, null);
    }

    private static void sendPreparedStatement(String sql, Object... parameters) {
        sendQueryStatement(sql, null, parameters);
    }

    private static Object sendQueryStatement(String sql, String query, Object... parameters) {
        Object result = null;

        try {
            Connection conn = connect();
            if((parameters == null || parameters.length == 0) && query == null) {
                //Simple statement
                Statement statement = conn.createStatement();
                statement.execute(sql);
            } else {
                PreparedStatement ps = conn.prepareStatement(sql);
                for(int i=0; i<parameters.length; i++) {
                    ps.setObject(i+1, parameters[i]);
                }

                if(query == null) {
                    //Prepared statement
                    ps.execute();
                } else {
                    //Query statement
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()) result = rs.getObject(query);
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS aurora ("
                + "id integer PRIMARY KEY AUTOINCREMENT, "
                + "player text NOT NULL UNIQUE, "
                + "toggle tinyint NOT NULL);";

        sendStatement(sql);
    }

    public void initData(String playerName) {
        String sql = "INSERT OR IGNORE INTO aurora(player, toggle) VALUES(?, ?)";
        sendPreparedStatement(sql, playerName, 1);
    }

    public boolean getToggleStatus(String playerName) {
        initData(playerName);
        boolean status = false;

        String sql = "SELECT toggle FROM aurora WHERE player = ?";
        Object toggleInt = sendQueryStatement(sql, "toggle", playerName);
        if(toggleInt != null && toggleInt instanceof Integer) {
            status = (Integer) toggleInt == 1 ? true : false;
        }
        return status;
    }

    public void setToggleStatus(String playerName, boolean status) {
        initData(playerName);
        int toggleInt = status ? 1 : 0;

        String sql = "UPDATE aurora SET toggle = ? WHERE player = ?";
        sendPreparedStatement(sql, toggleInt, playerName);
    }
}
