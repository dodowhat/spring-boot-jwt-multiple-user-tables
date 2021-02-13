package com.example.scaffold.serializers;

import com.example.scaffold.models.AdminAction;
import com.example.scaffold.models.AdminActionGroup;
import com.example.scaffold.models.AdminRole;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@JsonComponent
public class AdminActionSerializer extends JsonSerializer<AdminAction> {
    @Override
    public void serialize(AdminAction adminAction, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", adminAction.getId());
        jsonGenerator.writeStringField("name", adminAction.getName());
        jsonGenerator.writeStringField("action", adminAction.getAction());

        AdminActionGroup group = new AdminActionGroup(adminAction.getGroup().getName());
        group.setId(adminAction.getGroup().getId());
        jsonGenerator.writeObjectField("group", group);

        Set<AdminRole> roles = new HashSet<>();
        for (AdminRole adminRole : adminAction.getRoles()) {
            AdminRole role = new AdminRole(adminRole.getName());
            role.setId(adminRole.getId());
            roles.add(role);
        }

        jsonGenerator.writeObjectField("roles", roles);

        jsonGenerator.writeEndObject();
    }
}
