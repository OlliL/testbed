package net.salatschuessel.testbed.webservice;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.sun.xml.ws.api.message.Packet;

//import com.sun.xml.ws.handler.SOAPMessageContextImpl;

import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import net.salatschuessel.testbed.BlockingQueueHolder;
import net.salatschuessel.testbed.model.SoapMessageContainer;

public class LoggingSOAPReflectionHandler implements SOAPHandler<SOAPMessageContext> {

	private final BlockingQueueHolder blockingQueueHandler;
	private static final Logger LOGGER = Logger.getLogger(LoggingSOAPReflectionHandler.class.getName());

	public LoggingSOAPReflectionHandler(final BlockingQueueHolder blockingQueueHandler) {
		this.blockingQueueHandler = blockingQueueHandler;
	}

	@Override
	public boolean handleMessage(final SOAPMessageContext context) {
		try {
			final Field f = context.getClass().getSuperclass().getDeclaredField("packet");
			f.setAccessible(true);
			final Packet packet = (Packet) f.get(context);
			final Packet clonedPacket = packet.copy(true);

			if (!this.blockingQueueHandler
					.offer(new SoapMessageContainer(Instant.now(), null, null, clonedPacket,
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
