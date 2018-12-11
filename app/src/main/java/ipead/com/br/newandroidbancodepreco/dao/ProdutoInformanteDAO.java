package ipead.com.br.newandroidbancodepreco.dao;

import android.content.Context;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;

/**
 * Created by daniel on 23/01/2018.
 */
public class ProdutoInformanteDAO {

    private MultiBD db;
    private String createProdutoInformante = "CREATE  TABLE IF NOT EXISTS `produtoInformante` ("
            + " `idProduto` INT NOT NULL,"
            + " `idInformante` INT NOT NULL,"
            + " `statusColeta` INT NOT NULL DEFAULT 1,"
            + " PRIMARY KEY (`idProduto`, `idInformante`),"
            + " FOREIGN KEY (`idProduto`) REFERENCES `produto` (`idProduto`),"
            + " FOREIGN KEY (`idInformante`) REFERENCES `informante`(`idInformante`))";
    private String insertProdutoInformante = "INSERT INTO produtoInformante (idProduto, idInformante)"
            + " SELECT idProduto, idInformante"
            + " FROM marca"
            + " GROUP BY idProduto, idInformante";
    private Context context;

    public ProdutoInformanteDAO(Context context, String filePath) {
        this.context = context;
        db = new MultiBD(filePath);
    }

    public void createTableProdutoInformante()
    {
        db.execSQL(createProdutoInformante);
//		Log.i("PRODUTO INFORMANTE", "CRIADO TABELA produtoInformante");

        db.execSQL(insertProdutoInformante);
//		Log.i("PRODUTO INFORMANTE", "Populado TABELA produtoInformante");
    }

    public static void updateStatusColeta(int idProduto, int idInformante, String filePath) {
        MultiBD con = new MultiBD(filePath);

        String sql = "UPDATE produtoInformante"
                + " SET statusColeta = COALESCE((SELECT MAX((CASE WHEN preco IS NOT NULL OR preco <> 0 THEN 3 ELSE 2 END)) AS status"
                + " FROM coleta"
                + " JOIN marca USING (idMarcaProdutoInformante)"
                + " WHERE idProduto = " + idProduto + " AND idInformante = " + idInformante + "), 1)"
                + " WHERE idProduto = " + idProduto + " AND idInformante = " + idInformante;

        try {
            con.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            con.close();
            throw new RuntimeException();
        }

        con.close();
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }


}
