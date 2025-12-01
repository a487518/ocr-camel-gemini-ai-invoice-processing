package org.aadvika.ocr_using_apache_camel.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {

    public record Content(List<Part> parts) {
    }

    public record Part(String text, InlineData inline_data) {
    }

    public record InlineData(String mime_type, String data) {
    }

    public record GenerationConfig(String responseMimeType, Map<String, Object> responseSchema) {
    }
}
