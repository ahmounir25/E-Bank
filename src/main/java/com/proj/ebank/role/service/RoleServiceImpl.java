package com.proj.ebank.role.service;

import com.proj.ebank.exceptions.BadRequestException;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.response.Response;
import com.proj.ebank.role.entity.Role;
import com.proj.ebank.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;


    @Override
    public Response<Role> createRole(Role role) {

        if (roleRepo.findByName(role.getName()).isPresent()) {
            throw new BadRequestException("Role already exists");
        }
        Role savedRole = roleRepo.save(role);

        return Response.<Role>builder()
                .StatusCode(HttpStatus.CREATED.value())
                .message("New Role has been created")
                .data(savedRole).build();
    }

    @Override
    public Response<Role> updateRole(Role role) {

        Role fetchedRole = roleRepo.findById(role.getId())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        fetchedRole.setName(role.getName());

        Role updatedRole = roleRepo.save(fetchedRole);

        return Response.<Role>builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Role has been updated")
                .data(updatedRole).build();
    }

    @Override
    public Response<?> deleteRole(Long roleId) {
        if(!roleRepo.existsById(roleId)){
            throw  new NotFoundException("Role Not Found");
        }

        roleRepo.deleteById(roleId);

        return Response.builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Role has been deleted")
                .build();
    }

    @Override
    public Response<List<Role>> getAllRoles() {

        List<Role> allRoles = roleRepo.findAll();

        return Response.<List<Role>>builder()
                .StatusCode(HttpStatus.OK.value())
                .message(" get all roles successfully")
                .data(allRoles)
                .build();
    }
}
