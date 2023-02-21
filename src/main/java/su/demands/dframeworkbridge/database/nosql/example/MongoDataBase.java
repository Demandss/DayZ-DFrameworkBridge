package su.demands.dframeworkbridge.database.nosql.example;

import lombok.Getter;
import lombok.SneakyThrows;
import su.demands.dframeworkbridge.database.nosql.NoSQLDataBase;

public class MongoDataBase extends NoSQLDataBase {
    @Getter
    protected String url = "mongodb://" + host;

    public MongoDataBase(String type, String host, String user, String password) {
        super(type, host, user, password);
    }

    @SneakyThrows
    public boolean connection() {
        return false;
    }

    @Override
    public void disconnection() {

    }
}
