package com.esentri.integration;

import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SentryLoggingErrorHandlerRoute extends RouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(SentryLoggingErrorHandlerRoute.class);

    @Override
    public void configure() throws Exception {
	logger.debug("Configure route...");
	errorHandler(loggingErrorHandler());

	restConfiguration().apiContextPath("api-docs").apiProperty("api.title", "sentri.io integration test")
		.bindingMode(RestBindingMode.json);

	rest("/logginghandler").get("{type}").to("direct:logginghandler");

	from("direct:logginghandler").routeId("logging-route").log(LoggingLevel.INFO, "${body}")
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
