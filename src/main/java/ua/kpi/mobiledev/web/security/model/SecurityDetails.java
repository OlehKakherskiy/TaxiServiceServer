package ua.kpi.mobiledev.web.security.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "users")
@Table(name = "user_credential")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SecurityDetails implements UserDetails {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @Setter
    private String password;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authority", joinColumns = @JoinColumn(name = "username"))
    private List<Role> authorityList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList.stream().map(Role::getAuthority).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
