package com.example.triptrack;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;


/**
 * Esta clase representa un decorador para el día seleccionado en el calendario.
 * Se utiliza para resaltar el día seleccionado en el calendario.
 */

public class SelectedDayDecorator implements DayViewDecorator {
    private final Drawable highlightDrawable; // Drawable para el fondo del día seleccionado
    private CalendarDay selectedDate; // Fecha seleccionada

    /**
     * Método constructor de la clase.
     * Crea un decorador para el día seleccionado en el calendario.
     * @param context contexto de la aplicación
     */
    public SelectedDayDecorator(Context context) {
        // Crear un drawable para el fondo del día seleccionado
        highlightDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.selected_day_background, null);
    }

    /**
     * Método para establecer la fecha seleccionada.
     * @param date fecha seleccionada
     */
    public void setSelectedDate(CalendarDay date) {
        // Establecer la fecha seleccionada
        selectedDate = date;
    }

    /**
     * Método para comprobar si el día debe ser decorado.
     * @param day día del calendario
     * @return true si el día debe ser decorado, false en caso contrario
     */
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Devolver true si el día es igual a la fecha seleccionada
        return day.equals(selectedDate);
    }

    /**
     * Método para decorar el día.
     * @param view vista del día
     */
    @Override
    public void decorate(DayViewFacade view) {
        // Establecer el drawable como fondo del día seleccionado
        view.setSelectionDrawable(highlightDrawable);
    }
}

