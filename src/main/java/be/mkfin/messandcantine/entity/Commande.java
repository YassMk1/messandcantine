package be.mkfin.messandcantine.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "commande")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Transient
    private LocalDateTime orderLocalDateTime;

    @Column(name = "order_date")
    private Timestamp orderDate;

    @Column
    private int quantity;

    @Column(nullable = false)
    private boolean canceled = false;

    @ManyToOne
    @JoinColumn(name = "commander", nullable = false)
    private UserRegistered commander;

}
