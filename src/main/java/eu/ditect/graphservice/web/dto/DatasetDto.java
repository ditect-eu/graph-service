package eu.ditect.graphservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;

/**
 * Represent an instrument metric with meta data.
 */
@Data
public class DatasetDto {
  @JsonProperty("ditect:partnerCode")
  private String partner;
  @JsonProperty("ditect:pilotCode")
  private String pilotCode;
  @JsonProperty("schema:country")
  private String country;
  @JsonProperty("schema:region")
  private String region;
  @JsonProperty("ditect:manufacturingProcessing")
  private boolean manufacturingProcessing;
  @JsonProperty("ditect:primaryProduction")
  private boolean primaryProduction;
  @JsonProperty("ditect:distributionRetail")
  private boolean distributionRetail;
  @JsonProperty("ditect:packingStage")
  private boolean packingStage;
  @JsonProperty("ditect:instrumentName")
  private String instrumentName;
  @JsonProperty("ditect:typeOfAnalysis")
  private String typeOfAnalysis;
  @JsonProperty("schema:dateCreated")
  private Instant createdDate;
}
