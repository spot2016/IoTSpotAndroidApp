package com.iot2016.spot;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Profile extends AppCompatActivity{

    private Firebase db;
    private String Email;

    private TextView etiqueta;
    private EditText nombre;
    private EditText placas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);

        Intent i = getIntent();
        this.Email = i.getStringExtra("email");

        this.db = new Firebase(getString(R.string.db)+"/usuarios/"+this.Email.substring(0,this.Email.lastIndexOf("@")));

        this.etiqueta = (TextView) findViewById(R.id.textView5);
        this.etiqueta.setText(this.Email);

        this.nombre = (EditText) findViewById(R.id.editText);
        this.placas = (EditText) findViewById(R.id.editText2);


        Firebase nombredb = this.db.child("/name");
        nombredb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombre.setText(dataSnapshot.getValue() + "");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"Oops!, make sure you're connected", Toast.LENGTH_SHORT);
            }
        });

        Firebase placasdb = this.db.child("/plates");
        placasdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placas.setText(dataSnapshot.getValue() + "");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"Oops!, make sure you're connected", Toast.LENGTH_SHORT);
            }
        });

    }

    public void editarHorario(View v)
    {
        Intent i = new Intent(this,Schedule.class);
        i.putExtra("email",this.Email.substring(0,this.Email.lastIndexOf("@")));
        startActivityForResult(i,8);
    }

    public void Guardar(View v)
    {
        this.Email = null;

        Firebase nuevoNombre = this.db.child("/name");
        nuevoNombre.setValue(this.nombre.getText().toString());
        Firebase nuevaPlaca = this.db.child("/plates");
        nuevaPlaca.setValue(this.placas.getText().toString());

        setResult(Activity.RESULT_OK);
        finish();
    }

    public void Cancelar(View v)
    {
        this.Email = null;
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 8){
            if(resultCode == RESULT_OK){
            }
        }
    }
}
