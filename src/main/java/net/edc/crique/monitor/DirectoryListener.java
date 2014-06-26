package net.edc.crique.monitor;

import java.io.File;

/**
 *
 * Interface nécessaire pour obtenir les notifications
 * provenant du moniteur de répertoire relativement à
 * la nature des changements.
 */

public interface DirectoryListener {

  /**
   *  Appelée à chaque modification sur un fichier du répertoire.
   */
  public void fileChanged(File file);

  /**
   * Appelée chaque fois qu'un fichier est ajouté au répertoire.
   * à l'attachement du listener au moniteur, cette méthode sera
   * appelée pour chaque fichier surveillé par le moniteur lors de
   * la dernière vérification effectuée.
   */
  public void fileAdded(File file);

  /**
   * Appelée à chaque suppression de fichiers survenue dans le
   * répertoire.
   */
  public void fileDeleted(File file);
}
