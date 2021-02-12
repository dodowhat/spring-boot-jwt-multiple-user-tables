package com.example.scaffold.request;

import java.util.Set;

public class AdminRoleAssignActionsRequestBody {
    private Set<Long> actionIds;

    public Set<Long> getActionIds() {
        return actionIds;
    }
}
