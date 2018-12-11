package ipead.com.br.newandroidbancodepreco.dao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import ipead.com.br.newandroidbancodepreco.ProdutoActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.entity.Host;

public class HostDAO {

    protected Activity context;
    protected SQLiteDatabase bancoDados;

    public HostDAO(Activity context){
        this.context = context;
        bancoDados = context.openOrCreateDatabase("appHosts", SQLiteDatabase.OPEN_READWRITE, null);
        createTableHost();
    }

    public void insertExistentes() {

        bancoDados.execSQL("INSERT INTO hosts (nomeHost, ipHost) SELECT * FROM (SELECT 'Produção', " + "'"
                + context.getResources().getString(R.string.producao) + "/syncprice/" +  "')" +
                " AS data WHERE NOT EXISTS ( SELECT nomeHost FROM hosts WHERE nomeHost = 'Produção') LIMIT 1");

        bancoDados.execSQL("INSERT INTO hosts (nomeHost, ipHost) SELECT * FROM (SELECT 'Desenvolvimento', " +
                "'" + context.getResources().getString(R.string.producao) + "/syncpricetest/" +  "')" +
                " AS data WHERE NOT EXISTS ( SELECT nomeHost FROM hosts WHERE nomeHost = 'Desenvolvimento') LIMIT 1");

    }

    public void createTableHost(){

        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS hosts(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " nomeHost VARCHAR(255), ipHost VARCHAR(255) ) ");

    }

    public void adicionarHost(String nomeHoste, String ipHoste){
        try{
            ContentValues cv = new ContentValues();
            cv.put("nomeHost", nomeHoste);
            cv.put("ipHost",  ipHoste);
            bancoDados.insert("hosts", null, cv);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public List<Host> selectHosts(){

        Cursor c = bancoDados.rawQuery("SELECT * FROM hosts", null);
        List<Host> hosts = new ArrayList<>();

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Host host = new Host();
                host.setID(c.getInt(0));
                host.setNomeHost(c.getString(1));
                host.setIdHost(c.getString(2));
                hosts.add(host);
                c.moveToNext();
            }
        }

        return hosts;
    }

    public boolean update(String nome, String ip, int id){
        try {
            ContentValues cv = new ContentValues();
            cv.put("nomeHost", nome);
            cv.put("ipHost", ip);
            String where = "id = " + id;
            if(bancoDados.update("hosts", cv, where, null) != -1){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void deletarHost(String hostIp, int id){
        bancoDados.execSQL("DELETE FROM hosts WHERE ipHost = '" + hostIp + "' AND id = " + id);
    }

}
