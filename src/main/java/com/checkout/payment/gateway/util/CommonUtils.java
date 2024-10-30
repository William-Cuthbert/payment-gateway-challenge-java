package com.checkout.payment.gateway.util;

public class CommonUtils {
  public static final long DECLINED_CARD_NUMBER = 2222405343248112L;
  public static final String AUTHORIZATION_CODE = "0bb07405-6d44-4b50-a14f-7ae0beff13ad";
  public static final long AUTHORIZED_CARD_NUMBER = 2222405343248877L;
  public static final int AUTHORIZED_LAST_FOUR_DIGIT_CARD_NUMBER = 8877;
  public static final int AUTHORIZED_CVV_NUMBER = 123;
  public static final int AUTHORIZED_EXPIRY_MONTH_NUMBER = 4;
  public static final int AUTHORIZED_EXPIRY_YEAR_NUMBER = 2025;
  public static final int AUTHORIZED_AMOUNT = 100;
  public static final int DECLINED_AMOUNT = 60000;
  public static final String INVALID_ID_MESSAGE = "Invalid ID";
  public static final String EXPIRY_DATE = "expiry_date";
  public static final String EXPIRY_MONTH = "expiryMonth";
  public static final String EXPIRY_YEAR = "expiryYear";
  public static final String LAST_FOUR_DIGIT_CARD_NUMBER = "cardNumberLastFour";
  public static final String EXPIRY_DATE_FORMAT = "%02d/%d";
}
