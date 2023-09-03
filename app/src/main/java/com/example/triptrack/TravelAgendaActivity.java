package com.example.triptrack;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TravelAgendaActivity extends AppCompatActivity {

    private String tripId;
    private MaterialCalendarView calendarView;
    private ListView scheduledTripsList;
    final ArrayList<Plans> scheduledPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_agenda);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        tripId = getIntent().getStringExtra("tripId");
        loadPlans(tripId);

        calendarView = findViewById(R.id.calendarView);

        // Crear una instancia del decorador personalizado
        SelectedDayDecorator decorator = new SelectedDayDecorator(this);

        // Agregar el decorador al MaterialCalendarView
        calendarView.addDecorator(decorator);

        FloatingActionButton addTripButton = findViewById(R.id.add_trip_button);
        scheduledTripsList = findViewById(R.id.scheduled_trips_list);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, date.getYear());
            calendar.set(Calendar.MONTH, date.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
            long dateInMillis = calendar.getTimeInMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(new Date(dateInMillis));
            Log.d("TravelAgendaActivity", "Fecha seleccionada: " + formattedDate);

            // Establecer la fecha seleccionada en el decorador
            decorator.setSelectedDate(date);
            // Invalidar los decoradores para actualizar la vista
            widget.invalidateDecorators();

            updateScheduledPlansList();
        });

        addTripButton.setOnClickListener(v -> showAddPlanDialog());

        updateScheduledPlansList();
    }


    private void showAddPlanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nuevo plan");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_plan, null);

        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        EditText locationEditText = view.findViewById(R.id.locationEditText);

        Spinner prioritySpinner = view.findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);

        builder.setView(view);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {

            if (locationEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Por favor ingresa una ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            if (prioritySpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Por favor selecciona una prioridad", Toast.LENGTH_SHORT).show();
                return;
            }
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            String time = String.format("%02d:%02d", hour, minute);

            String location = locationEditText.getText().toString();

            String priorityString = prioritySpinner.getSelectedItem().toString();
            int priority;
            switch (priorityString) {
                case "Alta":
                    priority = 1;
                    break;
                case "Media":
                    priority = 2;
                    break;
                case "Baja":
                    priority = 3;
                    break;
                default:
                    priority = 0;
                    break;
            }

            String description = descriptionEditText.getText().toString();

// Obtener la fecha seleccionada usando el método getSelectedDate()
            CalendarDay selectedDate = calendarView.getSelectedDate();
// Crear un objeto Calendar a partir de la fecha seleccionada
            Calendar calendar = selectedDate.getCalendar();
// Obtener el tiempo en milisegundos de la fecha seleccionada
            long dateInMillis = calendar.getTimeInMillis();
// Formatear la fecha en el formato deseado
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date(dateInMillis));
            Log.d("TravelAgendaActivity", "Date: " + date);

            Plans trip = new Plans(tripId, dateInMillis, time, location, priority, description);
            scheduledPlans.add(trip);

            savePlans();
            updateScheduledPlansList();

        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void updateScheduledPlansList() {

        // Obtener la fecha seleccionada usando el método getSelectedDate()
        CalendarDay selectedDate = calendarView.getSelectedDate();
        // Verificar que selectedDate no sea null
        if (selectedDate != null) {
            // Crear un objeto Calendar a partir de la fecha seleccionada
            Calendar calendar = selectedDate.getCalendar();
            // Establecer la hora, minuto, segundo y milisegundo a cero
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // Obtener el tiempo en milisegundos de la fecha seleccionada
            long selectedDateInMillis = calendar.getTimeInMillis();
            Log.d("TravelAgendaActivity", "Updating scheduled plans list for date: " + selectedDateInMillis);

            ArrayList<Plans> filteredTrips = new ArrayList<>();
            for (Plans plan : scheduledPlans) {
                calendar.setTimeInMillis(plan.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long planDate = calendar.getTimeInMillis();
                if (planDate == selectedDateInMillis) {
                    filteredTrips.add(plan);
                }
            }

            Log.d("TravelAgendaActivity", "Filtered plans: " + filteredTrips);

            PlansAdapter adapter = new PlansAdapter(this, filteredTrips);
            scheduledTripsList.setAdapter(adapter);

            // Llamar al método updateCalendarView() para actualizar el MaterialCalendarView
            updateCalendarView(scheduledPlans, calendarView);
        }
    }

    private void updateCalendarView(@NotNull List<Plans> scheduledPlans, MaterialCalendarView calendarView) {

        // Crear un decorador personalizado para cada plan
        for (Plans plan : scheduledPlans) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(plan.getDate());
            CalendarDay day = CalendarDay.from(calendar);

            // Crear una lista de CustomCircleSpan para dibujar los círculos
            List<CustomCircleSpan> spans = new ArrayList<>();
            switch (plan.getPriority()) {
                case 1:
                    spans.add(new CustomCircleSpan(5, Color.RED, -15, 15));
                    break;
                case 2:
                    spans.add(new CustomCircleSpan(5, Color.YELLOW, 0, 15));
                    break;
                case 3:
                    spans.add(new CustomCircleSpan(5, Color.GREEN, 15, 15));
                    break;
            }

            // Crear un decorador personalizado para el plan
            PlanDayDecorator decorator = new PlanDayDecorator(day, spans);

            // Agregar el decorador al MaterialCalendarView
            calendarView.addDecorator(decorator);
        }
    }

    public void savePlans() {
        try {
            File plansDir = new File(getFilesDir(), "Planes");
            if (!plansDir.exists()) {
                plansDir.mkdir();
            }

            File tripDir = new File(plansDir, tripId);
            if (!tripDir.exists()) {
                tripDir.mkdir();
            }

            File plansFile = new File(tripDir, "plans.txt");
            FileOutputStream outputStream = new FileOutputStream(plansFile);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Plans plan : scheduledPlans) {
                String date = dateFormat.format(new Date(plan.getDate()));
                String planString = plan.getTripId() + ";" + date + ";" + plan.getTime() + ";" + plan.getLocation() + ";" + plan.getPriority() + ";" + plan.getDescription() + "\n";
                outputStream.write(planString.getBytes());
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePlan(Plans plan) {
        // Eliminar el plan de la lista de planes programados
        scheduledPlans.remove(plan);

        // Guardar los cambios y actualizar la vista
        savePlans();
        updateScheduledPlansList();
    }


    private void loadPlans(String tripId) {
        try {
            File plansDir = new File(getFilesDir(), "Planes");
            File tripDir = new File(plansDir, tripId);
            File plansFile = new File(tripDir, "plans.txt");


            if (plansFile.exists()) {
                FileInputStream inputStream = new FileInputStream(plansFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("TravelAgendaActivity", "Read plan from file: " + line);
                    String[] parts = line.split(";");
                    if (parts.length == 6) {
                        tripId = parts[0];
                        Date date = dateFormat.parse(parts[1]);
                        String time = parts[2];
                        String location = parts[3];
                        int priority = Integer.parseInt(parts[4]);
                        String description = parts[5];

                        Plans plan = new Plans(tripId, date.getTime(), time, location, priority, description);
                        scheduledPlans.add(plan);
                    } else {
                        Log.w("TravelAgendaActivity", "Invalid plan line: " + line);
                    }
                }

                reader.close();
                inputStream.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


}

