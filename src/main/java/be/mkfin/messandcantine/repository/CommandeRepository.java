package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Commande;
import be.mkfin.messandcantine.entity.UserRegistered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findAllByCommander(UserRegistered connectedEmployee);
}
