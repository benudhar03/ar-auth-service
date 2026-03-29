package com.baseoauth.service;

import com.baseoauth.dto.*;
import com.baseoauth.resp.dto.AbstractResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {

	// =============================================
	// User Management
	// =============================================

	ResponseEntity<? extends AbstractResponse> createUser(UserModel userModel);

	ResponseEntity<List<UserModel>> getAllUsers(int page, int size, String username);
	ResponseEntity<List<UserModel>> getAllUsers();

	ResponseEntity<UserModel> getUserById(Long id);

	ResponseEntity<UserModel> getUserByUsername(String username);

	ResponseEntity<? extends AbstractResponse> updateUser(UserModel userModel);

	ResponseEntity<? extends AbstractResponse> deleteUser(Long id);

	ResponseEntity<? extends AbstractResponse> updateUserStatus(Long id, boolean enabled);

	ResponseEntity<? extends AbstractResponse> resetUserPassword(Long id, PasswordResetModel passwordResetModel);

	ResponseEntity<? extends AbstractResponse> assignRoleToUser(Long userId, Long roleId);

	ResponseEntity<? extends AbstractResponse> removeRoleFromUser(Long userId, Long roleId);

	ResponseEntity<? extends AbstractResponse> bulkCreateUsers(List<UserModel> userModels);

	// =============================================
	// Role Management
	// =============================================

	ResponseEntity<? extends AbstractResponse> createRole(RoleModel roleModel);

	ResponseEntity<List<RoleModel>> getAllRoles();

	ResponseEntity<RoleModel> getRoleById(Long id);

	ResponseEntity<RoleModel> getRoleByName(String roleName);

	ResponseEntity<? extends AbstractResponse> updateRole(RoleModel roleModel);

	ResponseEntity<? extends AbstractResponse> deleteRole(Long id);

	ResponseEntity<? extends AbstractResponse> assignPermissionToRole(Long roleId, Long permissionId);

	ResponseEntity<? extends AbstractResponse> removePermissionFromRole(Long roleId, Long permissionId);

	ResponseEntity<? extends AbstractResponse> bulkCreateRoles(List<RoleModel> roleModels);

	// =============================================
	// Permission Management
	// =============================================

	ResponseEntity<? extends AbstractResponse> createPermission(PermissionModel permissionModel);

	ResponseEntity<List<PermissionModel>> getAllPermissions(int page, int size, String name);

	ResponseEntity<List<PermissionModel>> getAllPermissions();

	ResponseEntity<PermissionModel> getPermissionById(Long id);

	ResponseEntity<PermissionModel> getPermissionByName(String name);

	ResponseEntity<? extends AbstractResponse> updatePermission(PermissionModel permissionModel);

	ResponseEntity<? extends AbstractResponse> deletePermission(Long id);

	ResponseEntity<? extends AbstractResponse> bulkCreatePermissions(List<PermissionModel> permissionModels);

	// =============================================
	// Statistics & Reports
	// =============================================

	ResponseEntity<SystemStatistics> getSystemStatistics();

	ResponseEntity<List<AuditLogModel>> getAuditLogs(int page, int size);
}