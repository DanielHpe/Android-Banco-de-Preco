package ipead.com.br.newandroidbancodepreco.entity;

/**
 * Created by daniel on 31/08/17.
 */

public class Coleta {

    private int idMarcaProdutoInformante;
    private int idPeriodoColeta;
    private int tipo;
    private int quantidade;
    private double preco;
    private String data;
    private double localColetaLat;
    private double localColetaLong;


    public int getIdMarcaProdutoInformante() {
        return idMarcaProdutoInformante;
    }

    public int getIdPeriodoColeta() {
        return idPeriodoColeta;
    }

    public int getTipo() {
        return tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public String getData() {
        return data;
    }

    public void setIdMarcaProdutoInformante(int sIdMarca) {
        idMarcaProdutoInformante = sIdMarca;
    }

    public void setIdPeriodoColeta(int sIdPeriodoColeta) {
        idPeriodoColeta = sIdPeriodoColeta;
    }

    public void setTipo(int sTipo) {
        tipo = sTipo;
    }

    public void setQuantidade(int sQuantidade) {
        quantidade = sQuantidade;
    }

    public void setPreco(double sPreco) {
        preco = sPreco;
    }

    public void setData(String sData) {
        data = sData;
    }

    public double getLocalColetaLat() {
        return localColetaLat;
    }

    public void setLocalColetaLat(double localColetaLat) {
        this.localColetaLat = localColetaLat;
    }

    public double getLocalColetaLong() {
        return localColetaLong;
    }

    public void setLocalColetaLong(double localColetaLong) {
        this.localColetaLong = localColetaLong;
    }

}
