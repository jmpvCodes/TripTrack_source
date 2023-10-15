package com.example.triptrack;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Esta clase representa un decorador para un día con plan registrado.
 * Se utiliza para resaltar los días con planes registrados en el calendario.
 * Los días con planes registrados se resaltan con un círculo de color.
 * Los colores de los círculos dependen de la prioridad de los planes.
 */
public class PlanDayDecorator implements DayViewDecorator {
    
    private final CalendarDay date;
    private final List<CustomCircleSpan> spans;

    /**
     * Método constructor de la clase.
     * Crea un decorador para un día con plan registrado. 
     * @param date fecha del plan
     * @param spans lista de CustomCircleSpan para el día con plan registrado
     */
    public PlanDayDecorator(CalendarDay date, List<CustomCircleSpan> spans) {
        this.date = date;
        this.spans = spans;
    }

    /**
     * Método para comprobar si el día debe ser decorado.
     * @param day día del calendario
     * @return true si el día debe ser decorado, false en caso contrario
     */	
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Devolver true si el día tiene un plan registrado
        return day.equals(date);
    }

    /**
     * Método para decorar el día.
     * @param view vista del día
     */
    @Override
    public void decorate(DayViewFacade view) {
        // Agregar múltiples CustomCircleSpan a la vista del día
        for (CustomCircleSpan span : spans) {
            view.addSpan(span);
        }
    }
}
