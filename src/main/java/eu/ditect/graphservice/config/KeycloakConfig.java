package eu.ditect.graphservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {
  private String url;
  private String user;
  private String password;
  private String realm;
  private String clientId;
  private String clientSecret;
}