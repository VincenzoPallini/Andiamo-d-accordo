package com.example.chatapp.Fragments;

import static com.example.chatapp.Fragments.TrovamicoActivity.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentActivity;

import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.Incontro;
import com.example.chatapp.Model.User;
import com.example.chatapp.Pop;
import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentsCercaincontroBinding;
import com.example.chatapp.databinding.FragmentsTrovamicoBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

public class Cercaincontro extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private FragmentsCercaincontroBinding binding;
    LatLng latLng;
    private String incontrodentroid;
    private ProgressDialog progressDialog;
    private DatabaseReference reference,reference1,reference2;
    private FirebaseUser firebaseUser;
    private View windowView;
    private List<Incontro> Incontri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragments_cercaincontro);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d(TAG, "String User: " + firebaseUser.getUid());
        reference = FirebaseDatabase.getInstance().getReference().child("Incontro");
        Incontri = new ArrayList<>();


         ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotincontro) {
                for (DataSnapshot b : snapshotincontro.getChildren()) {
                    Incontro incontro = b.getValue(Incontro.class);

                    final String key = b.getKey();
                    incontro.setIncontroidId(key);
                    Incontri.add(incontro);
                }

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapincontro);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(Cercaincontro.this);


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


     try{
            for (int i = 0; i < Incontri.size(); i++) {
                String incontro_address = Incontri.get(i).getAddressorganizz();
                //Log.d(TAG, "onMapReady: position: " + i);
                if (incontro_address == null) continue;
                //if (!incontro_address.equals("To be added")) {

                latLng = getLocationFromAddress(incontro_address);
                Incontri.get(i).setLatlng(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                //markerOptions.title("Sei tu");
                markerOptions.snippet(Incontri.get(i).getIncontroidId());
                MarkerOptions Gruppo = markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_group_primary));

                    googleMap.addMarker(markerOptions);
               // }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        googleMap.setOnInfoWindowClickListener(this);


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {

                windowView = View.inflate(getApplicationContext(), R.layout.window_group_map, null);


                TextView cittaorg = (TextView) windowView.findViewById(R.id.Citywindow);
                TextView data = (TextView) windowView.findViewById(R.id.Datawindow);
                TextView orario = (TextView) windowView.findViewById(R.id.Orarioincontrowindow);
                TextView partecipanti = (TextView) windowView.findViewById(R.id.Partecipantiwindow);
                TextView summary = (TextView) windowView.findViewById(R.id.Summarywindow);

                String uid = marker.getSnippet();
                for(Incontro incontro : Incontri) {
                    if(incontro.getIncontroidId().equals(uid)) { //error line
                        cittaorg.setText("Città: " + incontro.getCittaorg());
                        orario.setText("Ora: " + incontro.getOrario());
                        data.setText("Data: " + incontro.getDate());
                        partecipanti.setText("Partecipanti: " + incontro.getPartecipanti());
                        summary.setText("Descrizione: " + incontro.getSummary());
                        break;
                    }
                }

                    return windowView;
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
            List<Address> addressIncontro;
            LatLng p1;

            try {
                addressIncontro = coder.getFromLocationName(strAddress, 5);
                if (addressIncontro == null) {
                    return null;
                }
                Address location = addressIncontro.get(0);
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


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        LatLng latLon = marker.getPosition();

        //Cycle through places array
        for(Incontro incontro : Incontri) {
            if (latLon.equals(incontro.getLatlng())) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Incontro")
                        .child(incontro.getIncontroidId())
                        .child("Partecipanti");

                DatabaseReference uidRef = reference.child(uid);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Toast.makeText(Cercaincontro.this, "Partecipi già in questo incontro!", Toast.LENGTH_SHORT).show();

                        } else {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Incontro").child(incontro.getIncontroidId());
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("userid", firebaseUser.getUid());
                            reference1.child("Partecipanti").child(uid).setValue(true);
                            reference1.child("partecipanti").setValue(ServerValue.increment(1));
                            Intent myIntent = new Intent(Cercaincontro.this, Popwindow.class);
                            startActivity(myIntent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", error.getMessage());
                    }
                };
                uidRef.addListenerForSingleValueEvent(valueEventListener);

            }
        }



    }



}