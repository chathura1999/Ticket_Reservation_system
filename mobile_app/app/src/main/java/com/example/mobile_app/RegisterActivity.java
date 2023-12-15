package com.example.mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Model.tblUser;
import com.example.mobile_app.Remote.RetrofitClient;


import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    IMyAPI iMyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edit_user,edit_password,edit_nic;
    Button btn_create;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init API
        iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);
        //view
        edit_user = (EditText) findViewById(R.id.edit_user_name);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_nic = (EditText) findViewById(R.id.edit_nic);
        btn_create = (Button) findViewById(R.id.btn_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tblUser user = new tblUser("",edit_user.getText().toString(),
                        edit_password.getText().toString(),"",edit_nic.getText().toString(),true,new ArrayList<>());
                compositeDisposable.add(iMyAPI.registerUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                if(s.contains("successfully")){
                                    finish();
                                }
                                Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(RegisterActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        })
                );
            }
        });

    }
}