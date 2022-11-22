package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Payement;
import be.mkfin.messandcantine.entity.UserRegistered;
import com.paypal.api.payments.Payment;

import java.util.List;

public interface PayementService {
    Payement findById(Long id);

    void makePaypalPayment(Payement payement, Payment payment);

    Payement save(Payement payement);

    Payement update(Payement payement);

    Payement reject(Payement payement);

    List<Payement> getAllMyPayemens();

    List<Payement> getAllPayemensOfCooker(UserRegistered connectedCooker);
}
