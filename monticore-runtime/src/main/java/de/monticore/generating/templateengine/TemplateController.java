/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.SimpleHashFactory;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import freemarker.core.Macro;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

/**
 * Provides methods for manipulating the content of templates, mainly for
 * calling and including of templates.
 *
 */
public class TemplateController {

  /**
   * Variable name for the current node (used in the templates)
   **/
  public static final String AST = "ast";

  /**
   * Variable name for the current TemplateController (used in the templates)
   **/
  public static final String TC = "tc";

  /**
   * Variable name for the GLEX object (used in the templates)
   **/
  public static final String GLEX = "glex";

  /**
   * General config variables that hold for all template executions
   */
  private final GeneratorSetup config;

  /**
   * Name of the current template (usually fully qualified)
   */
  private String templatename;

   /**
   * According to FreemArker, templates don't have a "signature"
   * We can mimic such a signature through method calls:
   * The signature(...) method defines a list of variables.
   * And the include call allows a list of arguments that are bound to
   * these variables, when the template is executed.
   */
  private List<Object> arguments = newArrayList();

  private SimpleHash data = SimpleHashFactory.getInstance().createSimpleHash();

  public TemplateController(GeneratorSetup setup, String templatename) {
    this.config = setup;
    this.templatename = templatename;
  }

  /**
   * getters and setters for the relevant attributes
   */
  public GeneratorSetup getGeneratorSetup() {
    return config;
  }

  public String getTemplatename() {
    return templatename;
  }

  protected void setTemplatename(String templatename) {
    this.templatename = templatename;
  }

  /**
   * Execute each of the templates on each ASTNode of the list. Concatenate the
   * results together in one big String and include that into the currently
   * processed output. We iterate on the templates and ASTNodes. In case order
   * is important: The iteration goes like this:
   * 
   *   for ( templates ) { 
   *     for ( ASTNodes ) {...}
   *   } 
   *
   * Inside the inner loop, it is checked whether Hookpoints are to be called.
   *
   * Template filename may be qualified (using "."). When it
   * is not qualified, the filename is taken from the current package (same as
   * the calling template).
   *
   * @param templatenames list of filenames, qualified or not
   * @param astlist where we execute the template on in an iteration
   * @return produced output
   */
  public StringBuilder include(List<String> templatenames, List<ASTNode> astlist) {
    StringBuilder ret = new StringBuilder();
    for (String template : templatenames) {
      for (ASTNode ast : astlist) {
        List<HookPoint> templateForwardings =
			  config.getGlex().getTemplateForwardings(template, ast);
        for (HookPoint templateHp : templateForwardings) {
          ret.append(templateHp.processValue(this, ast));
        }
      }
    }

    return ret;
  }

  /**
   * Execute a template without resolving the forwardings. This method is
   * package default and should only be used by the template hook point
   *
   * @param templateName the name of the template
   * @param ast the ast node
   * @return produced output
   */
  StringBuilder includeWithoutForwarding(String templateName, ASTNode ast) {
    StringBuilder ret = new StringBuilder();
    ret.append(processTemplate(templateName, ast, new ArrayList<>()));
    return ret;
  }

