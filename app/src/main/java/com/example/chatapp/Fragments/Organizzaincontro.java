package com.example.chatapp.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Data;

import com.example.chatapp.GroupChatActivity;
import com.example.chatapp.LoginActivity;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Incontro;
import com.example.chatapp.Pop;
import com.example.chatapp.R;
import com.example.chatapp.StartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
public class Organizzaincontro extends AppCompatActivity {


    static class Incontro {
        String idinc = "";
        String citta = "";
        String indirizzo = "";
        String orario = "";
        String date = "";
        String summary = "";

        public boolean isValid() {
            return !( citta.isEmpty() || indirizzo.isEmpty() || orario.isEmpty() || date.isEmpty() || summary.isEmpty() );
        }
    }

    private String incontroId;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Button btnorganizzaincontro;
    DatePickerDialog datePickerDialog;
    private ProgressDialog progressDialog;

    // made the controls instance members

    private EditText editincontro1;
    private EditText editincontro2;
    private EditText date;
    private EditText editincontro3;
    private EditText editincontro4;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizzaincontro);

        btnorganizzaincontro = findViewById(R.id.Confermabutton);

        // initialize instance variables instead of local variables

        editincontro1 = findViewById(R.id.Cittaincontro);
        editincontro2 = findViewById(R.id.Addressorganizza);
        date = findViewById(R.id.Dataincontro);
        editincontro3 = findViewById(R.id.Orarioincontro);
        editincontro4 = findViewById(R.id.Summaryincontro);

        btnorganizzaincontro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizzaIncontro();
            }
        });

        // don't need to set the listener multiple times

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        editincontro3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Organizzaincontro.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        editincontro3.setText(hourOfDay + ":" + minutes);

                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }

        });

    }

    private void selectDate() {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // set day of month , month and year value in the edit text
                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void organizzaIncontro() {
        Incontro incontro = viewToIncontro();

        if(!incontro.isValid()) {
            Toast.makeText(Organizzaincontro.this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            createIncontro(incontro);
            clearInputs();

            Intent myIntent = new Intent(Organizzaincontro.this, Pop.class);
            startActivity(myIntent);
        }
    }

    // returns an Incontro object containing the values the user entered.
    private Incontro viewToIncontro() {
        Incontro incontro = new Incontro();
        incontro.citta = editincontro1.getText().toString();
        incontro.indirizzo = editincontro2.getText().toString();
        incontro.date = date.getText().toString();
        incontro.orario = editincontro3.getText().toString();
        incontro.summary = editincontro4.getText().toString();
        return incontro;
    }

    private void clearInputs() {
        editincontro1.getText().clear();
        editincontro2.getText().clear();
        editincontro3.getText().clear();
        editincontro4.getText().clear();
        date.getText().clear();
    }

    private void createIncontro(Incontro incontro) {
        HashMap<String, Object> hashMap = new HashMap<>();
        final long time = System.currentTimeMillis();


        hashMap.put("cittaorg", incontro.citta);
        hashMap.put("addressorganizz", incontro.indirizzo);
        hashMap.put("date", incontro.date);
        hashMap.put("orario", incontro.orario);
        hashMap.put("summary", incontro.summary);
        hashMap.put("partecipanti", 0);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Incontro");

        reference.child(time + "").setValue(hashMap);
    }
}
