package org.aadvika.ocr_using_apache_camel.route;

import org.aadvika.ocr_using_apache_camel.config.GeminiConfiguration;
import org.aadvika.ocr_using_apache_camel.model.GeminiResponse;
import org.aadvika.ocr_using_apache_camel.processor.GeminiRequestProcessor;
import org.aadvika.ocr_using_apache_camel.processor.GeminiResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OcrRoute extends RouteBuilder {

    @Autowired
    private GeminiRequestProcessor geminiRequestProcessor;

    @Autowired
    private GeminiResponseProcessor geminiResponseProcessor;

    @Autowired
    private GeminiConfiguration geminiConfiguration;

    @Override
    public void configure() throws Exception {
        from("file:inputs?noop=true")
                .routeId("ocr-route")
                .log("Reading file: ${header.CamelFileName}")
                .process(geminiRequestProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant(geminiConfiguration.getResponseMimeTypeJson()))
                .setHeader(geminiConfiguration.getApiKeyHeader(), constant(geminiConfiguration.getApiKeyValue()))
                .to(geminiConfiguration.getApiUrl())
                .unmarshal().json(JsonLibrary.Jackson, GeminiResponse.class)
                .process(geminiResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .log("Response: ${body}");
    }
}
