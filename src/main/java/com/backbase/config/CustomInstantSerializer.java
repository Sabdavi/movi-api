package com.backbase.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomInstantSerializer extends StdSerializer<Instant> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneOffset.UTC);

    public CustomInstantSerializer() {
        super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(FORMATTER.format(value));
    }
}
