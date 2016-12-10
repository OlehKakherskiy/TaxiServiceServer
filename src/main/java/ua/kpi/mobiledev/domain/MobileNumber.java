package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;

/**
 * Created by Oleg on 05.11.2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter(AccessLevel.NONE)
@Entity
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMobileNumber")
    private Integer id;

    @Column(name = "mobileNumber")
    @NonNull
    private String mobileNumber;

}
