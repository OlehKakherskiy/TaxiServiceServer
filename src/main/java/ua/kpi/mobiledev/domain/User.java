package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.DiscriminatorType.INTEGER;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
@Entity
@Table(name = "user")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = INTEGER)
@DiscriminatorValue("0")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public enum UserType {
        CUSTOMER,

        TAXI_DRIVER
    }

    @Column(name = "user_type", insertable = false, updatable = false)
    @Enumerated(ORDINAL)
    private UserType userType;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Set<MobileNumber> mobileNumbers;
}
