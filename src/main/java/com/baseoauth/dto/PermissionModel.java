package com.baseoauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Permission Model for fine-grained access control")
public class PermissionModel {

	@Schema(description = "Permission ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

	@NotBlank(message = "Permission name is required")
	@Size(min = 3, max = 100, message = "Permission name must be between 3 and 100 characters")
	@Pattern(regexp = "^[A-Z_]+$", message = "Permission name must be uppercase with underscores")
	@Schema(description = "Permission name (UPPERCASE with underscores)",
			example = "USER_CREATE", required = true)
	private String name;

	@Size(max = 200, message = "Description cannot exceed 200 characters")
	@Schema(description = "Permission description",
			example = "Permission to create new users")
	private String description;

	@Schema(description = "Resource category", example = "USER")
	private String resource;

	@Schema(description = "Action type", example = "CREATE",
			allowableValues = {"CREATE", "READ", "UPDATE", "DELETE", "EXECUTE"})
	private String action;

	@Schema(description = "Permission category/group", example = "USER_MANAGEMENT")
	private String category;

	@Schema(description = "Whether permission is active", example = "true")
	private boolean isActive = true;

	@Schema(description = "Permission priority (higher = more important)", example = "1")
	private Integer priority = 0;

	@Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	@Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime updatedAt;

	@Schema(description = "Created by user", accessMode = Schema.AccessMode.READ_ONLY)
	private String createdBy;

	@Schema(description = "Last updated by user", accessMode = Schema.AccessMode.READ_ONLY)
	private String updatedBy;

	@Schema(description = "Permission version for optimistic locking", accessMode = Schema.AccessMode.READ_ONLY)
	private Long version;

	// Constructors
	public PermissionModel(String name) {
		this.name = name.toUpperCase();
		this.isActive = true;
		this.priority = 0;
	}

	public PermissionModel(String name, String description) {
		this.name = name.toUpperCase();
		this.description = description;
		this.isActive = true;
		this.priority = 0;
	}

	public PermissionModel(String name, String description, String resource, String action) {
		this.name = name.toUpperCase();
		this.description = description;
		this.resource = resource;
		this.action = action;
		this.isActive = true;
		this.priority = 0;
	}

	// Static factory methods for common permissions
	public static PermissionModel createPermission(String resource, String action) {
		String name = resource + "_" + action;
		PermissionModel permission = new PermissionModel(name);
		permission.setResource(resource);
		permission.setAction(action);
		permission.setCategory(resource + "_MANAGEMENT");
		permission.setDescription("Permission to " + action.toLowerCase() + " " + resource.toLowerCase() + "s");
		return permission;
	}

	public static PermissionModel createReadPermission(String resource) {
		return createPermission(resource, "READ");
	}

	public static PermissionModel createCreatePermission(String resource) {
		return createPermission(resource, "CREATE");
	}

	public static PermissionModel createUpdatePermission(String resource) {
		return createPermission(resource, "UPDATE");
	}

	public static PermissionModel createDeletePermission(String resource) {
		return createPermission(resource, "DELETE");
	}

	public static PermissionModel createExecutePermission(String resource) {
		return createPermission(resource, "EXECUTE");
	}

	// Convenience methods
	public boolean isSystemPermission() {
		return name != null && (name.startsWith("SYSTEM_") || name.equals("ADMIN_ALL"));
	}

	public String getShortName() {
		if (name != null && name.contains("_")) {
			String[] parts = name.split("_");
			if (parts.length >= 2) {
				return parts[1];
			}
		}
		return name;
	}

	public String getResource() {
		if (resource != null) return resource;
		if (name != null && name.contains("_")) {
			return name.split("_")[0];
		}
		return null;
	}

	public String getAction() {
		if (action != null) return action;
		if (name != null && name.contains("_")) {
			String[] parts = name.split("_");
			if (parts.length >= 2) {
				return parts[parts.length - 1];
			}
		}
		return null;
	}

	// Validation method
	public boolean isValid() {
		return name != null && name.matches("^[A-Z_]+$") && name.length() >= 3 && name.length() <= 100;
	}

	// Getters with proper naming for boolean fields
	@JsonProperty("active")
	public boolean isActive() {
		return isActive;
	}

	// Builder pattern
	public static PermissionModelBuilder builder() {
		return new PermissionModelBuilder();
	}

	public static class PermissionModelBuilder {
		private Long id;
		private String name;
		private String description;
		private String resource;
		private String action;
		private String category;
		private boolean isActive = true;
		private Integer priority = 0;

		public PermissionModelBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public PermissionModelBuilder name(String name) {
			this.name = name != null ? name.toUpperCase() : null;
			return this;
		}

		public PermissionModelBuilder description(String description) {
			this.description = description;
			return this;
		}

		public PermissionModelBuilder resource(String resource) {
			this.resource = resource;
			return this;
		}

		public PermissionModelBuilder action(String action) {
			this.action = action;
			return this;
		}

		public PermissionModelBuilder category(String category) {
			this.category = category;
			return this;
		}

		public PermissionModelBuilder active(boolean active) {
			this.isActive = active;
			return this;
		}

		public PermissionModelBuilder priority(Integer priority) {
			this.priority = priority;
			return this;
		}

		public PermissionModel build() {
			PermissionModel model = new PermissionModel();
			model.setId(id);
			model.setName(name);
			model.setDescription(description);
			model.setResource(resource);
			model.setAction(action);
			model.setCategory(category);
			model.setActive(isActive);
			model.setPriority(priority);
			return model;
		}
	}

	// Override toString for better logging
	@Override
	public String toString() {
		return "PermissionModel{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", resource='" + resource + '\'' +
				", action='" + action + '\'' +
				", isActive=" + isActive +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PermissionModel)) return false;
		PermissionModel that = (PermissionModel) o;
		return name != null && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}