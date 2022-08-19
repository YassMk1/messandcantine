package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.repository.UserRepository;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository ;
    @Autowired
    private PasswordEncoder encoder ;
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication.getPrincipal() instanceof String ) ;
    }

    @Override
    public List<UserRegistered> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public UserRegistered save(UserRegistered user) {
        if (user.getUsername().startsWith(",")) {
            user.setUsername(user.getUsername().replaceAll(",", ""));
        }
        // Generate the token
        String token = UUID.randomUUID().toString();
        user.setToken(token); // ça sera utilisé plutard pour valider les adresse mails
        user.setActive(true); // tout les nouveaux utilisateurs sont active pour le moment, on doit switcher ce field à false lorsque on introduit la validation du boite mail.
        user.setPassword(encoder.encode(user.getPassword()));
        user.setPhone(user.getPhone().replaceAll("-",""));
       return  repository.save(user);
    }

    @Override
    public UserRegistered getConnectedEmployee() {
        UserRegistered employee = getConnectedUser() ;
        if (employee != null && employee.getRole() == UserRegistered.Role.EMPLOYEE){
            return employee ;
        }
        return  null ;
    }
    @Override
    public UserRegistered getConnectedCooker() {
        UserRegistered cooker = getConnectedUser() ;
        if (cooker != null && cooker.getRole() == UserRegistered.Role.COOKER){
            return cooker ;
        }
        return  null ;
    }

    @Override
    public UserRegistered getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication.getPrincipal() instanceof  UserRegistered){
            return (UserRegistered)  authentication.getPrincipal() ;
        }
        return null;
    }
}
