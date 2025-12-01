package org.aadvika.ocr_using_apache_camel.processor;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aadvika.ocr_using_apache_camel.config.GeminiConfiguration;
import org.aadvika.ocr_using_apache_camel.model.GeminiRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeminiRequestProcessor implements Processor {

    @Autowired
    private GeminiConfiguration geminiConfiguration;

    @Override
    public void process(Exchange exchange) throws Exception {
        byte[] fileContent = exchange.getIn().getBody(byte[].class);
        String base64Content = Base64.getEncoder().encodeToString(fileContent);

        String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        String mimeType = geminiConfiguration.getMimeTypeJpeg();
        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            mimeType = geminiConfiguration.getMimeTypePdf();
        } else if (fileName != null && fileName.toLowerCase().endsWith(".png")) {
            mimeType = geminiConfiguration.getMimeTypePng();
        }

        // Create parts
        GeminiRequest.InlineData inlineData = new GeminiRequest.InlineData(mimeType, base64Content);
        GeminiRequest.Part part1 = new GeminiRequest.Part(geminiConfiguration.getOcrPrompt(), null);
        GeminiRequest.Part part2 = new GeminiRequest.Part(null, inlineData);

        // Create content
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part1, part2));

        // Define schema
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("required", List.of("invoiceNumber", "invoiceDate", "billToParty", "billToAddress",
                "invoiceAmount", "taxAmount", "subTotal", "lines"));

        Map<String, Object> properties = new HashMap<>();
        properties.put("invoiceNumber", Map.of("type", "string"));
        properties.put("invoiceDate", Map.of("type", "string", "format", "date"));
        properties.put("billToParty", Map.of("type", "string"));
        properties.put("billToAddress", Map.of("type", "string"));
        properties.put("shipToParty", Map.of("type", "string"));
        properties.put("shipToAddress", Map.of("type", "string"));
        properties.put("invoiceAmount", Map.of("type", "number"));
        properties.put("taxAmount", Map.of("type", "number"));
        properties.put("subTotal", Map.of("type", "number"));
        properties.put("dueAmount", Map.of("type", "string"));

        Map<String, Object> lineItems = new HashMap<>();
        lineItems.put("type", "object");
        lineItems.put("required",
                List.of("lineNumber", "product", "quantity", "unitPrice", "lineTotal"));
        lineItems.put("properties", Map.of(
                "lineNumber", Map.of("type", "integer"),
                "product", Map.of("type", "string"),
                "quantity", Map.of("type", "number"),
                "unitPrice", Map.of("type", "number"),
                "lineTotal", Map.of("type", "number")));

        properties.put("lines", Map.of("type", "array", "items", lineItems));
        schema.put("properties", properties);

        // Create generation config
        GeminiRequest.GenerationConfig generationConfig = new GeminiRequest.GenerationConfig(
                geminiConfiguration.getResponseMimeTypeJson(), schema);

        // Create request
        GeminiRequest request = new GeminiRequest(List.of(content), generationConfig);

        exchange.getIn().setBody(request);
    }
}
