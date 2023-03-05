package com.phoenixacces.apps.persistence.entities.template;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "t_templates", schema = "moments_gateway_db")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Templates implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="code", nullable = false, length = 5)
    private String code;


    @Column(name="type", nullable = false, length = 60)
    private String type;


    @Lob
    @Column(name="content")
    private String content;


    @Column(name="active", nullable = false)
    private boolean active;


    @Column(name="version", length = 1, nullable = false)
    private int version;


    private Instant creation;

    private Instant lastUpdate;
}
