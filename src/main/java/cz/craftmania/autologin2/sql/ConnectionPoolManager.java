package cz.craftmania.autologin2.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.craftmania.autologin2.Main;
import cz.craftmania.autologin2.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private HikariDataSource dataSource;
    private String host;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    public ConnectionPoolManager() {
        try {
            init();
            setupPool();
            Log.success("Database loaded!");
        } catch (Exception e) {
            Log.error("Could not load database.");
            e.printStackTrace();
        }
    }

    private void init() {
        host = Main.getConfig().getString("sql.hostname");
        database = Main.getConfig().getString("sql.database");
        username = Main.getConfig().getString("sql.username");
        password = Main.getConfig().getString("sql.password");
        minimumConnections = Main.getConfig().getInt("settings.minimumConnections");
        maximumConnections = Main.getConfig().getInt("settings.maximumConnections");
        connectionTimeout = Main.getConfig().getInt("settings.timeout");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":3306" + "/" + database + "?characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }
}
