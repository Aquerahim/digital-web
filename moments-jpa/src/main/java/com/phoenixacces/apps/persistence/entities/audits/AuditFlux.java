package com.phoenixacces.apps.persistence.entities.audits;

import com.phoenixacces.apps.enumerations.Direction;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_audit_flux", schema = "moments_gateway_db", uniqueConstraints = {
        @UniqueConstraint(name = "uk_audit_flux_key", columnNames = { "idflux","direction" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AuditFlux implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "idflux", updatable = false)
    private String idFlux;

    @NonNull
    @Column(name = "creation", updatable = false)
    private Instant creation;

    @NonNull
    @Column(name = "module", updatable = false)
    private String module;

    @NonNull
    @Column(name = "operation", updatable = false)
    private String operation;

    @NonNull
    @Lob
    @Column(name = "flux", updatable = false)
    private String flux;

    @NonNull
    @Column(name = "exception_message", updatable = false)
    private String exceptionMessage;

    @NonNull
    private Direction direction;

}
