package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;

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
    private Integer idMobileNumber;

    @Column(name = "mobileNumber")
    @NonNull
    private String mobileNumber;

}
