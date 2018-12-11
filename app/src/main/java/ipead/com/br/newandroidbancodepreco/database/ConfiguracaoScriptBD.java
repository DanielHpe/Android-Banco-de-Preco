package ipead.com.br.newandroidbancodepreco.database;

/**
 * Created by daniel
 *
 */

public class ConfiguracaoScriptBD {

    public static final String DBNAME_INFORMANTE = "informanteColeta";

    public static String createInformante =
        "CREATE  TABLE IF NOT EXISTS `informante` ("
        + " `idInformante` INT NOT NULL ,"
        + " `idPeriodoColeta` INT NOT NULL ,"
        + " `descricao` VARCHAR(100) NOT NULL ,"
        + " `transporte` VARCHAR(255) NULL ,"
        + " `observacao` TEXT NULL ,"
        + " `orcamento` VARCHAR(45) NULL ,"
        + " `endereco` VARCHAR(512) NULL ,"
        + " `tipo` INT NOT NULL ,"
        + " `latitude` FLOAT NULL ,"
        + " `longitude` FLOAT NULL ,"
        + " `telefone` VARCHAR(15) NULL ,"
        + " `contato` VARCHAR(255) NULL ,"
        + " `validade` DATE NOT NULL ,"
        + " `status` INT NOT NULL ,"
        + " `idUsuario` INT NOT NULL ,"
        + " `periodoColeta` VARCHAR(45) NOT NULL ,"
        + " `inicio` DATE NOT NULL ,"
        + " `fim` DATE NOT NULL ,"
        + " `rota` VARCHAR(45) NOT NULL ,"
        + " `tipoRota` VARCHAR(45) NOT NULL ,"
        + " `nome` VARCHAR(100) NOT NULL ,"
        + " `login` VARCHAR(45) NOT NULL ,"
        + " `senha` VARCHAR(255) NOT NULL ,"
        + " `idRota` INT NOT NULL ,"
        + "  PRIMARY KEY (`idInformante`, `idRota`, `idPeriodoColeta`) )";


    public static String createGrupo =
        "CREATE TABLE IF NOT EXISTS grupo ("
        + " 'idGrupo' INT NOT NULL,"
        + " 'descricao' VARCHAR(100) NOT NULL,"
        + " PRIMARY KEY (`idGrupo`) )";

    public static String createProduto =
            "CREATE  TABLE IF NOT EXISTS `produto` ("
            + " `idProduto` INT NOT NULL ,"
            + " `descricao` VARCHAR(512) NOT NULL ,"
            + " `unidade` VARCHAR(255) NOT NULL ,"
            + " `idGrupo` INT NOT NULL ,"
            + " PRIMARY KEY (`idProduto`) ,"
            + " FOREIGN KEY (`idGrupo` )"
            + " REFERENCES `grupo` (`idGrupo` ))";

    /*public static String createProdutoInformante =
            "CREATE  TABLE IF NOT EXISTS `produtoInformante` ("
                    + " `idProduto` INT NOT NULL,"
                    + " `idInformante` INT NOT NULL,"
                    + " `statusColeta` INT NOT NULL DEFAULT 1,"
                    + " PRIMARY KEY (`idProduto`, `idInformante`),"
                    + " FOREIGN KEY (`idProduto`) REFERENCES `produto` (`idProduto`),"
                    + " FOREIGN KEY (`idInformante`) REFERENCES `informante`(`idInformante`))";*/

    public static String createMarca =
        "CREATE TABLE IF NOT EXISTS `marca` ("
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

    public static String createColeta =
        "CREATE  TABLE IF NOT EXISTS `coleta` ("
            + " `idMarcaProdutoInformante` INT NOT NULL ,"
            + " `idPeriodoColeta` VARCHAR(45) NOT NULL ,"
            + " `tipo` INT NOT NULL ,"
            + " `quantidade` SMALLINT NULL ,"
            + " `preco` FLOAT NULL ,"
            + " `data` TIMESTAMP NOT NULL ,"
            + " `localColetaLat` DOUBLE,"
            + " `localColetaLong` DOUBLE,"
            + " PRIMARY KEY (`idMarcaProdutoInformante`, `idPeriodoColeta`, `tipo`) ,"
            + " FOREIGN KEY (`idMarcaProdutoInformante` )"
            + " REFERENCES `marca` (`idMarcaProdutoInformante` ),"
            + " FOREIGN KEY (`idPeriodoColeta` )"
            + " REFERENCES `configuracao` (`idPeriodoColeta` ))";



