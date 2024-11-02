package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import com.checkout.payment.gateway.enums.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number")
  private String  cardNumber;

  @JsonProperty("expiry_month")
  private int expiryMonth;

  @JsonProperty("expiry_year")
  private int expiryYear;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("amount")
  private int amount;

  @JsonProperty("cvv")
  private int cvv;

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%02d/%d", expiryMonth, expiryYear);
  }
  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