    /**
   * Defines the signature of a template. <br />
   * <br />
   * Note that, due to technical constraints, at first, the current template is
   * included and the arguments are passed. Second, the signature is defined.
   *
   * @param parameterNames the list of the parameter names (=signature)
   */
  public void signature(List<String> parameterNames) {
    Log.errorIfNull(parameterNames);

    checkArgument(parameterNames.size() == arguments.size(),
        "0xA5298 Template '" + templatename + "': Signature size (#" + parameterNames.size() +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");

    // bind values (arguments) to names (parameters/signature)
    // and inject into template
    for (int i = 0; i < parameterNames.size(); i++) {
      data.put(parameterNames.get(i), arguments.get(i));
    }

  }

  /**
   * Delegates to {@link #signature(List)}.
   */
  public void signature(String... parameterName) {
    signature(Lists.newArrayList(parameterName));
  }

  List<Object> getArguments() {
    return arguments;
  }


  /**
   * Includes a template without an explicit ast. (ast is current ast node)
   *
   * @param templateName
   * @return
   */
  public StringBuilder include(String templateName) {
    return include(newArrayList(templateName), newArrayList(getAST()));
  }

  /**
   * Includes a template without an explicit ast. (ast is current ast node)
   * Execute each of the templates on the ASTNode. Concatenate the results
   * together in one big String and include that into the currently processed
   * output. We iterate on the templates. Template filename may be qualified
   * (using "."). When it is not qualified, the filename is taken from the
   * current package (same as the calling template).
   *
   * @param templateNames list of filenames, qualified or not
   * @return produced output
   */
  public StringBuilder include(List<String> templateNames) {
    return include(templateNames, newArrayList(getAST()));
  }

  /**
   * Execute each of the templates on the ASTNode. Concatenate the results
   * together in one big String and include that into the currently processed
   * output. We iterate on the templates. Template filename may be qualified
   * (using "."). When it is not qualified, the filename is taken from the
   * current package (same as the calling template).
   *
   * @param templateNames list of filenames, qualified or not
   * @param ast ast-node the template is operating on
   * @return produced output
   */
  public StringBuilder include(List<String> templateNames, ASTNode ast) {
    return include(templateNames, newArrayList(ast));
  }

  /**
   * Execute the single template on each ASTNode of the list. Concatenate the
   * results together in one big String and include that into the currently
   * processed output. We iterate on the ASTNodes. Template filename may be
   * qualified (using "."). When it is not qualified, the filename is taken from
   * the current package (same as the calling template).
   *
   * @param templateName filename, qualified or not
   * @param astlist where we execute the template on in an iteration
   * @return produced output
   */
  public StringBuilder include(String templateName, List<ASTNode> astlist) {
    return include(newArrayList(templateName), astlist);
  }

  /**
   * Include the template into the currently processed output. The template is
   * executed on ASTNode ast. Remark: even though the name suggests to run
   * several templates, this is the version that executes on a single template
   * given as string. We only handle one template on one node. Template filename
   * may be qualified (using "."). When it is not qualified, the filename is
   * taken from the current package (same as the calling template).
   *
   * @param templateName name of the template to be executed, qualified or not
   * @param ast ast-node the template is operating on
   * @return output for the file (may be part of a file only)
   */
  public StringBuilder include(String templateName, ASTNode ast) {
    return include(newArrayList(templateName), newArrayList(ast));
  }

  /**
   * Include the template into the currently processed output. Remark: even
   * though the name suggests to run several templates, this is the version that
   * executes on a single template given as string. We only handle one template
   * on one node. Template filename may be qualified (using "."). When it is not
   * qualified, the filename is taken from the current package (same as the
   * calling template).
   *
   * @param templateName name of the template to be executed, qualified or not
   * @param templateArguments additional data that is passed to the called
   * template
   * @return output for the file (may be part of a file only)
   */
  public StringBuilder includeArgs(String templateName, ASTNode node, List<Object> templateArguments) {
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = config.getGlex().getTemplateForwardings(templateName, node);
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(this, node, templateArguments));
    }

