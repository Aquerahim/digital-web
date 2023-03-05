package com.phoenixacces.apps.persistence.entities.parametrage;

import com.phoenixacces.apps.enumerations.TypeNotification;
import com.phoenixacces.apps.persistence.entities.authentication.Profile;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_notification_systeme", schema = "digitalweb_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk__reference_request", columnNames = {"reference"})
})
@Data
@Inheritance
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "Notification Systeme", description = "Modele pour la Notification sur toute les opérations effectuées sur le compte")
public class NotificationSysteme implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "reference")
    private String reference;

    @NonNull
    @Column(name = "type")
    private TypeNotification type;

    @NonNull
    @Column(name = "notification")
    private String notification;

    @ManyToOne
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk__notification__profile_id"))
    private Profile profile;

    @NonNull
    private boolean active;

    private Instant creation;

    private Instant lastUpdate;
}
