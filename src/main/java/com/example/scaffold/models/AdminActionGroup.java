package com.example.scaffold.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "admin_action_groups")
public class AdminActionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<AdminAction> actions;

    public AdminActionGroup() {}

    public AdminActionGroup(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AdminAction> getActions() {
        return actions;
    }

    public void setActions(Set<AdminAction> actions) {
        this.actions = actions;
    }
}
