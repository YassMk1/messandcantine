package be.mkfin.messandcantine.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "basket")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "basket")
    private Set<Commande> commandes = new HashSet<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Transient
    private LocalDateTime orderLocalDateTime;

    @Column(name = "order_date")
    private Timestamp orderDate;

    @ManyToOne
    @JoinColumn(name = "commander", nullable = false)
    private UserRegistered commander;

    public String total(){
        if (commandes == null  || commandes.isEmpty()){
            return "0€";
        }
        else {
           return commandes.stream()
                    .mapToDouble(cmd -> cmd.getQuantity()*cmd.getAvailability().getPrice())
                    .sum()+"€";
        }
    }
    @Column(nullable = false)
    private boolean canceled = false;

    @Column(length = 45, nullable = false)
    @Enumerated(EnumType.STRING)
    private BasketStatus status = BasketStatus.INITIATED;

}
