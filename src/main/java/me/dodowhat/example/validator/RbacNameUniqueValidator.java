package me.dodowhat.example.validator;

import me.dodowhat.example.repository.AdministratorRepository;
import org.casbin.jcasbin.main.Enforcer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static me.dodowhat.example.config.security.RbacConstants.IMPLICIT_USER;

public class RbacNameUniqueValidator implements ConstraintValidator<RbacNameUnique, String> {

    private final AdministratorRepository administratorRepository;
    private final Enforcer enforcer;

    public RbacNameUniqueValidator(AdministratorRepository administratorRepository,
                                   Enforcer enforcer) {
        this.administratorRepository = administratorRepository;
        this.enforcer = enforcer;
    }

    @Override
    public void initialize(RbacNameUnique constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return !administratorRepository.existsByUsername(name) && !enforcer.hasRoleForUser(IMPLICIT_USER, name);
    }

}