    return ret;
  }

  /**
   * Include the template into the currently processed output. Remark: even
   * though the name suggests to run several templates, this is the version that
   * executes on a single template given as string. We only handle one template
   * on one node. Template filename may be qualified (using "."). When it is not
   * qualified, the filename is taken from the current package (same as the
   * calling template).
   *
   * @param templateName name of the template to be executed, qualified or not
   * @param templateArguments additional data that is passed to the called
   * template
   * @return output for the file (may be part of a file only)
   */
  public StringBuilder includeArgs(String templateName, List<Object> templateArguments) {
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = config.getGlex().getTemplateForwardings(templateName, getAST());
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(this, templateArguments));
    }

    return ret;
  }

  /**
   * Delegates to {@link #includeArgs(String, List)}
   */
  public StringBuilder includeArgs(String templateName, String... templateArgument) {
    return includeArgs(templateName, Lists.newArrayList(templateArgument));
  }

  /**
   * This is an unusual method. It does nothing and should not be used. It will
   * be called from freemarker automatically, when one of the arguments (either
   * the filename or the ast) is null. In this case an error is issued.
   *
   * @param empty1
   * @param empty2
   */
  public void include(Object empty1, Object empty2) {
    // Extension points in templates (holes) can be unused. In this case
    // includeTemplates
    // is called with null which is no error and can therefore be ignored.

    String msg;
    if (empty1 == null && empty2 == null) {
      msg = "0xA2936 Template name and ast node are null in " + templatename;
    }
    else if (empty1 == null) {
      msg = "0xA2937 Template name is null in " + templatename + " using " + empty2.getClass();
    }
    else {
      msg = "0xA2938 Ast node is null in " + templatename + " calling template " + empty1;
    }
    Log.error(msg + " ## You made an illegal call of includeTemplate. As the error says at least "
        + "one argument was null. That shouldn't happen. ##");
  }

  /**
   * Execute the template and put the result into a file. The template is
   * executed on ASTNode ast. File qualifiedFileName + default file extension is
   * opened, written and closed again!
   *
   * @param templateName full qualified filename
   * @param ast where we execute the template on
   * @return none (= empty string within Freemarker)
   */
  public void write(String templateName, String qualifiedFileName, ASTNode ast) {
    writeArgs(templateName, qualifiedFileName, config.getDefaultFileExtension(), ast,
        new ArrayList<>());
  }

  public void write(String templateName, String qualifiedFileName, String fileExtension, ASTNode ast) {
    writeArgs(templateName, qualifiedFileName, fileExtension, ast, new ArrayList<>());
  }

  public void write(final String templateName, final Path filePath, final ASTNode ast) {
    writeArgs(templateName, filePath, ast, new ArrayList<>());
  }

  /**
   * Execute the template and put the result into a file. The template is
   * executed on ASTNode ast. And the file qualifiedFileName + fileExtension is
   * opened, written and closed again! If fileExtension == null, then use "".
   * fileExtension may start with ".", otherwise one is added. So ".java" and
   * "java" have the same effect. Template filename may be qualified (using
   * "."). When it is not qualified, the filename is taken from the current
   * package (same as the calling template).
   *
   * @param templateName full qualified filename
   * @param ast where we execute the template on
   * @return none (= empty string within Freemarker)
   */
  public void writeArgs(final String templateName, final String qualifiedFileName,
                        final String fileExtension, final ASTNode ast, final List<Object> templateArguments) {
    String fileExtensionWithDot = Strings.nullToEmpty(fileExtension);
    if (fileExtensionWithDot.startsWith(".")) {
      fileExtensionWithDot = fileExtensionWithDot.substring(1);
    }

    final String filePathStr = Names.getPathFromPackage(qualifiedFileName) + "." + fileExtensionWithDot;
    final Path filePath = Paths.get(filePathStr);

    writeArgs(templateName, filePath, ast, templateArguments);
  }

  /**
   * Processes the template <code>templateName</code> with the <code>ast</code>
   * and the given <code>templateArguments</code> and writes the content into
   * the <code>filePath</code>. Note: Unless not absolute, the
   * <code>filePath</code> is relative to the configured target directory (i.e.,
   * {@link TemplateControllerConfiguration#getTargetDir()})
   *
   * @param templateName the template to be processes
   * @param filePath the file path in which the content is to be written
   * @param ast the ast
   * @param templateArguments additional template arguments (if needed).
   */
  public void writeArgs(final String templateName, final Path filePath, final ASTNode ast,
                        final List<Object> templateArguments) {
    final String qualifiedTemplateName = completeQualifiedName(templateName);

    StringBuilder content = new StringBuilder();
    // Replacement added: no also writeArgs replaces its Templates
    List<HookPoint> templateForwardings =
    		config.getGlex().getTemplateForwardings(templateName, ast);
    for (HookPoint tn : templateForwardings) {
      content.append(tn.processValue(this, ast, templateArguments));
    }

    if (content.length()==0) {
      Log.warn("0xA4057 Template " + qualifiedTemplateName + " produced no content for.");
    }

    // add trace to source-model:
    if (config.isTracing() && config.getModelName().isPresent()) {
      content.insert(0, config.getCommentStart() + " generated from model " + config.getModelName().get() + " " 
          + config.getCommentEnd() + "\n");
    }

    Path completeFilePath;
    if (filePath.isAbsolute()) {
      completeFilePath = filePath;
    }
    else {
      completeFilePath = Paths.get(config.getOutputDirectory().getAbsolutePath(), filePath.toString());
    }

    Reporting.reportFileCreation(qualifiedTemplateName, filePath, ast);

    config.getFileHandler().storeInFile(completeFilePath, content.toString());

    Log.debug(completeFilePath + " written successfully!", this.getClass().getName());

    Reporting.reportFileFinalization(qualifiedTemplateName, filePath, ast);
  }
  
  /**
   * Include a template with additional data: We only handle one template on one
   * node. This method allows to parameterize templates. Template filename may
   * be qualified (using "."). When it is not qualified, the filename is taken
   * from the current package. The resulting output may be stored or included in
   * another generation process.
   *
   * @param templateName name of the template to be executed, qualified or not
   * @param astNode ast-node the template is operating on
   * @param passedArguments additional data that is passed to the included
   * template
   * @return output for the file (may be part of a file only)
   */
  protected String processTemplate(String templateName, ASTNode astNode,
                                   List<Object> passedArguments) {
    String qualifiedTemplateName = completeQualifiedName(templateName);

    ASTNode ast = astNode;

    // If no ast is passed, get current ast, if exists
    if (ast == null) {
      ast = getAST();
    }

    Reporting.reportTemplateStart(qualifiedTemplateName, ast);

    StringBuilder ret = runInEngine(passedArguments, qualifiedTemplateName, ast);

    Reporting.reportTemplateEnd(qualifiedTemplateName, ast);

    return ret.toString();
  }

  StringBuilder runInEngine(List<Object> passedArguments, String templateName, ASTNode ast) {
    initAliases();

    // Load template
    // TODO:
    // It's pretty inefficient each time to create a new instance of the
    // FreeMarker configuration by FreeMarkerTemplateEngine.loadTemplate(...)
    // method
    // Template t = FreeMarkerTemplateEngine.loadTemplate(templatename,
    // classLoader, MontiCoreTemplateExceptionHandler.THROW_ERROR,
    // externalTemplatePath);
    Template template = config.getFreeMarkerTemplateEngine().loadTemplate(templateName);

    // add static functions to template
    for (Macro macro : config.getAliases()) {
      template.addMacro(macro);
    }

    return runInEngine(passedArguments, template, ast);
  }

  /**
   * Init template aliases if not already done. This happens once (for all main
   * templates)
   */
  @SuppressWarnings("rawtypes")
  protected void initAliases() {
    if (config.getAliases().isEmpty()) {
      Template aliasesTemplate = config.getFreeMarkerTemplateEngine().loadTemplate(
          GeneratorSetup.ALIASES_TEMPLATE);

      Set macros = aliasesTemplate.getMacros().entrySet();
      for (Object o : macros) {
        Entry e = (Entry) o;
        Macro macro = (Macro) e.getValue();
        config.addAlias(macro);
      }
    }
  }

  StringBuilder runInEngine(List<Object> passedArguments, Template template, ASTNode ast) {
    StringBuilder ret = new StringBuilder();

    if (template != null) {
      // Initialize standard-data for template

      // get ast
      if (ast == null) {
        ast = getAST();
      }

      TemplateController tc = config.getNewTemplateController(template.getName());

      SimpleHash d = SimpleHashFactory.getInstance().createSimpleHash();
      d.put(AST, ast);
      d.put(TC, tc);
      d.put(GLEX, config.getGlex());

      // add all global data to be accessible in the template
      try {
        d.putAll(config.getGlex().getGlobalData().toMap());
      }
      catch (TemplateModelException e) {
        String usage = this.templatename != null ? " (" + this.templatename + ")" : "";
        Log.error("0xA0128 Globally defined data could not be passed to the called template "
            + usage + ". ## This is an internal"
            + "error that should not happen. Try to remove all global data. ##");
      }

      tc.data = d;
      tc.arguments = newArrayList(passedArguments);

      // Run template with data to create output
      config.getFreeMarkerTemplateEngine().run(ret, d, template);
    }
    else {
      // no template
      String usage = this.templatename != null ? " (used in " + this.templatename + ")" : "";
      Log.error("0xA0127 Missing template "
          + usage
          + " ## You have tried to use a template that "
          + "doesn't exist. It may be in another package? The name printed is the qualified version, "
          + "but you may have used the unqualified name. ##");
    }

    // add trace to template:
    if (ret.length() != 0 && config.isTracing()) {
      ret.insert(0, config.getCommentStart() + " generated by template " + template.getName()
          + config.getCommentEnd() + "\n");
    }

    return ret;
  }
  
  /**
   * checks if the name seems to be already qualified: if not, adds the current
   * package (of the template it operates on)
   */
  private String completeQualifiedName(String name) {
    Log.errorIfNull(!isNullOrEmpty(name));

    if (name.contains(".")) {
      return name;
    }

    return Names.getQualifier(templatename) + "." + name;
  }

  ASTNode getAST() {
    ASTNode ast = null;

    Object o = getValueFromData(AST);
    if ((o != null) && (o instanceof ASTNode)) {
      ast = (ASTNode) o;
    }

    return ast;
  }

  private Object getValueFromData(String name) {
    try {
      return BeansWrapper.getDefaultInstance().unwrap(data.get(name));
    }
    catch (TemplateModelException e) {
      Log.error("0xA0124 Could not find value for \"" + name + "\" in template \"" + templatename
          + "\"", e);
    }
    return null;
  }

  /**
   * Can be used to instantiate any Java-class with a default constructor (no
   * args). The passed className is either in the same package as the calling
   * template, or it needs to be qualified (dot-separated).
   *
   * @param className name of the class to be instantiated
   * @return an object of the given className
   */
  public Object instantiate(String className) {
    Reporting.reportInstantiate(className, new ArrayList<>());
    return ObjectFactory.createObject(completeQualifiedName(className));
  }

  /**
   * Checks if a file with the default extension exists in the HWV directory.
   * 
   * @param filename Path of the file to search for
   * @return true if file exists
   */
  public boolean existsHandwrittenFile(String filename) {
    checkArgument(!isNullOrEmpty(filename));
    
    Path path = Paths.get(filename);
    if (FilenameUtils.getExtension(filename).isEmpty()) {
      path = Paths.get(filename + "." + config.getDefaultFileExtension());
    }
    
    Log.debug("Checking existence of handwritten code " + FilenameUtils.getName(filename)
        + " by searching for "
        + path.toString(), TemplateController.class.getName());
    
    boolean result = config.getHandcodedPath().exists(path);
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(config.getHandcodedPath().getResolvedPath(path).get(),
          path);
    }
    
    return result;
  }
  
  /**
   * Checks if a handwritten class with the given qualifiedName (dot-separated)
   * exists on the target path.
   * 
   * @param qualifiedName name of the class to search for
   * @param extension extension of file to search for
   * @return true if a handwritten class with the qualifiedName exists
   */
  public boolean existsHandwrittenClass(String qualifiedName,
      String extension) {
    checkArgument(!isNullOrEmpty(qualifiedName));
    
    if (Strings.nullToEmpty(extension).isEmpty()) {
      extension = config.getDefaultFileExtension();
    }
    
    Path handwrittenFile = Paths.get(Names
        .getPathFromPackage(qualifiedName)
        + "." + extension);
    Log.debug("Checking existence of handwritten class " + qualifiedName
        + " by searching for "
        + handwrittenFile.toString(), TemplateController.class.getName());
    
    boolean result = config.getHandcodedPath().exists(handwrittenFile);
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(
          config.getHandcodedPath().getResolvedPath(handwrittenFile).get(),
          handwrittenFile);
    }
    
    return result;
  }
  
  /**
   * Checks if a handwritten class with the given qualifiedName (dot-separated)
   * exists on the target path.
   * 
   * @param qualifiedName name of the class to search for
   * @return true if a handwritten class with the qualifiedName exists
   */
  public boolean existsHandwrittenClass(String qualifiedName) {
    return existsHandwrittenClass(qualifiedName, "");
  }

  /**
   * Can be used to instantiate any Java-class with constructor of a signature
   * fitting to the params. The passed className is either in the same package
   * as the calling template, or it needs to be qualified (dot-separated).
   *
   * @param className name of the class to be instantiated
   * @param params parameters provided for the constructor-call
   * @return an object of the given className
   */
  public Object instantiate(String className, List<Object> params) {
    Reporting.reportInstantiate(className, params);
    return ObjectFactory.createObject(completeQualifiedName(className), params);
  }
  
  /**
   * @see de.se_rwth.commons.logging.Log#trace(String, String)
   */
  public void trace(String msg, String logName) {
    Log.trace(msg, logName);
  }

  /**
   * @see de.se_rwth.commons.logging.Log#debug(String, String)
   */
  public void debug(String msg, String logName) {
    Log.debug(msg, logName);
  }

  /**
   * @see de.se_rwth.commons.logging.Log#info(String, String)
   */
  public void info(String msg, String logName) {
    Log.info(msg, logName);
  }

  /**
   * @see de.se_rwth.commons.logging.Log#warn(String)
   */
  public void warn(String msg) {
    Log.warn(msg);
  }

  /**
   * @see de.se_rwth.commons.logging.Log#error(String)
   */
  public void error(String msg) {
    Log.error(msg);
  }

}
