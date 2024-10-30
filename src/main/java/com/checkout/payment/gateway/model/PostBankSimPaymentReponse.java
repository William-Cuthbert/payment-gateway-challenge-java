package com.checkout.payment.gateway.model;

import lombok.Data;

@Data
public class PostBankSimPaymentReponse {
  private boolean authorized;
  private String authorization_code;
}
