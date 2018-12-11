package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */

public class Produto {

    private int idP;
    private String dR;
    private String u;
    private int idG;
    private boolean isChecked;
    private int tipoProduto;

    public int getIdProduto() {
        return idP;
    }

    public String getDescricao() {
        return dR;
    }

    public String getUnidade() {
        return u;
    }

    public int getIdGrupo() {
        return idG;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public int getTipoProduto() {
        return tipoProduto;
    }

    public void setIdProduto(int sIdProduto) {
        idP = sIdProduto;
    }

    public void setDescricao(String sDescricao) {
        dR = sDescricao;
    }

//    public void setUnidade(String sUnidade) {
//        u = sUnidade;
//    }

    public void setIdGrupo(int sIdGrupo) {
        idG = sIdGrupo;
    }

    public void setIsChecked(boolean sIsChecked) {
        isChecked = sIsChecked;
    }

    public void setTipoProduto(int sTipoProduto) {
        tipoProduto = sTipoProduto;
    }

}
