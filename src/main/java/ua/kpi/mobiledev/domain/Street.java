package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "street")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Street {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "street_id")
    private Integer streetId;

    @Column(name = "street_name")
    private String streetName;

    @ManyToOne
    private District district;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Address> addresses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Street street = (Street) o;

        if (streetId != null ? !streetId.equals(street.streetId) : street.streetId != null) return false;
        if (!streetName.equals(street.streetName)) return false;
        return district.equals(street.district);
    }

    @Override
    public int hashCode() {
        int result = streetId != null ? streetId.hashCode() : 0;
        result = 31 * result + streetName.hashCode();
        result = 31 * result + district.hashCode();
        return result;
    }
}
