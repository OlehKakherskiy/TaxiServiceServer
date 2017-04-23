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

import static javax.persistence.CascadeType.MERGE;

@Entity
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "house_number")
    private String houseNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, MERGE})
    @JoinColumn(name = "street_id")
    private Street street;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (addressId != null ? !addressId.equals(address.addressId) : address.addressId != null) return false;
        if (!houseNumber.equals(address.houseNumber)) return false;
        return street.equals(address.street);
    }

    @Override
    public int hashCode() {
        int result = addressId != null ? addressId.hashCode() : 0;
        result = 31 * result + houseNumber.hashCode();
        result = 31 * result + street.hashCode();
        return result;
    }
}
