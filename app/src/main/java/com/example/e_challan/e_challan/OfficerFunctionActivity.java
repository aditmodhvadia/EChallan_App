package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

public class OfficerFunctionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int exitCount;
    long currTime, prevTime;

    ProgressDialog progressDialog;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_function);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        auth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayFragment(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.officer_function, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayFragment(id);

        return true;
    }

    private void displayFragment(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_issue_challan) {
            // Handle the add officer fragment
            fragment = new IssueChallanOtpVerifyFragment();

        }else if (id == R.id.nav_logout) {

            logout();

        } else if (id == R.id.nav_change_password) {

            passwordReset();

        } else if(id == R.id.nav_contact_support) {

            //open new fragment for contact
            fragment = new ContactOfficerFragment();
        }
        else if(id == 100){
            fragment = new DisplayAnimationFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_officer_function_display, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            exitCount++;
            if (exitCount == 1) {
                Toast.makeText(getApplicationContext(), "Press back once more to logout and exit", Toast.LENGTH_SHORT).show();
                prevTime = System.currentTimeMillis();
            }
            if (exitCount == 2) {
                currTime = System.currentTimeMillis();
                if (currTime - prevTime > 2000) {
                    Toast.makeText(getApplicationContext(), "Press back once more to logout and exit", Toast.LENGTH_SHORT).show();
                    prevTime = System.currentTimeMillis();
                    exitCount = 1;
                } else {
                    FirebaseAuth.getInstance().signOut();
                    finish();/*
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();*/
                }
            }
        }
    }

    private void logout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to Logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.getInstance().signOut();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.show();
    }

    private void passwordReset()
    {
        if(auth!=null)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.passwordResetTitle);
            builder.setMessage(R.string.passwordResetConfirmation);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(auth.getCurrentUser()!=null)
                    {
                        progressDialog.setMessage("Sending Email");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.hide();
                                if(task.isSuccessful())
                                {

                                    Toast.makeText(getApplicationContext(),"Password Reset Email sent successfully",Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    finish();
                                }
                                else
                                {
                                    if (task.getException() instanceof FirebaseNetworkException)
                                    {
                                        Toast.makeText(getApplicationContext(), "Internet connectivity required", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Some error occurred. Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.show();

        }
        else
        {

        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}