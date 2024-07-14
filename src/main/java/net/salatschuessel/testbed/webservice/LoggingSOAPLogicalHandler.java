package net.salatschuessel.testbed.webservice;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.ws.handler.LogicalHandler;
import jakarta.xml.ws.handler.LogicalMessageContext;

//import com.sun.xml.ws.handler.SOAPMessageContextImpl;

import jakarta.xml.ws.handler.MessageContext;
import net.salatschuessel.testbed.BlockingQueueHolder;
import net.salatschuessel.testbed.model.SoapMessageContainer;

public class LoggingSOAPLogicalHandler implements LogicalHandler<LogicalMessageContext> {

	private final BlockingQueueHolder blockingQueueHandler;
	private static final Logger LOGGER = Logger.getLogger(LoggingSOAPLogicalHandler.class.getName());

	public LoggingSOAPLogicalHandler(final BlockingQueueHolder blockingQueueHandler) {
		this.blockingQueueHandler = blockingQueueHandler;
	}

	@Override
	public boolean handleMessage(final LogicalMessageContext context) {
		try {
			if (!this.blockingQueueHandler
					.offer(new SoapMessageContainer(Instant.now(), null, context.getMessage().getPayload(), null,
							Boolean.TRUE.equals(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))))) {
				// LOGGER.severe("Queue is full, Dropping SOAP Message...");
			}
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Exception", e);
		}
		return true;
	}

	@Override
	public boolean handleFault(final LogicalMessageContext context) {
		return false;
	}

	@Override
	public void close(final MessageContext context) {

	}
}
