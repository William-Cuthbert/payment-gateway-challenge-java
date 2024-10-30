package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.util.CommonUtils.EXPIRY_DATE;
import static com.checkout.payment.gateway.util.CommonUtils.EXPIRY_DATE_FORMAT;
import static com.checkout.payment.gateway.util.CommonUtils.EXPIRY_MONTH;
import static com.checkout.payment.gateway.util.CommonUtils.EXPIRY_YEAR;
import static com.checkout.payment.gateway.util.CommonUtils.LAST_FOUR_DIGIT_CARD_NUMBER;

import com.checkout.payment.gateway.model.PostBankSimulatorPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Mapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <S, T> T mapTo(S sourceObject, Class<T> targetClass) {
    ObjectNode node = objectMapper.convertValue(sourceObject, ObjectNode.class);

    if (sourceObject instanceof PostPaymentRequest request
        && targetClass == PostBankSimulatorPaymentRequest.class) {
      mapForBankSimPaymentRequest(node, request);
    }

    if (sourceObject instanceof PostPaymentRequest request
        && targetClass == PostPaymentResponse.class) {
      mapForPostPaymentResponse(node, request);
    }

    return objectMapper.convertValue(node, targetClass);
  }

  private static void mapForBankSimPaymentRequest(ObjectNode node, PostPaymentRequest request) {
    String expiryDate = String.format(EXPIRY_DATE_FORMAT, request.getExpiryMonth(), request.getExpiryYear());
    node.put(EXPIRY_DATE, expiryDate);
  }

  private static void mapForPostPaymentResponse(ObjectNode node, PostPaymentRequest request) {
    String cardNumberString = String.valueOf(request.getCardNumber());
    String lastFourDigits = cardNumberString.substring(cardNumberString.length() - 4);

    node.put(LAST_FOUR_DIGIT_CARD_NUMBER, lastFourDigits);
    node.put(EXPIRY_MONTH, request.getExpiryMonth());
    node.put(EXPIRY_YEAR, request.getExpiryYear());
  }
}
