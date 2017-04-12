package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.DiscriminatorType.INTEGER;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private List<MobileNumber> mobileNumbers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (!name.equals(user.name)) return false;
        if (!email.equals(user.email)) return false;
        if (userType != user.userType) return false;
        return mobileNumbers.equals(user.mobileNumbers);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + userType.hashCode();
        result = 31 * result + mobileNumbers.hashCode();
        return result;
    }
}
