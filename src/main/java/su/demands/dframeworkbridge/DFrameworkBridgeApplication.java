package su.demands.dframeworkbridge;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DFrameworkBridgeApplication {

    @SneakyThrows
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter path to data files or \"-\" for set default path");
        String line = reader.readLine();

        if (line.equalsIgnoreCase("-"))
            Reference.DATA_PATH = Paths.get(DFrameworkBridgeApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
        else
        {
            File dir = new File(line);

            if (dir.isDirectory())
                if (dir.exists())
                    Reference.DATA_PATH = line;
                else
                {
                    System.err.println("Directory not found");
                    System.exit(0);
                }
        }

        Reference.DATA_PATH = "E:\\colum\\Desktop\\JDBCTEST\\";

        SpringApplication.run(DFrameworkBridgeApplication.class, args);
    }
}