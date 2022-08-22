package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
