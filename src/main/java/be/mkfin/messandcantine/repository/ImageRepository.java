package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
