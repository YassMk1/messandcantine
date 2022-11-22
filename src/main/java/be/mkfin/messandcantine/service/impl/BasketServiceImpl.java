package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Basket;
import be.mkfin.messandcantine.repository.BasketRepository;
import be.mkfin.messandcantine.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository  basketRepository ;
    @Override
    public Basket findById(Long id) {
        Optional<Basket> byId = basketRepository.findById(id);
        return byId.orElse(null);
    }

    @Override
    public void save(Basket basket) {
        basketRepository.save(basket);
    }
}
