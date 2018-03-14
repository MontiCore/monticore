/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.freemarker.MontiCoreFileTemplateLoader;
import de.monticore.generating.templateengine.freemarker.MontiCoreTemplateExceptionHandler;
import de.monticore.generating.templateengine.freemarker.MontiCoreTemplateLoader;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.Log;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Macro;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * Setup for generator (see {@link GeneratorEngine}).
 */
public class GeneratorSetup {

  /**
   * Where to store all files (e.g. "gen" or "out")
   */
  private File outputDirectory = new File("out");

  /**
   * Used for handling variables and hook points;
   * Default is only created with first get-access.
   */
  private GlobalExtensionManagement glex = null;

  /**
   * The path for the handwritten code
   * Default is only created with first get-access.
   */
// TODO, XXX MB: (von BR 12/2017)
// Diese Klasse handcodedPath klann doch durch eine List<String> ersetzt werden?
// Oder auch was anderes, aber wir haben zuviele Lösungen für dasselbe ...
// siehe auch TemplateAutoImport und additionalTemplatePaths
// Vereinheitlichung!
  private IterablePath handcodedPath;

  /**
   * Additional path as the source of templates
   */
  private List<File> additionalTemplatePaths = new ArrayList<>();


  /**
   * Defines if tracing infos are added to the result as comments
   */
  private boolean tracing = true;

  /**
   * The characters for the start of a comment.
   * Usually these are the comments of the target language.
   */
  private String commentStart = "/*";

  /**
   * The characters for the end of a comment.
   * Usually these are the comments of the target language.
   */
  private String commentEnd = "*/";
  
  /**
   * The model name
   * (if the arftifacts are generated from one model, this could 
   * be an identifier of this model)
   * By default the model name is absent -- 
   * and then the according tracing info is not printed at all.
   */
  private Optional<String> modelName = Optional.empty();

  /**
   * The handler for File IO (also manages reporting)
   */
  private FileReaderWriter fileHandler;

  /**
   * The real engine provided by FreeMarker
   */
  private FreeMarkerTemplateEngine freeMarkerTemplateEngine;

  /**
   * Desired default file extension, e.g. "java"
   */
  private String defaultFileExtension = "java";

  /**
   * Additional Suffix for a generated Class, if the 
   * class itself already exists.
   */
  public final static String GENERATED_CLASS_SUFFIX = "TOP";
  
  /**
   * A list of all freemarker functions that serve as aliases for Java methods,
   * e.g. 'include' as alias for 'tc.include'
   */
  private List<Macro> aliases = Lists.newArrayList();

  public static final String ALIASES_TEMPLATE = "de.monticore.generating.templateengine.freemarker.Aliases";
  
