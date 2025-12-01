package org.aadvika.ocr_using_apache_camel.route;

import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

@CamelSpringBootTest
@SpringBootTest
public class OcrRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:gemini")
    private MockEndpoint mockGemini;

    @Test
    public void testOcrRoute() throws Exception {
        // Mock the Gemini API response
        Map<String, Object> mockResponse = Map.of(
                "candidates", List.of(
                        Map.of(
                                "content", Map.of(
                                        "parts", List.of(
                                                Map.of(
                                                        "text",
                                                        "```json\n{\"invoiceNumber\": \"INV-123\", \"total\": 100.0}\n```"))))));
        String jsonResponse = new ObjectMapper().writeValueAsString(mockResponse);

        AdviceWith.adviceWith(camelContext, "ocr-route", routeBuilder -> {
            routeBuilder.replaceFromWith("direct:start");
            routeBuilder
                    .weaveByToUri(
                            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                    .replace()
                    .setBody(routeBuilder.constant(jsonResponse)) // Simulate API response body
                    .to("mock:gemini");
        });

        mockGemini.expectedMessageCount(1);

        // Send a dummy file content
        Object result = producerTemplate.requestBodyAndHeader("direct:start", "dummy content".getBytes(),
                "CamelFileName", "test.png");

        // Verify the result is parsed JSON string
        String jsonResult = camelContext.getTypeConverter().convertTo(String.class, result);
        if (jsonResult != null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> resultMap = mapper.readValue(jsonResult, Map.class);

            if ("INV-123".equals(resultMap.get("invoiceNumber"))
                    && Double.valueOf(100.0).equals(resultMap.get("total"))) {
                System.out.println("Test Passed: JSON parsed successfully");
            } else {
                throw new AssertionError("JSON parsing failed: " + resultMap);
            }
        } else {
            throw new AssertionError("Result could not be converted to String: " + result.getClass().getName());
        }

        mockGemini.assertIsSatisfied();
    }
}
