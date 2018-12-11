package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.GrupoActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.dao.SemPrecoDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Informante;
import ipead.com.br.newandroidbancodepreco.entity.Ocorrencia;
import ipead.com.br.newandroidbancodepreco.entity.Produto;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daniel on 16/02/2018.
 */
public class RecycleInformanteAdapter extends RecyclerView.Adapter<RecycleInformanteAdapter.ViewHolder> {

    private List<Informante> listaInformante;
    private Activity context;
    private SweetAlertDialog progress;
    private int idRota;
    private int idPeriodoColeta;
    private String filepath;
    private String mainPath;
    private Boolean isEmpty = true;
    private DirectoryBrowser browser;
    private String login;
    private String senha;
    private int idUser;
    private int contadorProdutosSelecionados = 0;
    private SyncpriceService service;
    private String userCredentials;
    private SemPrecoListAdapter adapter;
    private InformanteDAO informanteDAO;
    private static int selectedItem;

    public RecycleInformanteAdapter(Activity context, List<Informante> list,
                                    int idRota, int idPeriodoColeta,
                                    String login, String senha, String mainPath){
        this.context = context;
        this.listaInformante = list;
        this.idRota = idRota;
        this.idPeriodoColeta = idPeriodoColeta;
        this.login = login;
        this.senha = senha;
        this.mainPath = mainPath;
        this.service = new SyncpriceService(context);
        SharedPref.init(context);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtInformante;
        public TextView txtPeriodo;
        public TextView txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            txtInformante = itemView.findViewById(R.id.txtInformante);
            txtPeriodo = itemView.findViewById(R.id.txtPeriodo);
            txtStatus = itemView.findViewById(R.id.txtStatus);

        }

    }

    @Override
    public RecycleInformanteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.model_recycleview, parent, false);

        RecycleInformanteAdapter.ViewHolder holder = new RecycleInformanteAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecycleInformanteAdapter.ViewHolder holder, final int position) {

        final Informante configSelect = listaInformante.get(position);
        final int idInformante = configSelect.getIdInformante();
        browser = new DirectoryBrowser(context);

        holder.txtInformante.setText(configSelect.getDescricao());
        holder.txtPeriodo.setText(configSelect.getPeriodicidade());
        filepath = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                + "-" + idRota + "-" + idInformante;

        informanteDAO = new InformanteDAO(context, filepath, mainPath);

        if(SharedPref.readBoolean("restart", false)){
            holder.itemView.setSelected(selectedItem == position);
            holder.itemView.setBackgroundColor(selectedItem == position ? Color.CYAN : Color.TRANSPARENT);
        }

        SharedPref.writeBoolean("restart", false);

        switch(configSelect.getStatus().charAt(0)){
            case '1':
                holder.txtStatus.setText("Aberto");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(21, 160, 51));
                break;
            case '2':
                holder.txtStatus.setText("Fechado");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;
            case '3':
                holder.txtStatus.setText("Fechado (Vazio)");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;
            case '4':
                holder.txtStatus.setText("Transferido");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(241, 119, 4));
                break;
            case '5':
                holder.txtStatus.setText("Transferido (Vazio)");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(241, 119, 4));
                break;
            case '6':
                holder.txtStatus.setText("Erro");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPref.writeInt("pos", position);

                selectedItem = holder.getAdapterPosition();

                if(Integer.parseInt(configSelect.getStatus()) != 1){

                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Aviso")
                            .setContentText("Não é possível ter acesso a um Informante Fechado!")
                            .setConfirmText("OK")
                            .show();

                } else {

                    Intent intent = new Intent(context, GrupoActivity.class);
                    intent.putExtra("idRota", idRota).putExtra("idPeriodo", idPeriodoColeta).putExtra(
                            "idInfo", configSelect.getIdInformante())
                            .putExtra("position", position).putExtra("mainPath", mainPath);
                    context.startActivity(intent);

                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String status = configSelect.getStatus();

                if(status.equals("1") || status.equals("2") || status.equals("3")){

                    Button verDetalhes;
                    Button fecharInformante;

                    AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
                    View mView = context.getLayoutInflater().inflate(R.layout.alert_dialog_informante, null);

                    verDetalhes = mView.findViewById(R.id.btnVerDetalhes);
                    fecharInformante = mView.findViewById(R.id.btnFecharInformante);

                    myDialog.setView(mView);
                    final AlertDialog dialog = myDialog.create();
                    dialog.show();

                    verDetalhes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showInfosInformante(configSelect, dialog);

                        }
                    });

                    fecharInformante.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            processoFecharInformante(idInformante, configSelect, position);
                            dialog.dismiss();
                        }
                    });

                } else {

                    Toast.makeText(context, "Informante já transferido!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listaInformante.size();
    }

    private void showInfosInformante(Informante configSelect, AlertDialog dialog) {

        TextView id;
        TextView nome;
        TextView endereco;
        TextView contato;
        TextView telefone;
        TextView transporte;
        TextView orcamento;
        TextView observacao;
        TextView tipo;

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        final View mView = context.getLayoutInflater().inflate(R.layout.alertbox_list_detalhes_info, null);

        id = mView.findViewById(R.id.infosIdInformante);
        nome = mView.findViewById(R.id.infosNomeInformante);
        endereco = mView.findViewById(R.id.infosEnderecoInformante);
        contato = mView.findViewById(R.id.infosContatoInformante);
        telefone = mView.findViewById(R.id.infosTelefoneInformante);
        transporte = mView.findViewById(R.id.infosTransporteInformante);
        orcamento = mView.findViewById(R.id.infosOrcamentoInformante);
        observacao = mView.findViewById(R.id.infosObservacaoInformante);
        tipo = mView.findViewById(R.id.infosTipoInformante);

        myDialog.setView(mView);
        final AlertDialog dialogList = myDialog.create();
        dialog.dismiss();
        dialogList.show();

        InformanteDAO informanteDAO = new InformanteDAO(context, null, mainPath);
        int idInformante = configSelect.getIdInformante();

        Cursor c = informanteDAO.getInformacoesInformante(String.valueOf(idInformante));

        if (c.moveToFirst()) {

            id.setText("Id Informante: " + String.valueOf(idInformante));
            nome.setText("Nome: " + c.getString(0));

            if(!c.getString(4).equalsIgnoreCase("null")){
                endereco.setText("Endereco: " + c.getString(1));
            } else {
                endereco.setText("Endereco: - ");
            }

            contato.setText("Contato: " + c.getString(2));
            telefone.setText("Telefone: " + c.getString(3));

            if(!c.getString(4).equalsIgnoreCase("null")){
                transporte.setText("Transporte: " + c.getString(4));
            } else {
                transporte.setText("Transporte: - ");
            }

            orcamento.setText("Orçamento: " + c.getString(5));
            observacao.setText("Observação: " + c.getString(6));

            if (Integer.parseInt(c.getString(7)) == 1)
                tipo.setText("Tipo: Atacado");
            else if (Integer.parseInt(c.getString(7)) == 2)
                tipo.setText("Tipo: Varejo");
            else if (Integer.parseInt(c.getString(7)) == 3)
                tipo.setText("Tipo: Atacado e Varejo");

        }
    }

    public void processoFecharInformante(final int idInformante, final Informante configSelect, int pos){

        new MaterialDialog.Builder(context)
                .title("Aviso")
                .content("Deseja realmente fechar este Informante?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fecharInformante(idInformante, configSelect, dialog);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fecharInformante(int idInformante, final Informante configSelect, final MaterialDialog dialog) {

        int status;

        try{

            final int idInfo = configSelect.getIdInformante();
            filepath = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                    + "-" + idRota + "-" + idInfo;
            Log.d("IdInfo", String.valueOf(idInfo));

            InformanteDAO infoDAO = new InformanteDAO(context, filepath, mainPath);
            status = infoDAO.statusAtualInformante(String.valueOf(idInfo));

            if(status == InformanteDAO.INFORMANTE_PARCIAL){

                final ListView lista;
                final AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
                final View mView = context.getLayoutInflater().inflate(R.layout.alert_box_list, null);
                lista = mView.findViewById(R.id.listViewSemPrecoOptions);
                final List<Produto> produtosSemPreco = listaProdutoSempreco(configSelect, idInformante);

                myDialog.setView(mView);
                final AlertDialog dialogList = myDialog.create();
                dialogList.show();

                final CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                        final int pos = lista.getPositionForView(buttonView);

                        if(pos != ListView.INVALID_POSITION){

                            Produto produto = produtosSemPreco.get(pos);
                            produto.setIsChecked(isChecked);

                            if(produto.isChecked() == true){
                                contadorProdutosSelecionados++;
                            } else{
                                contadorProdutosSelecionados--;
                            }

                            if(contadorProdutosSelecionados == produtosSemPreco.size()){
                                showConfirmaProdutosSemPreco(configSelect);
                                dialog.dismiss();
                            }
                            Log.d("N de produtos marcados", String.valueOf(contadorProdutosSelecionados));

                        }

                    }
                };

                int tipo = informanteDAO.getTipoInformante(String.valueOf(idInfo));

                adapter = new SemPrecoListAdapter(context, produtosSemPreco, idInfo, idPeriodoColeta, tipo, filepath, check);
                lista.setAdapter(adapter);


            } else if(status == InformanteDAO.INFORMANTE_VAZIO){

                showAlertVazio(configSelect);
                dialog.dismiss();

            } else if(status == InformanteDAO.INFORMANTE_TODOS_PRECOS){

                infoDAO.fecharInformante(idInfo, false);
                isEmpty = false;
                showAlertValidade(configSelect);
                dialog.dismiss();

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showAlertVazio(final Informante configSelect){

        new MaterialDialog.Builder(context)
                .title("Aviso")
                .content("Deseja finalizar este informante sem nenhum preço válido cadastrado?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fecharVazio(configSelect);
                        dialog.dismiss();
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

    private void fecharVazio(final Informante configSelect) {

        final int idInfo = configSelect.getIdInformante();

        filepath = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                + "-" + idRota + "-" + idInfo;

        informanteDAO.fecharInformante(idInfo, true);
        informanteDAO.close();

        if (Conexao.verificaConexao(context)) {
            isEmpty = true;
            showAlertExportar(configSelect);
        } else {
            context.finish();
            context.startActivity(context.getIntent());
        }

    }

    public void showConfirmaProdutosSemPreco(final Informante configSelect){

        new MaterialDialog.Builder(context)
                .title("Aviso")
                .content("Deseja realmente colocar este(s) produto(s) como SEM PREÇO?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        confirmaSemPreco(configSelect, dialog);
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

    private void confirmaSemPreco(final Informante configSelect, MaterialDialog dialog) {

        final SemPrecoDAO semPrecoDAO = new SemPrecoDAO(context, filepath);
        final int idInfo = configSelect.getIdInformante();
        final String filepath = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                + "-" + idRota + "-" + idInfo;

        if(semPrecoDAO.confirmaTodosSemPreco(idInfo)){
            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Aviso")
                    .setContentText("Produtos confirmados como sem Preço!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            informanteDAO = new InformanteDAO(context, filepath, mainPath);
                            if(informanteDAO.fecharInformante(idInfo, false)){
                                showAlertValidade(configSelect);
                            }
                        }
                    })
                    .show();
            dialog.dismiss();
        } else {
            context.finish();
            context.startActivity(context.getIntent());
            semPrecoDAO.close();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showAlertValidade(final Informante configSelect){

        final Calendar cal = Calendar.getInstance();
        final DatePicker picker;
        final CheckBox semValidade;
        final Dialog dialog = new Dialog(context);
        final int idInfo = configSelect.getIdInformante();

        Calendar val = InformanteDAO.getValidade(context, idInfo);

        dialog.setContentView(R.layout.calendar_informante);
        dialog.setTitle("Informar Validade: ");
        picker = dialog.findViewById(R.id.datePickerValidade);
        semValidade = dialog.findViewById(R.id.checkValidade);

        dialog.show();

        semValidade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    picker.setVisibility(View.GONE);
                else
                    picker.setVisibility(View.VISIBLE);

            }
        });

        DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                if(dayOfMonth > 28) {
                    dayOfMonth = 28;
                    picker.updateDate(year, monthOfYear, dayOfMonth);
                }

            }
        };

        cal.add(Calendar.DAY_OF_MONTH, 1);
        if(val != null) {
            semValidade.setChecked(false);
            picker.init(val.get(Calendar.YEAR), val.get(Calendar.MONTH),
                    val.get(Calendar.DAY_OF_MONTH), dateListener);
            picker.setVisibility(View.VISIBLE);
        } else {
            semValidade.setChecked(true);
            picker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), dateListener);
            picker.setVisibility(View.GONE);
        }

        Button salvarValidade = dialog.findViewById(R.id.btnSalvarValidade);

        salvarValidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!semValidade.isChecked()) {
                    Calendar pick = Calendar.getInstance();
                    cal.set(picker.getYear(), picker.getMonth(),
                            picker.getDayOfMonth());
                    pick.add(Calendar.DAY_OF_YEAR, 7);

                    if (cal.before(pick)) {

                        Toast.makeText(context, "Data Inválida!", Toast.LENGTH_SHORT).show();

                    } else {

                        informanteDAO.setValidade(String.valueOf(idInfo), cal);

                        if (Conexao.verificaConexao(context)){
                            showAlertExportar(configSelect);
                        } else {
                            context.finish();
                            context.startActivity(context.getIntent());
                        }

                    }
                } else {

                    if (Conexao.verificaConexao(context)){
                        showAlertExportar(configSelect);
                    } else {
                        context.finish();
                        context.startActivity(context.getIntent());
                    }
                }
            }
        });


    }

    public void showAlertExportar(final Informante configSelect){

        new MaterialDialog.Builder(context)
                .title("Aviso")
                .content("Deseja enviar os dados desse Informante para o Servidor?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        progress = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                        progress.setTitleText("Enviando dados...");
                        progress.setCancelable(false);
                        progress.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendDadosServidor(configSelect);
                            }
                        }).start();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        context.finish();
                        context.startActivity(context.getIntent());
                    }
                })
                .show();
    }

    public void sendDadosServidor(Informante configSelect){

        try{

            UsuarioDAO userDAO = new UsuarioDAO(context);
            int statusInformante = -1;
            final int idInfo = configSelect.getIdInformante();
            final String path = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                    + "-" + idRota + "-" + idInfo;
            idUser = Integer.parseInt(userDAO.getIdUsuario(login));
            informanteDAO = new InformanteDAO(context, path, mainPath);
            userCredentials = userDAO.getUserCredentials(idUser);

            try{
                statusInformante = informanteDAO.statusAtualInformante(String.valueOf(idInfo));
            } catch (Exception e){
                DirectoryBrowser browser = new DirectoryBrowser(context);
                File[] f = new File(browser.dirF()).listFiles();
                String bdF = MultiBD.BD_INFORMANTE + idPeriodoColeta + "-" + idRota  + "-" + idInfo;
                if(browser.exists(f, bdF)) {
                    informanteDAO = new InformanteDAO(context, null, mainPath);
                    informanteDAO.setStatusInformanteTransferido(String.valueOf(idInfo), isEmpty);
                    alertSuccess();
                } else {
                    throw e;
                }
            }

            if(statusInformante == InformanteDAO.INFORMANTE_TODOS_PRECOS){
                sendInformante(idInfo);
            } else {
                sendInformanteVazio(idInfo);
            }

        } catch (Exception e){

            e.printStackTrace();
            final String locMessage = e.getLocalizedMessage();

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    informanteDAO.close();
                    progress.dismiss();
                    Toast.makeText(context, "Falha ao enviar dados!", Toast.LENGTH_LONG).show();
                    if(locMessage.equalsIgnoreCase("org.apache.http.client.HttpResponseException: Unauthorized")) {
//                        showDialog(MainActivity.ALERTA);
                    }

                }
            });

        }

    }

    public void sendInformante(final int idInfo) {

        String jsonColeta = informanteDAO.coletaInformanteToJson(context, String.valueOf(idInfo), login);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonColeta);

        service.getAPI()
                .enviarColeta(userCredentials, idInfo, body)
                .enqueue(new Callback<Ocorrencia>() {
                    @Override
                    public void onResponse(Call<Ocorrencia> call, Response<Ocorrencia> response) {
                        Ocorrencia ocorrencia = response.body();
                        alertSuccess();
                        Log.i("RESPONSE", ocorrencia.toString());
                        setInformanteTransferido(ocorrencia, idInfo);
                    }

                    @Override
                    public void onFailure(Call<Ocorrencia> call, Throwable t) {

                        try {
                            alertError();
                            throw new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    public void sendInformanteVazio(final int idInfo){

        service.getAPI().finalizarInformanteSemPreco(userCredentials, idInfo, idPeriodoColeta).enqueue(new Callback<Ocorrencia>() {
            @Override
            public void onResponse(Call<Ocorrencia> call, Response<Ocorrencia> response) {
                Ocorrencia ocorrencia = response.body();
                alertSuccess();
                Log.i("RESPONSE", ocorrencia.toString());
                setInformanteTransferido(ocorrencia, idInfo);
            }

            @Override
            public void onFailure(Call<Ocorrencia> call, Throwable t) {

                try {
                    alertError();
                    throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void setInformanteTransferido(final Ocorrencia ocorrencia, final int idInfo){

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ocorrencia != null && ocorrencia.getOcorrencia().equals("Informante finalizado com sucesso!")) {
                    isEmpty = false;
                    informanteDAO.setStatusInformanteTransferido(String.valueOf(idInfo), isEmpty);
                    moverDadosTransferidos();
                    alertSucessFinish("Dados enviados com sucesso!");
                } else if(ocorrencia != null && ocorrencia.getOcorrencia().equals("Informante Finalizado Vazio!")) {
                    isEmpty = true;
                    informanteDAO.setStatusInformanteTransferido(String.valueOf(idInfo), isEmpty);
                    moverDadosTransferidos();
                    alertSucessFinish("Dados enviados com sucesso!");
                } else if(ocorrencia != null && ocorrencia.getOcorrencia().equals("Este informante já foi finalizado!")){
                    informanteDAO.setStatusInformanteTransferido(String.valueOf(idInfo), isEmpty);
                    reenviarDados();
                } else {
                    alertErrorFinish();
                }
                informanteDAO.close();
            }
        });
    }

    public List<Produto> listaProdutoSempreco(Informante configSelect, int idInformante){

        SemPrecoDAO semPrecoDAO = new SemPrecoDAO(context, filepath);
        Cursor c = semPrecoDAO.produtosSemPreco(configSelect.getTipo(), String.valueOf(idInformante));
        List<Produto> produtos = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                Produto prod = new Produto();
                prod.setIdProduto(Integer.parseInt(c.getString(0)));
                prod.setIdGrupo(Integer.parseInt(c.getString(1)));
                prod.setDescricao(c.getString(2));

                if(configSelect.getTipo() == 3)
                    prod.setTipoProduto(Integer.parseInt(c.getString(3)));
                else
                    prod.setTipoProduto(configSelect.getTipo());

                prod.setIsChecked(false);
                produtos.add(prod);

            } while (c.moveToNext());
        }

        c.close();

        return produtos;

    }

    private void reenviarDados() {
        new MaterialDialog.Builder(context)
                .title("Aviso")
                .content("Os dados desse informante já foram enviados 1 vez. Deseja enviar novamente ?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    alertSucessFinish("Dados reenviados com sucesso!");
                    moverDadosTransferidos();
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

    public void moverDadosTransferidos(){

        File[] files = new File(browser.dirF()).listFiles();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, -10);

        for(int i = 0; i < files.length; i++){
            if (files[i].lastModified() < date.getTimeInMillis()) {
                files[i].delete();
            }
        }

        String moveFrom = browser.dir();
        int index = filepath.lastIndexOf('/') + 1;
        String nomeArquivo = filepath.substring(index, filepath.length());
        String moveTo = browser.dirF();
        browser.moverArquivo(moveFrom, nomeArquivo, moveTo);

        File[] filesProducao = new File(browser.dir()).listFiles();
        if(new File(browser.dir()).listFiles().length > 0){
            browser.deleteJournals(filesProducao);
        }

    }

    public void alertSuccess(){
        Dialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sucesso!")
                .setContentText("Dados enviados com sucesso!")
                .setConfirmText("OK");

        progress.dismissWithAnimation();
        dialog.show();
    }

    public void alertSucessFinish(String message){
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sucesso")
                .setContentText(message)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        context.finish();
                        context.startActivity(context.getIntent());
                    }
                })
                .show();
    }

    public void alertError(){
        Dialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Erro!")
                .setContentText("Falha ao enviar dados!")
                .setConfirmText("OK");
        progress.dismissWithAnimation();
        dialog.show();
    }

    public void alertErrorFinish(){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Erro")
                .setContentText("Erro ao enviar Dados!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        notifyDataSetChanged();
                    }
                })
                .show();
    }
}
