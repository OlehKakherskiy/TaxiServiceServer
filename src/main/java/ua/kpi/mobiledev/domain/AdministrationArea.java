package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "administration_area")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdministrationArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_area_id")
    private Integer adminAreaId;

    @Column(name = "admin_area_name")
    private String name;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<City> cities;
}
