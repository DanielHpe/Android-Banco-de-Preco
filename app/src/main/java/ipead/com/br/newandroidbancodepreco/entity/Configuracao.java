package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */
public class Configuracao {

    private int idPeriodoColeta;
    private int idUsuario;
    private String periodoColeta;
    private String nome;
    private String login;
    private String senha;
    private String inicio;
    private String fim;
    private int idRota;
    private String rota;
    private int informantesAbertos;
    private String tipo;

    public int getIdPeriodoColeta() {
        return idPeriodoColeta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getPeriodoColeta() {
        return periodoColeta;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getInicio() {
        return inicio;
    }

    public String getFim() {
        return fim;
    }

    public int getIdRota() {
        return idRota;
    }

    public String getRota() {
        return rota;
    }

    public void setIdPeriodoColeta(int sIdPeriodoColeta) {
        idPeriodoColeta = sIdPeriodoColeta;
    }

    public void setIdUsuario(int sIdUsuario) {
        idUsuario = sIdUsuario;
    }

    public void setPeriodoColeta(String sPeriodoColeta) {
        periodoColeta = sPeriodoColeta;
    }

    public void setNome(String sNome) {
        nome = sNome;
    }

    public void setLogin(String sLogin) {
        login = sLogin;
    }

    public void setSenha(String sSenha) {
        senha = sSenha;
    }

    public void setInicio(String sInicio) {
        inicio = sInicio;
    }

    public void setFim(String sFim) {
        fim = sFim;
    }

    public void setIdRota(int sIdRota) {
        idRota = sIdRota;
    }

    public void setRota(String sRota) {
        rota = sRota;
    }

    public int getInformantesAbertos() {
        return informantesAbertos;
    }

    public void setInformantesAbertos(int informantesAbertos) {
        this.informantesAbertos = informantesAbertos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
