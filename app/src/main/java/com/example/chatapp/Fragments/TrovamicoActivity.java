package com.example.chatapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentsTrovamicoBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class TrovamicoActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "TrovamicoActivity";

    private FragmentsTrovamicoBinding binding;
    LatLng latLng;
    
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private View windowView;

    private List<User> allUsers1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragments_trovamico);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d(TAG, "String User: " + firebaseUser.getUid());
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        allUsers1 = new ArrayList<>();

        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot a : snapshot.getChildren()) {
                    User user = a.getValue(User.class);
                    allUsers1.add(user);
                }


                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert mapFragment != null; //da levare
                mapFragment.getMapAsync(TrovamicoActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        };

        reference.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Toast.makeText(this, "size" + addresses.size(), Toast.LENGTH_SHORT).show();
        try{
            for (int i = 0; i < allUsers1.size(); i++) {
                String user_address = allUsers1.get(i).getAddress();
                //Log.d(TAG, "onMapReady: position: " + i);
                if (user_address == null) continue;
                if (!user_address.equals("To be added")) {
                    String user_gender = allUsers1.get(i).getGender() == null ? "Male" : allUsers1.get(i).getGender();
                    latLng = getLocationFromAddress(user_address);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    //markerOptions.title("Sei tu");
                    markerOptions.snippet(allUsers1.get(i).getId());
                    MarkerOptions male = markerOptions.icon(BitmapFromVector(getApplicationContext(), (user_gender.equals("Male")) ? R.drawable.male : R.drawable.female));

                    googleMap.addMarker(markerOptions);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        googleMap.setOnInfoWindowClickListener(this);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {

                windowView = View.inflate(getApplicationContext(), R.layout.window_information_map, null);


                TextView description = (TextView) windowView.findViewById(R.id.Description1);
                TextView age = (TextView) windowView.findViewById(R.id.Age);
                TextView city = (TextView) windowView.findViewById(R.id.City);
                TextView hobby = (TextView) windowView.findViewById(R.id.Hobby);


                // marker.getSnippet() è l'utente
                if(marker.getSnippet().equals(firebaseUser.getUid()))
                {
                    Toast.makeText(TrovamicoActivity.this, "SEI TU!", Toast.LENGTH_SHORT).show();

                    return null;

                }else {

                    String uid = marker.getSnippet();
                    for(User user : allUsers1){
                        if(user.getId().equals(uid)) {
                            description.setText("Descrizione: " + user.getDescription());
                            age.setText("Età: " + user.getAge());
                            city.setText("Città: " + user.getCity());
                            hobby.setText("Hobby: " + user.getHobby());
                            break;
                        }
                    }

                    return windowView;
                }
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {


        Intent myIntent = new Intent(TrovamicoActivity.this, MessageActivity.class);


        if(marker.getSnippet().equals(firebaseUser.getUid())){
            Toast.makeText(TrovamicoActivity.this, "SEI TU!", Toast.LENGTH_SHORT).show();
        }
        else {
            myIntent.putExtra("userid", marker.getSnippet()); //Optional parameters
            TrovamicoActivity.this.startActivity(myIntent);
        }



    }
}