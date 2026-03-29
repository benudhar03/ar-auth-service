package com.baseoauth.service;

import com.baseoauth.dto.*;
import com.baseoauth.entity.Permission;
import com.baseoauth.entity.Role;
import com.baseoauth.entity.UserEntity;
import com.baseoauth.repository.PermissionRepository;
import com.baseoauth.repository.RoleRepository;
import com.baseoauth.repository.UserRepository;
import com.baseoauth.resp.dto.AbstractResponse;
import com.baseoauth.resp.dto.ErrorResponse;
import com.baseoauth.resp.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	// =============================================
	// User Management Implementation
	// =============================================

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> createUser(UserModel userModel) {
		try {
			log.debug("Creating new user: {}", userModel.getUserName());

			// Check for existing user findByUserNameOrEmailOrMobileNo
			Optional<UserEntity> existingUser = userRepository.findByUserNameOrEmailOrMobileNo(
					userModel.getUserName(),
					userModel.getEmail(),
					userModel.getMobileNo());

			if (existingUser.isPresent()) {
				log.warn("User already exists: {}", userModel.getUserName());
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body(new ErrorResponse("USER_EXISTS", "User already exists with username, email, or mobile number"));
			}

			// Create new user entity
			UserEntity user = new UserEntity();
			BeanUtils.copyProperties(userModel, user);

			// Encode password
			user.setPassword(passwordEncoder.encode(userModel.getPassword()));

			// Set default values
			user.setEnabled(true);
			user.setCredentialsNonExpired(true);
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setFailedAttempts(0);
			user.setCreatedAt(LocalDateTime.now());

			// Assign roles
			if (userModel.getRoleIds() != null && !userModel.getRoleIds().isEmpty()) {
				Set<Role> roles = userModel.getRoleIds().stream()
						.map(roleId -> roleRepository.findById(roleId)
								.orElseThrow(() -> new RuntimeException("Role not found: " + roleId)))
						.collect(Collectors.toSet());
				user.setRoles(roles);
			}

			// Save user
			UserEntity savedUser = userRepository.save(user);
			log.info("User created successfully with ID: {}", savedUser.getId());

			// Convert to response model
			UserModel responseModel = convertToUserModel(savedUser);

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(new SuccessResponse("User created successfully", HttpStatus.CREATED.value(), responseModel));

		} catch (Exception e) {
			log.error("Error creating user: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("CREATION_FAILED", "Failed to create user: " + e.getMessage()));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<List<UserModel>> getAllUsers(int page, int size, String username) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("userName").ascending());
			Page<UserEntity> userPage;

			if (username != null && !username.isEmpty()) {
				userPage = userRepository.findByUserNameContainingIgnoreCase(username, pageable);
			} else {
				userPage = userRepository.findAll(pageable);
			}

			List<UserModel> users = userPage.getContent().stream()
					.map(this::convertToUserModel)
					.collect(Collectors.toList());

			return ResponseEntity.ok(users);
		} catch (Exception e) {
			log.error("Error fetching users: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<List<UserModel>> getAllUsers() {
		try {
			log.debug("Fetching all users");

			List<UserEntity> users = userRepository.findAll();
			List<UserModel> userModels = users.stream()
					.map(this::convertToUserModel)
					.collect(Collectors.toList());

			log.info("Retrieved {} users", userModels.size());
			return ResponseEntity.ok(userModels);

		} catch (Exception e) {
			log.error("Error fetching users: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<UserModel> getUserById(Long id) {
		try {
			UserEntity user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			return ResponseEntity.ok(convertToUserModel(user));
		} catch (Exception e) {
			log.error("Error fetching user by ID {}: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<UserModel> getUserByUsername(String username) {
		try {
			UserEntity user = userRepository.findByUserName(username)
					.orElseThrow(() -> new RuntimeException("User not found with username: " + username));

			return ResponseEntity.ok(convertToUserModel(user));
		} catch (Exception e) {
			log.error("Error fetching user by username {}: {}", username, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> updateUser(UserModel userModel) {
		try {
			UserEntity existingUser = userRepository.findById(userModel.getId())
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + userModel.getId()));

			// Update fields
			if (userModel.getEmail() != null) {
				existingUser.setEmail(userModel.getEmail());
			}
			if (userModel.getMobileNo() != null) {
				existingUser.setMobileNo(userModel.getMobileNo());
			}
			if (userModel.getPassword() != null && !userModel.getPassword().isEmpty()) {
				existingUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
			}

			// Update roles if provided
			if (userModel.getRoleIds() != null && !userModel.getRoleIds().isEmpty()) {
				Set<Role> roles = userModel.getRoleIds().stream()
						.map(roleId -> roleRepository.findById(roleId)
								.orElseThrow(() -> new RuntimeException("Role not found: " + roleId)))
						.collect(Collectors.toSet());
				existingUser.setRoles(roles);
			}

			existingUser.setUpdatedAt(LocalDateTime.now());
			UserEntity updatedUser = userRepository.save(existingUser);

			log.info("User updated successfully with ID: {}", updatedUser.getId());

			return ResponseEntity.ok(new SuccessResponse("User updated successfully", HttpStatus.OK.value(),
					convertToUserModel(updatedUser)));

		} catch (Exception e) {
			log.error("Error updating user: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("UPDATE_FAILED", "Failed to update user: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> deleteUser(Long id) {
		try {
			UserEntity user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			userRepository.delete(user);
			log.info("User deleted successfully with ID: {}", id);

			return ResponseEntity.ok(new SuccessResponse("User deleted successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error deleting user: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("DELETE_FAILED", "Failed to delete user: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> updateUserStatus(Long id, boolean enabled) {
		try {
			UserEntity user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			user.setEnabled(enabled);
			user.setUpdatedAt(LocalDateTime.now());
			userRepository.save(user);

			log.info("User status updated: {} - enabled: {}", id, enabled);

			return ResponseEntity.ok(new SuccessResponse(
					"User " + (enabled ? "enabled" : "disabled") + " successfully",
					HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error updating user status: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("UPDATE_FAILED", "Failed to update user status"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> resetUserPassword(Long id, PasswordResetModel passwordResetModel) {
		try {
			UserEntity user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			user.setPassword(passwordEncoder.encode(passwordResetModel.getNewPassword()));
			user.setUpdatedAt(LocalDateTime.now());
			userRepository.save(user);

			log.info("Password reset for user: {}", id);

			return ResponseEntity.ok(new SuccessResponse("Password reset successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error resetting password: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("RESET_FAILED", "Failed to reset password"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> assignRoleToUser(Long userId, Long roleId) {
		try {
			UserEntity user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

			Role role = roleRepository.findById(roleId)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

			user.getRoles().add(role);
			user.setUpdatedAt(LocalDateTime.now());
			userRepository.save(user);

			log.info("Role {} assigned to user {}", roleId, userId);

			return ResponseEntity.ok(new SuccessResponse("Role assigned successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error assigning role to user: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("ASSIGN_FAILED", "Failed to assign role to user"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> removeRoleFromUser(Long userId, Long roleId) {
		try {
			UserEntity user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

			user.getRoles().removeIf(role -> role.getId().equals(roleId));
			user.setUpdatedAt(LocalDateTime.now());
			userRepository.save(user);

			log.info("Role {} removed from user {}", roleId, userId);

			return ResponseEntity.ok(new SuccessResponse("Role removed successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error removing role from user: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("REMOVE_FAILED", "Failed to remove role from user"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> bulkCreateUsers(List<UserModel> userModels) {
		try {
			List<UserEntity> users = new ArrayList<>();
			for (UserModel userModel : userModels) {
				UserEntity user = new UserEntity();
				BeanUtils.copyProperties(userModel, user);
				user.setPassword(passwordEncoder.encode(userModel.getPassword()));
				user.setEnabled(true);
				user.setCredentialsNonExpired(true);
				user.setAccountNonExpired(true);
				user.setAccountNonLocked(true);
				users.add(user);
			}

			List<UserEntity> savedUsers = userRepository.saveAll(users);
			log.info("Bulk created {} users", savedUsers.size());

			return ResponseEntity.ok(new SuccessResponse(
					"Successfully created " + savedUsers.size() + " users",
					HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error bulk creating users: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("BULK_CREATE_FAILED", "Failed to create users"));
		}
	}

	// =============================================
	// Role Management Implementation
	// =============================================

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> createRole(RoleModel roleModel) {
		try {
			log.debug("Creating new role: {}", roleModel.getRoleName());

			// Check for existing role
			Optional<Role> existingRole = roleRepository.findByRoleName(roleModel.getRoleName().toUpperCase());
			if (existingRole.isPresent()) {
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body(new ErrorResponse("ROLE_EXISTS", "Role already exists"));
			}

			// Create role
			Role role = new Role();
			role.setRoleName(roleModel.getRoleName().toUpperCase());
			role.setDescription(roleModel.getDescription());

			// Assign permissions
			if (roleModel.getPermissionIds() != null && !roleModel.getPermissionIds().isEmpty()) {
				Set<Permission> permissions = roleModel.getPermissionIds().stream()
						.map(permId -> permissionRepository.findById(permId)
								.orElseThrow(() -> new RuntimeException("Permission not found: " + permId)))
						.collect(Collectors.toSet());
				role.setPermissions(permissions);
			}

			role.setCreatedAt(LocalDateTime.now());
			Role savedRole = roleRepository.save(role);
			log.info("Role created successfully with ID: {}", savedRole.getId());

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(new SuccessResponse("Role created successfully", HttpStatus.CREATED.value(),
							convertToRoleModel(savedRole)));

		} catch (Exception e) {
			log.error("Error creating role: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("CREATION_FAILED", "Failed to create role: " + e.getMessage()));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<List<RoleModel>> getAllRoles() {
		try {
			List<Role> roles = roleRepository.findAll();
			List<RoleModel> roleModels = roles.stream()
					.map(this::convertToRoleModel)
					.collect(Collectors.toList());

			return ResponseEntity.ok(roleModels);
		} catch (Exception e) {
			log.error("Error fetching roles: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<RoleModel> getRoleById(Long id) {
		try {
			Role role = roleRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

			return ResponseEntity.ok(convertToRoleModel(role));
		} catch (Exception e) {
			log.error("Error fetching role by ID {}: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<RoleModel> getRoleByName(String roleName) {
		try {
			Role role = roleRepository.findByRoleName(roleName.toUpperCase())
					.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

			return ResponseEntity.ok(convertToRoleModel(role));
		} catch (Exception e) {
			log.error("Error fetching role by name {}: {}", roleName, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> updateRole(RoleModel roleModel) {
		try {
			Role existingRole = roleRepository.findById(roleModel.getId())
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleModel.getId()));

			// Update fields
			if (roleModel.getRoleName() != null) {
				existingRole.setRoleName(roleModel.getRoleName().toUpperCase());
			}
			if (roleModel.getDescription() != null) {
				existingRole.setDescription(roleModel.getDescription());
			}

			// Update permissions if provided
			if (roleModel.getPermissionIds() != null) {
				Set<Permission> permissions = roleModel.getPermissionIds().stream()
						.map(permId -> permissionRepository.findById(permId)
								.orElseThrow(() -> new RuntimeException("Permission not found: " + permId)))
						.collect(Collectors.toSet());
				existingRole.setPermissions(permissions);
			}

			existingRole.setUpdatedAt(LocalDateTime.now());
			Role updatedRole = roleRepository.save(existingRole);

			log.info("Role updated successfully with ID: {}", updatedRole.getId());

			return ResponseEntity.ok(new SuccessResponse("Role updated successfully", HttpStatus.OK.value(),
					convertToRoleModel(updatedRole)));

		} catch (Exception e) {
			log.error("Error updating role: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("UPDATE_FAILED", "Failed to update role: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> deleteRole(Long id) {
		try {
			Role role = roleRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

			// Check if role is assigned to any users
			if (!role.getUsers().isEmpty()) {
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body(new ErrorResponse("ROLE_IN_USE", "Cannot delete role as it is assigned to users"));
			}

			roleRepository.delete(role);
			log.info("Role deleted successfully with ID: {}", id);

			return ResponseEntity.ok(new SuccessResponse("Role deleted successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error deleting role: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("DELETE_FAILED", "Failed to delete role: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> assignPermissionToRole(Long roleId, Long permissionId) {
		try {
			Role role = roleRepository.findById(roleId)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

			Permission permission = permissionRepository.findById(permissionId)
					.orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionId));

			role.getPermissions().add(permission);
			role.setUpdatedAt(LocalDateTime.now());
			roleRepository.save(role);

			log.info("Permission {} assigned to role {}", permissionId, roleId);

			return ResponseEntity.ok(new SuccessResponse("Permission assigned successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error assigning permission to role: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("ASSIGN_FAILED", "Failed to assign permission to role"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> removePermissionFromRole(Long roleId, Long permissionId) {
		try {
			Role role = roleRepository.findById(roleId)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

			role.getPermissions().removeIf(permission -> permission.getId().equals(permissionId));
			role.setUpdatedAt(LocalDateTime.now());
			roleRepository.save(role);

			log.info("Permission {} removed from role {}", permissionId, roleId);

			return ResponseEntity.ok(new SuccessResponse("Permission removed successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error removing permission from role: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("REMOVE_FAILED", "Failed to remove permission from role"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> bulkCreateRoles(List<RoleModel> roleModels) {
		try {
			List<Role> roles = new ArrayList<>();
			for (RoleModel roleModel : roleModels) {
				Role role = new Role();
				role.setRoleName(roleModel.getRoleName().toUpperCase());
				role.setDescription(roleModel.getDescription());
				roles.add(role);
			}

			List<Role> savedRoles = roleRepository.saveAll(roles);
			log.info("Bulk created {} roles", savedRoles.size());

			return ResponseEntity.ok(new SuccessResponse(
					"Successfully created " + savedRoles.size() + " roles",
					HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error bulk creating roles: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("BULK_CREATE_FAILED", "Failed to create roles"));
		}
	}

	// =============================================
	// Permission Management Implementation
	// =============================================

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> createPermission(PermissionModel permissionModel) {
		try {
			log.debug("Creating new permission: {}", permissionModel.getName());

			// Check for existing permission
			Optional<Permission> existingPermission = permissionRepository.findByName(permissionModel.getName().toUpperCase());
			if (existingPermission.isPresent()) {
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body(new ErrorResponse("PERMISSION_EXISTS", "Permission already exists"));
			}

			// Create permission
			Permission permission = new Permission();
			permission.setName(permissionModel.getName().toUpperCase());
			permission.setDescription(permissionModel.getDescription());
			permission.setCreatedAt(LocalDateTime.now());

			Permission savedPermission = permissionRepository.save(permission);
			log.info("Permission created successfully with ID: {}", savedPermission.getId());

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(new SuccessResponse("Permission created successfully", HttpStatus.CREATED.value(),
							convertToPermissionModel(savedPermission)));

		} catch (Exception e) {
			log.error("Error creating permission: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("CREATION_FAILED", "Failed to create permission: " + e.getMessage()));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<List<PermissionModel>> getAllPermissions(int page, int size, String name) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			Page<Permission> permissionPage;

			if (name != null && !name.isEmpty()) {
				permissionPage = permissionRepository.findByNameContainingIgnoreCase(name, pageable);
			} else {
				permissionPage = permissionRepository.findAll(pageable);
			}

			List<PermissionModel> permissions = permissionPage.getContent().stream()
					.map(this::convertToPermissionModel)
					.collect(Collectors.toList());

			return ResponseEntity.ok(permissions);
		} catch (Exception e) {
			log.error("Error fetching permissions: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<PermissionModel>> getAllPermissions() {
		try {
			log.debug("Fetching all permissions");

			List<Permission> permissions = permissionRepository.findAll();
			List<PermissionModel> permissionModels = permissions.stream()
					.map(this::convertToPermissionModel)
					.collect(Collectors.toList());

			log.info("Retrieved {} permissions", permissionModels.size());
			return ResponseEntity.ok(permissionModels);

		} catch (Exception e) {
			log.error("Error fetching permissions: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<PermissionModel> getPermissionById(Long id) {
		try {
			Permission permission = permissionRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

			return ResponseEntity.ok(convertToPermissionModel(permission));
		} catch (Exception e) {
			log.error("Error fetching permission by ID {}: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<PermissionModel> getPermissionByName(String name) {
		try {
			Permission permission = permissionRepository.findByName(name.toUpperCase())
					.orElseThrow(() -> new RuntimeException("Permission not found: " + name));

			return ResponseEntity.ok(convertToPermissionModel(permission));
		} catch (Exception e) {
			log.error("Error fetching permission by name {}: {}", name, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> updatePermission(PermissionModel permissionModel) {
		try {
			Permission existingPermission = permissionRepository.findById(permissionModel.getId())
					.orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionModel.getId()));

			// Update fields
			if (permissionModel.getName() != null) {
				existingPermission.setName(permissionModel.getName().toUpperCase());
			}
			if (permissionModel.getDescription() != null) {
				existingPermission.setDescription(permissionModel.getDescription());
			}

			existingPermission.setUpdatedAt(LocalDateTime.now());
			Permission updatedPermission = permissionRepository.save(existingPermission);

			log.info("Permission updated successfully with ID: {}", updatedPermission.getId());

			return ResponseEntity.ok(new SuccessResponse("Permission updated successfully", HttpStatus.OK.value(),
					convertToPermissionModel(updatedPermission)));

		} catch (Exception e) {
			log.error("Error updating permission: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("UPDATE_FAILED", "Failed to update permission: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> deletePermission(Long id) {
		try {
			Permission permission = permissionRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

			// Check if permission is assigned to any roles
			if (!permission.getRoles().isEmpty()) {
				return ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body(new ErrorResponse("PERMISSION_IN_USE", "Cannot delete permission as it is assigned to roles"));
			}

			permissionRepository.delete(permission);
			log.info("Permission deleted successfully with ID: {}", id);

			return ResponseEntity.ok(new SuccessResponse("Permission deleted successfully", HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error deleting permission: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("DELETE_FAILED", "Failed to delete permission: " + e.getMessage()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<? extends AbstractResponse> bulkCreatePermissions(List<PermissionModel> permissionModels) {
		try {
			List<Permission> permissions = new ArrayList<>();
			for (PermissionModel permModel : permissionModels) {
				Permission permission = new Permission();
				permission.setName(permModel.getName().toUpperCase());
				permission.setDescription(permModel.getDescription());
				permissions.add(permission);
			}

			List<Permission> savedPermissions = permissionRepository.saveAll(permissions);
			log.info("Bulk created {} permissions", savedPermissions.size());

			return ResponseEntity.ok(new SuccessResponse(
					"Successfully created " + savedPermissions.size() + " permissions",
					HttpStatus.OK.value()));
		} catch (Exception e) {
			log.error("Error bulk creating permissions: {}", e.getMessage(), e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("BULK_CREATE_FAILED", "Failed to create permissions"));
		}
	}

	// =============================================
	// Statistics Implementation
	// =============================================

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<SystemStatistics> getSystemStatistics() {
		try {
			SystemStatistics stats = new SystemStatistics();

			stats.setTotalUsers(userRepository.count());
			stats.setActiveUsers(userRepository.countByEnabledTrue());
			stats.setTotalRoles(roleRepository.count());
			stats.setTotalPermissions(permissionRepository.count());

			// Users by role
			Map<String, Long> usersByRole = new HashMap<>();
			List<Role> roles = roleRepository.findAll();
			for (Role role : roles) {
				long count = role.getUsers().size();
				usersByRole.put(role.getRoleName(), count);
			}
			stats.setUsersByRole(usersByRole);

			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			log.error("Error fetching system statistics: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<List<AuditLogModel>> getAuditLogs(int page, int size) {
		// This would typically come from an audit log repository
		// For now, return empty list
		return ResponseEntity.ok(new ArrayList<>());
	}

	// =============================================
	// Conversion Helper Methods
	// =============================================

	private UserModel convertToUserModel(UserEntity user) {
		UserModel model = new UserModel();
		BeanUtils.copyProperties(user, model);
		model.setEnabled(user.isEnabled());
		model.setCredentialsNonExpired(user.isCredentialsNonExpired());
		model.setAccountNonLocked(user.isAccountNonLocked());
		model.setAccountNonExpired(user.isAccountNonExpired());

		// Convert roles to role models
		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			Set<RoleModel> roleModels = user.getRoles().stream()
					.map(this::convertToRoleModel)
					.collect(Collectors.toSet());
			model.setRoles(roleModels);

			Set<Long> roleIds = user.getRoles().stream()
					.map(Role::getId)
					.collect(Collectors.toSet());
			model.setRoleIds(roleIds);
		}

		return model;
	}

	private RoleModel convertToRoleModel(Role role) {
		RoleModel model = new RoleModel();
		BeanUtils.copyProperties(role, model);

		if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
			Set<PermissionModel> permissionModels = role.getPermissions().stream()
					.map(this::convertToPermissionModel)
					.collect(Collectors.toSet());
			model.setPermissions(permissionModels);

			Set<Long> permissionIds = role.getPermissions().stream()
					.map(Permission::getId)
					.collect(Collectors.toSet());
			model.setPermissionIds(permissionIds);
		}

		return model;
	}

	private PermissionModel convertToPermissionModel(Permission permission) {
		PermissionModel model = new PermissionModel();
		BeanUtils.copyProperties(permission, model);
		return model;
	}
}