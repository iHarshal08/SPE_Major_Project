package iiitb.messagingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Employees")
public class Employees {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="password")
    private String password;

    @Column(name="first_name")
    private String firstName;
//
//    @Column(name="last_name")
//    private String lastName;

    @Column(name="email", unique = true, nullable = false)
    private String email;


}
