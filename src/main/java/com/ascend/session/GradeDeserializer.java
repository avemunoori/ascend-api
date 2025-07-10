package com.ascend.session;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class GradeDeserializer extends JsonDeserializer<Grade> {
    @Override
    public Grade deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        // Try enum name first
        try {
            return Grade.valueOf(value);
        } catch (IllegalArgumentException e) {
            // Try display value
            try {
                return Grade.fromString(value);
            } catch (IllegalArgumentException ex) {
                throw ctxt.weirdStringException(value, Grade.class, "Invalid grade: " + value);
            }
        }
    }
} 