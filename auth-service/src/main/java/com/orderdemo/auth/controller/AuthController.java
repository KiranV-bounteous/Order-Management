package com.orderdemo.auth.controller;

import com.orderdemo.auth.model.AuthResponse;
import com.orderdemo.auth.model.LoginRequest;
import com.orderdemo.auth.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    // Private maps for proper encapsulation
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> roles = new HashMap<>();

    @Autowired
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        users.put("admin", "admin");
        roles.put("admin", "ADMIN");
        users.put("trader", "trader");
        roles.put("trader", "TRADER");
        users.put("viewer", "viewer");
        roles.put("viewer", "VIEWER");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(401).body("Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(401).body("Password cannot be empty");
        }

        String storedPassword = users.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String role = roles.get(username);
            String token = jwtUtil.generateToken(username, role);
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
