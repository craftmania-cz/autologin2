package cz.craftmania.autologin2.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.craftmania.autologin2.AutoLogin;
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
        host = AutoLogin.getConfig().getString("sql.hostname");
        database = AutoLogin.getConfig().getString("sql.database");
        username = AutoLogin.getConfig().getString("sql.username");
        password = AutoLogin.getConfig().getString("sql.password");
        minimumConnections = AutoLogin.getConfig().getInt("settings.minimumConnections");
        maximumConnections = AutoLogin.getConfig().getInt("settings.maximumConnections");
        connectionTimeout = AutoLogin.getConfig().getInt("settings.timeout");
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
