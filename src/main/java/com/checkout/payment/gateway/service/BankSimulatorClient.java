package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.PostBankSimPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimPaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BankSimulatorClient {

  private final String bankSimUrl;
  private final RestTemplate restTemplate;

  @Autowired
  public BankSimulatorClient(RestTemplate restTemplate,
      @Value("${bank.simulator.url}") String bankSimUrl) {
    this.restTemplate = restTemplate;
    this.bankSimUrl = bankSimUrl;
  }

  public PostBankSimPaymentReponse processPayment(
      PostBankSimPaymentRequest postBankSimPaymentRequest) {
    return restTemplate.postForObject(bankSimUrl, postBankSimPaymentRequest,
        PostBankSimPaymentReponse.class);
  }
}
