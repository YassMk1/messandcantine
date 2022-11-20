package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.entity.Commande;
import be.mkfin.messandcantine.entity.Payement;
import be.mkfin.messandcantine.entity.PayementStatus;
import be.mkfin.messandcantine.repository.AvailabilityRepository;
import be.mkfin.messandcantine.repository.BasketRepository;
import be.mkfin.messandcantine.repository.PayementRepository;
import be.mkfin.messandcantine.service.PayementService;
import be.mkfin.messandcantine.service.UserService;
import com.paypal.api.payments.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class PayementServiceImpl implements PayementService {

    @Autowired
    PayementRepository payementRepository ;
    @Autowired
    UserService userService ;

    @Autowired
    BasketRepository basketRepository ;
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

        Optional<Commande> first = payement.getBasket().getCommandes()
                .stream()
                .filter(commande -> {
                    Availability availability = availabilityRepository.findById(commande.getAvailability().getId()).orElse(null);
                    return availability == null && commande.getQuantity() > availability.getNbreOrder();

                })
                .findFirst();
        if (first.isPresent()){
            throw new IllegalStateException(String.format("Too late , there is no more items available for the article %s ",first.get().getAvailability().getArticle().getName()));
        }
        payement.getBasket().getCommandes()
                .stream().forEach(cmd -> {
                    Availability availability =availabilityRepository.findById(cmd.getAvailability().getId()).orElseGet(null) ;
                   if( availability != null){
                       availability.setNbreOrder(availability.getNbreOrder() - cmd.getQuantity());
                       availabilityRepository.save(availability);
                   }

                });



        payement.setBasket(basketRepository.save(payement.getBasket()));
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
        Optional<Commande> first = payement.getBasket().getCommandes()
                .stream()
                .filter(commande -> {
                    Availability availability = availabilityRepository.findById(commande.getAvailability().getId()).orElse(null);
                    return availability == null && commande.getQuantity() > availability.getNbreOrder();

                })
                .findFirst();
        if (first.isPresent()){
            throw new IllegalStateException(String.format("can't find availability "));
        }
        payement.getBasket().getCommandes()
                .stream().forEach(cmd -> {
                    Availability availability =availabilityRepository.findById(cmd.getAvailability().getId()).orElseGet(null) ;
                    if( availability != null){
                        availability.setNbreOrder(availability.getNbreOrder() - cmd.getQuantity());
                        availabilityRepository.save(availability);
                    }

                });
        return payementRepository.save(payement);
    }

    @Override
    public List<Payement> getAllMyPayemens() {
        return payementRepository.findAllByBasketCommander(userService.getConnectedEmployee());
    }
}
