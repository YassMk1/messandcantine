package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository  extends JpaRepository<Availability, Long> {
}