    /*public static String[] dropTablesInformanteColeta = new String[] {
            "DROP TABLE IF EXISTS coleta",
            "DROP TABLE IF EXISTS produtoInformante",
            "DROP TABLE IF EXISTS informante",
            "DROP TABLE IF EXISTS marca",
            "DROP TABLE IF EXISTS produto",
            "DROP TABLE IF EXISTS grupo"
    };*/

    /*public static String[] createTablesInformanteColeta = new String[] {
            "CREATE TABLE IF NOT EXISTS grupo ("
                    + " 'idGrupo' INT NOT NULL,"
                    + " 'descricao' VARCHAR(100) NOT NULL,"
                    + " PRIMARY KEY ('idGrupo'))",
            "CREATE  TABLE IF NOT EXISTS `informante` ("
                    + " `idInformante` INT NOT NULL ,"
                    + " `descricao` VARCHAR(100) NOT NULL ,"
                    + " `transporte` VARCHAR(255) NULL ,"
                    + " `observacao` TEXT NULL ,"
                    + "  `orcamento` VARCHAR(45) NULL ,"
                    + "   `endereco` VARCHAR(512) NULL ,"
                    + "  `tipo` INT NOT NULL ,"
                    + "   `latitude` FLOAT NULL ,"
                    + "     `longitude` FLOAT NULL ,"
                    + " 	`telefone` VARCHAR(15) NULL ,"
                    + " 	`contato` VARCHAR(100) NULL ,"
                    + " 	`validade` DATE NOT NULL ,"
                    + " 	`status` INT NOT NULL ,"
                    + "     PRIMARY KEY (`idInformante`) )",
            "CREATE  TABLE IF NOT EXISTS `produto` ("
                    + " `idProduto` INT NOT NULL ,"
                    + " `descricao` VARCHAR(512) NOT NULL ,"
                    + " `unidade` VARCHAR(255) NOT NULL ,"
                    + " `idGrupo` INT NOT NULL ,"
                    + " PRIMARY KEY (`idProduto`) ,"
                    + " FOREIGN KEY (`idGrupo` )"
                    + " REFERENCES `grupo` (`idGrupo` ))",
            "CREATE  TABLE IF NOT EXISTS `produtoInformante` ("
                    + " `idProduto` INT NOT NULL,"
                    + " `idInformante` INT NOT NULL,"
                    + " `statusColeta` INT NOT NULL DEFAULT 1,"
                    + " PRIMARY KEY (`idProduto`, `idInformante`),"
                    + " FOREIGN KEY (`idProduto`) REFERENCES `produto` (`idProduto`),"
                    + " FOREIGN KEY (`idInformante`) REFERENCES `informante`(`idInformante`))",
            "CREATE TABLE IF NOT EXISTS `marca` ("
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
                    + " REFERENCES `informante` (`idInformante` ))",
            "CREATE  TABLE IF NOT EXISTS `coleta` ("
                    + " `idMarcaProdutoInformante` INT NOT NULL ,"
                    + " `idPeriodoColeta` VARCHAR(45) NOT NULL ,"
                    + " `tipo` INT NOT NULL ,"
                    + " `quantidade` SMALLINT NULL ,"
                    + " `preco` FLOAT NULL ,"
                    + " `data` TIMESTAMP NOT NULL ,"
                    + " `localColetaLat` DOUBLE,"
                    + " `localColetaLong` DOUBLE,"
                    + " PRIMARY KEY (`idMarcaProdutoInformante`, `idPeriodoColeta`, `tipo`) ,"
                    + " FOREIGN KEY (`idMarcaProdutoInformante` )"
                    + " REFERENCES `marca` (`idMarcaProdutoInformante` ),"
                    + " FOREIGN KEY (`idPeriodoColeta` )"
                    + " REFERENCES `configuracao` (`idPeriodoColeta` ))"
    };*/
}
