package su.demands.dframeworkbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DFrameworkBridgeApplication {


    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter path to data files or \"-\" for set default path");
        String line = reader.readLine();

        if (line.equalsIgnoreCase("-") || line.isEmpty()) {
            String dataPath = DFrameworkBridgeApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            Reference.DATA_PATH = dataPath.substring(6,dataPath.lastIndexOf("DFrameworkBridge"));
        }
        else
        {
            File dir = new File(line);

            if (dir.isDirectory())
                if (dir.exists())
                    Reference.DATA_PATH = dir.getPath() + "\\";
                else
                {
                    System.err.println("Directory not found");
                    System.exit(0);
                }
        }

        SpringApplication.run(DFrameworkBridgeApplication.class, args);
    }
}