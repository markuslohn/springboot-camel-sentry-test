package com.esentri.integration;

import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SentryDefaultErrorHandlerRoute extends RouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(SentryDefaultErrorHandlerRoute.class);

    @Override
    public void configure() throws Exception {
	onException(java.io.FileNotFoundException.class).handled(true).process(new Processor() {
	    public void process(Exchange exchange) throws Exception {
		Message originMsg = exchange.getUnitOfWork().getOriginalInMessage();
		Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

		StringBuilder sb = new StringBuilder();
		sb.append("ERROR:\n");
		sb.append(ex.getMessage());
		sb.append("\nPayload:\n");
		sb.append(originMsg.getBody());

		logger.error(sb.toString(), ex);
	    }
	});

	restConfiguration().apiContextPath("api-docs").apiProperty("api.title", "sentri.io integration test")
		.bindingMode(RestBindingMode.json);

	rest("/defaulthandler").get("{type}").to("direct:defaulthandler");

	from("direct:defaulthandler").routeId("default-route").log(LoggingLevel.INFO, "${body}")
		.process(new Processor() {
		    public void process(Exchange exchange) throws Exception {
			MDC.put("Kundennummer", exchange.getExchangeId());
		    }

		}).choice().when(header("type").isEqualTo("error")).process(new Processor() {
		    public void process(Exchange exchange) throws Exception {
			throw new FileNotFoundException("Could not find test.txt.");
		    }

		}).otherwise().transform().constant("Yea, no exception raised.");

    }

}
