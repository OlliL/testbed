package net.salatschuessel.testbed;

import java.util.List;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.infinispan.spring.starter.embedded.actuator.InfinispanCacheMeterBinderProvider;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class BeanProducer {
	private final CacheManager cacheManager;
	private final MeterRegistry meterRegistry;

	public BeanProducer(final CacheManager cacheManager, final MeterRegistry meterRegistry) {
		this.cacheManager = cacheManager;
		this.meterRegistry = meterRegistry;

		if (this.cacheManager instanceof final SpringEmbeddedCacheManager springEmbeddedCacheManager) {
			springEmbeddedCacheManager.getNativeCacheManager().defineConfiguration("testCache",
					new ConfigurationBuilder().statistics().enable().build());
			final CacheMetricsRegistrar cacheMetricsRegistrar = new CacheMetricsRegistrar(this.meterRegistry,
					List.of(new InfinispanCacheMeterBinderProvider()));

			for (final var cacheName : cacheManager.getCacheNames()) {
				System.out.println("Register cache " + cacheName);
				cacheMetricsRegistrar.bindCacheToRegistry(this.cacheManager.getCache(cacheName));
			}

			System.out.println(meterRegistry.getMeters());
		}

	}

//	@Bean
//	public JmsListenerContainerFactory<?> myFactory(final ConnectionFactory connectionFactory,
//			final DefaultJmsListenerContainerFactoryConfigurer configurer) {
//		final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//		// This provides all auto-configured defaults to this factory, including the
//		// message converter
//		configurer.configure(factory, connectionFactory);
//		// You could still override some settings if necessary.
//		return factory;
//	}
//
//	@Bean // Serialize message content to json using TextMessage public
//	MessageConverter jacksonJmsMessageConverter() {
//		final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//		converter.setTargetType(MessageType.TEXT);
//		converter.setTypeIdPropertyName("_type");
//		return converter;
//	}

//	@Bean
//	public Jaxb2Marshaller jaxb2Marshaller() {
//		final var marshaller = new Jaxb2Marshaller();
//		marshaller.setClassesToBeBound(Email.class);
//		return marshaller;
//	}
//
//	@Bean
//	public MessageConverter xmlMessageConverter(final Jaxb2Marshaller marshaller) {
//		final MarshallingMessageConverter converter = new MarshallingMessageConverter();
//		converter.setMarshaller(marshaller);
//		converter.setUnmarshaller(marshaller);
//
//		return converter;
//	}
//	@Bean(name = "jmsExecutor")
//	public Executor jmsExecutor() {
//		final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//		threadPoolTaskExecutor.setCorePoolSize(5);
//		threadPoolTaskExecutor.setMaxPoolSize(10);
////		threadPoolTaskExecutor.setQueueCapacity(20000);
//		threadPoolTaskExecutor.initialize();
//		return threadPoolTaskExecutor;
//	}

//	@Bean(name = "testCache")
//	public org.infinispan.configuration.cache.Configuration smallCache() {
//		return new ConfigurationBuilder().statistics().enable().build();
//	}
}