package com.phoenixacces.apps.persistence.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class StockDto implements Serializable {
    private String code;
    private String categorie;
    private String boisson;
    private String stockInitial;
    private String stockDate;
    private String prixVente;
    private String statut;
}
