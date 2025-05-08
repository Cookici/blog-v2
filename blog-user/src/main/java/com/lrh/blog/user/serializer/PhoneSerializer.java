package com.lrh.blog.user.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PhoneSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String phone, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        String encrypted = SensitiveUtils.encryptPhone(phone);
        jsonGenerator.writeString(encrypted);
    }

    public static class SensitiveUtils {
        public static String encryptPhone(String phone) {
            if (phone != null && phone.length() == 11) {
                return phone.substring(0, 3) + "****" + phone.substring(7);
            }
            return phone;
        }
    }
}
