package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Produto;

/**
 * Created by daniel on 23/01/2018.
 */
public class ProdutoDAO {

    private MultiBD db;
    private String filePath;
    private Context context;
    private String createProduto = "CREATE  TABLE IF NOT EXISTS `produto` ("
            + " `idProduto` INT NOT NULL ,"
            + " `descricao` VARCHAR(512) NOT NULL ,"
            + " `unidade` VARCHAR(255) NOT NULL ,"
            + " `idGrupo` INT NOT NULL ,"
            + " PRIMARY KEY (`idProduto`) ,"
            + " FOREIGN KEY (`idGrupo` )"
            + " REFERENCES `grupo` (`idGrupo` ))";

    public ProdutoDAO(String filePath, Context c) {
        this.filePath = filePath;
        context = c;
        db = new MultiBD(filePath);
    }

    public long inserirProduto(Produto produto) {

        ContentValues values = new ContentValues();
        values.put("idProduto", produto.getIdProduto());
        values.put("descricao", produto.getDescricao());
        values.put("unidade", produto.getUnidade());
        values.put("idGrupo", produto.getIdGrupo());

        long result = db.insert("produto", values);

        return result;
    }

    public boolean inserirListProduto(List<Produto> produtos)
    {
        int cadastrado = 0;
        db.beginTransaction();

        try {
            for (int i = 0; i < produtos.size(); i++) {
                inserirProduto(produtos.get(i));
                cadastrado++;
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
        }

        db.endTransaction();

        if(cadastrado == produtos.size())
            return true;
        else
            return false;
    }

    public int selectIdProduto(){

        String query = "SELECT idProduto FROM produto";

        int cont = 0;

        Cursor result = db.database.rawQuery( query, null );

        if(result.moveToFirst())
        {
            do{
                cont++;
            } while (result.moveToNext());
        }

        return cont;

    }

    public List<HashMap<String, String>> selectProduto(String idInformante, String idGrupo){

        boolean stat = SharedPref.readBoolean("stat", false);

        String sql;

        String[] whereArgs = new String[] { idInformante, idGrupo };

        if(stat){

            sql = "SELECT idProduto, p.descricao, p.unidade, COUNT(idMarcaProdutoInformante) AS total,"
                    + " (SELECT COUNT(DISTINCT idMarcaProdutoInformante)"
                    + " FROM marca mC"
                    + " INNER JOIN coleta c USING (idMarcaProdutoInformante)"
                    + " WHERE idProduto = m.idProduto "
                    + " AND idInformante = m.idInformante) AS coletado"
                    + " FROM produto p"
                    + " INNER JOIN marca m USING (idProduto)"
                    + " WHERE idInformante = " + whereArgs[0]
                    + " AND idGrupo = " + whereArgs[1]
                    + " GROUP BY p.idProduto"
                    + " ORDER BY p.descricao";

        } else {

            sql = "SELECT idProduto, p.descricao, p.unidade"
                    + " FROM produto p"
                    + " INNER JOIN marca m USING (idProduto)"
                    + " WHERE idInformante = " + whereArgs[0] + " AND idGrupo = "
                    + whereArgs[1] + " GROUP BY idProduto ORDER BY p.descricao";

        }

        Cursor c = db.query(sql, null);

        List<HashMap<String, String>> list = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                HashMap<String, String> item = new HashMap<>();
                item.put("idProduto", String.valueOf(c.getInt(0)));
                item.put("Nome", String.valueOf(c.getInt(0)) + " - " + c.getString(1) + " - " + c.getString(2));
                if(stat){
                    item.put("Status", c.getString(4) + " de " + c.getString(3) + " Marca(s) Cadastrada(s)");
                } else {
                    item.put("Status", "Estatística desabilitada");
                }

                list.add(item);

            } while (c.moveToNext());
        }

        c.close();

        return list;
    }

    public String retornaDescricaoProduto(String idInformante, String idGrupo, String idProduto){

        String[] whereArgs = new String[] { idInformante, idGrupo, idProduto};
        String descCompleta = "";

        String sql = "SELECT p.descricao, p.unidade"
                + " FROM grupo g"
                + " INNER JOIN produto p USING (idGrupo)"
                + " INNER JOIN marca m USING (idProduto)"
                + " WHERE idInformante = " + whereArgs[0]
                + " AND idGrupo = " + whereArgs[1]
                + " AND idProduto = " + whereArgs[2]
                + " GROUP BY idProduto";

        Cursor c = db.query(sql, null);

        if(c.moveToFirst() && c.getCount() > 0){
            String produto = c.getString(0) + " - " + c.getString(1);
            descCompleta = produto;
        }

        c.close();

        return descCompleta;
    }

    public void createTableProduto()
    {
        db.execSQL(createProduto);
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }
}
