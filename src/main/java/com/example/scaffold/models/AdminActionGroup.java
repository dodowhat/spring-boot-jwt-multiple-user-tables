package com.example.scaffold.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
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

    @OneToMany()
    @JoinColumn(name = "admin_action_group_id")
    private Set<AdminAction> actions;

    public AdminActionGroup() {}

    public AdminActionGroup(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
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
