package su.demands.dframeworkbridge.database.sql;

import com.google.gson.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import su.demands.dframeworkbridge.Reference;
import su.demands.dframeworkbridge.database.IDataBase;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLDataBase implements IDataBase {

    protected final Logger LOGGER = Reference.LOGGER;

    protected static Connection connection;
    protected static Statement statement;

    @Getter
    protected String file = Reference.DATA_PATH;

    protected String type;
    @Getter @Setter
    protected String host;
    @Getter
    protected String url = "jdbc:" + type + "://" + host;
    @Getter
    protected String user;
    @Getter
    protected String password;

    @Override
    public String getType() {
        return type.equalsIgnoreCase("sqlite") ?
                type + "::" + file :
                type + "::" + host + ":" + user;
    }

    public SQLDataBase(@NonNull String type)
    {
        this(type,null);
    }

    public SQLDataBase(@NonNull String type, String file) {
        this(type,file,"localhost:3306","root","root");
    }

    public SQLDataBase(@NonNull String type, String file, String host, String user, String password) {
        this.type = type.toLowerCase(Locale.ROOT);
        this.file += file;

        File dir = new File(this.file.substring(0, this.file.lastIndexOf("\\")));
        if (!dir.exists())
            dir.mkdirs();

        this.host = host;
        this.user = user;
        this.password = password;
    }

    @SneakyThrows
    public boolean connection() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        connection = null;

        switch (type) {
            case "sqlite" -> {
                dataSource.setDriverClassName("org.sqlite.JDBC");
                url = "jdbc:" + type + ":" + file + ".db";
            }
            case "mysql" ->
                dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            case "mariadb" ->
                dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
            case "postgresql" ->
                dataSource.setDriverClassName("org.postgresql.Driver");
            default -> {
                LOGGER.error("Unknown database type.");
                return false;
            }
        }

        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        connection = dataSource.getConnection();
        statement = connection.createStatement();

        return true;
    }

    public String update(String sql) {
        try {
            statement.executeLargeUpdate(sql);
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "Success";
    }

    @SneakyThrows
    public ResultSet query(String sql) {
        return statement.executeQuery(sql);
    }

    @SneakyThrows
    public String queryCSV(String sql) {
        ResultSet rs = query(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            result.append(rsmd.getColumnName(i));
            if (i < columnCount) {
                result.append(",");
            }
        }
        result.append(";\n");

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(rs.getString(i));
                if (i < columnCount) {
                    result.append(",");
                }
            }
            result.append(";\n");
        }

        return result.toString();
    }


    @SneakyThrows
    public String queryJson(String sql) {
        ResultSet rs = query(sql);
        Gson gson = new GsonBuilder().create();
        ResultSetMetaData rsmd = rs.getMetaData();
        JsonObject obj = new JsonObject();

        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                if (obj.has(column_name))
                    obj.getAsJsonArray(column_name).add(gson.toJsonTree(rs.getObject(column_name)));
                else
                    obj.add(column_name,new JsonArray());
            }
        }

        return obj.toString();
    }

    @SneakyThrows
    @Override
    public void disconnection() {
        if (connection != null) {
            connection.close();
            statement.close();
        }
    }
}
