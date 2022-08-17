package be.mkfin.messandcantine.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "availabilities")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    // the maximum number of orders
    @Column
    private int nbreOrder;
    @Column(nullable = false)
    private float price;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Transient
    private LocalDateTime startLocalDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Transient
    private LocalDateTime endLocalDateTime;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;


    @Column(nullable = false)
    private boolean removed = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "availability")
    private Set<Commande> commandes;

}
