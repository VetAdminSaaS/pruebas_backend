package apiFactus.factusBackend.Security;

import apiFactus.factusBackend.Domain.Entity.Usuario;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



import java.util.Collection;
import java.util.Collections;

@Data
public class UsuarioPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String contrasena;
    private Collection<? extends GrantedAuthority> authorities;
    private Usuario usuario;
    public UsuarioPrincipal(Long id, String email, String contrasena, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.contrasena = contrasena;
        this.authorities = authorities;
    }
    public static UsuarioPrincipal create(Usuario usuario) {
        String roleName = usuario.getRole().getName().name();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new UsuarioPrincipal(
                usuario.getId().longValue(),
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singleton(authority)
        );
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return contrasena;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
