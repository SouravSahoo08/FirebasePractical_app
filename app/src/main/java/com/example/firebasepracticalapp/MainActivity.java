package com.example.firebasepracticalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button logout,inputBtn;
    private EditText inputTxt;
    ListView infoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputTxt = findViewById(R.id.inputTxt);
        inputBtn = findViewById(R.id.inputBtn);
        logout = findViewById(R.id.logoutBtn);
        infoList = findViewById(R.id.infoList);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartupActivity.class));
            }
        });

       /* //adding data to realtime data base
        FirebaseDatabase.getInstance().getReference().child("Sourav sahoo")
                                                    .child("Android").setValue("Java");

        //adding a set of info to firebasedatabase
        HashMap<String,Object> map = new HashMap<>();
        map.put("Name","sourav kumar sahoo");
        map.put("clg","Trident");
        map.put("branch","CSE");
        FirebaseDatabase.getInstance().getReference().child("Sourav sahoo").child("info").updateChildren(map);*/

        //getting input and showing it on list view
        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = inputTxt.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("Sourav sahoo").child("info").child("input").setValue(txt);
            }
        });
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list,list);
        infoList.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sourav sahoo").child("info");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    list.add(snap.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}