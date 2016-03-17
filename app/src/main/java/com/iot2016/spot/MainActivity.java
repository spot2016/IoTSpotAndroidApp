package com.iot2016.spot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Firebase db;
    private String Email;

    FloatingActionButton fab;

    private View mProgressView;
    private View mHomeView;

    private TextView etiqueta;
    private Button park;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        this.db = new Firebase(getString(R.string.db));

        Intent i = getIntent();
        this.Email = i.getStringExtra("email");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etiqueta = (TextView) findViewById(R.id.textView2);
        park = (Button) findViewById(R.id.button5);

        mHomeView = findViewById(R.id.search_screen);
        mProgressView = findViewById(R.id.search_progress);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Searching for your spot!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                db.child("/jobs/" + Email.substring(0, Email.lastIndexOf("@"))).setValue("true");
                fab.setVisibility(View.GONE);
                showProgress(true);
                searchSpot();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mHomeView.setVisibility(show ? View.GONE : View.VISIBLE);
            mHomeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHomeView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
        }

        if (id == R.id.action_sign_out) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            compartirApp();
        } else if (id == R.id.nav_home) {
            return true;
        } else if (id == R.id.nav_profile) {
            openProfile();
        } else if (id == R.id.nav_settings) {
            openSettings();
        } else if (id == R.id.nav_about) {
            Toast.makeText(getApplicationContext(), "App built by Spot 2016 Team!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void compartirApp()
    {
        Toast.makeText(getApplicationContext(), "Share the app with your friends!", Toast.LENGTH_SHORT).show();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/roberto.fierroszepeda"));
        startActivity(browserIntent);
    }

    private void openProfile()
    {
        Intent i = new Intent(this, Profile.class);
        i.putExtra("email", this.Email);
        startActivityForResult(i, 5);
    }

    private void openSettings()
    {
        Intent i = new Intent(this,Settings.class);
        i.putExtra("email", this.Email);
        startActivityForResult(i, 6);
    }

    private void signOut()
    {
        this.Email = null;
        this.db.unauth();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void searchSpot()
    {
        Firebase sugerencia = this.db.child("/jobs/"+this.Email.substring(0,this.Email.lastIndexOf("@")));
        sugerencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String aux = dataSnapshot.getValue().toString();
                    System.out.print(aux);
                    if (aux != "true") {
                        Toast.makeText(getApplicationContext(), "Spot found!", Toast.LENGTH_LONG).show();
                        showProgress(false);
                        park.setVisibility(View.VISIBLE);
                        etiqueta.setText("Our suggestion is for you to go to " + dataSnapshot.getValue());
                    }
                }catch (NullPointerException e){
                    park.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    etiqueta.setText("Click the button at the bottom to search your spot!");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),"There was an error with the service!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void park(View v)
    {
        this.db.child("/jobs/"+this.Email.substring(0,this.Email.lastIndexOf("@"))).setValue(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 5) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Your info was saved", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Your info was not saved", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 6){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Your new data was saved", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Your data keeps the same", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
