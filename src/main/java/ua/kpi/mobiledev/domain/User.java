package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
@Entity
@Table(name = "User")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "userType", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUser")
    @Setter
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email; //e.g. email

    public enum UserType {
        CUSTOMER,

        TAXI_DRIVER
    }

    @Column(name = "userType", insertable = false, updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUser", referencedColumnName = "idUser")
    private Set<MobileNumber> mobileNumbers;
}
