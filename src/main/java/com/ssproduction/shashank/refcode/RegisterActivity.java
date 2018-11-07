package com.ssproduction.shashank.refcode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout email, userName, password, reffer;
    private Button SignUpBtn;
    private ProgressDialog dialog;
    private String mCurrentUser, info;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        dialog = new ProgressDialog(this);

        email = (TextInputLayout) findViewById(R.id.email);
        userName = (TextInputLayout) findViewById(R.id.user_name);
        password = (TextInputLayout) findViewById(R.id.password);
        reffer = (TextInputLayout) findViewById(R.id.reffer_id);
        SignUpBtn = (Button) findViewById(R.id.sign_up_btn);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)){
                handleSendText(intent);
            }
        }

        if (user != null){
            String mCurrentUser = mAuth.getCurrentUser().getUid();
            database = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String refer = dataSnapshot.child("reffer_id").getValue().toString();

                    reffer.getEditText().setText(refer);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setTitle("Signing Up");
                dialog.setMessage("please wait while user is signing up");
                dialog.show();

                String user_email = email.getEditText().getText().toString();
                String user_name =  userName.getEditText().getText().toString();
                String user_pass = password.getEditText().getText().toString();
                String user_reffer = reffer.getEditText().getText().toString();

                if (!TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_name)
                        || !TextUtils.isEmpty(user_pass)){

                    createAccoount(user_email, user_name, user_pass, user_reffer);

                }else {
                    Toast.makeText(RegisterActivity.this, "fill all the details below", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }

    private void handleSendText(Intent intent) {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null){
            reffer.getEditText().setText(text);
            SignUpBtn.setText(text);
        }
    }

    private void createAccoount(String user_email, final String user_name, String user_pass, String user_reffer) {

        mAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    mCurrentUser = mAuth.getCurrentUser().getUid();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", user_name);
                    map.put("reffer_id", createRandomCode(8));
                    map.put("reffered_user", "default");

                    database.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                dialog.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(mainIntent);

                            }
                        }
                    });


                }else {
                    dialog.hide();
                    Toast.makeText(RegisterActivity.this, "error in signing up", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public String createRandomCode(int codeLength){
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        System.out.println(output);
        return output ;
    }
}
