# Crique

Crique permet de monitorer et conserver en cache le contenu d'un répertoire source.

Les vérifications du répertoire s'effectuent à intervales réguliers.

Chaque fichier monitoré sous ce répertoire doit être déclaré dans la configuration (_white list_).

Une clé est associée à chaque fichier, qui sera utilisée pour accéder à son contenu/objet.

Crique fonctionne sous Java 5 et +.


## Comment?

Pour instancier un dépôt:

``` java
Depot depot = DepotFactory.getInstance( "chemin_vers_ma_config.xml" );
depot.init();
```

À partir du dépôt, pour accéder au contenu brut d'un fichier, utiliser la clé spécifiée:
``` java
byte[] content = depot.getContent("notes");
```

Si un _wildcard_ est présent dans le _path_ du fichier, le premier paramètre est la clé et le deuxième représente la valeur à insérer à la position du _wildcard_:
``` java
byte[] content = depot.getContent("texts", "june2014");
```

Si un _parser_ est spécifié pour un fichier, l'objet retourné sera déterminé par la valeur de retour du _parser_:
``` java
XmlData data = (XmlData)depot.getObject("xml", "firstQuarter");
```


## Configuration

La configuration se fait sous le format XML.

Il est possible de spécifier une classe _parser_ à appeler lorsqu'un changement (ajout ou modification) est détecté sur un fichier. Dans ce cas, l'objet retourné par le _parser_ sera conservé dans le dépôt. Sinon, le contenu brut du fichier est caché.

Dans le chemin du fichier, il est possible d'utiliser un _wildcard_, qui permettra de référencer plusieurs fichiers dans la même entrée d'une configuration.

Exemple de configuration:
``` xml

<?xml version="1.0" encoding="utf-8"?>
<!-- répertoire racine -->
<depotConfig src="/Users/ercote/Dropbox/Documents">
   <!-- OPTIONNELS

   <selector>net.edc.crique.LastResultsSelector</selector>
   <listener>net.edc.crique.LastResultsListener</listener>
   -->

  <!-- OPTIONNEL, 30 secondes par défaut -->
  <monitoringInterval>60</monitoringInterval>

  <!-- OPTIONNEL, false par défaut -->
  <live>false</live>

  <files>
    <file key="notes" path="Notes.txt"/>
    <file key="texts" path="*.txt"/>
    <file key="xml" path="data/*.xml">com.mycompany.data.XmlParser</file>
  </files>

</depotConfig>

```

L'élément _live_ indique au dépôt de vérifier le fichier ciblé à chaque demande de contenu/objet. Si le fichier est modifié depuis le dernier accès, il sera mis à jour dans le dépôt. Utile pour les phases de développement afin de ne pas être ralenti par l'intervale de vérification.
**Ne pas utiliser un dépôt _live_ dans un environnement de production.**

L'élément _selector_ permet de sélectionner programmatiquement les fichiers à monitorer sous le répertoire source. La classe du sélecteur doit découler de l'interface _net.edc.crique.monitor.DirectorySelector_.

L'élément _listener_ permet d'être notifié de chacun des changements détectés sous le répertoire source.
La classe du _listener_ doit découler de l'interface _net.edc.crique.monitor.DirectoryListener_.

## À faire

* Documentation en anglais
* Mettre à jour le monitoring afin d'utiliser les dernières avancées Java (java.nio.file)
* Scénarios de tests JUnit
