package ipead.com.br.newandroidbancodepreco.config;

/**
 * Created by daniel on 11/09/17.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Download da rota e de seus dados (informantes, grupos, produtos, marcas)
 * @author Daniel Henrique
 */
public class DirectoryBrowser {

    //private boolean multiFiles = false;

    private Context context;

    public DirectoryBrowser(Context ctx){
        this.context = ctx;
        SharedPref.init(ctx);
    }

    /*
     * Retorna o caminho e o nome do arquivo do diretorio padrão
     * @return
     *
    public String getFile(){
        File d = new File(dir());
        int stateFile = 0;

        if(!d.isDirectory()){
            d.mkdir();
        }

        File[] f = new File(dir()).listFiles();
        if(f.length == 1)
            stateFile = 1;
        else if (f.length == 2 && (f[0].getName().equals(f[1].getName()+"-journal") || f[1].getName().equals(f[0].getName()+"-journal") ))
            stateFile = 1;
        else
            stateFile = f.length;

        if(stateFile > 0){
            if(stateFile > 1)
                this.setMultiFiles(true);

            return f[0].getPath();
        }
        else
            return "";
    }*/

    /*
     * Retorna o caminho e o nome de um unico arquivo no diretorio informado caso exista mais de um ou nenhum retorna error
     * @param dir
     * @return
     *
    public String getFileFromDir(String dir){
        File[] f = new File(dir).listFiles();
        int stateFile = 0; //(int) f.length;

        if(f.length == 1)
            stateFile = 1;
        else if (f.length == 2 && (f[0].getName().equals(f[1].getName()+"-journal") || f[1].getName().equals(f[0].getName()+"-journal") ))
            stateFile = 1;
        else
            stateFile = f.length;

        if(stateFile > 0){
            if(stateFile > 1){
                this.setMultiFiles(true);
                Log.e("Error", "Existe mais de um arquivo na pasta especificada!");
            }

            return f[0].getPath();
        }
        else
            return "";
    }*/

    /*
     * E a recria ou cria se caso nao existir
     * @return
     *
    public boolean criarDiretorio(){
        if(new File(dirF()).listFiles().length <= 0)
            return false;

        File dir = new File(dirF());

        if(dir.isDirectory()){
            File[] f = new File(dirF()).listFiles();
            for (File i : f) {
                i.delete();
            }
        }

        dir.mkdirs();
        return true;
    }*/

