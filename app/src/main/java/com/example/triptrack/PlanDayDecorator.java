package com.example.triptrack;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class PlanDayDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final List<CustomCircleSpan> spans;

    public PlanDayDecorator(CalendarDay date, List<CustomCircleSpan> spans) {
        this.date = date;
        this.spans = spans;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Devolver true si el día tiene un plan registrado
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Agregar múltiples CustomCircleSpan a la vista del día
        for (CustomCircleSpan span : spans) {
            view.addSpan(span);
        }
    }
}
