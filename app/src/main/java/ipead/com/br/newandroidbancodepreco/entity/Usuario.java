package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */
public class Usuario {

    private int idUsuario;
    private int idGrupoUsuario;
    private String nome;
    private String login;
    private String senha;

    public int getIdUsuario() {
        return idUsuario;
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

    public void setIdUsuario(int sIdUsuario) {
        idUsuario = sIdUsuario;
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

    public int getIdGrupoUsuario() {
        return idGrupoUsuario;
    }
//
//    public void setIdGrupoUsuario(int sidGrupoUsuario) {
//        idGrupoUsuario = sidGrupoUsuario;
//    }
}
