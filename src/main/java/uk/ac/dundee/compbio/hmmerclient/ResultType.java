package uk.ac.dundee.compbio.hmmerclient;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class ResultType {

  String description;
  String fileSuffix;
  String identifier;
  String label;
  String mediaType;

  public ResultType(
      String description, String fileSuffix, String identifier, String label,
      String mediaType
  ) {
    this.description = requireNonNull(description);
    this.fileSuffix = requireNonNull(fileSuffix);
    this.identifier = requireNonNull(identifier);
    this.label = requireNonNull(label);
    this.mediaType = requireNonNull(mediaType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResultType that = (ResultType) o;
    return Objects.equals(description, that.description) &&
        Objects.equals(fileSuffix, that.fileSuffix) &&
        Objects.equals(identifier, that.identifier) &&
        Objects.equals(label, that.label) &&
        Objects.equals(mediaType, that.mediaType);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{description, fileSuffix, identifier, label, mediaType});
  }

  public String getDescription() {
    return description;
  }

  public String getFileSuffix() {
    return fileSuffix;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getLabel() {
    return label;
  }

  public String getMediaType() {
    return mediaType;
  }

  @Override
  public String toString() {
    return "ResultType{" +
        "description='" + description + '\'' +
        ", fileSuffix='" + fileSuffix + '\'' +
        ", identifier='" + identifier + '\'' +
        ", label='" + label + '\'' +
        ", mediaType='" + mediaType + '\'' +
        '}';
  }
}
