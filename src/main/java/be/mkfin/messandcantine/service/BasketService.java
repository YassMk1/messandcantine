package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Basket;

public interface BasketService {
    Basket findById(Long id);

    void save(Basket basket);
}