    public void moverArquivo(String inputPath, String inputFile, String outputPath){

        InputStream in;
        OutputStream out;

        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            if(!inputFile.contains("-journal")){

                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + inputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                // write the output file

                in.close();
                out.flush();
                out.close();

            }

            File f = new File(inputPath + inputFile);
            f.delete();

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    /*public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }*/

    /*public void deleteFile(String FileToDelete){

        try {

            new File(FileToDelete).delete();

        } catch (Exception e) {

            Log.e("tag", e.getMessage());

        }
    }*/

    /**
     * Move o conteudo da pasta para o diretorio final
     * @param filePath TODO
     */
    public void moveFile(String filePath, String moveTo){
        try {

            File[] l = new File(filePath).listFiles();
//            File c;

            if(filePath.equals(dirTemp())){

                for(int i = 0; i < l.length; i++){
                    int index = l[i].toString().lastIndexOf('/') + 1;
                    String nomeArquivo = l[i].toString().substring(index, l[i].toString().length());
                    moverArquivo(filePath, nomeArquivo, moveTo);
                    boolean x = delete(l, nomeArquivo);
                    Log.d("Boolean", String.valueOf(x));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** Copia o conteudo da pasta para o diretorio final
     * @param filePath TODO
     *
    public void copyFile(String filePath, String moveTo){
        try {
            int x;
            File[] l = new File(filePath).listFiles();
            File c;

            if(!l[0].getName().contains("-journal"))
                x = 0;
            else
                x = 1;

            c = new File(moveTo + l[x].getName());

            InputStream in = new FileInputStream(l[x].getPath());
            OutputStream out = new FileOutputStream(c);

            Log.i("directory", l[0].getPath());

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public boolean isMultiFiles() {
        return multiFiles;
    }*/

    /*public void setMultiFiles(boolean multiFiles) {
        this.multiFiles = multiFiles;
    }*/

    /**
     * @author Daniel Pereira
     * Cria pasta com nome especificado e seus diretorios anteriores caso necessario
     * @param path
     * @return True - criou, False - não criou
     */

    public boolean criarPasta(String path) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("Dir permission", "Permission before: " + dir.canWrite());

        if(!dir.canWrite()) {
            dir.setWritable(true);
            Log.i("Dir permission", "Permission after: " + dir.canWrite());
        }

        boolean isCreated = false;
        File folder = new File(path);

        if(!folder.exists()) {
            if(folder.mkdirs())
                isCreated = true;
        }

        return isCreated;
    }

    /*public boolean createSdCardPath(String sdCardPath){

        File dir = new File(sdCardPath);
        Log.i("Dir permission", "Permission before: " + dir.canWrite());

        if(!dir.canWrite()) {
            dir.setWritable(true);
            Log.i("Dir permission", "Permission after: " + dir.canWrite());
        }

        boolean isCreated = false;

        if(!dir.exists())
        {
            if(dir.mkdirs())
                isCreated = true;
        }

        return isCreated;
    }*/

    /**
     * @author Daniel Pereira
     * Verifica se existe arquivo o pasta dentro de um array de arquivos
     * @param f
     * @param nome
     * @return
     */
    public boolean exists(File[] f, String nome) {
        boolean result = false;

        try {
            for (int i = 0; i < f.length; i++) {
                if (f[i].getName().equalsIgnoreCase(nome)){
                    return true;
                }
            }
        } catch(Exception e){
            result = false;
        }

        return result;
    }

    public boolean existsFolder(File[] f, String nome){
        boolean result = false;

        try {
            for (int i = 0; i < f.length; i++) {
                if (f[i].toString().equalsIgnoreCase(nome)){
                    return true;
                }
            }
        } catch(Exception e){
            result = false;
        }

        return result;
    }

    /**
     * @author Daniel Pereira
     * Verifica se existe arquivo o pasta dentro de um array de arquivos
     * e deleta o arquivo e o -journal se houver
     * @param f File[]
     * @param nome String
     * @return
     */
    public boolean delete(File[] f, String nome) {
        boolean result = false;

        try {
            for (int i = 0; i < f.length; i++) {
                if (f[i].getName().equalsIgnoreCase(nome) || f[i].getName().equalsIgnoreCase(nome + "-journal")){
                    return f[i].delete();
                }
            }
        } catch(Exception e){
            result = false;
        }

        return result;
    }

    public boolean deleteJournals(File[] f){
        boolean result = false;

        try {
            for (int i = 0; i < f.length; i++) {
                if (f[i].getName().toString().contains("-journal")){
                    return f[i].delete();
                }
            }
        } catch(Exception e){
            result = false;
        }

        return result;
    }

    public void chmod777(String path){
        File file = new File(path);
        file.setWritable(true);
    }

    public String standardPath(){

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        Log.v("SDFinder","Path: " + dir);
        return dir;
    }

    public String dir(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        } else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "producao/";

        return dir;
    }

    /**
     *
     * @description Diretorio Finalizado
     *
     * @return
     */
    public String dirF(){

        String novoCaminho = SharedPref.readString("path", "");
        String dirF = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dirF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }  else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dirF = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dirF += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dirF = "/storage/extSdCard";

        dirF += "finalizado/";

        return dirF;

    }

    public String dirTemp(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        }  else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "temp/";

        return dir;

    }

    public String dirSistemas(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        } else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        return dir;

    }

    public String dirError(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        }  else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "error/";

        return dir;

    }

    public String dirAtrasado(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        } else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "atrasado/";

        return dir;

    }

    public String dirFinalizadoAutomatico(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }  else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "finalizadoAutomatico/";

        return dir;

    }

    public String dirBancoDados(){

        String novoCaminho = SharedPref.readString("path", "");
        String dir = "";

        if(novoCaminho.equals("") || novoCaminho.equals("padrao")){
            dir = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/";
        }  else if(!novoCaminho.equals("") || !novoCaminho.equals("padrao")) {
            dir = novoCaminho;
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("LGE"))
            dir += "/external_sd";
//		else if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.MODEL.equalsIgnoreCase("SM-G355M"))
//			dir = "/storage/extSdCard";

        dir += "privado/";

        return dir;

    }

    public boolean isSdDirectoryExists(){

        File[] dirs = ContextCompat.getExternalFilesDirs(context, null);

        if(dirs.length > 1){
            return dirs[1].isDirectory();
        } else {
            return false;
        }

    }

    // Try one of the possibles sdcard paths below
    public String sdCardPath(){

        File[] dirs = ContextCompat.getExternalFilesDirs(context, null);
        String pathSD = dirs[1].toString();
        Log.v("SDFinder","Path: " + pathSD);

        return pathSD;
    }
}
