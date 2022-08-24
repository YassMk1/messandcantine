package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.entity.Payement;
import be.mkfin.messandcantine.entity.PayementStatus;
import be.mkfin.messandcantine.repository.AvailabilityRepository;
import be.mkfin.messandcantine.repository.CommandeRepository;
import be.mkfin.messandcantine.repository.PayementRepository;
import be.mkfin.messandcantine.service.PayementService;
import be.mkfin.messandcantine.service.UserService;
import com.paypal.api.payments.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PayementServiceImpl implements PayementService {

    @Autowired
    PayementRepository payementRepository ;
    @Autowired
    UserService userService ;

    @Autowired
    CommandeRepository commandeRepository ;
    @Autowired
    AvailabilityRepository availabilityRepository ;
    @Override
    public Payement findById(Long id) {
        return payementRepository.findById(id).orElse(null);
    }

    @Override
    public void makePaypalPayment(Payement payement, Payment payment) {
           // payement.setDate(new Date());
            payement.setStatus(PayementStatus.PAYPAL_TENTATIVE);
            payement.setPaypalPayementId(payment.getId());
            payementRepository.save(payement);
    }


    @Override
    public Payement save(Payement payement) {
        Availability availability = availabilityRepository.findById(payement.getCommande().getAvailability().getId()).orElse(null);
        if( availability == null && payement.getCommande().getQuantity() >availability.getNbreOrder()){
            throw new IllegalStateException("Too late , there is no more items available for your command ");
        }
        availability.setNbreOrder(availability.getNbreOrder() - payement.getCommande().getQuantity());
        availabilityRepository.save(availability);
        payement.setCommande(commandeRepository.save(payement.getCommande()));
        payement.setDate(new Date());
        payement.setStatus(PayementStatus.INITIATED);
        return payementRepository.save(payement);
    }

    @Override
    public Payement update(Payement payement) {
        return payementRepository.save(payement);
    }

    @Override
    public Payement reject(Payement payement) {
        Availability availability = availabilityRepository.findById(payement.getCommande().getAvailability().getId()).orElse(null);
        if( availability == null ){
            throw new IllegalStateException("Can't find availability ");
        }
        availability.setNbreOrder(availability.getNbreOrder()+ payement.getCommande().getQuantity());
        availabilityRepository.save(availability);
        return payementRepository.save(payement);
    }

    @Override
    public List<Payement> getAllMyPayemens() {
        return payementRepository.findAllByCommandeCommander(userService.getConnectedEmployee());
    }
}
