package com.proj.ebank.role.service;

import com.proj.ebank.response.Response;
import com.proj.ebank.role.entity.Role;

import java.util.List;

public interface RoleService {
    Response<Role> createRole(Role role);

    Response<Role> updateRole(Role role);

    Response<?> deleteRole(Long roleId);

    Response<List<Role>> getAllRoles();

}
