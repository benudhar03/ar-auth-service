package com.baseoauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Model for OAuth2 Authentication")
public class UserModel {

	@Schema(description = "User ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
	@Schema(description = "Username for login", example = "john_doe", required = true)
	private String userName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	@Schema(description = "User email address", example = "john.doe@example.com", required = true)
	private String email;

	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
	@Schema(description = "Mobile number", example = "9876543210")
	private String mobileNo;

	@NotBlank(message = "Password is required", groups = {CreateGroup.class})
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Password must contain at least one digit, one lowercase, one uppercase, " +
					"one special character, and no whitespace")
	@Schema(description = "User password", example = "Password@123", required = true,
			accessMode = Schema.AccessMode.WRITE_ONLY)
	private String password;

	@Schema(description = "Account status", example = "true")
	private boolean isEnabled = true;

	@Schema(description = "Credentials non-expired status", example = "true")
	private boolean isCredentialsNonExpired = true;

	@Schema(description = "Account non-locked status", example = "true")
	private boolean isAccountNonLocked = true;

	@Schema(description = "Account non-expired status", example = "true")
	private boolean isAccountNonExpired = true;

	@Schema(description = "User roles", accessMode = Schema.AccessMode.READ_ONLY)
	private Set<RoleModel> roles = new HashSet<>();

	@Schema(description = "Role IDs for assignment", accessMode = Schema.AccessMode.WRITE_ONLY)
	private Set<Long> roleIds = new HashSet<>();

	@Schema(description = "User permissions (calculated from roles)", accessMode = Schema.AccessMode.READ_ONLY)
	private Set<String> permissions = new HashSet<>();

	@Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(description = "Last login timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime lastLogin;

	@Schema(description = "Failed login attempts", accessMode = Schema.AccessMode.READ_ONLY)
	private int failedAttempts = 0;

	@Schema(description = "Account lock until timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime accountLockedUntil;

	@JsonIgnore
	@Schema(hidden = true)
	private String confirmPassword;

	// Constructor for creating new user
	public UserModel(String userName, String email, String password) {
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.isEnabled = true;
		this.isCredentialsNonExpired = true;
		this.isAccountNonLocked = true;
		this.isAccountNonExpired = true;
	}

	// Constructor for updating user
	public UserModel(Long id, String userName, String email, String mobileNo) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.mobileNo = mobileNo;
	}

	// Convenience methods
	public void addRole(RoleModel role) {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}
		this.roles.add(role);
		if (role.getId() != null) {
			this.roleIds.add(role.getId());
		}
	}

	public void removeRole(RoleModel role) {
		if (this.roles != null) {
			this.roles.remove(role);
			if (role.getId() != null) {
				this.roleIds.remove(role.getId());
			}
		}
	}

	public void addRoleId(Long roleId) {
		if (this.roleIds == null) {
			this.roleIds = new HashSet<>();
		}
		this.roleIds.add(roleId);
	}

	public void removeRoleId(Long roleId) {
		if (this.roleIds != null) {
			this.roleIds.remove(roleId);
		}
	}

	public void addPermission(String permission) {
		if (this.permissions == null) {
			this.permissions = new HashSet<>();
		}
		this.permissions.add(permission);
	}

	public boolean hasRole(String roleName) {
		if (roles == null) return false;
		return roles.stream().anyMatch(role -> role.getRoleName().equals(roleName));
	}

	public boolean hasPermission(String permissionName) {
		if (permissions == null) return false;
		return permissions.contains(permissionName);
	}

	public boolean isAccountLocked() {
		if (accountLockedUntil == null) return false;
		return LocalDateTime.now().isBefore(accountLockedUntil);
	}

	// Getters with proper naming for boolean fields
	@JsonProperty("enabled")
	public boolean isEnabled() {
		return isEnabled;
	}

	@JsonProperty("credentialsNonExpired")
	public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	@JsonProperty("accountNonLocked")
	public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	@JsonProperty("accountNonExpired")
	public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	// Validation groups
	public interface CreateGroup {}
	public interface UpdateGroup {}
	public interface LoginGroup {}
}