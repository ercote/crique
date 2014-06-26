package net.edc.crique;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.edc.crique.run.DepotFichiersRunner;
import net.edc.crique.run.FileContentRetriever;

public class DepotUtils {

    /**
     * Obtenir le contenu sur disque d'un fichier présent dans le
     * dépôt.
     * Aucune vérificaiton ni mise é jour de l'index n'est effectuée.
     * Pour obtenir le contenu d'un fichier dont le suffix est é
     * omettre de l'index, utiliser cette méthode. Si le suffix du
     * fichier peut étre inclus dans l'index, utiliser plutét
     * la méthode getContenu afin d'accéder l'index.
     *
     * @param relativePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public byte[] getContenuSurDisque(File file, long milliSeconds) throws FileNotFoundException, IOException {
        FileContentRetriever retriever = new FileContentRetriever(file);
        retriever.setName(FileContentRetriever.class.getName());
        DepotFichiersRunner runner = new DepotFichiersRunner(milliSeconds);
        runner.run(retriever);
        return retriever.getBytes();
    }


  /**
   * Permet d'obtenir un tableau d'octets représentant le contenu d'un fichier.
   *
   * @param f
   * @return byte[] : le contenu du fichier
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static byte[] getFileContent(File f) throws FileNotFoundException, IOException {
    FileInputStream is = new FileInputStream(f);
    long length = f.length();
    byte[] bytes = null;
    if (length <= Integer.MAX_VALUE) {
      bytes = new byte[(int)length];
      int offset = 0;
      int numRead = 0;
      while (offset < bytes.length &&
          (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
        offset += numRead;
      }
      is.close();
    }
    return bytes;
  }

  /**
   * Permet d'obtenir la liste des fichiers sous un répertoire.
   * Cette méthode est récursive.
   * Les fichiers de tous les sous-répertoires sont inclus.
   *
   * @param dir
   * @param filter (peut étre null)
   * @return List<File> : liste des fichiers
   */
  public static List<File> getFiles(File dir, FileFilter filter) {
    ArrayList<File> list = new ArrayList<File>();
    File[] files = filter != null ? dir.listFiles(filter) : dir.listFiles();
    if (files != null) {
      for(File f : files) {
        if (f.isDirectory()) {
          list.addAll(getFiles(f, filter));
        } else {
          list.add(f);
        }
      }
    }
    return list;
  }

  /**
   *  Permet de standardiser les séparateurs utilisés dans un path pour
   *  utilisation dans DepotFichiers
   *  @param String
   *  path: path é standardiser
   *
   *  @return String
   *  La chaîne standardisée
   */
  static public String standardizePath(String path) {
    char[] chars = path.toCharArray();
    for (int i = 0; i < chars.length; ++i) {
      if (chars[i] == '\\' || chars[i] == '/')
        chars[i] = File.separatorChar;
    }
    return String.valueOf(chars);
  }
}
