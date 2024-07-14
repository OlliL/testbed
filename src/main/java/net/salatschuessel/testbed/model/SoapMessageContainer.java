package net.salatschuessel.testbed.model;

import java.time.Instant;

import javax.xml.transform.Source;

import com.sun.xml.ws.api.message.Packet;

import jakarta.xml.soap.SOAPMessage;

/**
 * https://stackoverflow.com/questions/78745119/performance-soaphandler-handlemessage/78745247#78745247
 * 
 * 
 * SELECT count(*), direction, length(body) body_length from remote_service_log
 * group by direction, body;
 */
public record SoapMessageContainer(Instant timestamp, SOAPMessage message, Source source, Packet packet,
		boolean isOutbound) {
};
