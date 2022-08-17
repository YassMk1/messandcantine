package be.mkfin.messandcantine.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 60, nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition="TEXT")
    private String description;

    @Column(length = 45)
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cooker")
    private UserRegistered cooker;

    @Column(nullable = false)
    private boolean removed = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article")
    private Set<Image> images;

}