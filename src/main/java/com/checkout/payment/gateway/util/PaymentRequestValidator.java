package com.checkout.payment.gateway.util;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

public class PaymentRequestValidator {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentRequestValidator.class);

  private static final HashSet<CurrencyCode> VALID_CURRENCIES = new HashSet<>(
      Arrays.asList(CurrencyCode.EUR, CurrencyCode.GBP, CurrencyCode.USD));

  public static boolean isPaymentRequestValid(PostPaymentRequest paymentRequest) {
    if (isInvalidCardNumber(paymentRequest.getCardNumber())) {
      LOG.info("Invalid card number: must be numeric and between 14 and 19 digits.");
      return false;
    }
    if (isInvalidExpiryMonth(paymentRequest.getExpiryMonth())) {
      LOG.info("Invalid expiry month: must be between 1 and 12.");
      return false;
    }
    if (isInvalidExpiryYear(paymentRequest.getExpiryYear())) {
      LOG.info("Invalid expiry year: must be the current year or later.");
      return false;
    }
    if (isInvalidCurrency(paymentRequest.getCurrency())) {
      LOG.info("Invalid currency: must be one of {}", VALID_CURRENCIES);
      return false;
    }
    if (isInvalidAmount(paymentRequest.getAmount())) {
      LOG.info("Invalid amount: must be at least 1.");
      return false;
    }
    if (isInvalidCvv(paymentRequest.getCvv())) {
      LOG.info("Invalid CVV: must be numeric and 3 or 4 digits.");
      return false;
    }
    return true;
  }

  private static boolean isInvalidCardNumber(String cardNumber) {
    return cardNumber.length() < 14 || cardNumber.length() > 19 || !StringUtils.isNumeric(cardNumber);
  }

  private static boolean isInvalidExpiryMonth(int month) {
    return month < 1 || month > 12;
  }

  private static boolean isInvalidExpiryYear(int year) {
    return year < LocalDate.now().getYear();
  }

  private static boolean isInvalidCurrency(String currency) {
    return VALID_CURRENCIES.stream()
        .noneMatch(validCurrency -> validCurrency.getCurrencyCode().equals(currency));
  }

  private static boolean isInvalidAmount(int amount) {
    return amount < 1;
  }

  private static boolean isInvalidCvv(int cvv) {
    String cvvString = String.valueOf(cvv);
    return !StringUtils.isNumeric(cvvString) || cvvString.length() < 3 || cvvString.length() > 4;
  }
}
