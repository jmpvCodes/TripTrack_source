package com.example.triptrack;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Esta clase representa un marcador personalizado.
 * Se utiliza para mostrar un marcador personalizado en el mapa.
 * Los marcadores personalizados se utilizan para mostrar los lugares de interés de un viaje.
 */
public class CustomOverlayItem extends OverlayItem {
    private String id;

    /**
     * Método constructor de la clase.
     * Crea un marcador personalizado.
     * @param aTitle título del marcador
     * @param aDescription descripción del marcador
     * @param aGeoPoint coordenadas del marcador
     */
    public CustomOverlayItem(String aTitle, String aDescription, GeoPoint aGeoPoint) {
        super(aTitle, aDescription, aGeoPoint);
    }

    /**
     * Método para obtener el ID del marcador.
     * @return ID del marcador
     */
    public String getId() {
        return id;
    }

    /**
     * Método para establecer el ID del marcador.
     * @param id ID del marcador
     */
    public void setId(String id) {
        this.id = id;
    }
}

