package net.salatschuessel.testbed.webservice;

import java.time.Instant;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

//import com.sun.xml.ws.handler.SOAPMessageContextImpl;

import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import net.salatschuessel.testbed.BlockingQueueHolder;
import net.salatschuessel.testbed.model.SoapMessageContainer;

public class LoggingSOAPHandler implements SOAPHandler<SOAPMessageContext> {

	private final BlockingQueueHolder blockingQueueHandler;
	private static final Logger LOGGER = Logger.getLogger(LoggingSOAPHandler.class.getName());

	public LoggingSOAPHandler(final BlockingQueueHolder blockingQueueHandler) {
		this.blockingQueueHandler = blockingQueueHandler;
	}

	@Override
	public boolean handleMessage(final SOAPMessageContext context) {
		try {
			if (!this.blockingQueueHandler
					.offer(new SoapMessageContainer(Instant.now(), context.getMessage(), null, null,
							Boolean.TRUE.equals(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))))) {
				// LOGGER.severe("Queue is full, Dropping SOAP Message...");
			}
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Exception", e);
		}
		return true;
	}

	@Override
	public boolean handleFault(final SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(final MessageContext context) {

	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}
