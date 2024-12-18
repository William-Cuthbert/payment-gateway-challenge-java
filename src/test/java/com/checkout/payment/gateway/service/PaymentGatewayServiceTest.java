package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZATION_CODE;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_AMOUNT;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CVV_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_EXPIRY_MONTH_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_EXPIRY_YEAR_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_LAST_FOUR_DIGIT_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.DECLINED_AMOUNT;
import static com.checkout.payment.gateway.util.CommonUtils.DECLINED_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.INVALID_ID_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PaymentGatewayServiceTest {

  @Mock
  private PaymentsRepository paymentsRepository;

  @Mock
  private BankSimulatorClient bankSimulatorClient;

  @InjectMocks
  private PaymentGatewayService paymentGatewayService;

  private PostPaymentRequest paymentRequest;
  private PostPaymentResponse expectedResponse;

  @BeforeEach
  public void setUp() {
    paymentRequest = testData();
    expectedResponse = mockResponseData();
  }

  @Test
  public void processPayment_Authorized() {
    PostBankSimulatorPaymentReponse bankResponse = new PostBankSimulatorPaymentReponse();
    bankResponse.setAuthorized(true);
    bankResponse.setAuthorization_code(AUTHORIZATION_CODE);

    when(bankSimulatorClient.processPayment(any(PostBankSimulatorPaymentRequest.class))).thenReturn(
        bankResponse);
    doNothing().when(paymentsRepository).add(expectedResponse);

    PostPaymentResponse actualResult = paymentGatewayService.processPayment(paymentRequest);

    assertPaymentResponse(expectedResponse, actualResult);
  }

  @Test
  public void processPayment_Declined() {
    paymentRequest.setCardNumber(DECLINED_CARD_NUMBER);
    paymentRequest.setAmount(DECLINED_AMOUNT);
    paymentRequest.setCurrency(CurrencyCode.USD.getCurrencyCode());

    PostBankSimulatorPaymentReponse bankResponse = new PostBankSimulatorPaymentReponse();
    bankResponse.setAuthorized(false);
    bankResponse.setAuthorization_code("");

    when(bankSimulatorClient.processPayment(any(PostBankSimulatorPaymentRequest.class))).thenReturn(
        bankResponse);
    PostPaymentResponse expectedResult = new PostPaymentResponse();
    expectedResult.setStatus(PaymentStatus.DECLINED);

    PostPaymentResponse actualResult = paymentGatewayService.processPayment(paymentRequest);

    assertEquals(expectedResult.getStatus(), actualResult.getStatus());
  }

  @Test
  public void getPaymentById_Success() {
    UUID id = UUID.randomUUID();
    expectedResponse.setId(id);
    when(paymentsRepository.get(id)).thenReturn(Optional.of(expectedResponse));

    PostPaymentResponse actualResult = paymentGatewayService.getPaymentById(id);

    assertNotNull(actualResult);
    assertEquals(expectedResponse, actualResult);
    verify(paymentsRepository, times(1)).get(id);
  }

  @Test
  public void getPaymentById_Failure() {
    UUID invalidId = UUID.randomUUID();
    when(paymentsRepository.get(invalidId)).thenReturn(Optional.empty());

    EventProcessingException exception = assertThrows(EventProcessingException.class, () -> {
      paymentGatewayService.getPaymentById(invalidId);
    });

    assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    verify(paymentsRepository, times(1)).get(invalidId);
  }

  @ParameterizedTest
  @MethodSource("provideInvalidPaymentRequests")
  public void processPayment_Rejected_InvalidRequest(PostPaymentRequest invalidRequest) {
    PostPaymentResponse actualResult = paymentGatewayService.processPayment(invalidRequest);

    assertEquals(PaymentStatus.REJECTED, actualResult.getStatus());
  }

  private static Stream<Arguments> provideInvalidPaymentRequests() {
    return Stream.of(Arguments.of(
            createPaymentRequest("2222", AUTHORIZED_EXPIRY_MONTH_NUMBER, AUTHORIZED_EXPIRY_YEAR_NUMBER,
                AUTHORIZED_CVV_NUMBER, AUTHORIZED_AMOUNT)), Arguments.of(
            createPaymentRequest(AUTHORIZED_CARD_NUMBER, 13, AUTHORIZED_EXPIRY_YEAR_NUMBER,
                AUTHORIZED_CVV_NUMBER, AUTHORIZED_AMOUNT)), Arguments.of(
            createPaymentRequest(AUTHORIZED_CARD_NUMBER, AUTHORIZED_EXPIRY_MONTH_NUMBER,
                getCurrentYear() - 1, AUTHORIZED_CVV_NUMBER, AUTHORIZED_AMOUNT)),
        Arguments.of(createPaymentRequest(AUTHORIZED_CARD_NUMBER, AUTHORIZED_EXPIRY_MONTH_NUMBER,
            AUTHORIZED_EXPIRY_YEAR_NUMBER, 12, AUTHORIZED_AMOUNT)),
        Arguments.of(createPaymentRequest(AUTHORIZED_CARD_NUMBER, AUTHORIZED_EXPIRY_MONTH_NUMBER,
            AUTHORIZED_EXPIRY_YEAR_NUMBER, AUTHORIZED_CVV_NUMBER, 0)));
  }

  private static PostPaymentRequest createPaymentRequest(String cardNumber, int expiryMonth,
      int expiryYear,
      Integer cvv, int amount) {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber(cardNumber);
    request.setExpiryMonth(expiryMonth);
    request.setExpiryYear(expiryYear);
    request.setCvv(cvv);
    request.setAmount(amount);
    request.setCurrency(CurrencyCode.GBP.getCurrencyCode());
    return request;
  }

  private static int getCurrentYear() {
    return java.time.LocalDate.now().getYear();
  }

  private void assertPaymentResponse(PostPaymentResponse expected, PostPaymentResponse actual) {
    assertEquals(expected.getStatus(), actual.getStatus());
    assertEquals(expected.getCardNumberLastFour(), actual.getCardNumberLastFour());
    assertEquals(expected.getExpiryMonth(), actual.getExpiryMonth());
    assertEquals(expected.getExpiryYear(), actual.getExpiryYear());
    assertEquals(expected.getCurrency(), actual.getCurrency());
    assertEquals(expected.getAmount(), actual.getAmount());
  }

  private PostPaymentRequest testData() {
    paymentRequest = new PostPaymentRequest();
    paymentRequest.setCardNumber(AUTHORIZED_CARD_NUMBER);
    paymentRequest.setExpiryMonth(AUTHORIZED_EXPIRY_MONTH_NUMBER);
    paymentRequest.setExpiryYear(AUTHORIZED_EXPIRY_YEAR_NUMBER);
    paymentRequest.setCvv(AUTHORIZED_CVV_NUMBER);
    paymentRequest.setAmount(AUTHORIZED_AMOUNT);
    paymentRequest.setCurrency(CurrencyCode.GBP.getCurrencyCode());
    return paymentRequest;
  }

  private PostPaymentResponse mockResponseData() {
    expectedResponse = new PostPaymentResponse();
    expectedResponse.setAmount(AUTHORIZED_AMOUNT);
    expectedResponse.setCardNumberLastFour(AUTHORIZED_LAST_FOUR_DIGIT_CARD_NUMBER);
    expectedResponse.setCurrency(CurrencyCode.GBP.getCurrencyCode());
    expectedResponse.setExpiryMonth(AUTHORIZED_EXPIRY_MONTH_NUMBER);
    expectedResponse.setExpiryYear(AUTHORIZED_EXPIRY_YEAR_NUMBER);
    expectedResponse.setStatus(PaymentStatus.AUTHORIZED);
    return expectedResponse;
  }
}
