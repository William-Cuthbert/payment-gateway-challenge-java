package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentRequest;
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
    // TODO: Need implementation on rejected status based on invalid payment request
    LOG.info("Start processing payment: {}", paymentRequest.toString());
    PostBankSimulatorPaymentRequest bankRequest = Mapper.mapTo(paymentRequest,
        PostBankSimulatorPaymentRequest.class);
    LOG.info("Sending payment to the Bank");
    PostBankSimulatorPaymentReponse bankResponse = bankSimulatorClient.processPayment(bankRequest);
    LOG.info("Retrieved response from Bank");
    PostPaymentResponse paymentResponse = new PostPaymentResponse();
    final UUID id = UUID.randomUUID();
    paymentResponse.setId(id);

    if (bankResponse.getAuthorization_code().isEmpty() && !bankResponse.isAuthorized()) {
      paymentResponse.setStatus(PaymentStatus.DECLINED);
      LOG.info("Payment got DECLINED: {}", paymentRequest);
      return paymentResponse;
    }

    paymentResponse = Mapper.mapTo(paymentRequest, PostPaymentResponse.class);
    paymentResponse.setId(id);
    paymentResponse.setStatus(PaymentStatus.AUTHORIZED);
    paymentsRepository.add(paymentResponse);
    LOG.info("Payment got AUTHORIZED: {} and now adding into paymentsRepository with ID {}",
        paymentResponse, paymentResponse.getId());
    return paymentResponse;
  }
}
