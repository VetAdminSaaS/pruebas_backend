package apiFactus.factusBackend.integration.Authentication.Google;

import apiFactus.factusBackend.Security.TokenProvider;
import apiFactus.factusBackend.Service.UsuarioService;
 // Importa tu servicio de usuarios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UsuarioService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");


        String role = String.valueOf(userService.getUserRoleByEmail(email));

        String token = tokenProvider.createAccessToken(email, role);

        System.out.println("Token generado con rol: " + token);

        return oauth2User;
    }
}
