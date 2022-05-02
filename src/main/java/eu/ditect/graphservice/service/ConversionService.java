package eu.ditect.graphservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdOptions;
import eu.ditect.graphservice.config.CentralRepositoryConfig;
import eu.ditect.graphservice.config.JsonLdConfig;
import eu.ditect.graphservice.config.KeycloakConfig;
import eu.ditect.graphservice.web.dto.DatasetDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.JsonLDReadContext;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.apache.jena.sparql.util.Context;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ConversionService {
  public static final String HTTP = "http";
  private JsonLdConfig jsonLdConfig;
  private RestTemplate restTemplate;
  private CentralRepositoryConfig centralRepositoryConfig;
  private KeycloakConfig keycloakConfig;
  private ObjectMapper objectMapper;

  public ConversionService(JsonLdConfig jsonLdConfig,
      RestTemplate restTemplate, CentralRepositoryConfig centralRepositoryConfig,
      KeycloakConfig keycloakConfig, ObjectMapper objectMapper) {
    this.jsonLdConfig = jsonLdConfig;
    this.restTemplate = restTemplate;
    this.centralRepositoryConfig = centralRepositoryConfig;
    this.keycloakConfig = keycloakConfig;
    this.objectMapper = objectMapper;
  }

  @Scheduled(cron = "${sync.cron}")
  public void convertJsonLd() throws IOException, URISyntaxException {
    URIBuilder builder = new URIBuilder();
    Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
    String datePath = yesterday + "/" + Instant.now();
    builder
        .setScheme(HTTP)
        .setHost(centralRepositoryConfig.getHost())
        .setPort(centralRepositoryConfig.getPort())
        .setPath(centralRepositoryConfig.getMetricByDatePath() + "/" + datePath);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer "+ getAccessToken());
    HttpEntity<String> entity = new HttpEntity<>(null,headers);
    ResponseEntity<List<DatasetDto>> response = restTemplate.exchange(builder.build(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<DatasetDto>>() {});
    for(DatasetDto datasetDto : response.getBody()){
        log.info(datasetDto.toString());
        JsonLdOptions options = new JsonLdOptions();
        JsonLDReadContext jenaCtx = new JsonLDReadContext();
        jenaCtx.setOptions(options);
        jenaCtx.setJsonLDContext(jsonLdConfig);
        String jsonString = objectMapper.writer().writeValueAsString(datasetDto);
        Dataset dataSet = jsonld2dataset(jsonString, jenaCtx);
        RDFDataMgr.write(System.out, dataSet, Lang.NQUADS) ;
    }
  }

  private Dataset jsonld2dataset(String jsonld, Context jenaCtx) throws IOException {
    Dataset ds = DatasetFactory.create();

    try (InputStream in = new ByteArrayInputStream(jsonld.getBytes(StandardCharsets.UTF_8))) {
      RDFParser.create()
          .source(in)
          .errorHandler(ErrorHandlerFactory.errorHandlerNoLogging)
          .lang(Lang.JSONLD)
          .context(jenaCtx)
          .parse(ds.asDatasetGraph());
    }

    return ds;
  }

  private String getAccessToken(){
    Keycloak keycloak = KeycloakBuilder
        .builder()
        .serverUrl(keycloakConfig.getUrl())
        .realm(keycloakConfig.getRealm())
        .username(keycloakConfig.getUser())
        .password(keycloakConfig.getPassword())
        .clientId(keycloakConfig.getClientId())
        .clientSecret(keycloakConfig.getClientSecret())
        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
        .build();
    AccessTokenResponse accessToken = keycloak.tokenManager().getAccessToken();
    return accessToken.getToken();
  }
}
