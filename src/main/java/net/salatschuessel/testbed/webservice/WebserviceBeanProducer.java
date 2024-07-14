package net.salatschuessel.testbed.webservice;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.CountriesPortService;
import jakarta.xml.ws.BindingProvider;
import net.salatschuessel.testbed.BlockingQueueHolder;

public class WebserviceBeanProducer {

	@Bean
	public CountriesPort countriesPort(final BlockingQueueHolder blockingQueueHandler)
			throws MalformedURLException, URISyntaxException {
		final CountriesPortService countriesPortService = new CountriesPortService(
				new URI("classpath:/countries.wsdl").toURL());
		final var countriesPort = countriesPortService.getCountriesPortSoap11();
		final var binding = ((BindingProvider) countriesPort).getBinding();
		final var handlerList = binding.getHandlerChain();
//		handlerList.add(new LoggingSOAPHandler(blockingQueueHandler));
//		handlerList.add(new LoggingSOAPReflectionHandler(blockingQueueHandler));
		handlerList.add(new LoggingSOAPLogicalHandler(blockingQueueHandler));
		binding.setHandlerChain(handlerList);

		return countriesPort;
	}

	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
			final ApplicationContext applicationContext) {
		final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/ws/*");
	}

	@Bean(name = "countries")
	public DefaultWsdl11Definition defaultWsdl11Definition(final XsdSchema countriesSchema) {
		final DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CountriesPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
	}
}
