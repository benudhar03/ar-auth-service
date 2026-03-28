package com.baseoauth.config;

import com.baseoauth.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class AuthUserDetails implements UserDetails {

	@Serial
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String username;
	private final String password;
	private final String email;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;
	private final Collection<GrantedAuthority> authorities;

	public AuthUserDetails(UserEntity user) {
		this.id = user.getId();
		this.username = user.getUserName();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.accountNonExpired = user.isAccountNonExpired();
		this.accountNonLocked = user.isAccountNonLocked();
		this.credentialsNonExpired = user.isCredentialsNonExpired();
		this.enabled = user.isEnabled();
		this.authorities = extractAuthorities(user);
	}

	private Collection<GrantedAuthority> extractAuthorities(UserEntity user) {
		Set<GrantedAuthority> authorities = new HashSet<>();

		if (user.getRoles() != null) {
			user.getRoles().forEach(role -> {
				// Add role authority
				authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

				// Add permission authorities
				if (role.getPermissions() != null) {
					role.getPermissions().stream()
							.map(permission -> new SimpleGrantedAuthority(permission.getName()))
							.forEach(authorities::add);
				}
			});
		}

		return authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	// Convenience methods for easier access
	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public boolean hasRole(String roleName) {
		return authorities.stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_" + roleName)
						|| auth.getAuthority().equals(roleName));
	}

	public boolean hasPermission(String permission) {
		return authorities.stream()
				.anyMatch(auth -> auth.getAuthority().equals(permission));
	}
}