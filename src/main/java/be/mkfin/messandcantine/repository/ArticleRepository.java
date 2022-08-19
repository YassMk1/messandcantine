package be.mkfin.messandcantine.repository;

import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.entity.UserRegistered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByCooker(UserRegistered coocker);
}
