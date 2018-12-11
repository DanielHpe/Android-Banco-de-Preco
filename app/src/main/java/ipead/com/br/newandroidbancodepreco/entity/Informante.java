package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */

public class Informante {

    private int idI;
    private int idPeriodoColeta;

    private int idUsuario;

    private String rS;
    private String t;
    private String ob;
    private String o;
    private String end;
    private int tipo;
    private double lat;
    private double lon;
    private String tel;
    private String c;
    private String val;
    private String s;
    //    private String produtosNaoColetados;
    private String periodicidade;
    private String tipoRota;
    private boolean isSelected;
    public int getIdInformante() {
        return idI;
    }

    public String getDescricao() {
        return rS;
    }

    public int getIdPeriodoColeta() {
        return idPeriodoColeta;
    }

    public void setIdPeriodoColeta(int idInformantePeriodoColeta) {
        this.idPeriodoColeta = idInformantePeriodoColeta;
    }

    public String getTransporte() {
        return t;
    }

    public String getObservacao() {
        return ob;
    }

    public String getOrcamento() {
        return o;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getEndereco() {
        return end;
    }

    public int getTipo() {
        return tipo;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public String getTelefone() {
        return tel;
    }

    public String getContato() {
        return c;
    }

    public String getValidade() {
        return val;
    }

    public String getStatus() {
        return s;
    }


//    public String getProdutosNaoColetados() {
    public void setIdInformante(int sIdInformante) {
        idI = sIdInformante;
    }

    //    }
    public void setDescricao(String sDescricao) {
        rS = sDescricao;
    }

    //        return produtosNaoColetados;
    public void setTransporte(String sTransporte) {
        t = sTransporte;
    }

    public void setObservacao(String sObservacao) {
        ob = sObservacao;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setOrcamento(String sOrcamento) {
        o = sOrcamento;
    }

    public void setEndereco(String sEndereco) {
        end = sEndereco;
    }

    public void setTipo(int sTipo) {
        tipo = sTipo;
    }

    public void setLatitude(double sLatitude) {
        lat = sLatitude;
    }

    public void setLongitude(double sLongitude) {
        lon = sLongitude;
    }

    public void setTelefone(String sTelefone) {
        tel = sTelefone;
    }

    public void setContato(String sContato) {
        c = sContato;
    }

    public void setValidade(String sValidade) {
        val = sValidade;
    }

    public void setStatus(String sStatus) {
        s = sStatus;
    }

//    public void setProdutosNaoColetados(String sProdutos) {
//        produtosNaoColetados = sProdutos;
//    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public String getTipoRota() {
        return tipoRota;
    }

    public void setTipoRota(String tipoRota) {
        this.tipoRota = tipoRota;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
