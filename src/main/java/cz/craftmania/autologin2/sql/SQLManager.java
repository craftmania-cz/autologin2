package cz.craftmania.autologin2.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.craftmania.autologin2.utils.Log;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLManager {

    private final ConnectionPoolManager pool;
    private HikariDataSource dataSource;

    public SQLManager() {
        pool = new ConnectionPoolManager();
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    private final List<String> nicksInDatabase = new ArrayList<>();

    public void cacheOriginalNicksInDatabase() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM autologin_players;");
            final ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                nicksInDatabase.add(resultSet.getString("nick"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS autologin_players (id int auto_increment, uuid varchar(64) not null, nick varchar(32) not null, last_online datetime null, constraint autologin_players_pk primary key (id));");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public boolean isInDatabase(String nick, UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM autologin_players WHERE nick = ? AND uuid = ?;");
            ps.setString(1, nick);
            ps.setString(2, uuid.toString());
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public String getNickFromDatabase(String nick) {
        if (this.nicksInDatabase.contains(nick)) return nick;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM autologin_players WHERE nick = ?;");
            ps.setString(1, nick);
            final ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                final String nick1 = resultSet.getString("nick");
                this.nicksInDatabase.add(nick1);
                return nick1;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public boolean isInDatabase(String nick) {
        if (this.nicksInDatabase.contains(nick)) return true;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM autologin_players WHERE nick = ?;");
            ps.setString(1, nick);
            final ResultSet resultSet = ps.executeQuery();
            if (ps.getResultSet().next()) this.nicksInDatabase.add(resultSet.getString("nick"));
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void insertData(UUID uuid, String nick) {
        nicksInDatabase.add(nick);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO autologin_players (nick, uuid, last_online) VALUES(?, ?, CURRENT_TIMESTAMP);");
            ps.setString(1, nick);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void quit(String nick, UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE autologin_players SET last_online = CURRENT_TIMESTAMP, uuid = ? WHERE nick = ?;");
            ps.setString(1, nick);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public void remove(String nick) {
        this.nicksInDatabase.remove(nick);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM autologin_players WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public int query(String query, Object... variables) {
        final long time = System.currentTimeMillis();
        //final ArrayList<dbRow> rows = new ArrayList<dbRow>();
        //ResultSet result = null;
        int toReturn = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= variables.length; ++i) {
                Object obj = variables[i - 1];
                if (obj != null && obj.toString().equalsIgnoreCase("null")) {
                    obj = null;
                }
                if (obj instanceof Blob) {
                    ps.setBlob(i, (Blob) obj);
                } else if (obj instanceof InputStream) {
                    ps.setBinaryStream(i, (InputStream) obj);
                } else if (obj instanceof byte[]) {
                    ps.setBytes(i, (byte[]) obj);
                } else if (obj instanceof Boolean) {
                    ps.setBoolean(i, (boolean) obj);
                } else if (obj instanceof Integer) {
                    ps.setInt(i, (int) obj);
                } else if (obj instanceof String) {
                    ps.setString(i, (String) obj);
                } else {
                    ps.setObject(i, obj);
                }
            }
            ps.executeUpdate();

            try {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                toReturn = rs.getInt(1);
            } catch (SQLException ignored) {
                //
            }

        } catch (Exception exception) {
            Log.fatal("§cSEVERE: Error has occured in query: '" + query + "'");
            exception.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        final Long diff = System.currentTimeMillis() - time;
        Log.debug("This query takes " + diff + "ms: '" + query + "'");
        if (diff > 500L) {
            Log.fatal("§cSEVERE: This query is taking too long (" + diff + "ms): '" + query + "'");
        }
        return toReturn;
    }
}
