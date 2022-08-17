package be.mkfin.messandcantine.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Payement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;

    @Column(length = 45, nullable = false)
    @Enumerated(EnumType.STRING)
    private PayementStatus status;

    @Column(length = 45, nullable = false)
    private Date date;

    @Column(length = 45)
    private String message;
    @Column(length = 45)
    private String amount;

    @Column
    private String paypalPayementId ;
    @Column
    private String refundID ;

    @Column(columnDefinition = "TEXT")
    private String paypalPayment ;
}
