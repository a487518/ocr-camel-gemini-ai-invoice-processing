package org.aadvika.ocr_using_apache_camel.processor;

import org.aadvika.ocr_using_apache_camel.model.GeminiResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GeminiResponseProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        GeminiResponse response = exchange.getIn().getBody(GeminiResponse.class);
        if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
            GeminiResponse.Candidate candidate = response.candidates().get(0);
            if (candidate.content() != null && candidate.content().parts() != null
                    && !candidate.content().parts().isEmpty()) {
                String text = candidate.content().parts().get(0).text();
                // Clean up the text if it contains markdown code blocks
                if (text.startsWith("```json")) {
                    text = text.substring(7);
                }
                if (text.startsWith("```")) {
                    text = text.substring(3);
                }
                if (text.endsWith("```")) {
                    text = text.substring(0, text.length() - 3);
                }

                ObjectMapper mapper = new ObjectMapper();
                Object jsonObject = mapper.readValue(text, Object.class);
                exchange.getIn().setBody(jsonObject);
            }
        }
    }
}
