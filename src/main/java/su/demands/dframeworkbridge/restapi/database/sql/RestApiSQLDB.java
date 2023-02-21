package su.demands.dframeworkbridge.restapi.database.sql;

import org.apache.logging.log4j.Logger;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.demands.dframeworkbridge.Reference;
import su.demands.dframeworkbridge.database.sql.SQLDataBase;

import java.util.HashMap;

@RestController
@RequestMapping("/api/database/sql")
public class RestApiSQLDB {

    private final Logger LOGGER = Reference.LOGGER;

    private static final HashMap<String, SQLDataBase> dataBases = new HashMap<>();

    @GetMapping("/connect")
    public String createConnection(@RequestParam("mod") String modification, @RequestParam("type") String type, @Param(value = "file") String file, @Param(value = "host") String host, @Param(value = "user") String user, @Param(value = "password") String password) {
        String result = "failed";

        file = file == null ? "database" : file;

        closeConnection(modification);

        SQLDataBase dataBase;
        if (host == null || user == null || password == null)
            dataBase = new SQLDataBase(type, String.format("%s\\DataBase\\%s",modification,file));
        else
            dataBase = new SQLDataBase(type, String.format("%s\\DataBase\\%s",modification,file), host, user, password);

        dataBases.put(modification, dataBase);

        if (dataBase.connection())
            result = "successfully";

        type = (type.toLowerCase()).contains("sqlite") ?
                type + "::selectedPath:" + modification + "\\DataBase\\" + file + ".db":
                type + "::" + host + ":" + user;

        LOGGER.info(String.format("%s connected to the database %s %s",
                modification,
                type,
                result));

        return result;
    }

    @GetMapping(value = "/query")
    public ResponseEntity<String> query(@RequestParam("mod") String modification, @RequestParam("type") String resultType, @RequestParam("request") String request) {
        SQLDataBase dataBase = dataBases.get(modification);

        HttpHeaders responseHeaders = new HttpHeaders();

        String result = "failed";
        String dataBaseType = "ERROR";

        if (dataBase != null) {
            switch (resultType) {
                case "csv" -> {
                    result = dataBase.queryCSV(request);
                    responseHeaders.set("Content-Type","text/csv; charset=utf-8");
                }
                case "json" -> {
                    result = dataBase.queryJson(request);
                    responseHeaders.set("Content-Type","application/json; charset=utf-8");
                }
            }
            dataBaseType = dataBase.getType();
        }

        LOGGER.info(String.format("%s used the \"%s\" command on the database \"%s\" %nResult: %n%s",
                modification,
                request,
                dataBaseType,
                result));

        return ResponseEntity.ok().headers(responseHeaders).body(result);
    }

    @GetMapping("/update")
    public String update(@RequestParam("mod") String modification, @RequestParam("request") String request) {
        SQLDataBase dataBase = dataBases.get(modification);

        if (dataBase == null)
            return "failed";

        String result = dataBase.update(request);

        LOGGER.info(String.format("%s used the \"%s\" command on the database \"%s\" %nResult: %n %s",
                modification,
                request,
                dataBase.getType(),
                result));

        return result;
    }

    @PostMapping("/disconnect")
    public void closeConnection(@RequestParam("mod") String modification) {
        SQLDataBase dataBase = dataBases.get(modification);
        if (dataBase != null)
        {
            dataBases.remove(modification);
            dataBase.disconnection();
        }
    }
}
