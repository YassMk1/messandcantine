package be.mkfin.messandcantine.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity(name = "user_registered")
@Getter
@Setter
public  class UserRegistered implements UserDetails, Serializable {

    private static final long serialVersionUID = 5155720064139820502L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(length = 30, nullable = false, unique= true) // Chaque utilisateur aura un « login » unique
    private  String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "firstname",length = 60, nullable = false)
    private  String firstName;

    @Column(name = "lastname",length = 60, nullable = false)
    private  String lastName;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 100)
    private String mail;

    @Column(length = 12)
    private String phone;

    // This field is designed for cooker
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cooker")
    private Set<Article> articles;
    
    private String token;

    private boolean active;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public final Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> collect = new ArrayList<>();
        collect.add(new SimpleGrantedAuthority(role.asString()));
        return collect;
    }

    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    
    @Override
    public boolean isAccountNonLocked() {
        return isActive();
    }

    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }


    public enum Role {
        ADMIN, EMPLOYEE, COOKER;
        public String asString() {
            return "ROLE_" + name();
        }

    }

}
