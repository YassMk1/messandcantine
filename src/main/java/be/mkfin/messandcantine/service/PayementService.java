package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Payement;
import com.paypal.api.payments.Payment;

public interface PayementService {
    Payement findById(Long id);

    void makePaypalPayment(Payement payement, Payment payment);

    Payement save(Payement payement);

    Payement update(Payement payement);

    Payement reject(Payement payement);
}
