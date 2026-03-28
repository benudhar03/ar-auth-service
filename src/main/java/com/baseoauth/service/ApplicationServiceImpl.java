package com.baseoauth.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baseoauth.dto.PermissionModel;
import com.baseoauth.dto.RoleModel;
import com.baseoauth.dto.UserModel;
import com.baseoauth.entity.Permission;
import com.baseoauth.entity.Role;
import com.baseoauth.entity.UserEntity;
import com.baseoauth.repository.PermissionRepository;
import com.baseoauth.repository.RoleRepository;
import com.baseoauth.repository.UserRepository;
import com.baseoauth.resp.dto.AbstractResponse;
import com.baseoauth.resp.dto.StatusResponse;

@Service
public class ApplicationServiceImpl implements ApplicationService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder PasswordEncoder; 

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;
	
	@Override
	public ResponseEntity<? extends AbstractResponse> createUser(UserModel userModel) {
		try {
			UserEntity user =  new UserEntity();
			Set<Role> setOfRole = new HashSet<>();
			Optional<UserEntity> optionalUserList = userRepository.findByUserNameOrEmailOrMobileNoEquals(
					userModel.getUserName(),
					userModel.getEmail(),
					userModel.getMobileNo());
			if (optionalUserList.isPresent()) {
				return new ResponseEntity<>(new StatusResponse(201, "User Exist, Please avoid duplication"), HttpStatus.BAD_REQUEST);
			}
			userModel.setPassword(PasswordEncoder.encode(userModel.getPassword()));
			userModel.getRoles().stream().forEach(role->{
				Role userRole = roleRepository.findById(role.getRoleId()).get();
				setOfRole.add(userRole);
			});			
			BeanUtils.copyProperties(userModel, user);
			user.setEnabled(true);
			user.setCredentialsNonExpired(true);
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setRoles(setOfRole);
			userRepository.save(user);
			return new ResponseEntity<>(new StatusResponse(1, "Successfullu User Registered"), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new StatusResponse(0, "Failed, Due to server error"), HttpStatus.NOT_ACCEPTABLE);
		}
	}


	@Override
	public ResponseEntity<? extends AbstractResponse> createRole(RoleModel roleModel) {
		try {
			Role role = new Role();
			Set<Permission> permissions = new HashSet<>();
			System.out.println(roleModel.toString());
			roleModel.setRoleName(roleModel.getRoleName().toUpperCase());
			Optional<Role> optionalRole = roleRepository.findByRoleName(roleModel.getRoleName());
			if (optionalRole.isPresent()) {
				return new ResponseEntity<>(new StatusResponse(201, "Role Exist, Please avoid duplication"), HttpStatus.BAD_REQUEST);
			}
			roleModel.getPermissions().stream().forEach(perm-> {
				Permission entity = permissionRepository.findById(perm.getId()).get();
				permissions.add(entity);
			});			
			BeanUtils.copyProperties(roleModel,role);
			role.setPermissions(permissions);
			System.out.println(role.toString());
			roleRepository.save(role);
			return new ResponseEntity<>(new StatusResponse(1,"Sucessfully Role Created"), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new StatusResponse(0, "Failed, Due to server error"), HttpStatus.NOT_ACCEPTABLE);
		}
	}


	@Override
	public ResponseEntity<? extends AbstractResponse> createPermission(PermissionModel permissionModel) {
		try {
			Permission permission =  new Permission();
			permissionModel.setName(permissionModel.getName().toUpperCase());
			Optional<Permission> optionalPermission = permissionRepository.findByName(permissionModel.getName());			
			if (optionalPermission.isPresent()) {
				return new ResponseEntity<>(new StatusResponse(201, "Permission Exist, Please avoid duplication"), HttpStatus.BAD_REQUEST);
			}			
			BeanUtils.copyProperties(permissionModel, permission);
			permissionRepository.save(permission);
			return new ResponseEntity<>(new StatusResponse(1, "Successfully Permission Stored"), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new StatusResponse(0, "Failed, Due to server error"), HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
}
