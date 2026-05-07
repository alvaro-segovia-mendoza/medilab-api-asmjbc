package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'user_profiles'.
 *
 * Relación 1:1 con 'users' mediante primary
 */
@Data
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    /** BIGINT PRIMARY KEY, también FK a users.id */
    @Id
    @Column(name = "user_id")
    private Long id;

    /**
     * Relación 1:1 con User usando clave primaria compartida.
     * 'user_id' actúa como PK y FK a 'users.id'.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    /** VARCHAR(60) NULL */
    @Column(name = "first_name", length = 60)
    private String firstName;

    /** VARCHAR(80) NULL */
    @Column(name = "last_name", length = 80)
    private String lastName;

    /** VARCHAR(30) NULL */
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    /** VARCHAR(255) NULL - Ruta/URL de la imagen de perfil */
    @Column(name = "profile_image", length = 255)
    private String profileImage;

    /** VARCHAR(500) NULL - Pequeña descripción / biografía */
    @Column(name = "bio", length = 500)
    private String bio;

    /** VARCHAR(10) NULL - Código idioma/locale (es_ES, en_ES...) */
    @Column(name = "locale", length = 10)
    private String locale;

    /** VARCHAR(20) NULL UNIQUE */
    @Column(name = "dni", unique = true, length = 20)
    private String dni;

    /** DATE NULL */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /** VARCHAR(150) NULL */
    @Column(name = "address", length = 150)
    private String address;

    /** VARCHAR(50) NULL */
    @Column(name = "city", length = 50)
    private String city;

    /** VARCHAR(50) NULL */
    @Column(name = "province", length = 50)
    private String province;

    /** DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor de conveniencia sin campos de auditoría.
     * El id se tomará del User asociado (por @MapsId).
     */
    public UserProfile(User user,
                       String firstName,
                       String lastName,
                       String phoneNumber,
                       String profileImage,
                       String bio,
                       String locale,
                       String dni,
                       LocalDate dateOfBirth,
                       String address,
                       String city,
                       String province) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.bio = bio;
        this.locale = locale;
        this.dni = dni;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.province = province;
    }
}
