package com.opus_bd.realtimemessagin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    ArrayList<MessageModel> messagesList = new ArrayList<>();


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.etChat)
    EditText etChat;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;


    @BindView(R.id.progress_circular)
    ProgressBar progress_circular;


    CustomAdapter customAdapter;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        user = getIntent().getExtras().get("user").toString();

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.


                messagesList.clear();

                try {

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.d("TAG", "onDataChange: 1    " + childDataSnapshot.getKey());
                        MessageModel messageModel = new MessageModel();

                        for (DataSnapshot childDataSnapshot2 : childDataSnapshot.getChildren()) {
                            Log.d("TAG", "onDataChange: 2    " + childDataSnapshot2.getKey());
                            Log.d("TAG", "onDataChange: 2    " + childDataSnapshot2.getValue());

                            if (childDataSnapshot2.getKey().toString().equals("user")) {

                                if (childDataSnapshot2.getValue().toString().equals(user)) {
                                    messageModel.setMessageType(2);
                                } else
                                    messageModel.setMessageType(1);
                            }

                            messageModel.setImage(false);

                            if (childDataSnapshot2.getKey().toString().equals("value")) {
                                messageModel.setMessage(childDataSnapshot2.getValue().toString());
                            }

                            try {

                                if (Boolean.valueOf(childDataSnapshot2.getKey().toString().equals("isImage"))) {
                                    messageModel.setImage(true);
                                    Log.i("df5df5d","image found");
                                } else
                                    messageModel.setImage(false);

                            } catch (Exception e) {

                            }

                        }

                        messagesList.add(messageModel);

                    }

                    customAdapter.notifyDataSetChanged();

                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 1000);

                    btnSubmit.setVisibility(View.VISIBLE);
                    progress_circular.setVisibility(View.GONE);

                    dialog.dismiss();

                /*dataSnapshot.child()
                MessageModel messageModel1 =  new MessageModel(messageModel.get, i % 2 == 0 ? CustomAdapter.MESSAGE_TYPE_IN : CustomAdapter.MESSAGE_TYPE_OUT);*/

                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });



        /*for (int i=0;i<10;i++) {
            messagesList.add(new MessageModel("Hi", i % 2 == 0 ? CustomAdapter.MESSAGE_TYPE_IN : CustomAdapter.MESSAGE_TYPE_OUT));
        }*/

        customAdapter = new CustomAdapter(this, messagesList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);

    }


    @OnClick(R.id.btnSubmit)
    public void btnSubmit() {

        String currentKey = myRef.push().getKey();

        myRef.child(currentKey).child("value").setValue(etChat.getText().toString());
        myRef.child(currentKey).child("user").setValue(user);
        myRef.child(currentKey).child("date").setValue(new Date());
        myRef.child(currentKey).child("isImage").setValue(false);

        btnSubmit.setVisibility(View.GONE);
        progress_circular.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.imagePicker)
    public void imagePicker() {
        CropImage.activity().start(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        try {
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                String currentKey = myRef.push().getKey();

                myRef.child(currentKey).child("value").setValue("");
                myRef.child(currentKey).child("user").setValue(user);
                myRef.child(currentKey).child("date").setValue(new Date());
                myRef.child(currentKey).child("isImage").setValue(true);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == 3333) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Toast.makeText(this, resultUri.toString(), Toast.LENGTH_SHORT).show();

            } else if (resultCode == 3333) {
                Exception error = result.getError();
            }
        }

    }
}