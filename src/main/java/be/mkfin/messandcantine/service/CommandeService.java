package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Commande;

import java.util.List;

public interface CommandeService {
    List<Commande> getAllMyCommands();
}
