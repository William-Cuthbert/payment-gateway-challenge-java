package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostBankSimulatorPaymentRequest {

  @JsonProperty("card_number")
  private long cardNumber;

  @JsonProperty("expiry_date")
  private String expiryDate;

  @JsonProperty("currency")
  private CurrencyCode currency;

  @JsonProperty("amount")
  private int amount;

  @JsonProperty("cvv")
  private int cvv;

  @Override
  public String toString() {
    return "PostBankSimulatorPaymentRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryDate='" + expiryDate + '\'' +
        ", currency=" + currency +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
