package ua.kpi.mobiledev.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@ToString
@Setter
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

    public MobileNumber(Integer idMobileNumber, String mobileNumber) {
        this.idMobileNumber = idMobileNumber;
        this.mobileNumber = mobileNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MobileNumber that = (MobileNumber) o;

        if (idMobileNumber != null ? !idMobileNumber.equals(that.idMobileNumber) : that.idMobileNumber != null)
            return false;
        return mobileNumber.equals(that.mobileNumber);
    }

    @Override
    public int hashCode() {
        int result = idMobileNumber != null ? idMobileNumber.hashCode() : 0;
        result = 31 * result + mobileNumber.hashCode();
        return result;
    }
}
