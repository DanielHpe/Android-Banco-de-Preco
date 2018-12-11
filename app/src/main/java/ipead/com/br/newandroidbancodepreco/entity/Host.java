package ipead.com.br.newandroidbancodepreco.entity;

public class Host {

  private String nomeHost;
  private String idHost;
  private int ID;
  private boolean isMarked;

  public int getID() {
    return ID;
  }

  public void setID(int ID) {
    this.ID = ID;
  }

  public boolean isMarked() {
    return isMarked;
  }

  public void setMarked(boolean marked) {
    isMarked = marked;
  }

  public String getNomeHost() {
    return nomeHost;
  }

  public void setNomeHost(String nomeHost) {
    this.nomeHost = nomeHost;
  }

  public String getIdHost() {
    return idHost;
  }

  public void setIdHost(String idHost) {
    this.idHost = idHost;
  }
}
