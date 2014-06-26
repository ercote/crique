package net.edc.crique.jsp;


public class Tag {}
/**
 * @author coteeri
 *
 * Taglib utile afin d'intégrer le contenu d'un fichier du dépét
 * dans une jsp.
 *
 * Paramétres:
 * servletContextReference: valeur de la référence du dépét dans le contexte du servlet.
 * relativePath: chemin relatif du fichier dans le dépét.
 * silent (optionnel): 	si true, une exception survenue n'apparaétra pas dans la page.
 * 						true par défaut.
 * replace: doit étre un objet String[][].
 * Permet de remplacer des chaénes de caractéres dans le contenu du fichier.
 * Remplace toutes les instances de [i][0] pour [i][1].
 *
 *  Utilisation:
 <depot:insert
   servletContextReference="String"
   relativePath="String"
   silent="true|false"
   replace="String[][]"/>


public class Tag extends TagSupport {

  public static final long serialVersionUID = 2L;

  protected static Logger log = Logger.getLogger(DepotTag.class);

  String servletContextReference, relativePath, silent;

  String[][] replace;

  public int doStartTag() throws JspException {
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {

    boolean bSilent = 	silent == null ||
              !String.valueOf(false).equals(silent); // true par défaut

    Depot depot = DepotPlugIn.getDepot(this.pageContext.getServletContext(), servletContextReference);
    if (depot != null) {
      try {
        byte[] bytes = depot.getContenu(relativePath);
        if (bytes != null) {
          String contenu = new String(bytes);
          pageContext.getOut().write(doReplace(contenu));
        }
      }
      catch(Exception e) {
        if (bSilent) {
          if (log.isErrorEnabled()) {
            log.error(DepotTag.class.getName(), e);
          }
        } else {
          throw new JspException("DepotStrutsTag.doEndTag() : ", e);
        }
      }
    } else {
      String msg = DepotTag.class.getName() +
            " : Aucun dépét de fichiers attribué é la référence [" + servletContextReference +
            "] dans le contexte du servlet actuel. \nRéférence invalide: " + servletContextReference;
      if (bSilent) {
          log.error(msg);
      } else {
        throw new JspException(msg);
      }
    }

    return EVAL_PAGE;
  }

  String doReplace(String s) {
    if(	replace != null && replace.length > 0 &&
      s != null && s.length() > 0) {
      for(int i=0; i<replace.length; ++i) {
        s = s.replaceAll(replace[i][0], replace[i][1]);
      }
    }
    return s;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public String getServletContextReference() {
    return servletContextReference;
  }

  public void setServletContextReference(String servletContextReference) {
    this.servletContextReference = servletContextReference;
  }

  public String getSilent() {
    return silent;
  }

  public void setSilent(String silent) {
    this.silent = silent;
  }

  public String[][] getReplace() {
    return replace;
  }

  public void setReplace(String[][] replace) {
    this.replace = replace;
  }
}
*/
