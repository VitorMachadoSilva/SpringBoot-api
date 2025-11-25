package com.vitor.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.vitor.demo.models.User;
import com.vitor.demo.projections.UserProjection;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Projection para retornar apenas dados seguros
    @Query("SELECT u.id as id, u.username as username, u.email as email, u.ativo as ativo FROM User u WHERE u.id = :id")
    Optional<UserProjection> findSafeById(@Param("id") Long id);
    
    @Query("SELECT u.id as id, u.username as username, u.email as email, u.ativo as ativo FROM User u")
    List<UserProjection> findAllSafe();
}