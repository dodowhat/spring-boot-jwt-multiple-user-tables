package me.dodowhat.example.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.dodowhat.example.model.Administrator;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Configurable
public class AdministratorSerializer extends JsonSerializer<Administrator> {
    @Autowired
    private Enforcer enforcer;

    @Override
    public void serialize(
            Administrator value,
            JsonGenerator generator,
            SerializerProvider provider
    ) throws IOException {
        generator.writeStartObject();

        generator.writeNumberField("id", value.getId());
        generator.writeStringField("username", value.getUsername());
        generator.writeObjectField("roles", enforcer.getRolesForUser(value.getUsername()));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        generator.writeStringField("created_at", format.format(value.getCreatedAt()));
        generator.writeStringField("updated_at", format.format(value.getUpdatedAt()));

        generator.writeEndObject();
    }
}
