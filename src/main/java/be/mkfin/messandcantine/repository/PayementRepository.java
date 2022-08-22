package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Payement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayementRepository  extends JpaRepository<Payement, Long> {
}
