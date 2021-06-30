package com.example.apipokemon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, SensorEventListener, com.example.apipokemon.FetchAddressTask.OnTaskCompleted {
    private EditText pokeBusca;
    private TextView nmPoke;
    private TextView idPoke;
    private TextView heightPoke;
    private TextView weightPoke;
    private TextView txtSensor;
    private PokemonDAO dao;
    public String pokeName;
    public String pokeId;
    public String pokeHeight;
    public String pokeWeight;

    //localização
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private Button mLocationButton;
    private TextView mLocationTextView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private boolean mTrackingLocation;

    private String lastLatitude = "";
    private String lastLongitude = "";
    private String lastAdress = "";

    private CustomView mCustomView;

    SensorManager sensorManager;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        pokeBusca = findViewById(R.id.pokeInput);
        nmPoke = findViewById(R.id.txtPokeName);
        idPoke = findViewById(R.id.txtPokeId);
        heightPoke = findViewById(R.id.txtPokeHeight);
        weightPoke = findViewById(R.id.txtPokeWeight);

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

        dao = new PokemonDAO(this);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //localização
        mLocationButton = (Button) findViewById(R.id.button_location);
        mLocationTextView = (TextView) findViewById(R.id.textview_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this);

        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggle the tracking state.
             * @param view The track location button.
             */
            @Override
            public void onClick(View view) {
                mCustomView.swapColor();
                if (!mTrackingLocation) {
                    startTrackingLocation();
                } else {
                    stopTrackingLocation();
                }
            }
        });


        mLocationCallback = new LocationCallback() {
            /**
             * This is the callback that is triggered when the
             * FusedLocationClient updates your location.
             * @param locationResult The result containing the device location.
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (mTrackingLocation) {
                    new FetchAddressTask(MainActivity.this, MainActivity.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };

        mCustomView = (CustomView) findViewById(R.id.customView);

    }

    public void buscaPoke(View view) {
        String queryString = pokeBusca.getText().toString().toLowerCase();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
        }
        else {
            if (queryString.length() == 0) {
                Toast.makeText(this, R.string.no_search_term, Toast.LENGTH_LONG).show();
                nmPoke.setText(null);
                idPoke.setText(null);
                heightPoke.setText(null);
                weightPoke.setText(null);
            }
            else {
                Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new CarregaPokemon(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("id");
            String height = jsonObject.getString("height");
            String weight = jsonObject.getString("weight");

            pokeName = name;
            pokeId = id;
            pokeHeight = height;
            pokeWeight = weight;

            if (name != null && id != null) {
                nmPoke.setText("Nome: "+name.toUpperCase());
                idPoke.setText("#"+id);
                heightPoke.setText("Altura: "+height+"dm");
                weightPoke.setText("Peso: "+weight+"hg");

            }
            else {
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void Salvar(View view) {
        try {
            if (pokeName != null && pokeId != null && pokeHeight != null && pokeWeight != null) {
                String namesql = pokeName;
                String idsql = pokeId;
                String heightsql = pokeHeight;
                String weightsql = pokeWeight;

                Pokemon pokemon = new Pokemon();
                pokemon.setNome(namesql);
                pokemon.setId(Integer.parseInt(idsql));
                pokemon.setAltura(Integer.parseInt(heightsql));
                pokemon.setPeso(Integer.parseInt(weightsql));
                dao.insert(pokemon);

                Toast.makeText(this, "O Pokémon " + namesql + " foi registrado!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Dadas não encontrados!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Erro: "+e, Toast.LENGTH_LONG).show();
        }
    }

    public void Deletar(View view) {
        try {
            Pokemon pokemon = new Pokemon();
            dao.delete(pokemon);
            Toast.makeText(this, "Pokémon soltos!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Erro: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    public void Listar(View view) {
        Intent i = new Intent(this, ListPokemonActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }

        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }

        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] < 200)
                getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
            else
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Inicia a nusca da localização.
     * Busca as permissões e requisição se não estiverem presentes
     * Se estiverem requisitas as atualizações, define texto de carregamento e a animação
     */
    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

            mLocationTextView.setText(getString(R.string.address_text,
                    getString(R.string.loading), null, null));
            mLocationButton.setText(R.string.stop_tracking_location);
        }
    }

    /**
     * Define os location requests
     *
     * @return retorna os parametros.
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Para a busca da localização, animação e altera texto botão
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mLocationButton.setText(R.string.start_tracking_location);
            mLocationTextView.setText(R.string.textview_hint);
        }
    }

    /**
     * Callback chamado com a resposta da request permission
     *
     * @param requestCode  Código da requisição
     * @param permissions  Array com as requisições solicitadas.
     * @param grantResults Array com a resposta das requisições
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // Permissão garantida
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onTaskCompleted(String[] result) {
        if (mTrackingLocation) {
            // Update the UI
            lastLatitude = result[1];
            lastLongitude = result[2];
            lastAdress = result[0];
            mLocationTextView.setText(getString(R.string.address_text,
                    lastAdress, lastLatitude, lastLongitude));
        }
    }


}

