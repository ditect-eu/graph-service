package eu.ditect.graphservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "central-repository")
public class CentralRepositoryConfig {
  private String host;
  private int port;
  private String metricByDatePath;
}
