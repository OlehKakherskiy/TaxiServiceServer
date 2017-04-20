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
@Table(name = "district")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_name")
    private String districtName;

    @ManyToOne
    private City city;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Street> streets;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        District district = (District) o;

        if (districtId != null ? !districtId.equals(district.districtId) : district.districtId != null) return false;
        if (!districtName.equals(district.districtName)) return false;
        return city.equals(district.city);
    }

    @Override
    public int hashCode() {
        int result = districtId != null ? districtId.hashCode() : 0;
        result = 31 * result + districtName.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
