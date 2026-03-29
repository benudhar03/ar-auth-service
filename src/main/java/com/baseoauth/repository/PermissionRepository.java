package com.baseoauth.repository;

import com.baseoauth.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	// =============================================
	// Basic Queries
	// =============================================

	Optional<Permission> findByName(String name);

	Optional<Permission> findByNameIgnoreCase(String name);

	List<Permission> findByNameContainingIgnoreCase(String name);

	List<Permission> findByNameIn(Set<String> names);

	boolean existsByName(String name);

	boolean existsByNameIgnoreCase(String name);

	// =============================================
	// Pagination and Sorting
	// =============================================

	Page<Permission> findAll(Pageable pageable);

	Page<Permission> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Permission> findByResource(String resource, Pageable pageable);

	Page<Permission> findByAction(String action, Pageable pageable);

	Page<Permission> findByCategory(String category, Pageable pageable);

	Page<Permission> findByIsActiveTrue(Pageable pageable);

	// =============================================
	// Resource and Action Queries
	// =============================================

	List<Permission> findByResource(String resource);

	List<Permission> findByAction(String action);

	List<Permission> findByResourceAndAction(String resource, String action);

	List<Permission> findByCategory(String category);

	@Query("SELECT DISTINCT p.resource FROM Permission p WHERE p.resource IS NOT NULL")
	List<String> findAllResources();

	@Query("SELECT DISTINCT p.action FROM Permission p WHERE p.action IS NOT NULL")
	List<String> findAllActions();

	@Query("SELECT DISTINCT p.category FROM Permission p WHERE p.category IS NOT NULL")
	List<String> findAllCategories();

	// =============================================
	// Role Related Queries
	// =============================================

	@Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
	List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.roleName = :roleName")
	List<Permission> findPermissionsByRoleName(@Param("roleName") String roleName);

	@Query("SELECT p FROM Permission p WHERE SIZE(p.roles) = 0")
	List<Permission> findPermissionsWithoutRoles();

	@Query("SELECT p FROM Permission p WHERE SIZE(p.roles) > 0")
	List<Permission> findPermissionsWithRoles();

	// =============================================
	// Status Queries
	// =============================================

	List<Permission> findByIsActiveTrue();

	List<Permission> findByIsActiveFalse();

	@Modifying
	@Transactional
	@Query("UPDATE Permission p SET p.isActive = :active WHERE p.id = :permissionId")
	int updatePermissionStatus(@Param("permissionId") Long permissionId, @Param("active") boolean active);

	@Modifying
	@Transactional
	@Query("UPDATE Permission p SET p.isActive = :active WHERE p.category = :category")
	int updatePermissionsByCategory(@Param("category") String category, @Param("active") boolean active);

	// =============================================
	// Priority Queries
	// =============================================

	List<Permission> findByOrderByPriorityDesc();

	List<Permission> findByPriorityGreaterThan(int priority);

	@Query("SELECT MAX(p.priority) FROM Permission p")
	Integer findMaxPriority();

	// =============================================
	// Count Queries
	// =============================================

	long countByIsActiveTrue();

	long countByIsActiveFalse();

	long countByResource(String resource);

	long countByCategory(String category);

	@Query("SELECT COUNT(p) FROM Permission p WHERE SIZE(p.roles) >= :minRoles")
	long countPermissionsWithAtLeastRoles(@Param("minRoles") int minRoles);

	// =============================================
	// Batch Operations
	// =============================================

	@Modifying
	@Transactional
	@Query("DELETE FROM Permission p WHERE p.id IN :permissionIds AND SIZE(p.roles) = 0")
	int deletePermissionsBatch(@Param("permissionIds") List<Long> permissionIds);

	@Modifying
	@Transactional
	@Query("UPDATE Permission p SET p.isActive = false WHERE p.createdAt < :cutoffDate")
	int deactivateOldPermissions(@Param("cutoffDate") LocalDateTime cutoffDate);

	// =============================================
	// Native Queries
	// =============================================

	@Query(value = "SELECT * FROM admin.permission WHERE name ILIKE :namePattern", nativeQuery = true)
	List<Permission> findPermissionsByNamePatternNative(@Param("namePattern") String namePattern);

	@Query(value = "SELECT p.* FROM admin.permission p " +
			"JOIN admin.role_permission rp ON p.id = rp.permission_id " +
			"JOIN admin.role r ON rp.role_id = r.id " +
			"WHERE r.role_name = :roleName", nativeQuery = true)
	List<Permission> findPermissionsByRoleNameNative(@Param("roleName") String roleName);

	// =============================================
	// Custom Projections
	// =============================================

	@Query("SELECT p.name FROM Permission p WHERE p.id IN :permissionIds")
	List<String> findPermissionNamesByIds(@Param("permissionIds") Set<Long> permissionIds);

	@Query("SELECT p.name FROM Permission p WHERE p.resource = :resource")
	List<String> findPermissionNamesByResource(@Param("resource") String resource);
}