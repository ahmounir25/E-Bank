package com.proj.ebank.role.controller;

import com.proj.ebank.response.Response;
import com.proj.ebank.role.entity.Role;
import com.proj.ebank.role.service.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {
    private final RoleServiceImpl roleService;

    @PostMapping
    public ResponseEntity<Response<Role>> createRole(@RequestBody Role roleReq) {
        return ResponseEntity.ok(roleService.createRole(roleReq));
    }

    @PutMapping
    public ResponseEntity<Response<Role>> updateRole(@RequestBody Role roleReq) {
        return ResponseEntity.ok(roleService.updateRole(roleReq));
    }

    @GetMapping
    public ResponseEntity<Response<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }

}
