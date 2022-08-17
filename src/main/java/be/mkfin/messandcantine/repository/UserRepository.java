package be.mkfin.messandcantine.repository;


import be.mkfin.messandcantine.entity.UserRegistered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserRegistered, Long> {


    UserRegistered findByUsername(String username);
}
