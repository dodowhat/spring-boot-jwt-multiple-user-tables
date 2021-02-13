package com.example.scaffold.models;

import com.example.scaffold.serializers.AdminActionSerializer;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "admin_actions")
@JsonSerialize(using = AdminActionSerializer.class)
public class AdminAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(unique = true)
    private String action;

    private String name;

    @ManyToMany(mappedBy = "actions")
    private Set<AdminRole> roles;

    @ManyToOne()
    @JoinColumn(name = "admin_action_group_id")
    private AdminActionGroup group;

    public AdminAction() {}

    public AdminAction(String action, String name) {
        this.action = action;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public Set<AdminRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AdminRole> roles) {
        this.roles = roles;
    }

    public AdminActionGroup getGroup() {
        return group;
    }

    public void setGroup(AdminActionGroup group) {
        this.group = group;
    }
}
