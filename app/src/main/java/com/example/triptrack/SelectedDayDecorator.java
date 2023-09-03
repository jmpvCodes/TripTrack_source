package com.example.triptrack;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDayDecorator implements DayViewDecorator {
    private final Drawable highlightDrawable;
    private CalendarDay selectedDate;

    public SelectedDayDecorator(Context context) {
        // Crear un drawable para el fondo del día seleccionado
        highlightDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.selected_day_background, null);
    }

    public void setSelectedDate(CalendarDay date) {
        // Establecer la fecha seleccionada
        selectedDate = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Devolver true si el día es igual a la fecha seleccionada
        return day.equals(selectedDate);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Establecer el drawable como fondo del día seleccionado
        view.setSelectionDrawable(highlightDrawable);
    }
}

