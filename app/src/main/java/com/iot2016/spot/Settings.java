package com.iot2016.spot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Settings extends AppCompatActivity {

    private String Email;
    private Firebase db;
    private EditText oldPassword;
    private EditText newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        Intent i = getIntent();
        this.Email = i.getStringExtra("email");
        this.db = new Firebase(getString(R.string.db));
        setContentView(R.layout.activity_settings);
        this.oldPassword = (EditText) findViewById(R.id.editText4);
        this.newPassword = (EditText) findViewById(R.id.editText3);
    }

    public void Guardar(View v)
    {
        this.db.changePassword(this.Email, this.oldPassword.getText().toString(), this.newPassword.getText().toString(), new Firebase.ResultHandler(){

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"The new password has been set!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"There was an error, please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Cancelar(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void BorrarCuenta(View v)
    {

        this.db.removeUser(this.Email, this.oldPassword.getText().toString(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"The account has been deleted", Toast.LENGTH_SHORT).show();
                db.child("/"+Email.substring(0,Email.lastIndexOf("@"))).setValue(null);
                System.exit(0);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"Make sure you typed the correct password", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
