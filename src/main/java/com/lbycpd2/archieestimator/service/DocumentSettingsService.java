package com.lbycpd2.archieestimator.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lbycpd2.archieestimator.jackson.deserializer.ParametersDeserializer;
import com.lbycpd2.archieestimator.jackson.serializer.ParametersSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Getter
@Setter
public class DocumentSettingsService {
    @JsonProperty
    private Map<String, Object> parameters = new HashMap<>();
    private File documentSettings;

    // Singleton instance with lazy initialization
    private static DocumentSettingsService instance;

    private DocumentSettingsService() {
        documentSettings = new File("documentSettings.json");
        try {
            if (!documentSettings.exists()) {
                createJSONSettings(); // Create the JSON file if it doesn't exist
            } else {
                readJSONSettings(); // Load settings from the JSON file
            }
        } catch (IOException e) {
            log.error("Error initializing DocumentSettings: {}", e.getMessage());
        }
    }

    // Lazy initialization method for Singleton
    public static DocumentSettingsService getInstance() {
        if (instance == null) {
            synchronized (DocumentSettingsService.class) { // Thread-safe initialization
                if (instance == null) {
                    instance = new DocumentSettingsService();
                }
            }
        }
        return instance;
    }

    // Initialize default parameters
    private void initializeParameters() {
        parameters.put("REPORT_LOCALE", new Locale("en","PH"));
        File file = new File(String.valueOf(getClass().getResource("logo.png")));
        if (!file.exists()) {log.info("LOGO NOT FOUND ;-;");} else {log.info("LOGO FOUND ^v^");}
        parameters.put("LOGO_IMAGE_PATH", Objects.requireNonNull(getClass().getResource("logo.png")).getPath());
        parameters.put("HEADER_COMPANY_NAME", "Archi Magnet, Inc.");
        parameters.put("HEADER_COMPANY_ADDRESS", "25D 2F Zeta II Bldg. 191 Salcedo St., Legaspi Village, San Lorenzo 1223, City of Makati, NCR, Fourth District Philippines");
        parameters.put("HEADER_COMPANY_TELEPHONE", "720-8798 * 882-1075");
        parameters.put("HEADER_COMPANY_MOBILE", "(0917) 3152398 * (0998) 9941627");
        parameters.put("HEADER_COMPANY_TIN", "009-588-719-000");
        parameters.put("HEADER_REFERENCE_NUMBER", "000001");
        parameters.put("HEADER_SUBJECT", "Fit Out Cost Proposal");
        parameters.put("HEADER_SUBJECT_LOCATION", "Fit Out Location");
        parameters.put("HEADER_CLIENT_ORGANIZATION", "CLIENT ORGANIZATION");
        parameters.put("HEADER_CLIENT_ADDRESS", "CLIENT ADDRESS");
        parameters.put("HEADER_CLIENT_NAME", "MR/MS CLIENT");
        parameters.put("HEADER_CLIENT_POSITION", "CLIENT POSITION");
        parameters.put("HEADER_GREETINGS", "Dear Mr/Ms Client,");
        parameters.put("HEADER_PARAGRAPH", "I am pleased to submit our formal quotation, prepared in accordance with our recent discussions. The attached document provides a comprehensive breakdown of the scope of work, pricing, and terms for your review");
        parameters.put("SUMMARY_VALUE_ADDED_TAX", new BigDecimal("0.12")); // Using BigDecimal for documentVAT
        parameters.put("CONFORME_BANK_NAME","ASIA UNITED BANK");
        parameters.put("CONFORME_BANK_ADDRESS","G/F VERNIDA 1 AMORSOLO ST., LEGASPI VILL., MAKATI CITY");
        parameters.put("CONFORME_BANK_ACCOUNT_NAME","ARCHI MAGNET, INC.");
        parameters.put("CONFORME_BANK_ACCOUNT_NUMBER","052-01-000103-8");
        parameters.put("CONFORME_BANK_SWIFT_CODE","AUBKPHMM");
        parameters.put("CONFORME_EXCLUSIONS", "* Office equipment, appliances, LAN, WiFi, Server switches, PMS of existing Air Condition Units");
        parameters.put("CONFORME_REMARKS", "* Any works not mentioned on this quotation will be considered variation order and shall be charged accordingly");
        parameters.put("CONFORME_TERMS_OF_PAYMENT", "30% Down payment; 2x 30% Progress Billing; 10% Balance upon job completion.");
        parameters.put("CONFORME_JOB_COMPLETION", "Within 60 days upon receipt of Conforme and Down payment.");
        parameters.put("CONFORME_VALIDITY", "This offer is valid for 30 days from date hereon.");
        parameters.put("CONFORME_WARRANTY", "All works are covered within one (1) year of any defects from date completed.");
        parameters.put("CONFORME_PREPARER_NAME", "RUBEN 'Jhun' BINALLA, JR.");
        parameters.put("CONFORME_PREPARER_POSITION", "President");
    }

    // Create JSON file with default settings
    private void createJSONSettings() throws IOException {
        ObjectMapper objectMapper = createObjectMapper();
        initializeParameters();
        objectMapper.writeValue(documentSettings, parameters);
        log.info("Document Settings JSON file created successfully!");
    }

    // Read settings from JSON file
    private void readJSONSettings() throws IOException {
        ObjectMapper objectMapper = createObjectMapper();

        Map<String, Object> loadedParameters = objectMapper.readValue(documentSettings, HashMap.class);

        parameters.clear();
        parameters.putAll(loadedParameters);
        log.info("Document Settings JSON file read successfully!");
    }

    // Retrieve a specific setting
    public Object getSetting(String key) {
        return parameters.getOrDefault(key, "Setting not found");
    }

    // Read all settings
    public void readSettings() {
        parameters.forEach((key, value) -> log.debug("{}: {}", key, value));
    }

    public void updateSetting(String key, Object value) {
        if (parameters.containsKey(key)) {
            parameters.put(key, value); // Update the parameter value
            log.info("Updated setting: {} -> {}", key, value);
            try {
                // Save updated parameters to the JSON file
                ObjectMapper objectMapper = createObjectMapper();
                objectMapper.writeValue(documentSettings, parameters);
                log.info("JSON file updated successfully!");
            } catch (IOException e) {
                log.error("Failed to update JSON file: {}", e.getMessage());
            }
        } else {
            log.warn("Setting key '{}' not found. Update failed.", key);
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(new ParametersSerializer());
        module.addDeserializer(Object.class, new ParametersDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
