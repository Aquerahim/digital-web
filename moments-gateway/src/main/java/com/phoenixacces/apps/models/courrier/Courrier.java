package com.phoenixacces.apps.models.courrier;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Courrier implements Serializable {
    private int find;
    private List<CourrierMapping> list;
}
