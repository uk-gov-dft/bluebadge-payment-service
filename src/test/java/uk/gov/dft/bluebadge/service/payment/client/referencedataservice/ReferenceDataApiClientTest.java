package uk.gov.dft.bluebadge.service.payment.client.referencedataservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.ReferenceData;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.ReferenceDataResponse;

@RunWith(MockitoJUnitRunner.class)
public class ReferenceDataApiClientTest {
  public static final String TEST_URI = "http://justtesting:7777/test/";
  private static final String BASE_ENDPOINT = TEST_URI + "reference-data";

  private ReferenceDataApiClient client;
  private MockRestServiceServer mockServer;
  private ObjectMapper om = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(TEST_URI));
    mockServer = MockRestServiceServer.bindTo(restTemplate).build();

    client = new ReferenceDataApiClient(restTemplate);
  }

  @Test
  public void retrieveReferenceData() throws Exception {
    ReferenceData referenceData1 = buildReferenceData(1);
    ReferenceData referenceData2 = buildReferenceData(2);
    ReferenceData referenceData3 = buildReferenceData(3);
    List<ReferenceData> referenceDataList =
        Lists.newArrayList(referenceData1, referenceData2, referenceData3);
    ReferenceDataResponse response = new ReferenceDataResponse();
    response.setData(referenceDataList);
    String responseBody = om.writeValueAsString(response);

    mockServer
        .expect(once(), requestTo(BASE_ENDPOINT + "/APP"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

    List<ReferenceData> result = client.retrieveReferenceData("APP");
    assertThat(result).isEqualTo(referenceDataList);
  }

  private ReferenceData buildReferenceData(int i) {
    return ReferenceData.builder()
        .description("description" + 1)
        .displayOrder(i)
        .groupDescription("groupDescription" + i)
        .groupShortCode("groupShortCode" + i)
        .shortCode("shortCode" + i)
        .subgroupDescription("subGroupDescription" + i)
        .subgroupShortCode("subGroupShortCode" + i)
        .build();
  }
}
