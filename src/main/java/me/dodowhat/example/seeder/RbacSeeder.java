package me.dodowhat.example.seeder;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static me.dodowhat.example.config.security.RbacConstants.IMPLICIT_USER;
import static me.dodowhat.example.config.security.RbacConstants.SUPER_ROLE;

@Component
public class RbacSeeder {
    private final Enforcer enforcer;

    public RbacSeeder(Enforcer enforcer) {
        this.enforcer = enforcer;
    }

    @EventListener
    public void run(ContextRefreshedEvent event) {
        enforcer.addRoleForUser(IMPLICIT_USER, SUPER_ROLE);
        enforcer.addRoleForUser(IMPLICIT_USER, "editor");
    }
}
