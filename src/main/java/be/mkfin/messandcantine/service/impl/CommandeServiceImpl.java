package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Commande;
import be.mkfin.messandcantine.repository.CommandeRepository;
import be.mkfin.messandcantine.service.CommandeService;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommandeServiceImpl implements CommandeService {


    @Autowired
    private CommandeRepository commandeRepository ;
    @Autowired
    private UserService userService ;

    @Override
    public List<Commande> getAllMyCommands() {
        return commandeRepository.findAllByCommander(userService.getConnectedEmployee());
    }
}
