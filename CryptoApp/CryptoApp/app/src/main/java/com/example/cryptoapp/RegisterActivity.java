package com.example.cryptoapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button RegisterButton;
    private EditText loginmail, loginpassword, loginpasswordagain;
    private TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //Kontrol Tanımlamaları
        RegisterButton=findViewById(R.id.register_button);
        loginmail=findViewById(R.id.register_email);
        loginpassword=findViewById(R.id.register_password);
        loginpasswordagain=findViewById(R.id.register_password2);
        signin=findViewById(R.id.account);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginmail.getText().toString().trim();
                String password = loginpassword.getText().toString().trim();

                // Firebase Authentication kullanarak yeni kullanıcı hesabı oluşturun
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Kullanıcı başarıyla oluşturuldu
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Kayıt işlemi tamamlandıktan sonra yapmak istediğiniz işlemleri buraya ekleyebilirsiniz
                                    // Örneğin, kullanıcının profiline adını eklemek:
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("Kullanıcının Adı")
                                            .build();
                                    user.updateProfile(profileUpdates);

                                    // Kullanıcıyı başka bir sayfaya yönlendirin
                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish(); // RegisterActivity'yi kapatın
                                } else {
                                    // Kayıt işlemi başarısız oldu
                                    Toast.makeText(RegisterActivity.this, "Kayıt işlemi başarısız. Lütfen tekrar deneyin.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginActivityIntent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(LoginActivityIntent);
            }
        });


    }
}