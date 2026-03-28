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
@Table(name = "role", schema = "admin")
public class Role implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "role_name", unique = true, nullable = false, length = 50)
	private String roleName;

	@Column(name = "description", length = 200)
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "role_permission",
			schema = "admin",
			joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
	)
	private Set<Permission> permissions = new HashSet<>();

	@ManyToMany(mappedBy = "roles")
	private Set<UserEntity> users = new HashSet<>();

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Convenience methods
	public void addPermission(Permission permission) {
		if (permissions == null) {
			permissions = new HashSet<>();
		}
		permissions.add(permission);
	}

	public void removePermission(Permission permission) {
		if (permissions != null) {
			permissions.remove(permission);
		}
	}

	// Explicit getter methods (optional, but ensures clarity)
	public String getRoleName() {
		return roleName;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}
}