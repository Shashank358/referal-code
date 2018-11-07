package com.ssproduction.shashank.refcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button shareBtn, logout;
    private DatabaseReference mDatabase;
    private TextView userName, userReffer, userPoints;
    private FirebaseUser mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUsers = mAuth.getCurrentUser();

        if (mUsers != null){
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUsers.getUid());

        }


        shareBtn = (Button) findViewById(R.id.share_btn);
        userName = (TextView) findViewById(R.id.user_name);
        userReffer = (TextView) findViewById(R.id.user_reffer_id);
        userPoints = (TextView) findViewById(R.id.user_points);
        logout = (Button) findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (mUsers != null){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    final String reffer_id = dataSnapshot.child("reffer_id").getValue().toString();
                    String reffer_user = dataSnapshot.child("reffered_user").getValue().toString();

                    userName.setText(name);
                    userReffer.setText(reffer_id);

                    userReffID(reffer_id);

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent share = new Intent();
                            share.setAction(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, "hey i am inviting you to use my reffer code " + reffer_id + " click on that link " +"http://www.reffer.com/"+reffer_id);
                            startActivity(Intent.createChooser(share, "Share using"));
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }


    }

    private void userReffID(String reffer_id) {

        String reffer = reffer_id;
        userReffer.setText(reffer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUsers == null){
            Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
            regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(regIntent);
            finish();
        }
    }
}
