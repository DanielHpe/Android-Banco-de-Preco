package ipead.com.br.newandroidbancodepreco.config;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by daniel on 26/01/2018.
 */
/*
public class JSONConnect {

    public static void postJSONFromApi(final String url, final String encodedJson){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL newUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject(encodedJson);
//
                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

//                    int codigoResposta = conn.getResponseCode();

//                    if(codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST){
////                        is = conexao.getInputStream();
//                    }else{
////                        is = conexao.getErrorStream();
//                    }

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public static String getJSONFromAPI(String url, String userCredentials){

        String retorno = "";

        try {
            URL apiEnd = new URL(url);
            int codigoResposta;
            HttpURLConnection conexao;
            InputStream is;


            conexao = (HttpURLConnection) apiEnd.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setDoOutput(true);
            conexao.setRequestProperty("Auth", userCredentials);
            String get = conexao.getRequestProperty("Auth");
            conexao.setConnectTimeout(15000);
            conexao.setReadTimeout(15000);
            conexao.connect();

            codigoResposta = conexao.getResponseCode();

            if(codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST){
                is = conexao.getInputStream();
            }else{
                is = conexao.getErrorStream();
            }

            retorno = conectarHttp(is);
            is.close();
            conexao.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return retorno;
    }


    public static String conectarHttp(InputStream is){

        StringBuffer stringBuffer = new StringBuffer();

        try{

            BufferedReader bufferedReader;
            String linha;

            bufferedReader = new BufferedReader(new InputStreamReader(is));

            while ((linha = bufferedReader.readLine()) != null) {
                stringBuffer.append(linha);
            }

            bufferedReader.close();

        } catch(Exception e){

            e.printStackTrace();

        }

        return stringBuffer.toString();
    }
}
*/
