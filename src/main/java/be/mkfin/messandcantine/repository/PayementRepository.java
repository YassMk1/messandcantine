package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Payement;
import be.mkfin.messandcantine.entity.UserRegistered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayementRepository  extends JpaRepository<Payement, Long> {
    List<Payement> findAllByCommandeCommander(UserRegistered connectedEmployee);

    List<Payement> findAllByCommandeAvailabilityArticleCooker(UserRegistered connectedEmployee);
}
