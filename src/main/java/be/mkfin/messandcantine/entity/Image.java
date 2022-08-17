package be.mkfin.messandcantine.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column
    private boolean profile;

    @ManyToOne
    @JoinColumn(name = "article")
    private Article  article;

    @Transient
    private String fullUrlImg;

    public String buildPathName(){
        return ""+article.getId()+"_"+id+".jpeg";
    }
}
