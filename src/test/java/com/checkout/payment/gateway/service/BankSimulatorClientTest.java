package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZATION_CODE;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_AMOUNT;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CVV_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.DECLINED_AMOUNT;
import static com.checkout.payment.gateway.util.CommonUtils.DECLINED_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.DECLINED_CVV_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.model.PostBankSimPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimPaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class BankSimulatorClientTest {

  @Value("${bank.simulator.url}")
  private String bankSimUrl;

  private MockRestServiceServer mockServer;

  @InjectMocks
  private BankSimulatorClient bankSimulatorClient;

  @BeforeEach
  public void setUp() {
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    bankSimulatorClient = new BankSimulatorClient(restTemplate, bankSimUrl);
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testProcessPayment_SuccessfulResponse() {
    PostBankSimPaymentRequest request = new PostBankSimPaymentRequest();
    request.setAmount(AUTHORIZED_AMOUNT);
    request.setCardNumber(AUTHORIZED_CARD_NUMBER);
    request.setCurrency(CurrencyCode.GBP);
    request.setCvv(AUTHORIZED_CVV_NUMBER);
    request.setExpiryDate("04/25");

    PostBankSimPaymentReponse expectedResponse = new PostBankSimPaymentReponse();
    expectedResponse.setAuthorized(true);
    expectedResponse.setAuthorization_code(AUTHORIZATION_CODE);

    mockServer.expect(requestTo(bankSimUrl)).andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andRespond(withSuccess(
            "{\"authorized\":true, \"authorization_code\":\"0bb07405-6d44-4b50-a14f-7ae0beff13ad\"}",
            MediaType.APPLICATION_JSON));

    PostBankSimPaymentReponse actualResponse = bankSimulatorClient.processPayment(request);

    assertEquals(expectedResponse.isAuthorized(), actualResponse.isAuthorized());
    assertEquals(expectedResponse.getAuthorization_code(), actualResponse.getAuthorization_code());
    mockServer.verify();
  }

  @Test
  public void testProcessPayment_DeclinedResponse() {
    PostBankSimPaymentRequest request = new PostBankSimPaymentRequest();
    request.setAmount(DECLINED_AMOUNT);
    request.setCardNumber(DECLINED_CARD_NUMBER);
    request.setCurrency(CurrencyCode.USD);
    request.setCvv(DECLINED_CVV_NUMBER);
    request.setExpiryDate("01/26");

    PostBankSimPaymentReponse expectedResponse = new PostBankSimPaymentReponse();
    expectedResponse.setAuthorized(false);
    expectedResponse.setAuthorization_code("");

    mockServer.expect(requestTo(bankSimUrl)).andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andRespond(
            withSuccess("{\"authorized\":false, \"authorization_code\":\"\"}",
                MediaType.APPLICATION_JSON));

    PostBankSimPaymentReponse actualResponse = bankSimulatorClient.processPayment(request);

    assertEquals(expectedResponse.isAuthorized(), actualResponse.isAuthorized());
    assertEquals(expectedResponse.getAuthorization_code(), actualResponse.getAuthorization_code());
    mockServer.verify();
  }
}
