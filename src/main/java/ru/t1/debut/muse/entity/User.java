package ru.t1.debut.muse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.debut.muse.dto.UserDTO;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long externalId;
    @Column
    private UUID internalId;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column
    private String name;
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts;
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Vote> votes;

    public User(UserDTO userDTO) {
        this.id = userDTO.id();
        this.externalId = userDTO.externalId();
        this.internalId = userDTO.internalId();
        this.userType = userDTO.userType();
        this.name = userDTO.name();
    }
}

