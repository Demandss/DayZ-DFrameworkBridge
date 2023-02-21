package su.demands.dframeworkbridge.database;

public interface IDataBase {

    String getType();

    boolean connection();
    
    void disconnection();
}
