package com.example.itzshovo.send_data_firebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
EditText mTittleEt , mDescrEt ;
ImageView mPostIv;
    String a;
Button mUploadBtn ;
TextView link ;
String databasearent_name = "Hotels/El-CLassico/Drinks" ;
String mStoragePath = "All IMage/";
String mDatabasePath = databasearent_name ;

Uri mFilePathUri ;
StorageReference mStorageReference ;
DatabaseReference mDatabaseReference ;
ProgressDialog mprogressDialog ;

// image request code for
     int IMAGE_REQUEST_CODE = 5  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTittleEt =(EditText) findViewById(R.id.pTitleEt);
        mDescrEt = (EditText)findViewById(R.id.pDescrEt);
        mPostIv = (ImageView)findViewById(R.id.pImageTV);
        mUploadBtn = findViewById(R.id.muploadBtn);
        link = findViewById(R.id.text_link);
//Action Bar
       ActionBar actionBar = getSupportActionBar() ;
       actionBar.setTitle("Add New Post");


        // image click button
        mPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image") , IMAGE_REQUEST_CODE);


            }
        });

        // upload btn
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call method to upload image to firebase
                uploadDataToFirebase();

            }
        });
//assign FirebaseStorage Instance to stoerage  obhesct
        mStorageReference = FirebaseStorage.getInstance().getReference();
        //asssaing reference  instance with root database

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);


        //pregress dialog
        mprogressDialog = new ProgressDialog(MainActivity.this  );

    }

    private  void uploadDataToFirebase(){
            //check whether filepath uri ts empty or not
        if(mFilePathUri != null){
            mprogressDialog.setTitle("Image is Uploading...........");
            mprogressDialog.show();

            //create second  storgae
            StorageReference storageReference2nd = mStorageReference.child(mStoragePath+System.currentTimeMillis()+
                    "."
            + getFileExtention(mFilePathUri));

            //..adding onsucceslistenr

            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get title
                            String mPostTile =  mTittleEt.getText().toString();
                            String mpostDetail = mDescrEt.getText().toString();

                            mprogressDialog.dismiss();
                            //show toast
                            Toast.makeText(getApplicationContext() , " Uploaded SuccessFully .....", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTile , mpostDetail ,taskSnapshot.getDownloadUrl().toString()  );
                             a = taskSnapshot.getDownloadUrl().toString();
                            link.setText(a);
                             //geting image upload id
                            String imageUploadid = mDatabaseReference.push().getKey() ;
                            //adding imge upload
                            mDatabaseReference.child(imageUploadid).setValue(imageUploadInfo);



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // hide progrees bar
                            mprogressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mprogressDialog.setTitle(" Uploading ...........");
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Select image or add image Name ", Toast.LENGTH_SHORT).show();
        }
    }
//method to get the selecte image file exentension
    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //returning file extension

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            mFilePathUri = data.getData();
            try {
                // getting image  into bitmap

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePathUri);

                mPostIv.setImageBitmap(bitmap);

            }
            catch (Exception e ){
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }






}
