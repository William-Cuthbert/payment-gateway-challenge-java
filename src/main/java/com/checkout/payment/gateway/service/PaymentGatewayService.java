package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentReponse;
import com.checkout.payment.gateway.model.PostBankSimulatorPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.UUID;
import com.checkout.payment.gateway.util.PaymentRequestValidator;
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
  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankSimulatorClient bankSimulatorClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankSimulatorClient = bankSimulatorClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    if (paymentRequest == null) {
      LOG.error("Received null payment request");
      throw new IllegalArgumentException("Payment request cannot be null");
    }

    LOG.info("Start processing payment: {}", paymentRequest);

    if (!PaymentRequestValidator.isPaymentRequestValid(paymentRequest)) {
      LOG.info("Payment request REJECTED due to invalid information: {}", paymentRequest);
      return createRejectedResponse();
    }

    PostBankSimulatorPaymentRequest bankRequest = Mapper.mapTo(paymentRequest, PostBankSimulatorPaymentRequest.class);
    LOG.info("Sending payment to the Bank");

    PostBankSimulatorPaymentReponse bankResponse;
    try {
      bankResponse = bankSimulatorClient.processPayment(bankRequest);
      LOG.info("Retrieved response from Bank: {}", bankResponse);
    } catch (Exception e) {
      LOG.error("Failed to process payment with Bank: {}", e.getMessage());
      throw new EventProcessingException("Bank processing failed");
    }

    return createPaymentResponse(paymentRequest, bankResponse);
  }

  private PostPaymentResponse createRejectedResponse() {
    PostPaymentResponse rejectedResponse = new PostPaymentResponse();
    rejectedResponse.setId(UUID.randomUUID());
    rejectedResponse.setStatus(PaymentStatus.REJECTED);
    return rejectedResponse;
  }

  private PostPaymentResponse createPaymentResponse(PostPaymentRequest paymentRequest, PostBankSimulatorPaymentReponse bankResponse) {
    PostPaymentResponse paymentResponse = new PostPaymentResponse();
    UUID id = UUID.randomUUID();
    paymentResponse.setId(id);

    if (bankResponse.getAuthorization_code().isEmpty() && !bankResponse.isAuthorized()) {
      paymentResponse.setStatus(PaymentStatus.DECLINED);
      LOG.info("Payment DECLINED for request: {}", paymentRequest);
      return paymentResponse;
    }

    paymentResponse = Mapper.mapTo(paymentRequest, PostPaymentResponse.class);
    paymentResponse.setId(id);
    paymentResponse.setStatus(PaymentStatus.AUTHORIZED);
    paymentsRepository.add(paymentResponse);
    LOG.info("Payment AUTHORIZED and added to repository with ID {}", id);
    return paymentResponse;
  }
}
