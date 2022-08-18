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
    public void save(UserRegistered user) {
        if (user.getUsername().startsWith(",")) {
            user.setUsername(user.getUsername().replaceAll(",", ""));
        }
        user.setPassword(encoder.encode("Welcome@MessAndCantine_PleaseChangeMeASAP"));

        repository.save(user);

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
    public UserRegistered getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication.getPrincipal() instanceof  UserRegistered){
            return (UserRegistered)  authentication.getPrincipal() ;
        }
        return null;
    }
}
