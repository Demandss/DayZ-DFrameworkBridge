package su.demands.dframeworkbridge.restapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiMainController {
    @RequestMapping
    String WTF() {
        return """
                So far, I have not done anything here, but you can look at this cat.
                
                """;
    }
}
