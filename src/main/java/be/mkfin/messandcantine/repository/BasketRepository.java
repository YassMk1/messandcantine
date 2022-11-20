package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Basket;
import be.mkfin.messandcantine.entity.Commande;
import be.mkfin.messandcantine.entity.UserRegistered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findAllByCommander(UserRegistered connectedEmployee);
}
