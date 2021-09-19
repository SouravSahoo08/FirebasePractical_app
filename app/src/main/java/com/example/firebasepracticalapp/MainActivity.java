package com.example.firebasepracticalapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private final static int IMAGE_REQUEST = 2;
    ListView infoList;
    private Button logout, inputBtn;
    private EditText inputTxt;
    private Uri imageUri;

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

       
                   //-------------------------- FIREBASE REALTIME DATABASE ----------------------------------
                //---------------------------- ADDING DATA TO REALTIME DATA BASE -----------------------------

        FirebaseDatabase.getInstance().getReference().child("Sourav sahoo")
                                                    .child("Android").setValue("Java");

        //----------------------------------- adding a set of info to firebasedatabase ----------------------------

        HashMap<String,Object> map = new HashMap<>();
        map.put("Name","sourav kumar sahoo");
        map.put("clg","Trident");
        map.put("branch","CSE");
        FirebaseDatabase.getInstance().getReference().child("Sourav sahoo").child("info").updateChildren(map);

        //----------------------------------- GETTING INPUT AND SHOWING IT ON LIST VIEW -----------------------------

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

        //----------------------------------- GETTING DATA FROM A SET OF DATA --------------------------------------

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list,list);
        infoList.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sourav sahoo").child("credentials");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Information info = snap.getValue(Information.class);
                    String txt=null;
                    if(info!=null)
                        txt = info.getEmail() + " : " + info.getname();
                    list.add(txt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        

        // ------------------------------------------- CLOUD FIRESTORE ----------------------------------------
        // -------------------------- ADDING COLLECTIONS AND DOCUMENTS TO FIRESTORE ----------------------------

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String , Object> city = new HashMap<>();
        city.put("Name","Bhubaneswar");
        city.put("State","Odisha");

        db.collection("Cities").document("BBS").set(city);

        // -------------------------- ADDING DATA TO EXISTING DOCUMENTS IN FIRESTORE ----------------------------

        city.put("isPresent",false);
        db.collection("Cities").document("BBS").set(city, SetOptions.merge());

        // ------------------------- ADDING SET OF DATA INSIDE A UNIQUE ID -----------------------------

        Map<String , Object> country = new HashMap<>();
        country.put("India","Delhi");
        country.put("USA","Washington DC");
        country.put("Japan","Tokyo");
        country.put("Abu dhabi","Dubai");

        db.collection("Countries").add(country);

        // --------------------------- UPDATING EXISTING DATA ----------------------------

        DocumentReference ref = FirebaseFirestore.getInstance().collection("Cities").document("BBS");
        ref.update("isPresent", true);


        // ------------------------------ RETRIEVING DATA FROM FIRESTORE -----------------------------

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list,list);
        infoList.setAdapter(adapter);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("Cities").document("BBS");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists())
                        list.add(doc.getData().toString());
                }
                adapter.notifyDataSetChanged();
            }
        });
        

        // ------------------------------ UPLOADING FILE TO DATABASE STORAGE -----------------------------

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading File");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileInfo = FirebaseStorage.getInstance().getReference().child("Uploads")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileInfo.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileInfo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("Document URL", url);
                            pd.setMessage("Almost Done...");
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
