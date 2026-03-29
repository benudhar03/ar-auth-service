package com.baseoauth.repository;

import com.baseoauth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	// =============================================
	// Basic Queries
	// =============================================

	Optional<Role> findByRoleName(String roleName);

	Optional<Role> findByRoleNameIgnoreCase(String roleName);

	List<Role> findByRoleNameContainingIgnoreCase(String roleName);

	boolean existsByRoleName(String roleName);

	// =============================================
	// Pagination
	// =============================================

	Page<Role> findAll(Pageable pageable);

	Page<Role> findByRoleNameContainingIgnoreCase(String roleName, Pageable pageable);

	// =============================================
	// Permission Related Queries
	// =============================================

	@Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
	List<Role> findRolesByPermissionId(@Param("permissionId") Long permissionId);

	@Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
	List<Role> findRolesByPermissionName(@Param("permissionName") String permissionName);

	@Query("SELECT r FROM Role r WHERE SIZE(r.permissions) = 0")
	List<Role> findRolesWithoutPermissions();

	// =============================================
	// User Related Queries
	// =============================================

	@Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
	List<Role> findRolesByUserId(@Param("userId") Long userId);

	@Query("SELECT r FROM Role r WHERE SIZE(r.users) > 0")
	List<Role> findRolesWithUsers();

	@Query("SELECT r FROM Role r WHERE SIZE(r.users) = 0")
	List<Role> findRolesWithoutUsers();

	// =============================================
	// Update Queries
	// =============================================

	@Modifying
	@Transactional
	@Query("UPDATE Role r SET r.description = :description WHERE r.roleName = :roleName")
	int updateRoleDescription(@Param("roleName") String roleName, @Param("description") String description);

	@Modifying
	@Transactional
	@Query("DELETE FROM Role r WHERE r.id = :roleId AND SIZE(r.users) = 0")
	int deleteRoleIfNoUsers(@Param("roleId") Long roleId);

	// =============================================
	// Permission Assignment Queries
	// =============================================

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO admin.role_permission (role_id, permission_id) VALUES (:roleId, :permissionId)",
			nativeQuery = true)
	int assignPermissionToRole(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM admin.role_permission WHERE role_id = :roleId AND permission_id = :permissionId",
			nativeQuery = true)
	int removePermissionFromRole(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM admin.role_permission WHERE role_id = :roleId", nativeQuery = true)
	int removeAllPermissionsFromRole(@Param("roleId") Long roleId);

	// =============================================
	// Count Queries
	// =============================================

	@Query("SELECT COUNT(r) FROM Role r WHERE SIZE(r.permissions) >= :minPermissions")
	long countRolesWithAtLeastPermissions(@Param("minPermissions") int minPermissions);

	@Query("SELECT COUNT(r) FROM Role r WHERE SIZE(r.users) >= :minUsers")
	long countRolesWithAtLeastUsers(@Param("minUsers") int minUsers);

	// =============================================
	// Batch Operations
	// =============================================

	@Modifying
	@Transactional
	@Query("DELETE FROM Role r WHERE r.id IN :roleIds AND SIZE(r.users) = 0")
	int deleteRolesBatch(@Param("roleIds") List<Long> roleIds);

	// =============================================
	// Native Queries
	// =============================================

	@Query(value = "SELECT * FROM admin.role WHERE role_name ILIKE :roleNamePattern", nativeQuery = true)
	List<Role> findRolesByNamePatternNative(@Param("roleNamePattern") String roleNamePattern);

	@Query(value = "SELECT r.* FROM admin.role r " +
			"JOIN admin.role_permission rp ON r.id = rp.role_id " +
			"JOIN admin.permission p ON rp.permission_id = p.id " +
			"WHERE p.name = :permissionName", nativeQuery = true)
	List<Role> findRolesByPermissionNameNative(@Param("permissionName") String permissionName);
}