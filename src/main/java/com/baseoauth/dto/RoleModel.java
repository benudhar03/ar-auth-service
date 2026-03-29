package com.baseoauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Role Model for OAuth2 Authorization")
public class RoleModel {

	@Schema(description = "Role ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

	@NotBlank(message = "Role name is required")
	@Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Z_]+$", message = "Role name must be uppercase with underscores")
	@Schema(description = "Role name", example = "ADMIN", required = true)
	private String roleName;

	@Size(max = 200, message = "Description cannot exceed 200 characters")
	@Schema(description = "Role description", example = "Administrator with full access")
	private String description;

	@Schema(description = "Permissions assigned to this role")
	private Set<PermissionModel> permissions = new HashSet<>();

	@Schema(description = "Permission IDs for assignment", accessMode = Schema.AccessMode.WRITE_ONLY)
	private Set<Long> permissionIds = new HashSet<>();

	@Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	// Convenience methods
	public void addPermission(PermissionModel permission) {
		if (this.permissions == null) {
			this.permissions = new HashSet<>();
		}
		this.permissions.add(permission);
		if (permission.getId() != null) {
			this.permissionIds.add(permission.getId());
		}
	}

	public void addPermissionId(Long permissionId) {
		if (this.permissionIds == null) {
			this.permissionIds = new HashSet<>();
		}
		this.permissionIds.add(permissionId);
	}

	public boolean hasPermission(String permissionName) {
		if (permissions == null) return false;
		return permissions.stream().anyMatch(p -> p.getName().equals(permissionName));
	}
}