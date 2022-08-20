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
    @Column(nullable = false, columnDefinition = "TEXT")
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "article")
    private Set<Availability> availabilities;

    public Image getMainImage() {
        if (images != null && !images.isEmpty()) {
            Image mainImage = images.iterator().next();
            return images.stream().filter(img -> img.isProfile()).findFirst().orElse(mainImage);
        }
        return new Image();
    }

    public boolean haveImage() {
        return images != null && ! images.isEmpty();

    }

}
