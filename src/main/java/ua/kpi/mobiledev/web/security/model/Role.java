package ua.kpi.mobiledev.web.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Embeddable;
import javax.persistence.Table;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "authority")
public class Role {

    private GrantedAuthority authority;
}
