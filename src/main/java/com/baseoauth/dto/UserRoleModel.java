package com.baseoauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Role Model")
public class UserRoleModel {

	@Schema(description = "Role ID", example = "1")
	private Long roleId;

	@NotBlank(message = "Role name is required")
	@Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Z_]+$", message = "Role name must be uppercase with underscores")
	@Schema(description = "Role name", example = "ADMIN")
	private String roleName;

	@Schema(description = "Role description", example = "Administrator with full access")
	private String description;

	@Schema(description = "Permissions assigned to this role")
	private Set<PermissionModel> permissions;
}