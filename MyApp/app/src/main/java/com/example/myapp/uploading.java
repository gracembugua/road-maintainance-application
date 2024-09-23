package com.example.myapp;
import android.util.Log;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class uploading extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 4;
    private static final int REQUEST_LOCATION_PERMISSION = 5;

    private Spinner roadsSpinner;
    private EditText locationEditText, estimatedSizeEditText, usernameEditText;
    private DatePicker datePicker;
    private ImageView photoImageView;
    private VideoView videoView;
    private Button uploadPhotoButton, takePhotoButton, recordVideoButton, submitButton;
    private TextView selectedDateTextView;

    private Uri photoUri, videoUri;
    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploading);

        databaseReference = FirebaseDatabase.getInstance().getReference("Potholes");

        roadsSpinner = findViewById(R.id.roadsSpinner);
        locationEditText = findViewById(R.id.locationEditText);
        estimatedSizeEditText = findViewById(R.id.estimatedSizeEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        photoImageView = findViewById(R.id.photoImageView);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        recordVideoButton = findViewById(R.id.recordVideoButton);
        submitButton = findViewById(R.id.submitButton);
        datePicker = findViewById(R.id.datePicker);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        videoView = findViewById(R.id.videoview);

        setupSpinners();
        setupMap();
        setupLocationListener();

        datePicker.setVisibility(View.GONE);
        selectedDateTextView.setOnClickListener(view -> {
            if (datePicker.getVisibility() == View.VISIBLE) {
                datePicker.setVisibility(View.GONE);
            } else {
                datePicker.setVisibility(View.VISIBLE);
            }
        });

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    selectedDateTextView.setText(selectedDate);
                    // Collapse DatePicker
                    datePicker.setVisibility(View.GONE);
                });

        uploadPhotoButton.setOnClickListener(view -> openGallery());
        takePhotoButton.setOnClickListener(view -> openCamera(MediaStore.ACTION_IMAGE_CAPTURE));
        recordVideoButton.setOnClickListener(view -> openCamera(MediaStore.ACTION_VIDEO_CAPTURE));
        submitButton.setOnClickListener(view -> submitForm());
    }

    private void setupSpinners() {
        // Options for roadsSpinner
        String[] roadsOptions = {
                "Eastern Bypass (A8)", "Limuru Road", "Kiambu Road", "Waiyaki Way (A104)",
                "Nairobi-Nakuru Highway (A104)", "Gatundu Road", "Ruiru-Kiambu Road",
                "Githunguri Road", "Juja Road", "Kamiti Road", "Banana Hill Road",
                "Kiambu-Kirigiti Road", "Kenyatta Road", "Karura-Kimbo Road",
                "Ndumberi-Kiambu Road", "Kikuyu-Ondiri Road", "Gachie-Kihara Road"
        };

        ArrayAdapter<String> roadsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roadsOptions);
        roadsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roadsSpinner.setAdapter(roadsAdapter);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupLocationListener() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("LocationUpdate", "Location changed: " + location);
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Retrieve the address
                String address = getAddressFromLocation(userLocation.latitude, userLocation.longitude);
                Log.d("LocationUpdate", "Address fetched: " + address);

                // Format the display string
                String locationText = String.format(Locale.getDefault(),
                        "Lat: %.4f, Lng: %.4f\n%s",
                        userLocation.latitude,
                        userLocation.longitude,
                        address
                );

                // Update EditText on the UI thread
                runOnUiThread(() -> {
                    locationEditText.setText(locationText);
                    Log.d("LocationUpdate", "Location text set: " + locationText);
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Request updates from both GPS and Network providers
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }


    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i)).append("\n");
                }
                return addressBuilder.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("GeocoderError", "Geocoder failed: " + e.getMessage());
        }
        return "Address not found";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    private void openCamera(String action) {
        Intent intent = new Intent(action);
        if (action.equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else if (action.equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void submitForm() {
        String road = roadsSpinner.getSelectedItem().toString();
        String location = locationEditText.getText().toString();
        String potholeSize = estimatedSizeEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String selectedDate = selectedDateTextView.getText().toString();

        if (location.isEmpty() || potholeSize.isEmpty() || username.isEmpty() || (photoUri == null && videoUri == null) || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and upload a photo or video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine whether to upload photo or video
        if (photoUri != null) {
            uploadMedia(photoUri);
        } else if (videoUri != null) {
            uploadMedia(videoUri);
        }
    }

    private void uploadMedia(Uri mediaUri) {
        String id = databaseReference.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Failed to generate unique ID", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("pothole_media").child(id);
        storageReference.putFile(mediaUri).addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String mediaUrl = uri.toString();

                // Create Pothole object and save in Firebase Database
                Pothole pothole = new Pothole(id, roadsSpinner.getSelectedItem().toString(), locationEditText.getText().toString(),
                        estimatedSizeEditText.getText().toString(), usernameEditText.getText().toString(),
                        selectedDateTextView.getText().toString(), mediaUrl);
                databaseReference.child(id).setValue(pothole).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(uploading.this, "Pothole reported successfully", Toast.LENGTH_SHORT).show();
                        // Optional: Close the current activity
                        finish();
                    } else {
                        Toast.makeText(uploading.this, "Failed to report pothole", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(uploading.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(uploading.this, "Failed to upload media", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                photoUri = data.getData();
                photoImageView.setImageURI(photoUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photoImageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_VIDEO_CAPTURE && data != null) {
                videoUri = data.getData();
                videoView.setVideoURI(videoUri);
                videoView.start();
            }
        }
    }
}
