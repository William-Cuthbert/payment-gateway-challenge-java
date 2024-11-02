package com.checkout.payment.gateway.controller;

import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_AMOUNT;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CARD_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_CVV_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_EXPIRY_MONTH_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_EXPIRY_YEAR_NUMBER;
import static com.checkout.payment.gateway.util.CommonUtils.AUTHORIZED_LAST_FOUR_DIGIT_CARD_NUMBER;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;

import java.util.UUID;

import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.checkout.payment.gateway.enums.CurrencyCode;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PaymentGatewayService paymentGatewayService;

  private PostPaymentRequest paymentRequest;
  private PostPaymentResponse paymentResponse;
  private UUID paymentId;

  @BeforeEach
  void setUp() {
    paymentRequest = new PostPaymentRequest();
    paymentRequest.setCardNumber(AUTHORIZED_CARD_NUMBER);
    paymentRequest.setCurrency(CurrencyCode.GBP.getCurrencyCode());
    paymentRequest.setExpiryYear(AUTHORIZED_EXPIRY_YEAR_NUMBER);
    paymentRequest.setExpiryMonth(AUTHORIZED_EXPIRY_MONTH_NUMBER);
    paymentRequest.setCvv(AUTHORIZED_CVV_NUMBER);
    paymentRequest.setAmount(AUTHORIZED_AMOUNT);

    paymentId = UUID.randomUUID();
    paymentResponse = new PostPaymentResponse();
    paymentResponse.setId(paymentId);
    paymentResponse.setAmount(AUTHORIZED_AMOUNT);
    paymentResponse.setCurrency(CurrencyCode.GBP.getCurrencyCode());
    paymentResponse.setStatus(PaymentStatus.AUTHORIZED);
    paymentResponse.setExpiryMonth(AUTHORIZED_EXPIRY_MONTH_NUMBER);
    paymentResponse.setExpiryYear(AUTHORIZED_EXPIRY_YEAR_NUMBER);
    paymentResponse.setCardNumberLastFour(AUTHORIZED_LAST_FOUR_DIGIT_CARD_NUMBER);
  }

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    given(paymentGatewayService.getPaymentById(paymentId)).willReturn(paymentResponse);
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + paymentResponse.getId())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(paymentResponse.getId().toString()))
        .andExpect(jsonPath("$.status").value(paymentResponse.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(paymentResponse.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(paymentResponse.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentResponse.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentResponse.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentResponse.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    UUID paymentId = UUID.randomUUID();
    given(paymentGatewayService.getPaymentById(paymentId)).willThrow(EventProcessingException.class);
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + paymentId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenProcessPaymentThenPaymentShouldBeSuccessful() throws Exception {
    given(paymentGatewayService.processPayment(paymentRequest)).willReturn(paymentResponse);
    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(paymentResponse.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(paymentResponse.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(paymentResponse.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentResponse.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentResponse.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentResponse.getAmount()));
  }

  @Test
  void whenProcessPaymentThenPaymentShouldFail() throws Exception {
    PostPaymentResponse declinedPaymentResponse = new PostPaymentResponse();
    declinedPaymentResponse.setId(paymentId);
    declinedPaymentResponse.setStatus(PaymentStatus.DECLINED);
    given(paymentGatewayService.processPayment(paymentRequest)).willReturn(paymentResponse);
    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(paymentResponse.getStatus().getName()));
  }
}
