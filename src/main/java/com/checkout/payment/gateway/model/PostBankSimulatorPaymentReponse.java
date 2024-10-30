package com.checkout.payment.gateway.model;

import lombok.Data;

@Data
public class PostBankSimulatorPaymentReponse {
  private boolean authorized;
  private String authorization_code;
}
