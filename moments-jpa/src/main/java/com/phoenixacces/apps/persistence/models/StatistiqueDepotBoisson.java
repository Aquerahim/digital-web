package com.phoenixacces.apps.persistence.models;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class StatistiqueDepotBoisson implements Serializable {

    private double mtVenteEspece;

    private double mtVenteCredit;

    private double mtAccompte;

    private double mtDepense;

    private double mtRemise;

    private double solde;

    private List<StockDto> stockDtoList;
}
