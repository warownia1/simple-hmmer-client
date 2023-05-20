package uk.ac.dundee.compbio.hmmerclient;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PhmmerClientRealJobTest {

  PhmmerClient client;
  String jobId;

  @BeforeClass
  public void setupClient() {
    client = new PhmmerClient();
  }

  @Test
  public void submit_SendSequence_JobIdReceived() throws IOException {
    var requestBuilder = PhmmerRequest.newBuilder();
    //noinspection DataFlowIssue
    requestBuilder.sequence(new InputStreamReader(
        getClass().getResourceAsStream("exampleInputSequence.fa")
    ));
    requestBuilder.database(PhmmerRequest.SequenceDatabase.UNIPROTKB);
    jobId = client.submit(requestBuilder.build(), "phmmer.client.test@example.org");
    assertThat(jobId, notNullValue());
    assertThat(jobId, startsWith("hmmer3_phmmer"));
    System.out.println("Job id: " + jobId);
  }

  @Test(dependsOnMethods = "submit_SendSequence_JobIdReceived")
  public void getStatus_ValidJobFinished_JobCompleted() throws IOException, InterruptedException {
    var status = PhmmerClient.Status.PENDING;
    while (status == PhmmerClient.Status.PENDING ||
        status == PhmmerClient.Status.QUEUED ||
        status == PhmmerClient.Status.RUNNING) {
      Thread.sleep(500);
      status = client.getStatus(jobId);
    }
    assertThat(status, is(PhmmerClient.Status.FINISHED));
  }

  @Test(dependsOnMethods = "getStatus_ValidJobFinished_JobCompleted")
  public void getResultTypes_CompletedJob_OutputResultTypePresent() throws IOException {
    var resultTypes = client.getResultTypes(jobId);
    var outputResultType = new ResultType(
        "The output from the tool itself",
        "txt", "out", "Tool Output", "text/plain"
    );
    assertThat(resultTypes, hasItem(outputResultType));
  }

  @Test(dependsOnMethods = "getStatus_ValidJobFinished_JobCompleted")
  public void getErrorOutput() throws IOException {
    var result = client.getResultStream(jobId, "error");
    var reader = new BufferedReader(new InputStreamReader(result));
    reader.lines().forEach(System.out::println);
  }
}
