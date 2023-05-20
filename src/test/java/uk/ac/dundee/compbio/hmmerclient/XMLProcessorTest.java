package uk.ac.dundee.compbio.hmmerclient;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class XMLProcessorTest {

  @DataProvider
  public Object[] singleResultXMLStream() {
    return new Object[]{
        getClass().getResourceAsStream("./OneResultType.xml"),
        getClass().getResourceAsStream("./OneResultTypeNewlineSeparated.xml"),
        getClass().getResourceAsStream("./OneResultTypeFormatted.xml"),
    };
  }

  @Test(dataProvider = "singleResultXMLStream")
  public void processResultTypesBody_OneResult(InputStream stream) throws IOException {
    Collection<ResultType> resultTypes;
    try (stream) {
      resultTypes = XMLProcessor.processResultTypesBody(stream);
    }
    var expected = List.of(
        new ResultType(
            "The output from the tool itself",
            "txt",
            "out",
            "Tool Output",
            "text/plain"
        )
    );
    assertEquals(resultTypes, expected);
  }
}