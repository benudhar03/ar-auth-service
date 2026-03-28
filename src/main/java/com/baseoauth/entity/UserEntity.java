package com.baseoauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", schema = "admin")
public class UserEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_name", unique = true, nullable = false, length = 50)
	private String userName;

	@Column(name = "email_id", unique = true, nullable = false, length = 100)
	private String email;

	@Column(name = "mobile_no", length = 15)
	private String mobileNo;

	@Column(name = "password", nullable = false, length = 15)
	private String password;

	@Column(name = "is_enabled", nullable = false)
	private boolean isEnabled = true;

	@Column(name = "is_credentials_non_expired", nullable = false)
	private boolean isCredentialsNonExpired = true;

	@Column(name = "is_account_non_locked", nullable = false)
	private boolean isAccountNonLocked = true;

	@Column(name = "is_account_non_expired", nullable = false)
	private boolean isAccountNonExpired = true;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "user_role",
			schema = "admin",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
	)
	private Set<Role> roles = new HashSet<>();

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "failed_attempts")
	private int failedAttempts = 0;

	@Column(name = "account_locked_until")
	private LocalDateTime accountLockedUntil;

	@Version
	@Column(name = "version")
	private Long version;

	// Convenience methods for managing roles
	public void addRole(Role role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		roles.add(role);
	}

	public void removeRole(Role role) {
		if (roles != null) {
			roles.remove(role);
		}
	}

	public boolean hasRole(String roleName) {
		// Null check and stream operation with explicit type
		if (roles == null || roles.isEmpty()) {
			return false;
		}

		return roles.stream()
				.anyMatch(role -> {
					// Debug print to verify
					System.out.println("Checking role: " + role.getRoleName() + " against: " + roleName);
					return role.getRoleName() != null && role.getRoleName().equals(roleName);
				});
	}

	public boolean hasPermission(String permissionName) {
		if (roles == null || roles.isEmpty()) {
			return false;
		}

		return roles.stream()
				.filter(role -> role.getPermissions() != null && !role.getPermissions().isEmpty())
				.flatMap(role -> role.getPermissions().stream())
				.anyMatch(permission -> {
					// Debug print to verify
					System.out.println("Checking permission: " + permission.getName() + " against: " + permissionName);
					return permission.getName() != null && permission.getName().equals(permissionName);
				});
	}

	// Method to increment failed login attempts
	public void incrementFailedAttempts() {
		this.failedAttempts++;
		if (this.failedAttempts >= 5) {
			this.accountLockedUntil = LocalDateTime.now().plusMinutes(30);
		}
	}

	// Method to reset failed attempts on successful login
	public void resetFailedAttempts() {
		this.failedAttempts = 0;
		this.accountLockedUntil = null;
	}

	// Method to check if account is locked
	public boolean isAccountLocked() {
		if (accountLockedUntil == null) return false;
		return LocalDateTime.now().isBefore(accountLockedUntil);
	}

	// Update last login timestamp
	public void updateLastLogin() {
		this.lastLogin = LocalDateTime.now();
	}
}