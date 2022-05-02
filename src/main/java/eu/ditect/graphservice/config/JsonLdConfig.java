package eu.ditect.graphservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "json-ld")
public class JsonLdConfig {
  private String context;
}
