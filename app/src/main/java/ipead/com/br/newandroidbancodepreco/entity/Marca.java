package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */
public class Marca {

    private int idMpi;
    private int idP;
    private int idI;
    private String m;
    private String u;
    private String rF;
    private String rI;
    private float uM;

    public int getIdMarcaProdutoInformante() {
        return idMpi;
    }

    public int getIdProduto() {
        return idP;
    }

    public int getIdInformante() {
        return idI;
    }

    public String getDescricao() {
        return m;
    }

    public String getUnidade() {
        return u;
    }

    public String getReferenciaFabricante() {
        return rF;
    }

    public String getReferenciaInformante() {
        return rI;
    }

    public float getUltimaMedia() {
        return uM;
    }

    public void setIdMarcaProdutoInformante(int sIdMarca) {
        idMpi = sIdMarca;
    }

    public void setIdProduto(int sIdProduto) {
        idP = sIdProduto;
    }

    public void setIdInformante(int sIdInformante) {
        idI = sIdInformante;
    }

    public void setDescricao(String sDescricao) {
        m = sDescricao;
    }

    public void setUnidade(String sUnidade) {
        u = sUnidade;
    }

    public void setReferenciaFabricante(String sRefeFabricante) {
        rF = sRefeFabricante;
    }

    public void setReferenciaInformante(String sRefeInformante) {
        rI = sRefeInformante;
    }

    public void setUltimaMedia(float sUltimaMedia) {
        uM = sUltimaMedia;
    }

}
