package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

import com.checkout.payment.gateway.enums.CurrencyCode;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPaymentResponse {
  private UUID id;
  private PaymentStatus status;
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private CurrencyCode currency;
  private int amount;

  @Override
  public String toString() {
    return "GetPaymentResponse{" +
        "id=" + id +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency.getCurrencyCode() + '\'' +
        ", amount=" + amount +
        '}';
  }
}
