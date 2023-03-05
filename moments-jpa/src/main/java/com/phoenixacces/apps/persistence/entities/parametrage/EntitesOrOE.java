package com.phoenixacces.apps.persistence.entities.parametrage;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_entites", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_gareroutiere", columnNames = {"gareroutiere"})
})
@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EntitesOrOE implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="gareroutiere", nullable = false)
    private String gareRoutiere;


    @Column(name="contact", nullable = false)
    private String contact;


    @Column(name="fax")
    private String fax;


    @Column(name="sitegeogareroutiere", nullable = false)
    private String siteGeoGareRoutiere;


    @Column(name="nomresponsable", nullable = false)
    private String nomresponsable;


    @Column(name="contactresponsable", nullable = false)
    private String contactResponsableGareRoutiere;


    @ManyToOne
    @JoinColumn(name = "compagnie_routiere_id", foreignKey = @ForeignKey(name = "fk_gare_routiere__compagnie_routiere_id"))
    private Entreprises compagnie;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
