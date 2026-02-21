package com.nipun.legalscale.feature.admin;

import com.nipun.legalscale.feature.user.UserService;
import com.nipun.legalscale.feature.admin.dto.ChangeRoleRequest;
import com.nipun.legalscale.feature.admin.dto.UserDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService adminService;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/change-role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> changeUserRole(@RequestBody ChangeRoleRequest request) {
        adminService.changeUserRole(request);
        return ResponseEntity.ok("User role updated successfully");
    }

    @PatchMapping("/ban")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> banUser(@RequestParam String email) {
        adminService.banUser(email);
        return ResponseEntity.ok("User banned successfully");
    }

    @PatchMapping("/unban")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> unbanUser(@RequestParam String email) {
        adminService.unbanUser(email);
        return ResponseEntity.ok("User unbanned successfully");
    }
}
