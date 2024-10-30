package com.checkout.payment.gateway.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BankSimulatorClientTest {

  @InjectMocks
  private BankSimulatorClient bankSimulatorClient;

  @Test
  public void processPayment_Success() {

  }

  @Test
  public void processPayment_Failure() {
    
  }
}
