package com.example.triptrack;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Esta clase representa un plan programado.
 * Se utiliza para almacenar los planes programados de un viaje.
 */
public class Plans {
    private String tripId;
    private long date;
    private String time;
    private String location;
    private int priority;
    private String description;

    /**
     * Método constructor de la clase.
     * Crea un plan programado.
     * @param tripId ID del viaje
     * @param date fecha del plan
     * @param time hora del plan
     * @param location ubicación del plan
     * @param priority prioridad del plan
     * @param description descripción del plan
     */
    public Plans(String tripId, long date, String time, String location, int priority, String description) {
        this.tripId = tripId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.priority = priority;
        this.description = description;
    }

    /**
     * Método para obtener el ID del viaje.
     * @return ID del viaje
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Método para obtener la fecha del plan.
     * @return fecha del plan
     */
    public long getDate() {
        return date;
    }

    /**
     * Método para obtener la hora del plan.
     * @return hora del plan
     */
    public String getTime() {
        return time;
    }

    /**
     * Método para obtener la ubicación del plan.
     * @return ubicación del plan
     */
    public String getLocation() {
        return location;
    }

    /**
     * Método para obtener la prioridad del plan.
     * @return prioridad del plan
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Método para obtener la descripción del plan.
     * @return descripción del plan
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para establecer el id del plan.
     * @param tripId id del plan
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Método para establecer la fecha del plan.
     * @param time hora del plan
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Método para establecer la ubicación del plan.
     * @param location ubicación del plan
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Método para establecer la prioridad del plan.
     * @param priority prioridad del plan
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Método para establecer la descripción del plan.
     * @param description descripción del plan
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método para obtener una representación en cadena del plan.
     * @return representación en cadena del plan
     */
    @NotNull
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha
        String dateString = dateFormat.format(new Date(date));

        return dateString + " - " + time + " - " + location + " - " + description;
    }
}
