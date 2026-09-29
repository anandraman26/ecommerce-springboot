package com.ecommerce.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsService {
		@Value("${twilio.from-number}")
		private String fromNumber;
		
		public void sendSms(String to, String message) {
			Message.creator(new PhoneNumber(to), new PhoneNumber(fromNumber), message).create();
		}
}
