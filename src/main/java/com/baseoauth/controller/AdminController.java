package com.baseoauth.controller;

import com.baseoauth.dto.*;
import com.baseoauth.resp.dto.AbstractResponse;
import com.baseoauth.resp.dto.SuccessResponse;
import com.baseoauth.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin-rest/api")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for managing users, roles, and permissions")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

	private final ApplicationService applicationService;

	// =============================================
	// Health Check & Test Endpoints
	// =============================================

	@GetMapping("/health")
	@Operation(summary = "Health check endpoint", description = "Check if the service is running")
	public ResponseEntity<HealthResponse> healthCheck() {
		log.info("Health check endpoint called");
		return ResponseEntity.ok(new HealthResponse("UP", "OAuth2 Authorization Server is running"));
	}

	@GetMapping("/test")
	@Operation(summary = "Test endpoint", description = "Test endpoint to verify authentication")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('TEST_ACCESS')")
	public ResponseEntity<AbstractResponse> testServer() {
		log.info("Test endpoint called with authentication");
		return ResponseEntity.ok(new SuccessResponse("OAUTH2 Server is running", HttpStatus.OK.value()));
	}

	// =============================================
	// Permission Management Endpoints
	// =============================================

	@PostMapping("/permissions")
	@Operation(summary = "Create permission", description = "Create a new permission")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Permission created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input"),
			@ApiResponse(responseCode = "409", description = "Permission already exists"),
			@ApiResponse(responseCode = "403", description = "Access denied")
	})
	@PreAuthorize("hasRole('ADMIN') or hasPermission('PERMISSION_CREATE')")
	public ResponseEntity<? extends AbstractResponse> createPermission(
			@Valid @RequestBody PermissionModel permissionModel) {
		log.info("Creating new permission: {}", permissionModel.getName());
		return applicationService.createPermission(permissionModel);
	}

	@GetMapping("/permissions")
	@Operation(summary = "Get all permissions", description = "Retrieve all permissions")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('PERMISSION_READ')")
	public ResponseEntity<List<PermissionModel>> getAllPermissions() {
		log.info("Fetching all permissions");
		return applicationService.getAllPermissions();
	}

	@GetMapping("/permissions/{id}")
	@Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by ID")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('PERMISSION_READ')")
	public ResponseEntity<PermissionModel> getPermissionById(
			@PathVariable Long id) {
		log.info("Fetching permission with id: {}", id);
		return applicationService.getPermissionById(id);
	}

	@PutMapping("/permissions/{id}")
	@Operation(summary = "Update permission", description = "Update an existing permission")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('PERMISSION_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> updatePermission(
			@PathVariable Long id,
			@Valid @RequestBody PermissionModel permissionModel) {
		log.info("Updating permission with id: {}", id);
		permissionModel.setId(id);
		return applicationService.updatePermission(permissionModel);
	}

	@DeleteMapping("/permissions/{id}")
	@Operation(summary = "Delete permission", description = "Delete a permission by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Permission deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Permission not found")
	})
	@PreAuthorize("hasRole('ADMIN') or hasPermission('PERMISSION_DELETE')")
	public ResponseEntity<? extends AbstractResponse> deletePermission(
			@PathVariable Long id) {
		log.info("Deleting permission with id: {}", id);
		return applicationService.deletePermission(id);
	}

	// =============================================
	// Role Management Endpoints
	// =============================================

	@PostMapping("/roles")
	@Operation(summary = "Create role", description = "Create a new role with permissions")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Role created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input"),
			@ApiResponse(responseCode = "409", description = "Role already exists")
	})
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_CREATE')")
	public ResponseEntity<? extends AbstractResponse> createRole(
			@Valid @RequestBody RoleModel roleModel) {
		log.info("Creating new role: {}", roleModel.getRoleName());
		return applicationService.createRole(roleModel);
	}

	@GetMapping("/roles")
	@Operation(summary = "Get all roles", description = "Retrieve all roles with their permissions")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_READ')")
	public ResponseEntity<List<RoleModel>> getAllRoles() {
		log.info("Fetching all roles");
		return applicationService.getAllRoles();
	}

	@GetMapping("/roles/{id}")
	@Operation(summary = "Get role by ID", description = "Retrieve a specific role by ID")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_READ')")
	public ResponseEntity<RoleModel> getRoleById(
			@PathVariable Long id) {
		log.info("Fetching role with id: {}", id);
		return applicationService.getRoleById(id);
	}

	@PutMapping("/roles/{id}")
	@Operation(summary = "Update role", description = "Update an existing role")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> updateRole(
			@PathVariable Long id,
			@Valid @RequestBody RoleModel roleModel) {
		log.info("Updating role with id: {}", id);
		roleModel.setId(id);
		return applicationService.updateRole(roleModel);
	}

	@DeleteMapping("/roles/{id}")
	@Operation(summary = "Delete role", description = "Delete a role by ID")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_DELETE')")
	public ResponseEntity<? extends AbstractResponse> deleteRole(
			@PathVariable Long id) {
		log.info("Deleting role with id: {}", id);
		return applicationService.deleteRole(id);
	}

	@PostMapping("/roles/{roleId}/permissions/{permissionId}")
	@Operation(summary = "Assign permission to role", description = "Assign a permission to a role")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> assignPermissionToRole(
			@PathVariable Long roleId,
			@PathVariable Long permissionId) {
		log.info("Assigning permission {} to role {}", permissionId, roleId);
		return applicationService.assignPermissionToRole(roleId, permissionId);
	}

	@DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
	@Operation(summary = "Remove permission from role", description = "Remove a permission from a role")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('ROLE_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> removePermissionFromRole(
			@PathVariable Long roleId,
			@PathVariable Long permissionId) {
		log.info("Removing permission {} from role {}", permissionId, roleId);
		return applicationService.removePermissionFromRole(roleId, permissionId);
	}

	// =============================================
	// User Management Endpoints
	// =============================================

	@PostMapping("/users")
	@Operation(summary = "Create user", description = "Create a new user with roles")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input"),
			@ApiResponse(responseCode = "409", description = "User already exists")
	})
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_CREATE')")
	public ResponseEntity<? extends AbstractResponse> createUser(
			@Valid @RequestBody UserModel userModel) {
		log.info("Creating new user: {}", userModel.getUserName());
		return applicationService.createUser(userModel);
	}

	@GetMapping("/users")
	@Operation(summary = "Get all users", description = "Retrieve all users with their roles")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_READ')")
	public ResponseEntity<List<UserModel>> getAllUsers() {
		log.info("Fetching all users");
		return applicationService.getAllUsers();
	}

	@GetMapping("/users/{id}")
	@Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_READ')")
	public ResponseEntity<UserModel> getUserById(
			@PathVariable Long id) {
		log.info("Fetching user with id: {}", id);
		return applicationService.getUserById(id);
	}

	@GetMapping("/users/username/{username}")
	@Operation(summary = "Get user by username", description = "Retrieve a user by username")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_READ')")
	public ResponseEntity<UserModel> getUserByUsername(
			@PathVariable String username) {
		log.info("Fetching user with username: {}", username);
		return applicationService.getUserByUsername(username);
	}

	@PutMapping("/users/{id}")
	@Operation(summary = "Update user", description = "Update an existing user")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_UPDATE') or @authService.isCurrentUser(#id)")
	public ResponseEntity<? extends AbstractResponse> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UserModel userModel) {
		log.info("Updating user with id: {}", id);
		userModel.setId(id);
		return applicationService.updateUser(userModel);
	}

	@DeleteMapping("/users/{id}")
	@Operation(summary = "Delete user", description = "Delete a user by ID")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_DELETE')")
	public ResponseEntity<? extends AbstractResponse> deleteUser(
			@PathVariable Long id) {
		log.info("Deleting user with id: {}", id);
		return applicationService.deleteUser(id);
	}

	@PostMapping("/users/{userId}/roles/{roleId}")
	@Operation(summary = "Assign role to user", description = "Assign a role to a user")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> assignRoleToUser(
			@PathVariable Long userId,
			@PathVariable Long roleId) {
		log.info("Assigning role {} to user {}", roleId, userId);
		return applicationService.assignRoleToUser(userId, roleId);
	}

	@DeleteMapping("/users/{userId}/roles/{roleId}")
	@Operation(summary = "Remove role from user", description = "Remove a role from a user")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> removeRoleFromUser(
			@PathVariable Long userId,
			@PathVariable Long roleId) {
		log.info("Removing role {} from user {}", roleId, userId);
		return applicationService.removeRoleFromUser(userId, roleId);
	}

	@PutMapping("/users/{id}/status")
	@Operation(summary = "Update user status", description = "Enable or disable a user")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> updateUserStatus(
			@PathVariable Long id,
			@RequestParam boolean enabled) {
		log.info("Updating user {} status to enabled: {}", id, enabled);
		return applicationService.updateUserStatus(id, enabled);
	}

	@PostMapping("/users/{id}/reset-password")
	@Operation(summary = "Reset user password", description = "Reset user password (Admin only)")
	@PreAuthorize("hasRole('ADMIN') or hasPermission('USER_UPDATE')")
	public ResponseEntity<? extends AbstractResponse> resetUserPassword(
			@PathVariable Long id,
			@RequestBody PasswordResetModel passwordResetModel) {
		log.info("Resetting password for user: {}", id);
		return applicationService.resetUserPassword(id, passwordResetModel);
	}

	// =============================================
	// Bulk Operations
	// =============================================

	@PostMapping("/users/bulk")
	@Operation(summary = "Bulk create users", description = "Create multiple users at once")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<? extends AbstractResponse> bulkCreateUsers(
			@Valid @RequestBody List<UserModel> userModels) {
		log.info("Bulk creating {} users", userModels.size());
		return applicationService.bulkCreateUsers(userModels);
	}

	@PostMapping("/roles/bulk")
	@Operation(summary = "Bulk create roles", description = "Create multiple roles at once")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<? extends AbstractResponse> bulkCreateRoles(
			@Valid @RequestBody List<RoleModel> roleModels) {
		log.info("Bulk creating {} roles", roleModels.size());
		return applicationService.bulkCreateRoles(roleModels);
	}

	// =============================================
	// Statistics & Reports
	// =============================================

	@GetMapping("/statistics")
	@Operation(summary = "Get system statistics", description = "Get system-wide statistics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SystemStatistics> getSystemStatistics() {
		log.info("Fetching system statistics");
		return applicationService.getSystemStatistics();
	}

	@GetMapping("/audit-logs")
	@Operation(summary = "Get audit logs", description = "Retrieve system audit logs")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<AuditLogModel>> getAuditLogs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		log.info("Fetching audit logs - page: {}, size: {}", page, size);
		return applicationService.getAuditLogs(page, size);
	}
}