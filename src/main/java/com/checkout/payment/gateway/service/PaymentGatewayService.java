package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostBankSimPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankSimulatorClient bankSimulatorClient;

  @Autowired
  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      BankSimulatorClient bankSimulatorClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankSimulatorClient = bankSimulatorClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    PostBankSimPaymentRequest bankRequest = Mapper.mapTo(paymentRequest,
        PostBankSimPaymentRequest.class);
    PostBankSimPaymentReponse bankResponse = bankSimulatorClient.processPayment(bankRequest);
    PostPaymentResponse paymentResponse = new PostPaymentResponse();
    paymentResponse.setId(UUID.randomUUID());

    if (bankResponse.getAuthorization_code().isEmpty() && !bankResponse.isAuthorized()) {
      paymentResponse.setStatus(PaymentStatus.DECLINED);
      return paymentResponse;
    }

    paymentResponse = Mapper.mapTo(paymentRequest, PostPaymentResponse.class);
    paymentResponse.setId(paymentResponse.getId());
    paymentResponse.setStatus(PaymentStatus.AUTHORIZED);
    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }
}
