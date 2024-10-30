package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.CurrencyCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostBankSimPaymentRequest {
  @NotNull
  @Size(min=14, max=19)
  @Pattern(regexp="^[0-9]+$")
  @JsonProperty("card_number")
  private long cardNumber;
  @JsonProperty("expiry_date")
  private String expiryDate;
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
}
