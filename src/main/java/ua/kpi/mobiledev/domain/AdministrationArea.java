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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "administration_area")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdministrationArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_area_id")
    private Integer adminAreaId;

    @Column(name = "admin_area_name")
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "country_id")
    private Country country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdministrationArea that = (AdministrationArea) o;

        if (adminAreaId != null ? !adminAreaId.equals(that.adminAreaId) : that.adminAreaId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return country.equals(that.country);
    }

    @Override
    public int hashCode() {
        int result = adminAreaId != null ? adminAreaId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + country.hashCode();
        return result;
    }
}
