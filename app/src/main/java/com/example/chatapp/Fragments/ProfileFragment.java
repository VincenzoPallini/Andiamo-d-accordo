package com.example.chatapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.chatapp.ChangePasswordActivity;
import com.example.chatapp.DeleteProfileActivity;
import com.example.chatapp.LoginActivity;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.example.chatapp.UpdateProfileNameActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username, change_password, delete_profile, add_address, lista_incontri;
    private ListView listaincontri;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    // Permissions request constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    // Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    // Permissions to be requested
    private String[] cameraPermission;
    private String[] storagePermission;

    // Uri of picked image
    private Uri imageUri = null;

    private StorageReference storageReference;
    private StorageTask uploadTask;

    private ProgressDialog progressDialog;

    AlertDialog.Builder alertDialog;


    /*private PlacesClient placesClient;

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                System.out.println(status);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);

                        // Write a method to read the address components from the Place
                        // and populate the form with the address components
                        //Log.d(TAG, "Place: " + place.getAddressComponents());
                        //fillInAddress(place);

                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    //Log.i(TAG, "User canceled autocomplete");
                }
            });*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);

        change_password = view.findViewById(R.id.change_password);
        delete_profile = view.findViewById(R.id.delete_profile);
        add_address = view.findViewById(R.id.change_address);
        lista_incontri = view.findViewById(R.id.ListaIncontri);

        add_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                View viewcustom = getLayoutInflater().inflate(R.layout.customdialog, null);

                EditText edt1 = viewcustom.findViewById(R.id.description);
                EditText edt2 = viewcustom.findViewById(R.id.age);
                EditText edt3 = viewcustom.findViewById(R.id.hobby);
                EditText edt4 = viewcustom.findViewById(R.id.city);
                EditText edt5 = viewcustom.findViewById(R.id.address);

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity())
                            .setView(viewcustom)
                            .setPositiveButton("Ok", (dialogInterface, i) -> {

                                String Description = edt1.getText().toString();
                                String Age = edt2.getText().toString();
                                String Hobby = edt3.getText().toString();
                                String City = edt4.getText().toString();
                                String address = edt5.getText().toString();


                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                reference.child("description").setValue(Description);
                                reference.child("age").setValue(Age);
                                reference.child("hobby").setValue(Hobby);
                                reference.child("city").setValue(City);
                                reference.child("address").setValue(address);
                                Toast.makeText(requireContext(), "Indirizzo aggiunto con successo!", Toast.LENGTH_SHORT).show();


                            });
                    alertDialog.setNegativeButton("Cancel", null);
                    AlertDialog ad = alertDialog.show();
                    Button ok = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                    ok.setEnabled(false);


                final TextWatcher _checker = new TextWatcher() {

                    @Override public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {}

                    @Override public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        boolean isValid = false;

                        if(edt1.getText().length() > 0 && edt2.getText().length() > 0 && edt3.getText().length() > 0 && edt4.getText().length() > 0 && edt5.getText().length() > 0){ isValid = true; }

                        ok.setEnabled(isValid);
                     }
                };

                edt1.addTextChangedListener(_checker);
                edt2.addTextChangedListener(_checker);
                edt3.addTextChangedListener(_checker);
                edt4.addTextChangedListener(_checker);
                edt5.addTextChangedListener(_checker);


                // Initially OK button is disabled.
                ad.show();
                ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }




    });



        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get current user's id
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(user.getUsername());
                    if ("default".equals(user.getImageURL())) {
                        image_profile.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        // If the fragment is added to its activity and user has a image profile load it
                        if (isAdded()) {
                            Glide.with(requireContext()).load(user.getImageURL()).into(image_profile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // When you click the image call openImage method
        image_profile.setOnClickListener(v -> showImageImportDialog());

        delete_profile.setOnClickListener(v -> startActivity(new Intent(getContext(), DeleteProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        change_password.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        username.setOnClickListener(v -> startActivity(new Intent(getContext(), UpdateProfileNameActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        lista_incontri.setOnClickListener(v -> startActivity(new Intent(getContext(), ListaIncontriFragment.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        return view;
    }

    private void showImageImportDialog() {
        // Options to display
        String options[] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pick Image").setItems(options, (dialog, which) -> {
            // Handle clicks
            if (which == 0) {
                // Camera clicked
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    pickCamera();
                }
            }
            else {
                // Gallery clicked
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                }
                else {
                    pickGallery();
                }
            }
        }).show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "GroupImage");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "GroupImageDescription");

        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), cameraPermission, CAMERA_REQUEST_CODE);
        ActivityCompat.requestPermissions(requireActivity(), cameraPermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result =  ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }


    // Get file extension
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() throws IOException {
        // Show a progress dialog with a message
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // If imageUri has been initialized
        if (imageUri != null) {
            // Set storage reference
            storageReference = FirebaseStorage.getInstance().getReference("ProfileImages").child(System.currentTimeMillis()+ "." + getFileExtension(imageUri));

            // Compress the image
            Bitmap bmp = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
            byte[] data = baos.toByteArray();

            // Upload the image to data base
            uploadTask = storageReference.putBytes(data);

            //uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Return the image url
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        // Put image url to users reference
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);
                        // Close the progress dialog
                    }
                    else {
                        Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }
        else {
            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Receive the result from a previous call to startActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if requestCode is 1 and result code is -1 and there is some data (image selected)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null && data.getData() != null) {
                // Set imageUri with the given URI data
                imageUri = data.getData();
                // If you already uploading show a toast message
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(requireContext(), "Uploading in progress", Toast.LENGTH_SHORT).show();
                }
                // Else call uploadImage
                else {
                    try {
                        uploadImage();
                        //Toast.makeText(requireContext(), "Working", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(requireContext(), "Uploading in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        uploadImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}