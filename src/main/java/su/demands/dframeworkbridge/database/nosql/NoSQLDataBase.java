package su.demands.dframeworkbridge.database.nosql;

import lombok.Getter;
import org.apache.logging.log4j.Logger;
import su.demands.dframeworkbridge.Reference;
import su.demands.dframeworkbridge.database.IDataBase;

public abstract class NoSQLDataBase implements IDataBase {

    protected final Logger LOGGER = Reference.LOGGER;

    @Getter
    protected String type;
    @Getter
    protected String host;
    @Getter
    protected String url = null;
    @Getter
    protected String user;
    @Getter
    protected String password;

    public NoSQLDataBase(String type) {
        this(type,"localhost:3306","root","root");
    }

    public NoSQLDataBase(String type, String host, String user, String password) {
        this.type = type;
        this.host = host;
        this.user = user;
        this.password = password;
    }
}
