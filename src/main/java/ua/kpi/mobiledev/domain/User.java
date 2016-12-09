package ua.kpi.mobiledev.domain;

import com.sun.istack.internal.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Oleg on 05.11.2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
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
    private Integer id;

    @Column(name = "name")
    @NonNull
    private String name;

    @Column(name = "email")
    @NotNull
    private String email;

    public enum UserType {
        CUSTOMER,

        TAXI_DRIVER
    }

    @Column(name = "userType", insertable = false, updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<MobileNumber> mobileNumbers;
}
