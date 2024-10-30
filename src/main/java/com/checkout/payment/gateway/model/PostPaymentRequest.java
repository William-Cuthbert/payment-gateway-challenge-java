package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

import com.checkout.payment.gateway.enums.CurrencyCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class PostPaymentRequest implements Serializable {
  @NotNull
  @Size(min=14, max=19)
  @Pattern(regexp="^[0-9]+$")
  @JsonProperty("card_number")
  private long cardNumber;
  @NotNull
  @Min(value = 1)
  @Max(value = 12)
  @JsonProperty("expiry_month")
  private int expiryMonth;
  @NotNull
  @Future
  @JsonProperty("expiry_year")
  private int expiryYear;
  @NotBlank
  @Pattern(regexp="^[A-Z]{3}$")
  @JsonProperty("currency")
  private CurrencyCode currency;
  @NotNull
  @Positive
  @JsonProperty("amount")
  private int amount;
  @NotNull
  @Size(min=3, max=4)
  @Pattern(regexp="^[0-9]+$")
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
