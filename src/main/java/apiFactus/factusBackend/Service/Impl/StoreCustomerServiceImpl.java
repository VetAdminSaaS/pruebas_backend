package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Customer;
import apiFactus.factusBackend.Domain.Entity.Purchase;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Domain.enums.PaymentStatus;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UsuariosStoreDTO;
import apiFactus.factusBackend.Mapper.PurchaseMapper;
import apiFactus.factusBackend.Mapper.UserMapper;
import apiFactus.factusBackend.Repository.CustomerRepository;
import apiFactus.factusBackend.Repository.PurchaseRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.StoreService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreCustomerServiceImpl implements StoreService {
    private final CustomerRepository customerRepository;
    private final UserMapper userMapper;
    private final UsuarioRepository usuarioRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public UsuariosStoreDTO obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return userMapper.toUsuariosStoreDTO(customer);
    }
    @Transactional(readOnly = true)
    @Override
    public List<UserProfileDTO> findAll() {
        List<UserProfileDTO> list = usuarioRepository.findAll()
                .stream()
                .filter(user -> user.getRole().getName().equals(ERole.CUSTOMER))
                .map(userMapper::toUserProfileDTO)
                .collect(Collectors.toList());

        Collections.reverse(list); // Invertir el orden
        return list;
    }

    @Override
    public List<PurchaseDTO> getLastSixPaidPurchasesByAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = null;
        if(authentication !=null && !authentication.getPrincipal().equals("anonymousUser")){
            usuario = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
        }
        if(usuario == null || usuario.getCustomer() == null) {
            throw new RuntimeException("El usuario no tiene un cliente asociado.");
        }
        List<Purchase> purchases = purchaseRepository.findTop10ByUserAndPaymentStatusOrderByCreatedAtDesc(usuario, PaymentStatus.PAID);
        return  purchases.stream()
                .map(purchaseMapper::toPurchaseDTO)
                .collect(Collectors.toList());
    }






}
