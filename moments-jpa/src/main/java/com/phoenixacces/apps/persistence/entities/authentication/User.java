package com.phoenixacces.apps.persistence.entities.authentication;

import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_users", schema = "moments_gateway_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Nullable
    @Column(name="username", length = 50)
    private String username;


    @Nullable
    private String password;


    @Nullable
    @Column(name="default_password")
    private String defaultPassword;


    @Nullable
    @Column(name="roles", length = 50)
    private String roles;


    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__users__profile_id"))
    private Profile profile;



    @Column(name="first_cnx", length = 1)
    private int firstConnexion;


    @Temporal(TemporalType.DATE)
    @Column(name="date_chang_pwd")
    private Date dateChangPwd = new Date();


    @Column(name="active")
    private boolean active;


    @Column(name="version", length = 1)
    private int version;

    private Instant creation;

    private Instant lastUpdate;

    private Instant lastConnexion;
}
