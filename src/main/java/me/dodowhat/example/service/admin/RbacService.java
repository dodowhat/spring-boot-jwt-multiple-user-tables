package me.dodowhat.example.service.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

import static me.dodowhat.example.config.security.SwaggerConstants.GetSwaggerPassword;
import static me.dodowhat.example.config.security.SwaggerConstants.SWAGGER_USERNAME;

@Service
public class RbacService {
    @Value("${server.port}")
    private int port;
    @Value("${springfox.documentation.openApi.v3.path}")
    private String openApiPath;
    private final RestTemplate restTemplate;

    public RbacService(
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getPermissions() throws JsonProcessingException {
        String prefix = "/admin";
        String skippedPrefix = prefix + "/auth";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(getApiDocs()).get("paths");
        if (prefix.length() == 0) {
            return mapper.writeValueAsString(rootNode);
        } else {
            ObjectNode filteredRootNode = mapper.createObjectNode();
            Iterator<String> paths = rootNode.fieldNames();
            while (paths.hasNext()) {
                String path = paths.next();
                if (path.startsWith(prefix) && !path.startsWith(skippedPrefix)) {
                    ObjectNode filteredPathNode = mapper.createObjectNode();
                    JsonNode pathNode = rootNode.get(path);
                    Iterator<String> methods = pathNode.fieldNames();
                    while (methods.hasNext()) {
                        ObjectNode filteredMethodNode = mapper.createObjectNode();
                        String method = methods.next();
                        JsonNode methodNode = pathNode.get(method);
                        String key = "tags";
                        filteredMethodNode.set(key, methodNode.get(key));
                        key = "summary";
                        filteredMethodNode.set(key, methodNode.get(key));
                        key = "description";
                        if (methodNode.get(key) != null) {
                            filteredMethodNode.set(key, methodNode.get(key));
                        }
                        filteredPathNode.set(method, filteredMethodNode);
                    }
                    filteredRootNode.set(path, filteredPathNode);
                }
            }
            return mapper.writeValueAsString(filteredRootNode);
        }
    }

    private String getApiDocs() {
        String baseUrl = "http://127.0.0.1:";
        String url = baseUrl + port + openApiPath;
        String password = GetSwaggerPassword();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(SWAGGER_USERNAME, password);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response.getBody();
    }
}
