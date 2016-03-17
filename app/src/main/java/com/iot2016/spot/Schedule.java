package com.iot2016.spot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class Schedule extends AppCompatActivity {

    Firebase db;
    Spinner spinner;
    Spinner spinner2;
    Spinner spinner3;
    Spinner spinner4;
    Spinner spinner5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_schedule);
        Intent i = getIntent();
        this.db = new Firebase(getString(R.string.db) + "/usuarios/" + i.getStringExtra("email"));
        createSpinners();
    }


    public void createSpinners() {
        spinner = (Spinner) findViewById(R.id.spinnerMonday);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sections1, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner2 = (Spinner) findViewById(R.id.spinnerTuesday);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sections1, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner3 = (Spinner) findViewById(R.id.spinnerWednesday);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.sections1, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        spinner4 = (Spinner) findViewById(R.id.spinnerThursday);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.sections1, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter4);

        spinner5 = (Spinner) findViewById(R.id.spinnerFriday);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.sections1, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter5);
    }

    public void hacerRegistro(View v) {

        String s1 = spinner.getSelectedItem().toString();
        String s2 = spinner2.getSelectedItem().toString();
        String s3 = spinner3.getSelectedItem().toString();
        String s4 = spinner4.getSelectedItem().toString();
        String s5 = spinner5.getSelectedItem().toString();

        this.db.child("schedule").child("Monday").setValue(s1);
        this.db.child("schedule").child("Tuesday").setValue(s2);
        this.db.child("schedule").child("Wednesday").setValue(s3);
        this.db.child("schedule").child("Thursday").setValue(s4);
        this.db.child("schedule").child("Friday").setValue(s5);

        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Schedule updated!", Toast.LENGTH_SHORT);
        toast.show();
        setResult(RESULT_OK);
        finish();

    }

    public void Cancel(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
