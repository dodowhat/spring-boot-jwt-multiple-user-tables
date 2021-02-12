package com.example.scaffold.request;

import java.util.Set;

public class AdminUserAssignRolesRequestBody {
    private Set<Long> roleIds;

    public Set<Long> getRoleIds() {
        return roleIds;
    }
}
