package com.checkout.payment.gateway.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PaymentRequestValidatorTest {

  static Stream<PostPaymentRequest> validPaymentRequests() {
    return Stream.of(new PostPaymentRequest("12345678901234", 12, LocalDate.now().getYear(),
            CurrencyCode.USD.getCurrencyCode(), 100, 123),
        new PostPaymentRequest("1234567890123456", 5, LocalDate.now().getYear() + 1,
            CurrencyCode.GBP.getCurrencyCode(), 50, 1234),
        new PostPaymentRequest("1234567890123456789", 1, LocalDate.now().getYear(),
            CurrencyCode.EUR.getCurrencyCode(), 1, 999));
  }

  static Stream<PostPaymentRequest> invalidPaymentRequests() {
    return Stream.of(new PostPaymentRequest("12345abc789012", 12, LocalDate.now().getYear(),
            CurrencyCode.USD.getCurrencyCode(), 100, 123),
        new PostPaymentRequest("1234567890123456", 13, LocalDate.now().getYear(),
            CurrencyCode.USD.getCurrencyCode(), 100, 123),
        new PostPaymentRequest("1234567890123456", 5, LocalDate.now().getYear() - 1,
            CurrencyCode.USD.getCurrencyCode(), 100, 123),
        new PostPaymentRequest("1234567890123456", 5, LocalDate.now().getYear(),
            CurrencyCode.USD.getCurrencyCode(), 0, 123),
        new PostPaymentRequest("1234567890123456", 5, LocalDate.now().getYear(),
            CurrencyCode.USD.getCurrencyCode(), 100, 1));
  }

  @ParameterizedTest
  @MethodSource("validPaymentRequests")
  void testIsPaymentRequestValid(PostPaymentRequest validRequest) {
    assertTrue(PaymentRequestValidator.isPaymentRequestValid(validRequest));
  }

  @ParameterizedTest
  @MethodSource("invalidPaymentRequests")
  void testIsPaymentRequestInvalid(PostPaymentRequest invalidRequest) {
    assertFalse(PaymentRequestValidator.isPaymentRequestValid(invalidRequest));
  }
}
