package es.jander.codex.sbs.services.dos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Lazy
@Component
@ConfigurationProperties(prefix = "app.dos")
public class DosProperties
{
    private int bucketCapacity;
    private List<String> paths;
}
