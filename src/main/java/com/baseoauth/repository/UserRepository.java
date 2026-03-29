package com.baseoauth.repository;

import com.baseoauth.entity.Role;
import com.baseoauth.entity.UserEntity;
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

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	// =============================================
	// Basic Queries
	// =============================================

	Optional<UserEntity> findByUserName(String userName);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUserNameOrEmail(String userName, String email);

	Optional<UserEntity> findByUserNameOrEmailOrMobileNo(String userName, String email, String mobileNo);

	Optional<UserEntity> findByUserNameAndEnabledTrue(String userName);

	boolean existsByUserName(String userName);

	boolean existsByEmail(String email);

	boolean existsByMobileNo(String mobileNo);

	// =============================================
	// Pagination and Sorting
	// =============================================

	Page<UserEntity> findAll(Pageable pageable);

	Page<UserEntity> findByEnabledTrue(Pageable pageable);

	Page<UserEntity> findByEnabledFalse(Pageable pageable);

	Page<UserEntity> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);

	Page<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	Page<UserEntity> findByRolesRoleName(String roleName, Pageable pageable);


	// =============================================
	// Search Queries
	// =============================================

	@Query("SELECT u FROM UserEntity u WHERE " +
			"LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(u.mobileNo) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<UserEntity> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("SELECT u FROM UserEntity u WHERE u.isEnabled = :enabled AND " +
			"(LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
	Page<UserEntity> searchUsersByStatus(@Param("enabled") boolean enabled,
										 @Param("searchTerm") String searchTerm,
										 Pageable pageable);

	// =============================================
	// Account Status Queries
	// =============================================

	List<UserEntity> findByIsEnabledTrue();

	List<UserEntity> findByIsEnabledFalse();

	List<UserEntity> findByIsAccountNonLockedFalse();

	List<UserEntity> findByFailedAttemptsGreaterThanEqual(int failedAttempts);

	@Query("SELECT u FROM UserEntity u WHERE u.accountLockedUntil > :currentTime")
	List<UserEntity> findLockedAccounts(@Param("currentTime") LocalDateTime currentTime);

	@Query("SELECT u FROM UserEntity u WHERE u.accountLockedUntil < :currentTime AND u.accountLockedUntil IS NOT NULL")
	List<UserEntity> findAccountsWithExpiredLock(@Param("currentTime") LocalDateTime currentTime);

	// =============================================
	// Update Queries
	// =============================================

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.failedAttempts = u.failedAttempts + 1 WHERE u.userName = :userName")
	int incrementFailedAttempts(@Param("userName") String userName);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.failedAttempts = 0, u.accountLockedUntil = NULL WHERE u.userName = :userName")
	int resetFailedAttempts(@Param("userName") String userName);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.accountLockedUntil = :lockUntil WHERE u.userName = :userName")
	int lockAccount(@Param("userName") String userName, @Param("lockUntil") LocalDateTime lockUntil);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.isEnabled = :enabled WHERE u.id = :userId")
	int updateUserStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.lastLogin = :lastLogin WHERE u.userName = :userName")
	int updateLastLogin(@Param("userName") String userName, @Param("lastLogin") LocalDateTime lastLogin);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.password = :password WHERE u.userName = :userName")
	int updatePassword(@Param("userName") String userName, @Param("password") String password);

	// =============================================
	// Role Assignment Queries
	// =============================================

	@Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.roleName = :roleName")
	List<UserEntity> findUsersByRoleName(@Param("roleName") String roleName);

	@Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.id = :roleId")
	Page<UserEntity> findUsersByRoleId(@Param("roleId") Long roleId, Pageable pageable);

	@Query("SELECT u FROM UserEntity u WHERE :role MEMBER OF u.roles")
	Page<UserEntity> findUsersWithRole(@Param("role") Role role, Pageable pageable);

	// =============================================
	// Count Queries
	// =============================================

	long countByEnabledTrue();

	long countByEnabledFalse();

	long countByIsAccountNonLockedTrue();

	long countByIsAccountNonLockedFalse();

	@Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate")
	long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate,
								  @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COUNT(u) FROM UserEntity u WHERE u.lastLogin BETWEEN :startDate AND :endDate")
	long countActiveUsersBetween(@Param("startDate") LocalDateTime startDate,
								 @Param("endDate") LocalDateTime endDate);

	// =============================================
	// Batch Operations
	// =============================================

	@Modifying
	@Transactional
	@Query("DELETE FROM UserEntity u WHERE u.isEnabled = false AND u.createdAt < :cutoffDate")
	int deleteInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.isEnabled = false WHERE u.lastLogin < :cutoffDate")
	int disableInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

	// =============================================
	// Native Queries
	// =============================================

	@Query(value = "SELECT * FROM admin.user WHERE user_name = :userName", nativeQuery = true)
	Optional<UserEntity> findByUserNameNative(@Param("userName") String userName);

	@Query(value = "SELECT COUNT(*) FROM admin.user WHERE is_enabled = true", nativeQuery = true)
	long countActiveUsersNative();
}