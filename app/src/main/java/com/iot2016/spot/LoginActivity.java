package com.iot2016.spot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    /*
    * Code to identify the Activity when come back
    */
    private static final int CODE = 1;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.

    private UserLoginTask mAuthTask = null;
    */
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    //Firebase management

    private Firebase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        this.db = new Firebase(getString(R.string.db));
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        /*
            Register button
         */
        Button registerbutton = (Button) findViewById(R.id.email_sign_up_button);
        registerbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(mEmailView.getText().toString(),mPasswordView.getText().toString());
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*
        if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email    = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            this.db.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    logIn(mEmailView.getText().toString());
                    showProgress(false);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // Something went wrong :(
                    switch (firebaseError.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            // handle a non existing user
                            Toast.makeText(getApplicationContext(), "Your user doesn't exist. Try register :)", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            Toast.makeText(getApplicationContext(), "Wrong password :(", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            break;
                        case FirebaseError.DISCONNECTED:
                            Toast.makeText(getApplicationContext(), "Whoops! You are not connected!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        default:
                            // handle other errors
                            Toast.makeText(getApplicationContext(), "An error ocurred, please try again!"/*firebaseError.toString()*/, Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            break;
                    }
                }
            });
        }
    }


    private void attemptRegister(String email, String password)
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {

            this.db.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Toast.makeText(getApplicationContext(), "Welcome to Spot! \n Set your info in your profile menu", Toast.LENGTH_SHORT).show();
                    Firebase basededatos = new Firebase(getString(R.string.db));
                    String   aux     = mEmailView.getText().toString();
                    Firebase usuario = basededatos.child("usuarios").child(aux.substring(0,aux.lastIndexOf("@")));

                    Map<String, String> nuevo = new HashMap<String, String>();
                    nuevo.put("id", result.get("uid").toString());
                    nuevo.put("name", "type your name");
                    nuevo.put("email", aux);
                    nuevo.put("schedule", " ");
                    nuevo.put("plates", "type your plates");
                    usuario.setValue(nuevo);

                    Firebase AgregaHorario = usuario.child("schedule");
                    Map<String,String>  horario = new HashMap<String, String>();
                    horario.put("Monday", "W-A1");
                    horario.put("Tuesday", "W-A1");
                    horario.put("Thursday", "W-A1");
                    horario.put("Wednesday", "W-A1");
                    horario.put("Friday", "W-A1");
                    AgregaHorario.setValue(horario);

                    logIn(aux);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    switch (firebaseError.getCode()) {
                        case FirebaseError.DISCONNECTED:
                            Toast.makeText(getApplicationContext(), "Whoops! You are not connected!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        case FirebaseError.EMAIL_TAKEN:
                            Toast.makeText(getApplicationContext(), "That email is already in use!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        default:
                            // handle other errors
                            Toast.makeText(getApplicationContext(), firebaseError.toString(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            break;
                    }
                }
            });
        }
    }

    private void logIn(String email)
    {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("email",email);
        startActivityForResult(i, CODE);
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        if(email.contains("@")) {
            String aux = email.substring(email.lastIndexOf("@"));
            if (aux.contentEquals("@itesm.mx")) {
                return true;
            }
        }
        return false;
        //return email.contains("@itesm.mx");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }


    public void recoverEmail(View v)
    {
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        this.db.resetPassword(mEmailView.getText().toString(), new Firebase.ResultHandler() {

            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(),"A new password has been sent to your email!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getApplication(),"There was an error, type your email and try again please!", Toast.LENGTH_SHORT).show();
            }
        });

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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                this.db.unauth();
            }
        }
    }
}

