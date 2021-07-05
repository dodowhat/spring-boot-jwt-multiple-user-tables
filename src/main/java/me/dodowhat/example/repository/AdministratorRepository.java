package me.dodowhat.example.repository;

import me.dodowhat.example.model.Administrator;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends PagingAndSortingRepository<Administrator, Long> {
    Optional<Administrator> findByUsername(String username);
    boolean existsByUsername(String username);
}