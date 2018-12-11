package ipead.com.br.newandroidbancodepreco;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.GPSTracker;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.entity.Informante;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Timer timer;
    private TimerTask hourlyTask;
    private int idRota;
    private int idPeriodoColeta;
    private String mainPath;
    private String login;
    private String senha;
    private ArrayList<Marker> markers;
    private DirectoryBrowser browser;
    private InformanteDAO informanteDAO;
    private List<Informante> listInfo;
    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        SharedPref.init(getApplicationContext());

        browser = new DirectoryBrowser(getApplicationContext());

        idRota = b.getInt("idRota");
        idPeriodoColeta = b.getInt("idPeriodo");

        login = b.getString("login");
        senha = b.getString("senha");

        timer = new Timer ();
        hourlyTask = new TimerTask () {
            @Override
            public void run () {

                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

            }
        };

        timer.schedule (hourlyTask, 0l, 1000);


    }

    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        informanteDAO = new InformanteDAO(getApplicationContext(), null, mainPath);

        listInfo = informanteDAO.getAllLocations(String.valueOf(idRota), String.valueOf(idPeriodoColeta));

        for(int i = 0; i < listInfo.size(); i++){
            LatLng informante = new LatLng(listInfo.get(i).getLatitude(), listInfo.get(i).getLongitude());
            if(informante.latitude == 0.0 && informante.longitude == 0.0){
                String endereco =  listInfo.get(i).getEndereco();
                if(endereco != null && !endereco.equals("null")){
                    LatLng informanteByAdress = getLocationFromAddress(MapsActivity.this, endereco);
                    informante = new LatLng(informanteByAdress.latitude, informanteByAdress.longitude);
                    conferirStatusInformante(i, informante);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(informante, 12.75f));
                }
            } else {
                conferirStatusInformante(i, informante);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(informante, 12.75f));
            }
        }

    }

    private void conferirStatusInformante(int posicao, LatLng informante) {

        Marker marker;

        switch(listInfo.get(posicao).getStatus().charAt(0)){
            case '1':
                marker = mMap.addMarker(new MarkerOptions()
                        .position(informante)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(listInfo.get(posicao).getDescricao() + " (Aberto)"));

                marker.showInfoWindow();
                markers.add(marker);
                break;
            case '2':
                marker = mMap.addMarker(new MarkerOptions()
                        .position(informante)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(listInfo.get(posicao).getDescricao() + " (Fechado)"));
                marker.showInfoWindow();
                markers.add(marker);
                break;
            case '3':
                marker = mMap.addMarker(new MarkerOptions()
                        .position(informante)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(listInfo.get(posicao).getDescricao() + " (Fechado Vazio)"));

                marker.showInfoWindow();
                markers.add(marker);
                break;
            case '4':
                marker = mMap.addMarker(new MarkerOptions()
                        .position(informante)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(listInfo.get(posicao).getDescricao() + " (Transferido)"));
                marker.showInfoWindow();
                markers.add(marker);
                break;
            case '5':
                marker = mMap.addMarker(new MarkerOptions()
                        .position(informante)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(listInfo.get(posicao).getDescricao()  + " (Transferido Vazio)"));
                marker.showInfoWindow();
                markers.add(marker);
                break;

        }

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {

            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
