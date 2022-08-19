package be.mkfin.messandcantine.service;


import be.mkfin.messandcantine.entity.UserRegistered;

import java.util.List;

public interface UserService {


     boolean isAuthenticated();

     List<UserRegistered> getAllUsers();

    UserRegistered save(UserRegistered user);

    UserRegistered getConnectedEmployee();
    UserRegistered getConnectedCooker();
    UserRegistered getConnectedUser();

}
