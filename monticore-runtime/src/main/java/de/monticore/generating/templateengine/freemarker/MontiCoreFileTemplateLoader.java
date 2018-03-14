/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.freemarker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;
import freemarker.cache.FileTemplateLoader;

public class MontiCoreFileTemplateLoader extends FileTemplateLoader {
  
  /**
   * A template loader that uses files in a specified directory as the source of templates.
   * 
   * @author Galina Volkova
   */
  public MontiCoreFileTemplateLoader(File baseDir) throws IOException {
    super(baseDir);
  }
  
  @Override
  public java.lang.Object findTemplateSource(String templateName) throws java.io.IOException {
    Log.debug("Looking for template " + templateName, MontiCoreFileTemplateLoader.class.getName());
    
    String completeName = templateName.replace('.', '/').concat(
        FreeMarkerTemplateEngine.FM_FILE_EXTENSION);
    Object template = super.findTemplateSource(completeName);
    if (template == null) {
      if (templateName.endsWith(FreeMarkerTemplateEngine.FM_FILE_EXTENSION)) {
        template = super.findTemplateSource(templateName);
      }
      else {
        template = super.findTemplateSource(templateName
            .concat(FreeMarkerTemplateEngine.FM_FILE_EXTENSION));
      }
    }
    if (template != null) {
      Reporting.reportOpenInputFile(Optional.of(getBaseDirectory().toPath()), Paths.get(completeName));
      Reporting.reportUserSpecificTemplate(getBaseDirectory().toPath(), Paths.get(completeName));
    }
    else {
      List<Path> pathes = Lists.newArrayList();
      pathes.add(Paths.get(getBaseDirectory().getAbsolutePath()));
      Reporting.reportFileExistenceChecking(pathes,
          Paths.get(completeName));
    }
    return template;
  }
  
}
