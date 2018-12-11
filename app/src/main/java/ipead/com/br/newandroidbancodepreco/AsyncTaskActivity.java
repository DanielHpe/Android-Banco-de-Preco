package ipead.com.br.newandroidbancodepreco;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ipead.com.br.newandroidbancodepreco.config.DownloadBDManager;

public class AsyncTaskActivity extends AppCompatActivity {

    private String login;
    private String senha;
    private int idRota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        Bundle b = getIntent().getExtras();

        idRota = b.getInt("idRota");
        login = b.getString("login");
        senha = b.getString("senha");

        Async async = new Async();
        async.execute();

    }

    public class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                DownloadBDManager temp = new DownloadBDManager(AsyncTaskActivity.this);
                Log.i("import", "Inicializando banco");

                temp.inicializaDB(login, senha, idRota);

            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
