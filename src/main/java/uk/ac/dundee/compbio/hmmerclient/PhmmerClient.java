package uk.ac.dundee.compbio.hmmerclient;

import io.github.warownia1.simplehttpclient.HttpClient;
import io.github.warownia1.simplehttpclient.HttpRequest;
import io.github.warownia1.simplehttpclient.HttpResponse;
import io.github.warownia1.simplehttpclient.impl.WWWFormURLEncodedRequestBodyBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import static java.lang.String.format;

public class PhmmerClient {
  private static final URI defaultURL;

  static {
    try {
      defaultURL = new URI("https://www.ebi.ac.uk/Tools/services/rest/hmmer3_phmmer/");
    }
    catch (URISyntaxException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final URI phmmerUrl;
  private final HttpClient httpClient = HttpClient.newHttpClient();

  public PhmmerClient(URI uri) {
    this.phmmerUrl = uri;
  }

  public PhmmerClient(URL url) throws URISyntaxException {
    this(url.toURI());
  }

  public PhmmerClient() {
    this(defaultURL);
  }

  public URI getURL() {
    return phmmerUrl;
  }

  public boolean testEndpoint() {
    var requestBuilder = HttpRequest.newBuilder(phmmerUrl);
    requestBuilder.GET();
    try {
      var response = httpClient.send(requestBuilder.build(),
          HttpResponse.BodyHandlers.discarding());
      return response.statusCode() == 200;
    }
    catch (IOException e) {
      return false;
    }
  }

  public String submit(PhmmerRequest request, String email) throws IOException {
    var requestBuilder = HttpRequest.newBuilder();
    requestBuilder.uri(phmmerUrl.resolve("run"));
    requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
    var body = prepareSubmitRequestBody(request, email);
    requestBuilder.POST(body);
    var response = httpClient.send(requestBuilder.build(),
        HttpResponse.BodyHandlers.ofInputStream());
    try (var stream = response.body()) {
      if (response.statusCode() == 200) {
        // don't use StandardCharsets for j2s compatibility
        return new String(stream.readAllBytes(), "UTF-8");
      }
      else if (response.statusCode() == 400) {
        throw XMLProcessor.processErrorBody(stream);
      }
      else {
        throw new IOException(format("server returned %d code for URL %s",
            response.statusCode(), response.uri().toString()));
      }
    }
  }

  private HttpRequest.Body prepareSubmitRequestBody(PhmmerRequest request, String email)
      throws IOException {
    var body = new WWWFormURLEncodedRequestBodyBuilder();
    body.append("email", email);
    {
      var bufferedReader = new BufferedReader(request.getSequence());
      var sequence = new StringWriter();
      bufferedReader.transferTo(sequence);
      body.append("sequence", sequence.toString());
    }
    body.append("database", request.getDatabase().strvalue);
    if (request.getIncE() != null)
      body.append("incE", request.getIncE().toString());
    if (request.getIncdomE() != null)
      body.append("incdomE", request.getIncdomE().toString());
    if (request.getE() != null)
      body.append("E", request.getE().toString());
    if (request.getDomE() != null)
      body.append("domE", request.getDomE().toString());
    if (request.getIncT() != null)
      body.append("incT", request.getIncT().toString());
    if (request.getIncdomT() != null)
      body.append("incdomT", request.getIncdomT().toString());
    if (request.getT() != null)
      body.append("T", request.getT().toString());
    if (request.getDomT() != null)
      body.append("domT", request.getDomT().toString());
    if (request.getPopen() != null)
      body.append("popen", request.getPopen().toString());
    if (request.getPextend() != null)
      body.append("pextend", request.getPextend().toString());
    if (request.getMx() != null)
      body.append("mx", request.getMx().strvalue);
    body.append("nobias", Boolean.toString(request.getNoBias()));
    body.append("compressedout", Boolean.toString(request.getCompressedOut()));
    body.append("alignView", Boolean.toString(request.getAlignView()));
    if (request.getEvalue() != null)
      body.append("evalue", request.getEvalue().toString());
    if (request.getNhits() != null)
      body.append("nhits", request.getNhits().toString());
    return body.build();
  }

  public enum Status {
    PENDING, QUEUED, RUNNING, FINISHED, ERROR, FAILURE, NOT_FOUND, UNDEFINED
  }

  public Status getStatus(String jobId) throws IOException {
    var request = HttpRequest.newBuilder(phmmerUrl.resolve("status/" + jobId));
    var response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    try {
      return Status.valueOf(response.body());
    }
    catch (IllegalArgumentException e) {
      throw new IOException(format(
          "response body does not match any known job status \"%s\"",
          response.body()));
    }
  }

  public Collection<ResultType> getResultTypes(String jobId) throws IOException {
    var request = HttpRequest.newBuilder(phmmerUrl.resolve("resulttypes/" + jobId));
    var response = httpClient.send(request.build(),
        (info, stream) -> {
          if (info.statusCode() != 200)
            throw XMLProcessor.processErrorBody(stream);
          return XMLProcessor.processResultTypesBody(stream);
        }
    );
    return response.body();
  }

  public URI getResultURL(String jobId, String identifier) {
    return phmmerUrl.resolve("result/" + jobId + "/" + identifier);
  }

  public InputStream getResultStream(String jobId, String identifier) throws IOException {
    return getResult(jobId, identifier, HttpResponse.BodyHandlers.ofInputStream());
  }

  public <T> T getResult(String jobId, String identifier, HttpResponse.BodyHandler<T> handler)
      throws IOException{
    var request = HttpRequest.newBuilder(getResultURL(jobId, identifier));
    var response = httpClient.send(request.build(), handler);
    return response.body();
  }
}
