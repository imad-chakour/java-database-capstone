package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    // 2. Constructor Injection for Dependencies
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:604800000}") // 7 days default
    private long jwtExpiration;

    @Autowired
    public TokenService(AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 3. getSigningKey Method
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 4. generateToken Method
    public String generateToken(String identifier, String role) {
        return Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Additional method: Generate token with custom expiration
    public String generateToken(String identifier, String role, long customExpiration) {
        return Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + customExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 5. extractEmail Method (alias for extractIdentifier)
    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    // Extract identifier from token
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Extract role from token
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Extract all claims
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // 6. validateToken Method (with user parameter)
    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) {
                return false;
            }

            switch (user.toLowerCase()) {
                case "admin":
                    return adminRepository.findByUsername(identifier) != null;
                case "doctor":
                    return doctorRepository.findByEmail(identifier) != null;
                case "patient":
                    return patientRepository.findByEmail(identifier) != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Additional method: Generic token validation
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Additional method: Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Additional method: Extract user ID based on role
    public Long extractUserId(String token) {
        try {
            String identifier = extractIdentifier(token);
            String role = extractRole(token);
            
            if (identifier == null || role == null) {
                return null;
            }

            switch (role.toLowerCase()) {
                case "admin":
                    // Admin uses username as identifier
                    return adminRepository.findByUsername(identifier) != null ? 
                           adminRepository.findByUsername(identifier).getId() : null;
                case "doctor":
                    // Doctor uses email as identifier
                    return doctorRepository.findByEmail(identifier) != null ? 
                           doctorRepository.findByEmail(identifier).getId() : null;
                case "patient":
                    // Patient uses email as identifier
                    return patientRepository.findByEmail(identifier) != null ? 
                           patientRepository.findByEmail(identifier).getId() : null;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Extract doctor ID specifically
    public Long extractDoctorId(String token) {
        try {
            String identifier = extractIdentifier(token);
            String role = extractRole(token);
            
            if (!"doctor".equalsIgnoreCase(role) || identifier == null) {
                return null;
            }
            
            return doctorRepository.findByEmail(identifier) != null ? 
                   doctorRepository.findByEmail(identifier).getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Extract patient ID specifically
    public Long extractPatientId(String token) {
        try {
            String identifier = extractIdentifier(token);
            String role = extractRole(token);
            
            if (!"patient".equalsIgnoreCase(role) || identifier == null) {
                return null;
            }
            
            return patientRepository.findByEmail(identifier) != null ? 
                   patientRepository.findByEmail(identifier).getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Extract admin ID specifically
    public Long extractAdminId(String token) {
        try {
            String identifier = extractIdentifier(token);
            String role = extractRole(token);
            
            if (!"admin".equalsIgnoreCase(role) || identifier == null) {
                return null;
            }
            
            return adminRepository.findByUsername(identifier) != null ? 
                   adminRepository.findByUsername(identifier).getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Generate doctor-specific token
    public String generateDoctorToken(Long doctorId, String email) {
        return generateToken(email, "doctor");
    }

    // Additional method: Generate patient-specific token
    public String generatePatientToken(Long patientId, String email) {
        return generateToken(email, "patient");
    }

    // Additional method: Generate admin-specific token
    public String generateAdminToken(Long adminId, String username) {
        return generateToken(username, "admin");
    }

    // Additional method: Get token expiration date
    public Date getExpirationDate(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    // Additional method: Get token issue date
    public Date getIssuedAtDate(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getIssuedAt();
        } catch (Exception e) {
            return null;
        }
    }
}