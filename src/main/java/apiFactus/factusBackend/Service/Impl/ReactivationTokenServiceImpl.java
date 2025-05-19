package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.reactivationToken;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.reactivationToken;
import apiFactus.factusBackend.Repository.ReactivationTokenRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.TokenReactivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactivationTokenServiceImpl implements TokenReactivationService {
    private final ReactivationTokenRepository reactivationTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public reactivationToken generarToken(Usuario usuario) {
        Optional<reactivationToken> existingToken = reactivationTokenRepository.findByUsuario(usuario);

        if (existingToken.isPresent()) {
            reactivationToken tokenEntity = existingToken.get();
            tokenEntity.setToken(UUID.randomUUID().toString());
            tokenEntity.setExpirationDate(LocalDateTime.now().plusHours(24));
            tokenEntity.setCreatedAt(LocalDateTime.now());
            return reactivationTokenRepository.save(tokenEntity);
        } else {
            reactivationToken newToken = new reactivationToken();
            newToken.setToken(UUID.randomUUID().toString());
            newToken.setUsuario(usuario);
            newToken.setExpirationDate(LocalDateTime.now().plusHours(24));
            newToken.setCreatedAt(LocalDateTime.now());
            return reactivationTokenRepository.save(newToken);
        }
    }

    @Override
    public void validarYReactivarCuenta(String token) {
        reactivationToken reactivationToken = reactivationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (reactivationToken.isExpired()) {
            throw new IllegalArgumentException("Token inválido o expirado. Solicita uno nuevo.");
        }

        Usuario usuario = reactivationToken.getUsuario();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        reactivationTokenRepository.delete(reactivationToken);
    }
}