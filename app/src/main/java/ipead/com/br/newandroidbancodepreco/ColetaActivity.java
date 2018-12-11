package ipead.com.br.newandroidbancodepreco;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ipead.com.br.newandroidbancodepreco.config.GPSTracker;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ColetaDAO;
import ipead.com.br.newandroidbancodepreco.dao.MarcaDAO;
import ipead.com.br.newandroidbancodepreco.dao.ProdutoDAO;
import ipead.com.br.newandroidbancodepreco.dao.ProdutoInformanteDAO;
import ipead.com.br.newandroidbancodepreco.entity.Coleta;

public class ColetaActivity extends AppCompatActivity {

    private int idInformante;
    private int idGrupo;
    private int idPeriodoColeta;
    private int tipoInformante;
    private int tipoEscolhido = 1;
    private int idProduto;
    private int idMarca;
    private double porcentagemMediaMax = 1.25;
    private double porcentagemMediaMin = 0.75;
    private String filepath;
    private String descricaomarca;
    private boolean isCadastro = true;
    private StringBuilder newString;
    private ColetaDAO coletaDAO;
    private MarcaDAO marcaDAO;
    private ImageView arrowDown;
    private TextView nomeProduto;
    private TextView marcaNome;
    private EditText preco;
    private EditText qtd;
    private Button cadastrarPreco;
    private Button atualizarPreco;
    private Button semPreco;
    private Coleta coleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta);

        SharedPref.init(getApplicationContext());
        Bundle b = getIntent().getExtras();

        nomeProduto = findViewById(R.id.coletaNomeProduto);
        arrowDown = findViewById(R.id.arrowDown);
        marcaNome = findViewById(R.id.coletaNomeMarca);
        preco = findViewById(R.id.editTextPreco);
        qtd = findViewById(R.id.editTextQuantidade);
        cadastrarPreco = findViewById(R.id.btnCadastrarPreco);
        semPreco = findViewById(R.id.btnSemPreco);

        idInformante = b.getInt("idInfo");
        idGrupo = b.getInt("idGrupo");
        idProduto = b.getInt("idProduto");
        idMarca = Integer.parseInt(b.getString("idMarca"));
        idPeriodoColeta = b.getInt("idPeriodo");
        tipoInformante = b.getInt("tipo");
        descricaomarca = b.getString("descricaoMarca");
        filepath = b.getString("filepath");

        marcaNome.setText("Marca: " + descricaomarca);

        preco.addTextChangedListener(new MascaraMonetaria(preco));
        exibirInformacaoProduto();
        atualizarInfoBotoes();

        cadastrarPreco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cadastrarPreco.getText().toString().equals("Cadastrar")){
                    cadastrar();
                } else if(cadastrarPreco.getText().toString().equals("Atualizar")){
                    atualizar();
                }
            }
        });

        semPreco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarProdutoSemPreco();
            }
        });
    }

    public void atualizarInfoBotoes(){
        coleta = new Coleta();
        coletaDAO = new ColetaDAO(filepath);
        List<Coleta> coletas = coletaDAO.selectColetaDados(tipoInformante, tipoEscolhido, String.valueOf(idInformante),
                String.valueOf(idProduto), String.valueOf(idMarca));

        if(coletas.size() > 0){

            coleta.setPreco(coletas.get(0).getPreco());
            coleta.setQuantidade(coletas.get(0).getQuantidade());

            Log.i("coleta valor", "Valor: " + coleta.getPreco());

            if(coleta.getPreco() == 0.0){
                preco.setText("R$0.00");
            } else{
                int index = String.valueOf(coleta.getPreco()).indexOf(".");
                int subLgth = String.valueOf(coleta.getPreco()).substring(index).length();

                if(subLgth < 3)
                    preco.setText(String.valueOf(coleta.getPreco()) + "0");
                else
                    preco.setText(String.valueOf(coleta.getPreco()));
                preco.setSelection(preco.getText().length());
            }

            qtd.setText(String.valueOf(coleta.getQuantidade()));
            cadastrarPreco.setText("Atualizar");
            isCadastro = false;
        } else {
            preco.setText("R$0.00");
            qtd.setText("1");
            preco.setSelection(preco.getText().length());
            cadastrarPreco.setText("Cadastrar");
        }

    }

    public void exibirInformacaoProduto(){

        ProdutoDAO produtoDAO = new ProdutoDAO(filepath, getApplicationContext());
        final String descricaoProduto = produtoDAO.retornaDescricaoProduto(String.valueOf(idInformante),
                String.valueOf(idGrupo), String.valueOf(idProduto));
        final String fragmentDescricaoProduto = descricaoProduto.substring(0, 13) + "...";

        nomeProduto.setText(fragmentDescricaoProduto);

        arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(nomeProduto.getText().equals(fragmentDescricaoProduto)){

                nomeProduto.setText(descricaoProduto);

            } else if(nomeProduto.getText().equals(descricaoProduto)) {

                nomeProduto.setText(fragmentDescricaoProduto);

            }
            }
        });
    }

    private float setPriceInfo(){

        String precoDigitado = preco.getText().toString().replace("$", "").replace(",", ".")
                .replace("R","");

        newString = new StringBuilder(precoDigitado);

        for(int i = 0; i < precoDigitado.length() - 3; i++){
            if(String.valueOf(newString.charAt(i)).equals(".")){
                newString.setCharAt(i, ' ');
            }
        }

        for(int i = 0; i < newString.length(); i++){
            if(Character.isWhitespace(newString.charAt(i))){
                newString.deleteCharAt(i);
            }
        }

        return Float.parseFloat(newString.toString());

    }

    private void setColetaInfo(){

        coleta.setIdPeriodoColeta(idPeriodoColeta);
        coleta.setIdMarcaProdutoInformante(idMarca);

        if(tipoInformante < 3){
            coleta.setTipo(tipoInformante);
        }
        else{
            coleta.setTipo(tipoEscolhido);
        }

        coleta.setPreco(Double.parseDouble(newString.toString()));
        if(!qtd.getText().toString().equals("")){
            coleta.setQuantidade(Integer.parseInt(qtd.getText().toString()));
        } else {
            coleta.setQuantidade(0);
        }

        GPSTracker gps = new GPSTracker(ColetaActivity.this);

        Log.i("latLong", "Lat: " + gps.getLatitude() + " | Long: " + gps.getLongitude());

        coleta.setLocalColetaLat(gps.getLatitude());
        coleta.setLocalColetaLong(gps.getLongitude());

    }

    public void cadastrar() {

        if(setPriceInfo() > 0){

            setColetaInfo();

            if (verificaQuantidadeIgualAZero()) {
                if(verificaPreco(coleta.getPreco())){
                    inserePreco(1, false);
                } else {
                    conferePreco();
                }
            } else{
                showAlertQuantidade();
            }

        }
    }

    private void atualizar(){

        if(setPriceInfo() > 0){

            setColetaInfo();

            if(verificaQuantidadeIgualAZero()){
                if (verificaPreco(coleta.getPreco())) {
                    inserePreco(2, false);
                } else{
                    conferePreco();
                }
            } else {
                showAlertQuantidade();
            }

        }

    }

    private boolean verificaQuantidadeIgualAZero() {

        if(coleta.getQuantidade() == 0){
            return false;
        }

        return true;

    }

    private void conferePreco(){

        new MaterialDialog.Builder(this)
                .title("Aviso")
                .content("O preço deste produto está realmente correto?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(isCadastro){
                            inserePreco(1, false);
                            dialog.dismiss();
                        } else {
                            inserePreco(2, false);
                            dialog.dismiss();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void confirmarProdutoSemPreco(){

        new MaterialDialog.Builder(this)
                .title("Aviso")
                .content("Deseja confirmar este produto como sem preco ?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        coleta.setIdPeriodoColeta(idPeriodoColeta);
                        coleta.setIdMarcaProdutoInformante(idMarca);
                        coleta.setTipo(tipoInformante);
                        coleta.setPreco(0);
                        coleta.setQuantidade(0);

                        if(isCadastro){
                            inserePreco(1, true);
                            dialog.dismiss();
                        } else {
                            inserePreco(2, true);
                            dialog.dismiss();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void inserePreco(int chave, boolean isSempreco) {

        switch (chave){
            case 1:
                if(coletaDAO.inputData(coleta, 1)){
                    ProdutoInformanteDAO.updateStatusColeta(idProduto, idInformante, filepath);
                    if(isSempreco){
                        showAlertSuccess(3);
                    } else {
                        showAlertSuccess(1);
                    }
                } else {
                    showAlertError("Erro ao inserir dados!");
                }
                break;
            case 2:
                if (coletaDAO.inputData(coleta, 2)) {
                    ProdutoInformanteDAO.updateStatusColeta(idProduto, idInformante, filepath);
                    if(isSempreco){
                        showAlertSuccess(4);
                    } else {
                        showAlertSuccess(2);
                    }
                } else {
                    showAlertError("Erro ao atualizar dados!");
                }
                break;
        }

    }

    private void showAlertSuccess(final int chave){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String mensagem = "";

                switch (chave){
                    case 1:
                        mensagem = "Preço Cadastrado com sucesso!";
                        break;
                    case 2:
                        mensagem = "Preço Atualizado com sucesso!";
                        break;
                    case 3:
                        mensagem = "Preço Cadastrado como SEM PREÇO!";
                        break;
                    case 4:
                        mensagem = "Preço atualizado como SEM PREÇO!";
                        break;
                }

                new SweetAlertDialog(ColetaActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Aviso")
                        .setContentText(mensagem)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();


            }
        });

    }

    private void showAlertQuantidade(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new SweetAlertDialog(ColetaActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Aviso")
                        .setContentText("Quantidade não pode ser vazia ou 0")
                        .show();

            }
        });

    }

    private void showAlertError(final String mensagem){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new SweetAlertDialog(ColetaActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erro")
                        .setContentText(mensagem)
                        .show();
            }
        });

    }

    private boolean verificaPreco(double preco){
        boolean result = false;
        marcaDAO = new MarcaDAO(filepath);

        double avg = marcaDAO.getUltimaMediaFromMarca(String.valueOf(idMarca));
        double avgMax = avg * porcentagemMediaMax;
        double avgMin = avg * porcentagemMediaMin;
        Log.i("Verifica Preco Coleta", "Media: " + avg + " | Media+: " + avgMax + " | Media-: " + avgMin);

        if(avg == 0){
            return true;
        }

        if(preco >= avgMin && preco <= avgMax){
            result = true;
        }

        return result;
    }

    private class MascaraMonetaria implements TextWatcher{
        final EditText campo;

        public MascaraMonetaria(EditText campo) {
            super();
            this.campo = campo;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Locale myLocale = new Locale("pt", "BR");
            String cleanString = s.toString().replaceAll("[R$,.E]", "");
            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance(myLocale).format((parsed/100));

            preco.removeTextChangedListener(this);
            preco.setText(formatted);
            preco.setSelection(formatted.length());
            preco.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
