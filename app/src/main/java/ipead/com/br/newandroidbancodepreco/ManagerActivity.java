package ipead.com.br.newandroidbancodepreco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;

public class ManagerActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private ToggleButton toggleCertificado;
    private TextView currentHost;
    private TextView textSeekbar;
    private Button addHost;
    private Button setPath;
    private Button infosActivity;
    private Switch aSwitch;
    private DiscreteSeekBar seekBar;
    private boolean stat;
    public static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        SharedPref.init(getApplicationContext());

        toggleButton = findViewById(R.id.toggle);
        toggleCertificado = findViewById(R.id.toggleCertificado);
        addHost = findViewById(R.id.adicionarHost);
        infosActivity = findViewById(R.id.infosActivity);
        currentHost = findViewById(R.id.nomeConexaoAtual);
        setPath = findViewById(R.id.buttonPath);
        aSwitch = findViewById(R.id.gpsSwitch);
        seekBar = findViewById(R.id.seekBar);
        textSeekbar = findViewById(R.id.distanciaValor);

        configurarToggleEstatistica();
        configurarToggleConexao();
        configurarSwitchGps();
        configurarSeekbarStatus();

        addHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, HostActivity.class);
                startActivity(intent);
            }
        });

        setPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, PathActivity.class);
                startActivity(intent);
            }
        });

        infosActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        verificarHostAtual();
        verificarStatusEstatistica();
        verificarStatusProtocolo();
        verificarStatusNotificacaoGps();
        verificarSeekbarStatus();

    }

    private void verificarSeekbarStatus() {
        int valueSeek = SharedPref.readInt("valueSeek", 0);

        if(SharedPref.readInt("valueSeek", 0) != 0){
            seekBar.setProgress(valueSeek);
        } else {
            seekBar.setProgress(1);
        }
    }

    private void configurarSeekbarStatus() {
        seekBar.setMin(0);
        seekBar.setMax(25);
        seekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value * 200;
            }
        });

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                textSeekbar.setText("Distância Limite Pop-Up: " + value * 200 + "m");
                SharedPref.writeInt("valueSeek", value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        verificarHostAtual();
        if(SharedPref.readBoolean("mudouPath", false)){
            SharedPref.writeBoolean("mudouPath", true);
        } else {
            SharedPref.writeBoolean("mudouPath", false);
        }
    }

    private void verificarHostAtual(){

        currentHost.setText(SharedPref.readString("nomeHost", "Produção"));
        currentHost.setTextColor(android.graphics.Color.rgb(21, 160, 5));

    }

    private void verificarStatusEstatistica(){

        if(SharedPref.readBoolean("stat", false)){
            toggleButton.setText("ON");
            toggleButton.setChecked(true);
            toggleButton.setBackgroundResource(R.drawable.color_toggle_blue);
        } else {
            toggleButton.setText("OFF");
            toggleButton.setChecked(false);
            toggleButton.setBackgroundResource(R.drawable.color_toggle_red);
        }
    }

    private void verificarStatusProtocolo(){

        if(SharedPref.readString("protocolo", "").equals(getResources().getString(R.string.http))){
            toggleCertificado.setText("ON");
            toggleCertificado.setChecked(true);
            toggleCertificado.setBackgroundResource(R.drawable.color_toggle_blue);
        } else {
            toggleCertificado.setText("OFF");
            toggleCertificado.setChecked(false);
            toggleCertificado.setBackgroundResource(R.drawable.color_toggle_green);
        }
    }

    private void verificarStatusNotificacaoGps() {

        if(SharedPref.readBoolean("statusGps", false)){
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

    }


    private void configurarToggleEstatistica() {

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                switch (toggleButton.getText().toString()){
                    case "Habilitada":
                        toggleButton.setText("ON");
                        toggleButton.setChecked(true);
                        toggleButton.setBackgroundResource(R.drawable.color_toggle_blue);
                        stat = true;
                        SharedPref.writeBoolean("stat", stat);
                        break;
                    case "Desabilitada":
                        toggleButton.setText("OFF");
                        toggleButton.setChecked(false);
                        toggleButton.setBackgroundResource(R.drawable.color_toggle_red);
                        stat = false;
                        SharedPref.writeBoolean("stat", stat);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void configurarToggleConexao() {

        toggleCertificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                switch (toggleCertificado.getText().toString()){
                    case "Http":
                        toggleCertificado.setText("ON");
                        toggleCertificado.setChecked(true);
                        toggleCertificado.setBackgroundResource(R.drawable.color_toggle_blue);
                        SharedPref.writeString("protocolo", getResources().getString(R.string.http));
                        Log.d("Protocolo", "Http");
                        break;
                    case "Https":
                        toggleCertificado.setText("OFF");
                        toggleCertificado.setChecked(false);
                        toggleCertificado.setBackgroundResource(R.drawable.color_toggle_green);
                        SharedPref.writeString("protocolo",  getResources().getString(R.string.https));
                        Log.d("Protocolo", "Https");
                        break;
                    default:
                        break;

                }
            }
        });
    }

    private void configurarSwitchGps() {
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    verificarPermissoesGps();
                    SharedPref.writeBoolean("statusGps", isChecked);
                } else {
                    SharedPref.writeBoolean("statusGps", isChecked);
                }
            }
        });
    }

    public boolean verificarPermissoesGps(){
        if(ContextCompat.checkSelfPermission(ManagerActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_CODE);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            } else {
            }
        }
    }
}
