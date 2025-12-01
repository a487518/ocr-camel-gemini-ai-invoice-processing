package org.aadvika.ocr_using_apache_camel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfiguration {

    private String apiKeyHeader;
    private String apiKeyValue;
    private String apiUrl;
    private String mimeTypeJpeg;
    private String mimeTypePng;
    private String mimeTypePdf;
    private String responseMimeTypeJson;
    private String ocrPrompt;

    public String getApiKeyHeader() {
        return apiKeyHeader;
    }

    public void setApiKeyHeader(String apiKeyHeader) {
        this.apiKeyHeader = apiKeyHeader;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getMimeTypeJpeg() {
        return mimeTypeJpeg;
    }

    public void setMimeTypeJpeg(String mimeTypeJpeg) {
        this.mimeTypeJpeg = mimeTypeJpeg;
    }

    public String getMimeTypePng() {
        return mimeTypePng;
    }

    public void setMimeTypePng(String mimeTypePng) {
        this.mimeTypePng = mimeTypePng;
    }

    public String getMimeTypePdf() {
        return mimeTypePdf;
    }

    public void setMimeTypePdf(String mimeTypePdf) {
        this.mimeTypePdf = mimeTypePdf;
    }

    public String getResponseMimeTypeJson() {
        return responseMimeTypeJson;
    }

    public void setResponseMimeTypeJson(String responseMimeTypeJson) {
        this.responseMimeTypeJson = responseMimeTypeJson;
    }

    public String getOcrPrompt() {
        return ocrPrompt;
    }

    public void setOcrPrompt(String ocrPrompt) {
        this.ocrPrompt = ocrPrompt;
    }
}
