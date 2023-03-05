package com.phoenixacces.apps.persistence.entities.authentication;


import com.phoenixacces.apps.enumerations.Gender;
import com.phoenixacces.apps.enumerations.ProfileType;
import com.phoenixacces.apps.persistence.entities.parametrage.EntitesOrOE;
import com.phoenixacces.apps.utiles.sequences.DatePrefixedSequenceIdGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_profiles", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Profile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_digital")
    @GenericGenerator(
            name = "s_digital",
            strategy = "com.phoenixacces.apps.utiles.sequences.DatePrefixedSequenceIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = DatePrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1")
            }
    )

    @Column(name = "iddigital", length = 10)
    private String idDigital;


    @Column(name = "genre")
    private Gender genre;


    @Column(name = "nomprenoms")
    private String nomPrenoms;


    @Column(name = "birthdate")
    private LocalDate birthdate;


    @Column(name = "phone", length = 20)
    private String phone;


    @Column(name = "profileType")
    private ProfileType profileType;


    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "gare_routiere_id", foreignKey = @ForeignKey(name = "fk__profile__gare_routiere_id"))
    private EntitesOrOE gareRoutiere;


    @Column(name="isAccount", length = 1)
    private boolean isAccount;

    @NonNull
    private boolean active;


    private Instant creation;

    private Instant lastUpdate;

    @Transient
    private String username;


    @Column(name = "module")
    private String module;


    @Column(name = "facebook")
    private String facebook;


    @Column(name = "twitter")
    private String twitter;


    @Column(name = "instagram")
    private String instagram;


    @Column(name = "skype")
    private String skype;
}
