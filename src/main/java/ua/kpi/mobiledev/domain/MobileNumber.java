package ua.kpi.mobiledev.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Oleg on 05.11.2016.
 */
@Data
@Setter(AccessLevel.NONE)
@Entity
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMobileNumber")
    private Integer id;

    @Column(name = "mobileNumber")
    private String mobileNumber;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User user;
}
