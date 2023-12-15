package com.example.mobile_app;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.mobile_app.Model.tblUser;
import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Remote.RetrofitClient;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {



    IMyAPI iMyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edit_user,edit_password;
    Button btn_login, btn_signup;
    private boolean userLoggedIn = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init API
        iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

        edit_user = (EditText) findViewById(R.id.edit_user_name);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        //Event
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create user to login
                tblUser user = new tblUser("",edit_user.getText().toString(),
                        edit_password.getText().toString(),"","",true,new ArrayList<>());

                compositeDisposable.add(iMyAPI.loginUser(user)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                // Pass the user data from the API response
                                intent.putExtra("userData", s);
                                startActivity(intent);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(MainActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        })
                );

            }
        });

    }


    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (userLoggedIn) {
            // If the user is logged in, do nothing when the back button is pressed
        } else {
            super.onBackPressed();
        }
    }
}