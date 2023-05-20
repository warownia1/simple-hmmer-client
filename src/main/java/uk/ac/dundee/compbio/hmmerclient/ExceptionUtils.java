package uk.ac.dundee.compbio.hmmerclient;

final class ExceptionUtils {
  private ExceptionUtils() {}

  static IllegalArgumentException newIAE(String message) {
    return new IllegalArgumentException(message);
  }

  static IllegalStateException newISE(String message) {
    return new IllegalStateException(message);
  }
}