  public  Configuration getConfig() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_23);
    
    config.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
    // don't look for templateName.de.ftl when templateName.ftl requested
    config.setLocalizedLookup(false);
    
    MontiCoreTemplateLoader mcClassLoader = new MontiCoreTemplateLoader(getClassLoader());
    
    if (additionalTemplatePaths.isEmpty()) {
      config.setTemplateLoader(mcClassLoader);
    }
    else {
      List<TemplateLoader> loaders = new ArrayList<>();
      
      for (File file : additionalTemplatePaths) {
        // add file loaders. IO checks by FileTemplateLoader constructor
        try {
          loaders.add(new MontiCoreFileTemplateLoader(file));
        }
        catch (IOException e) {
          Log.warn("0xA1020 Unable to load templates from path " + file.getPath(), e);
        }
      }
            
      // this loader is added last such that all "built-in" templates located on the additionaltemplatepath are
      // taking precedence over this one
      loaders.add(mcClassLoader);
      
     config.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders
          .size()])));
    }
    
    config.setTemplateExceptionHandler(new MontiCoreTemplateExceptionHandler(
        MontiCoreTemplateExceptionHandler.THROW_ERROR));
        
    return config;
  }


  /*******************************************************/
  /**
   * Sets the default file extension used for the generated files, e.g. java or
   * .java (with leading dot).
   *
   * @param o the file extension, e.g. java or .java (with leading
   * dot)
   */
  public void setDefaultFileExtension(String o) {
    if (o.startsWith(".")) {
      this.defaultFileExtension = o.substring(1);
    }
    else {
      this.defaultFileExtension = o;
    }
  }

  public String getDefaultFileExtension() {
    return defaultFileExtension;
  }

  /*******************************************************/
  public void setFileHandler(FileReaderWriter o) {
    this.fileHandler = o;
  }

  public FileReaderWriter getFileHandler() {
    if (this.fileHandler == null) 
        this.fileHandler = new FileReaderWriter(); //default
    return fileHandler;
  }

  /*******************************************************/
  public void setFreeMarkerTemplateEngine(FreeMarkerTemplateEngine o) {
    this.freeMarkerTemplateEngine = o;
  }

  public FreeMarkerTemplateEngine getFreeMarkerTemplateEngine() {
    if (this.freeMarkerTemplateEngine == null) {
      this.freeMarkerTemplateEngine =  new FreeMarkerTemplateEngine(getConfig());
    }
    return freeMarkerTemplateEngine;
  }

  /*******************************************************/
  /*******************************************************/

  /**
   * Construtor
   */
  public GeneratorSetup() {
  }

  /*******************************************************/

  public void setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }


  protected ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  public void setGlex(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public GlobalExtensionManagement getGlex() {
    if (this.glex == null) 
    	this.glex = new GlobalExtensionManagement();  //default
    return this.glex;
  }

  public void setAdditionalTemplatePaths(List<File> additionalTemplatePaths) {
    this.additionalTemplatePaths = new ArrayList<>(additionalTemplatePaths);
  }

  public List<File> getAdditionalTemplatePaths() {
    return additionalTemplatePaths;
  }

  /**
   * @return targetPath
   */
  public IterablePath getHandcodedPath() {
    if (this.handcodedPath == null) 
    	this.handcodedPath = IterablePath.empty();  //default
    return this.handcodedPath;
  }

  /**
   * @param hwcpath the handcoded path to set
   */
  public void setHandcodedPath(IterablePath hwcPath) {
    this.handcodedPath = hwcPath;
  }

  /**
   * @param tracing defines if tracing infos are added to the result as comments.
   */
  public void setTracing(boolean tracing) {
    this.tracing = tracing;
  }

  /**
   * @return true, if tracing infos are added to the result as comments.
   */
  public boolean isTracing() {
    return tracing;
  }

  /**
   * @return the characters for the start of a comment. Usually same as the target language.
   */
  public String getCommentStart() {
    return commentStart;
  }

  /**
   * @param commentStart the characters for the start of a comment. Usually same as the target
   * language.
   */
  public void setCommentStart(String commentStart) {
    this.commentStart = commentStart;
  }

  /**
   * @return the characters for the end of a comment. Usually same as the target language.
   */
  public String getCommentEnd() {
    return commentEnd;
  }

  /**
   * @param commentEnd the characters for the end of a comment. Usually same as the target language.
   */
  public void setCommentEnd(String commentEnd) {
    this.commentEnd = commentEnd;
  }
  
  /**
   * @return modelName
   */
  public Optional<String> getModelName() {
    return this.modelName;
  }

  /**
   * @param modelName the modelName to set
   */
  public void setModelName(String modelName) {
    this.modelName = Optional.ofNullable(modelName);
  }

  
  /**
   * @return the aliases
   */
  public List<Macro> getAliases() {
    return this.aliases;
  }

  
  /**
   * @param aliases the aliases to set
   */
  public void setAliases(List<Macro> aliases) {
    this.aliases = aliases;
  }
  
  public void addAlias(Macro alias) {
    this.aliases.add(alias);
  }
  
  /**
   * This is the Method that creates TemplateControllers
   * (it is used afresh for each template that is called)
   * HotSPOT: If a different Template Controller shall be used
   * then override this method in a subclass
   */
  public TemplateController getNewTemplateController(String templateName) {
    return new TemplateController(this, templateName);
  }
  
}
