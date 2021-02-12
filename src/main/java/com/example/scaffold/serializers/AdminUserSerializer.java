package com.example.scaffold.serializers;

import com.example.scaffold.models.AdminRole;
import com.example.scaffold.models.AdminUser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@JsonComponent
public class AdminUserSerializer extends JsonSerializer<AdminUser> {
    @Override
    public void serialize(AdminUser adminUser, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", adminUser.getId());
        jsonGenerator.writeStringField("username", adminUser.getUsername());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonGenerator.writeStringField("created_at", simpleDateFormat.format(adminUser.getCreatedAt()));
        jsonGenerator.writeStringField("updated_at", simpleDateFormat.format(adminUser.getUpdatedAt()));

        Set<AdminRole> roles = new HashSet<>();
        for (AdminRole adminRole : adminUser.getRoles()) {
            AdminRole role = new AdminRole(adminRole.getName());
            role.setId(adminRole.getId());
            roles.add(role);
        }

        jsonGenerator.writeObjectField("roles", roles);

        jsonGenerator.writeEndObject();
    }
}
