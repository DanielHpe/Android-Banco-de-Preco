package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Marca;

/**
 * Created by daniel on 23/01/2018.
 */
public class MarcaDAO {

    private MultiBD db;
    private String filePath;
    private String createMarca = "CREATE TABLE IF NOT EXISTS `marca` ("
            + " `idMarcaProdutoInformante` INT NOT NULL ,"
            + " `idProduto` INT NOT NULL ,"
            + " `idInformante` INT NOT NULL ,"
            + " `descricao` VARCHAR(255) NOT NULL ,"
            + " `unidade` VARCHAR(255) NOT NULL ,"
            + " `referenciaFabricante` VARCHAR(255) NULL ,"
            + " `referenciaInformante` VARCHAR(255) NULL ,"
            + " `ultimaMedia` FLOAT ,"
            + " PRIMARY KEY (`idMarcaProdutoInformante`) ,"
            + " FOREIGN KEY (`idProduto` )"
            + " REFERENCES `produto` (`idProduto` ),"
            + " FOREIGN KEY (`idInformante` )"
            + " REFERENCES `informante` (`idInformante` ))";

    public MarcaDAO(String filePath){
        this.filePath = filePath;
        db = new MultiBD(filePath);
    }

    public long inserirMarca(Marca marca)
    {
        ContentValues values = new ContentValues();

        values.put("idMarcaProdutoInformante", marca.getIdMarcaProdutoInformante());
        values.put("idProduto", marca.getIdProduto());
        values.put("idInformante", marca.getIdInformante());
        values.put("descricao", marca.getDescricao());
        values.put("unidade", marca.getUnidade());
        values.put("referenciaFabricante", marca.getReferenciaFabricante());
        values.put("referenciaInformante", marca.getReferenciaInformante());
        values.put("ultimaMedia", marca.getUltimaMedia());

        long result = db.insert("marca", values);

        return result;
    }

    public boolean inserirListMarca(List<Marca> marcas)
    {
        int i = 0;
        db.beginTransaction();

        try {

            for (i = 0; i < marcas.size(); i++) {
                inserirMarca(marcas.get(i));
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
        }

        db.endTransaction();

        if (i == marcas.size())
            return true;
        else
            return false;
    }

    public int selectIdMarca(){

        String query = "SELECT idMarcaProdutoInformante FROM marca";

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

    public List<HashMap<String, String>> selectMarcas(String idInformante, String idProduto){

        String[] whereArgs = new String[]{ idInformante, idProduto };

        String sql = "SELECT idMarcaProdutoInformante, m.descricao,"
                + " (SELECT COUNT(idMarcaProdutoInformante)"
                + " FROM coleta c"
                + " WHERE c.idMarcaProdutoInformante = m.idMarcaProdutoInformante) AS coletado,"
                + " (SELECT COUNT(idMarcaProdutoInformante)"
                + " FROM coleta c"
                + " WHERE c.idMarcaProdutoInformante = m.idMarcaProdutoInformante AND preco IS NULL) AS semPreco,"
                + " m.referenciaFabricante, m.referenciaInformante"
                + " FROM produto p"
                + " INNER JOIN marca m USING (idProduto)"
                + " WHERE idInformante = " + whereArgs[0]
                + " AND idProduto = " + whereArgs[1]
                + " GROUP BY m.idMarcaProdutoInformante"
                + " ORDER BY m.descricao";

        Cursor c = db.query(sql, null);

        List<HashMap<String, String>> list = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                HashMap<String, String> item = new HashMap<>();
                item.put("idMarcaProdutoInformante",
                        String.valueOf(c.getInt(0)));
                item.put("idProduto", String.valueOf(whereArgs[1]));
                item.put("idInformante", String.valueOf(whereArgs[0]));

                if(c.getString(4).length() > 0 || c.getString(5).length() > 0) {
                    String refF = c.getString(4);
                    String refI = c.getString(5);

                    if(refF.equals(refI))
                        item.put("Nome", c.getString(1) + " (" + refI + ")");
                    else
                        item.put("Nome", c.getString(1) + " (" + refF + " | " + refI + ")");
                }
                else
                    item.put("Nome", c.getString(1));

                if(Integer.parseInt(c.getString(3)) > 0)
                    item.put("Status", "Sem Preço");
                else if(Integer.parseInt(c.getString(2)) > 0)
                    item.put("Status", "Preço Cadastrado");

                list.add(item);

            } while (c.moveToNext());
        }

        c.close();

        return list;
    }

    public double getUltimaMediaFromMarca(String idMarca){

        String[] whereArgs = new String[] { idMarca };

        MultiBD get = new MultiBD(filePath);
        String sql = "SELECT m.ultimaMedia"
                + " FROM produto p"
                + " INNER JOIN marca m USING (idProduto)"
                + " WHERE idMarcaProdutoInformante = " + whereArgs[0]
                + " GROUP BY idMarcaProdutoInformante;";
        Cursor c = get.query(sql, null);
        c.moveToFirst();
        double result = Double.parseDouble(c.getString(0));

        c.close();
        get.close();

        return result;
    }

    public void createTableMarca()
    {
        db.execSQL(createMarca);
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }
}
