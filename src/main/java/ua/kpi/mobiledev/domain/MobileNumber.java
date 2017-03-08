package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter(AccessLevel.NONE)
@Entity
@Table(name = "mobile_number")
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mobile_number_id")
    private Integer idMobileNumber;

    @Column(name = "mobile_number")
    @NonNull
    private String mobileNumber;

}
