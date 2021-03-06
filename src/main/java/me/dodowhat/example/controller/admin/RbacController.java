package me.dodowhat.example.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.dodowhat.example.config.exception.BadRequestException;
import me.dodowhat.example.config.exception.ForbiddenException;
import me.dodowhat.example.config.exception.NotFoundException;
import me.dodowhat.example.config.exception.UnprocessableEntityException;
import me.dodowhat.example.dto.admin.rbac.RoleDTO;
import me.dodowhat.example.model.Administrator;
import me.dodowhat.example.repository.AdministratorRepository;
import me.dodowhat.example.service.admin.RbacService;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.dodowhat.example.config.security.RbacConstants.IMPLICIT_USER;
import static me.dodowhat.example.config.security.RbacConstants.SUPER_ROLE;

@Api(tags = "RBAC")
@RestController
@RequestMapping(value = "/admin/rbac", produces = "application/json")
public class RbacController {

    @Value("${server.port}")
    private int port;
    private final Enforcer enforcer;
    private final AdministratorRepository administratorRepository;
    private final RbacService rbacService;

    public RbacController(
            RestTemplateBuilder restTemplateBuilder,
            Enforcer enforcer,
            AdministratorRepository administratorRepository,
            RbacService rbacService
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.enforcer = enforcer;
        this.administratorRepository = administratorRepository;
        this.rbacService = rbacService;
    }

    @ApiOperation("permission list")
    @GetMapping("/permissions")
    public ResponseEntity<String> permissions() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(rbacService.getPermissions());
        return ResponseEntity.ok(rbacService.getPermissions());
    }

    @ApiOperation(value = "assigning permissions for role", notes = "Request body format is same as permissions")
    @PutMapping("/roles/{name}/permissions")
    public void assignPermissionsForRole(@PathVariable String name, @RequestBody JsonNode requestNode)
            throws JsonProcessingException,
            NotFoundException,
            BadRequestException,
            ForbiddenException
    {
        if (name.equals(SUPER_ROLE)) {
            throw new ForbiddenException("Forbidden: altering root role");
        }
        if (!enforcer.hasRoleForUser(IMPLICIT_USER, name)) {
            throw new NotFoundException("Role doesn't exist: " + name);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(rbacService.getPermissions());
        if (!requestNode.isObject()) {
            throw new BadRequestException("Invalid params");
        }
        enforcer.deletePermissionsForUser(name);
        Iterator<String> requestPaths = requestNode.fieldNames();
        while (requestPaths.hasNext()) {
            String requestPath = requestPaths.next();
            JsonNode pathNode = rootNode.get(requestPath);
            if (pathNode != null) {
                Iterator<String> requestMethods = requestNode.get(requestPath).fieldNames();
                while (requestMethods.hasNext()) {
                    String requestMethod = requestMethods.next();
                    if (pathNode.get(requestMethod) != null) {
                        enforcer.addPermissionForUser(name, requestPath, requestMethod);
                    }
                }
            }
        }
    }

    @ApiOperation("permission list for role")
    @GetMapping("/roles/{name}/permissions")
    public ResponseEntity<List<List<String>>> permissionsForRole(@PathVariable String name) {
        return ResponseEntity.ok(enforcer.getPermissionsForUser(name));
    }

    @ApiOperation("role list")
    @GetMapping("/roles")
    public ResponseEntity<HashSet<String>> roles() {
        return ResponseEntity.ok().body(new HashSet<>(enforcer.getAllRoles()));
    }

    @ApiOperation("create role")
    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> createRole(@Validated @RequestBody RoleDTO roleDTO)
            throws UnprocessableEntityException {
        if (!enforcer.addRoleForUser(IMPLICIT_USER, roleDTO.getName())) {
            throw new UnprocessableEntityException("Role already exists: " + roleDTO.getName());
        }
        return ResponseEntity.ok().body(roleDTO);
    }

    @ApiOperation("delete role")
    @DeleteMapping("/roles/{name}")
    public void destroyRole(@PathVariable String name) throws ForbiddenException {
        if (name.equals(SUPER_ROLE)) {
            throw new ForbiddenException("Forbidden: deleting root role");
        }
        enforcer.deleteRole(name);
    }

    @ApiOperation("assigning roles for administrator")
    @PutMapping("/administrators/{username}/roles")
    public void assignRolesForAdministrator(
            @ApiIgnore Authentication authentication,
            @PathVariable String username,
            @RequestBody List<String> roles
    ) throws NotFoundException, ForbiddenException, UnprocessableEntityException {
        Administrator administrator = administratorRepository.findByUsername(username)
                .orElseThrow(NotFoundException::new);
        if (administrator.getUsername().equals(authentication.getName())) {
            throw new ForbiddenException("Forbidden: altering yourself");
        }
        if (roles.size() == 0) {
            enforcer.deleteRolesForUser(administrator.getUsername());
        } else {
            List<String> allRoles = enforcer.getAllNamedRoles("g");
            Set<String> intersection = allRoles.stream().distinct().filter(roles::contains).collect(Collectors.toSet());
            if (intersection.size() > 0) {
                enforcer.deleteRolesForUser(administrator.getUsername());
                for (String role : roles) {
                    enforcer.addRoleForUser(administrator.getUsername(), role);
                }
            } else {
                throw new UnprocessableEntityException("roles do not exist");
            }
        }
    }

}
