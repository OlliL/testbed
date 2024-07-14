package net.salatschuessel.testbed;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.salatschuessel.testbed.model.SoapMessageContainer;

@Component
public class BlockingQueueHolder {
	private final BlockingQueue<SoapMessageContainer> blockingQueue;
	private int maxLevel;

	@Autowired
	public BlockingQueueHolder(@Value("${testbed.queue.holder.size}") final int size,
			final MeterRegistry meterRegistry) {
		this.blockingQueue = new LinkedBlockingDeque<>(size);

		Gauge.builder("logging_queue_level_current", this.blockingQueue, BlockingQueue::size).register(meterRegistry);
		Gauge.builder("logging_queue_level_max", () -> this.maxLevel).register(meterRegistry);
	}

	public boolean offer(final SoapMessageContainer soapMessageContext) {
		return this.blockingQueue.offer(soapMessageContext);
	}

	public int drainTo(final Collection<SoapMessageContainer> c, final int maxElements) {
		if (this.blockingQueue.size() > this.maxLevel)
			this.maxLevel = this.blockingQueue.size();
		return this.blockingQueue.drainTo(c, maxElements);
	}

}
