package com.vitor.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.vitor.demo.repositories.UserRepository;
import com.vitor.demo.models.User;
import com.vitor.demo.security.ProfileEnum;
import com.vitor.demo.security.UserSpringSecurity;
import com.vitor.demo.handlers.ResourceNotFoundException;
import com.vitor.demo.handlers.BusinessException;
import com.vitor.demo.handlers.AuthorizationException;
import com.vitor.demo.dto.UserCreateDTO;
import com.vitor.demo.dto.UserUpdateDTO;
import com.vitor.demo.dto.UserResponseDTO;
import com.vitor.demo.projections.UserProjection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ========== MÉTODOS DE AUTORIZAÇÃO ==========

    public static UserSpringSecurity authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserSpringSecurity) {
                return (UserSpringSecurity) authentication.getPrincipal();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void validateSelfOrAdmin(Long userId) {
        UserSpringSecurity user = authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso negado: usuário não autenticado");
        }
        
        // Admin tem acesso a tudo
        if (user.hasRole(ProfileEnum.ADMIN)) {
            return;
        }
        
        // Usuário comum só pode acessar seus próprios dados
        if (!user.getId().equals(userId)) {
            throw new AuthorizationException("Acesso negado: você só pode acessar seus próprios dados");
        }
    }

    public static boolean isAdmin() {
        UserSpringSecurity user = authenticated();
        return user != null && user.hasRole(ProfileEnum.ADMIN);
    }

    public static Long getCurrentUserId() {
        UserSpringSecurity user = authenticated();
        if (user == null) {
            throw new AuthorizationException("Usuário não autenticado");
        }
        return user.getId();
    }

    // ========== MÉTODOS CRUD COM AUTORIZAÇÃO ==========

    public User findById(Long id) {
        // Validar se usuário pode acessar estes dados
        validateSelfOrAdmin(id);
        
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com username: " + username));
    }

    @Transactional
    public UserResponseDTO create(UserCreateDTO userDTO) {
        // Verifica se username já existe
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("Username já cadastrado: " + userDTO.getUsername());
        }
        
        // Verifica se email já existe
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + userDTO.getEmail());
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setAtivo(true);
        user.addProfile(ProfileEnum.USER);
        
        try {
            User savedUser = userRepository.save(user);
            return toResponseDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao criar usuário. Verifique os dados fornecidos.");
        }
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO userDTO) {
        // Validar se usuário pode atualizar estes dados
        validateSelfOrAdmin(id);
        
        User user = findById(id);
        
        // Verifica se username foi alterado e se já existe
        if (!user.getUsername().equals(userDTO.getUsername()) && 
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("Username já cadastrado: " + userDTO.getUsername());
        }
        
        // Verifica se email foi alterado e se já existe
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + userDTO.getEmail());
        }
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        
        // Atualiza senha apenas se for fornecida
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        try {
            User updatedUser = userRepository.save(user);
            return toResponseDTO(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erro ao atualizar usuário. Verifique os dados fornecidos.");
        }
    }

    public void delete(Long id) {
        // Validar se usuário pode excluir estes dados
        validateSelfOrAdmin(id);
        
        User user = findById(id);
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível excluir o usuário " + user.getUsername() + " pois existem registros vinculados.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    // ========== MÉTODOS COM DTOs E PROJECTIONS ==========

    public UserResponseDTO findByIdSafe(Long id) {
        // Validar se usuário pode acessar estes dados
        validateSelfOrAdmin(id);
        
        UserProjection projection = userRepository.findSafeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        
        User user = findById(id); // Para pegar os perfis
        return new UserResponseDTO(
                projection.getId(),
                projection.getUsername(),
                projection.getEmail(),
                projection.getAtivo(),
                user.getProfiles()
        );
    }

    public List<UserResponseDTO> findAllSafe() {
        // Apenas admin pode listar todos os usuários
        if (!isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem listar todos os usuários");
        }
        
        List<UserProjection> projections = userRepository.findAllSafe();
        return projections.stream()
                .map(projection -> {
                    User user = findById(projection.getId());
                    return new UserResponseDTO(
                            projection.getId(),
                            projection.getUsername(),
                            projection.getEmail(),
                            projection.getAtivo(),
                            user.getProfiles()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<User> findAll() {
        // Apenas admin pode listar todos os usuários (com dados completos)
        if (!isAdmin()) {
            throw new AuthorizationException("Acesso negado: apenas administradores podem listar todos os usuários");
        }
        
        return userRepository.findAll();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAtivo(),
                user.getProfiles()
        );
    }
}