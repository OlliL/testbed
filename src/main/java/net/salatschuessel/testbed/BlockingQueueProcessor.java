package net.salatschuessel.testbed;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import net.salatschuessel.testbed.model.RemoteServiceLog;
import net.salatschuessel.testbed.model.SoapMessageContainer;
import net.salatschuessel.testbed.persistence.RemoteServiceLogBatchRepository;

@Component
public class BlockingQueueProcessor {

	private final BlockingQueueHolder blockingQueueHolder;
	private final List<SoapMessageContainer> soapMessageContextList;
	private final int maxTake;
	private final DistributionSummary distributionSummary;
	private final RemoteServiceLogBatchRepository repository;
	private final TransformerFactory tf;

	private static final Logger LOGGER = Logger.getLogger(BlockingQueueProcessor.class.getName());

	@Autowired
	public BlockingQueueProcessor(@Value("${testbed.queue.processor.maxTake}") final int maxTake,
			final BlockingQueueHolder blockingQueueHolder, final MeterRegistry meterRegistry,
			final RemoteServiceLogBatchRepository repository) throws TransformerConfigurationException {
		this.blockingQueueHolder = blockingQueueHolder;
		this.soapMessageContextList = new ArrayList<>(maxTake);
		this.maxTake = maxTake;
		this.repository = repository;

		this.tf = TransformerFactory.newInstance();
		this.tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		this.distributionSummary = DistributionSummary.builder("logging_queue_data_processings")
				.register(meterRegistry);
	}

	@Scheduled(fixedRateString = "${testbed.queue.processor.fixedRate}")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processData() {
		this.soapMessageContextList.clear();
		this.blockingQueueHolder.drainTo(this.soapMessageContextList, this.maxTake);
		final int size = this.soapMessageContextList.size();
		if (size > 0) {
			final Long start = System.currentTimeMillis();
			this.distributionSummary.record(size);

			this.repository.insert(this.soapMessageContextList.parallelStream()
					.map(this::getRemoteServiceLog)
					.toList());

			LOGGER.log(Level.INFO, () -> "size: %s - %sms".formatted(size, System.currentTimeMillis() - start));
		}
	}

	private RemoteServiceLog getRemoteServiceLog(final SoapMessageContainer container) {

		final StringWriter stringResult = new StringWriter();

		final String direction = container.isOutbound() ? "OUTBOUND" : "INBOUND";
		try {
			if (container.message() != null) {
				final DOMSource source = new DOMSource(container.message().getSOAPBody());
				this.tf.newTransformer().transform(source, new StreamResult(stringResult));
			} else if (container.source() != null) {
				this.tf.newTransformer().transform(container.source(), new StreamResult(stringResult));
			} else {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				container.packet().writeTo(baos);
				stringResult.write(baos.toString());
			}

			return new RemoteServiceLog(direction, stringResult.toString(), container.timestamp());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

}
