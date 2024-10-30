package com.checkout.payment.gateway.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyCode {
  GBP("GBP"),
  USD("USD"),
  EUR("EUR");

  private final String code;

  CurrencyCode(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCurrencyCode() {
    return code;
  }
}
