/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.freemarker;

import java.net.URL;
import java.util.Optional;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import freemarker.cache.URLTemplateLoader;

/**
 * Is used to load templates with a given {@link ClassLoader}.
 * 
 * @author Arne Haber
 */
public class MontiCoreTemplateLoader extends URLTemplateLoader {
  
  /** the used class loader */
  private final ClassLoader classLoader;
  
  /**
   * Creates a new {@link MontiCoreTemplateLoader} that uses the given class loader to load
   * FreeMarker templates from the class path
   * 
   * @param classLoader
   */
  public MontiCoreTemplateLoader(ClassLoader classLoader) {
    Log.errorIfNull(classLoader,
        "0xA4049 ClassLoader must not be null in MontiCoreTemplateLoader constructor.");
    this.classLoader = classLoader;
  }
  
  /**
   * Resolves the location of a template on the local machine.
   * 
   * @see freemarker.cache.URLTemplateLoader#getURL(java.lang.String)
   * @param templateName The qualified name of the Template. Example: "cd2data.core.Attribute" or
   * "cd2data/core/Attribute" or "cd2data.core.Attribute.ftl"
   * @return URL of the template on the local machine. Example:
   * "jar:file:/C:/.../dex/gtr/target/dex-gtr-0.9.4-SNAPSHOT.jar!/cd2data/core/Attribute.ftl" (this
   * is from the toString method of the URL object)
   */
  @Override
  protected URL getURL(String templateName) {
    Log.debug("Requested template " + templateName, MontiCoreTemplateLoader.class.getName());
    FileReaderWriter ioWrapper = new FileReaderWriter();
    
    // Since the input is almost always dot separated, this method just goes ahead and converts it
    // without checking, only in the rare case that this procedure is unsuccessful are
    // alternatives considered
    Optional<URL> result = ioWrapper.getResource(classLoader,
        templateName.replace('.', '/').concat(FreeMarkerTemplateEngine.FM_FILE_EXTENSION));
    if (result.isPresent()) {
      return result.get();
    }
    // if the search was still unsuccessful the method tries once more and checks if the problem
    // was that the original input already had the .ftl suffix (in that case the previous
    // procedure would've added this suffix again).
    // Note: This part of the procedure was placed here because it is almost never chosen.
    if (templateName.endsWith(FreeMarkerTemplateEngine.FM_FILE_EXTENSION)) {
      String newName = templateName.substring(0,
          templateName.length() - FreeMarkerTemplateEngine.FM_FILE_EXTENSION.length());
      result = ioWrapper.getResource(classLoader,
          newName.replace('.', '/').concat(FreeMarkerTemplateEngine.FM_FILE_EXTENSION));
    }
    else {
      result = ioWrapper.getResource(classLoader, templateName);
    }
    return result.orElse(null);
  }
  
}
