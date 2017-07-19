/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.freemarker.SimpleHashFactory;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import freemarker.core.Macro;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

// TODO: describe methods better for JavaDoc

/**
 * Provides methods for manipulating the content of templates, mainly for
 * calling and including of templates.
 *
 * @author (last commit) $Author$
 */
// TODO PN check if all method docs are up to date.
public class TemplateController {

  private final TemplateControllerConfiguration config;

  private final GlobalExtensionManagement glex;

  private final FileReaderWriter fileHandler;

  private final FreeMarkerTemplateEngine freeMarkerTemplateEngine;

  private ITemplateControllerFactory tcFactory;

  private final String currPackage;

  private String templatename;

  private boolean isIncludeRunning = false;

  boolean signatureInitialized = false;

  private List<String> signature = newArrayList();

  private List<Object> arguments = newArrayList();

  public static final String DEFAULT_FILE_EXTENSION = "java";

  /**
   * A list of all freemarker functions that serve as aliases for Java methods,
   * e.g. 'include' as alias for 'tc.include'
   */
  private List<Macro> aliases;

  private SimpleHash data = SimpleHashFactory.getInstance().createSimpleHash();

  protected TemplateController(TemplateControllerConfiguration tcConfig, String templatename) {
    this.config = tcConfig;
    this.glex = tcConfig.getGlEx();
    this.fileHandler = tcConfig.getFileHandler();
    this.freeMarkerTemplateEngine = tcConfig.getFreeMarkerTemplateEngine();

    this.templatename = templatename;
    this.currPackage = Names.getQualifier(templatename);

    this.tcFactory = tcConfig.getTemplateControllerFactory();

    // TODO PN blocker
    // GenLogger.updateTemplateOperator(this);
  }

  public String getTemplatename() {
    return templatename;
  }

  protected void setTemplatename(String templatename) {
    this.templatename = templatename;
  }

  /**
   * @return isIncludeRunning
   */
  protected boolean isIncludeRunning() {
    return this.isIncludeRunning;
  }

  /**
   * @param isIncludeRunning the isIncludeRunning to set
   */
  protected void setIncludeRunning(boolean isIncludeRunning) {
    this.isIncludeRunning = isIncludeRunning;
  }

  /**
   * Execute each of the templates on each ASTNode of the list. Concatenate the
   * results together in one big String and include that into the currently
   * processed output. We iterate on the templates and ASTNodes. In case order
   * is important: The iteration goes like this for ( templates ) { for (
   * ASTNodes ) {...} } Template filename may be qualified (using "."). When it
   * is not qualified, the filename is taken from the current package (same as
   * the calling template).
   *
   * @param templatenames list of filenames, qualified or not
   * @param astlist where we execute the template on in an iteration
   * @return produced output
   */
  public String include(List<String> templatenames, List<ASTNode> astlist) {
    setIncludeRunning(true);
    StringBuilder ret = new StringBuilder();
    for (String template : templatenames) {
      for (ASTNode ast : astlist) {
        List<HookPoint> templateForwardings = glex.getTemplateForwardings(template, ast);
        for (HookPoint templateHp : templateForwardings) {
          ret.append(templateHp.processValue(this, ast));
        }
      }
    }

    setIncludeRunning(false);
    return ret.toString();
  }

  /**
   * Execute a template without resolving the forwardings. This method is
   * package default and should only be used by the template hook point
   *
   * @param templateName the name of the template
   * @param ast the ast node
   * @return produced output
   */
  String includeWithoutForwarding(String templateName, ASTNode ast) {
    setIncludeRunning(true);
    StringBuilder ret = new StringBuilder();
    ret.append(processTemplate(templateName, ast, new ArrayList<>()));
    setIncludeRunning(false);
    return ret.toString();
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
    checkArgument(!signatureInitialized,
        "0xA5297 Template '" + templatename + "': tried to invoke signature() twice");

    Log.errorIfNull(parameterNames);

    checkArgument(parameterNames.size() == arguments.size(),
        "0xA5298 Template '" + templatename + "': Signature size (#" + parameterNames.size() +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");

    this.signature = newArrayList(parameterNames);

    // bind values (arguments) to names (parameters/signature)
    // and inject into template
    for (int i = 0; i < parameterNames.size(); i++) {
      data.put(parameterNames.get(i), arguments.get(i));
    }

    signatureInitialized = true;
  }

  /**
   * Delegates to {@link #signature(List)}.
   */
  public void signature(String... parameterName) {
    signature(Lists.newArrayList(parameterName));
  }

  List<String> getSignature() {
    return ImmutableList.copyOf(signature);
  }

  List<Object> getArguments() {
    return ImmutableList.copyOf(arguments);
  }

  boolean isSignatureInitialized() {
    return signatureInitialized;
  }

  /**
   * Includes a template without an explicit ast. (ast is current ast node)
   *
   * @param templateName
   * @return
   */
  public String include(String templateName) {
    return include(newArrayList(templateName), newArrayList(getAST()));
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
  public String include(List<String> templateNames, ASTNode ast) {
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
  public String include(String templateName, List<ASTNode> astlist) {
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
  public String include(String templateName, ASTNode ast) {
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
  public String includeArgs(String templateName, List<Object> templateArguments) {
    setIncludeRunning(true);
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = glex.getTemplateForwardings(templateName, getAST());
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(this, templateArguments));
    }

    setIncludeRunning(false);
    return ret.toString();
  }

  /**
   * Processes the template with a list of additional arguments. This method is
   * package default and should only be called by the TemplateHookPoint class.
   *
   * @param templateName the template name
   * @param templateArguments the template arguments
   * @return
   */
  String includeArgsWithoutForwarding(String templateName, List<Object> templateArguments) {
    setIncludeRunning(true);
    StringBuilder ret = new StringBuilder();

    ret.append(processTemplate(templateName, getAST(), templateArguments));

    setIncludeRunning(false);
    return ret.toString();
  }

  /**
   * Delegates to {@link #includeArgs(String, List)}
   */
  public String includeArgs(String templateName, String... templateArgument) {
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
    content.append(processTemplate(qualifiedTemplateName, ast, templateArguments));

    if (Strings.isNullOrEmpty(content.toString())) {
      Log.error("0xA4057 Template " + qualifiedTemplateName + " produced no content for.");
    }

    // add trace to source-model:
    if (config.isTracing() && config.getModelName().isPresent()) {
      content.insert(0, config.getCommentStart() + " generated from model " + config.getModelName().get()
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

    fileHandler.storeInFile(completeFilePath, content.toString());

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
    Template template = freeMarkerTemplateEngine.loadTemplate(templateName);

    // add static functions to template
    for (Macro macro : aliases) {
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
    if (aliases == null) {
      aliases = newArrayList();

      Template aliasesTemplate = freeMarkerTemplateEngine.loadTemplate(
          TemplateControllerConfiguration.ALIASES_TEMPLATE);

      Set macros = aliasesTemplate.getMacros().entrySet();
      for (Object o : macros) {
        Entry e = (Entry) o;
        Macro macro = (Macro) e.getValue();
        aliases.add(macro);
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

      TemplateController tc = createTemplateController(template.getName());
      tc.tcFactory = this.tcFactory;

      SimpleHash d = SimpleHashFactory.getInstance().createSimpleHash();
      d.put(TemplateControllerConstants.AST, ast);
      d.put(TemplateControllerConstants.TC, tc);
      d.put(TemplateControllerConstants.GLEX, glex);
      d.put(TemplateControllerConstants.LOG, config.getLog());

      // add all global data to be accessible in the template
      try {
        d.putAll(glex.getGlobalData().toMap());
      }
      catch (TemplateModelException e) {
        String usage = this.templatename != null ? " (" + this.templatename + ")" : "";
        Log.error("0xA0128 Globally defined data could not be passed to the called template "
            + usage + ". ## This is an internal"
            + "error that should not happen. Try to remove all global data. ##");
      }

      tc.data = d;
      tc.arguments = newArrayList(passedArguments);

      if (aliases != null) {
        tc.setAliases(aliases);
      }

      // Run template with data to create output
      freeMarkerTemplateEngine.run(ret, d, template);
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

  protected TemplateController createTemplateController(String templateName) {
    return tcFactory.create(this.config, templateName);
  }

  /**
   * @param tcFactory the factory that should be used when new template
   * controllers are created.
   */
  public void setTemplateControllerFactory(ITemplateControllerFactory tcFactory) {
    this.tcFactory = tcFactory;
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

    return currPackage + "." + name;
  }

  ASTNode getAST() {
    ASTNode ast = null;

    Object o = getValueFromData(TemplateControllerConstants.AST);
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

  protected void logTemplateCallOrInclude(String templateName, ASTNode ast) {
    if (isIncludeRunning()) {
      Reporting.reportTemplateInclude(templateName, ast);
    }
    else {
      Reporting.reportTemplateWrite(templateName, ast);
    }
  }

  // TODO: can we remove this one?
  public String defineHookPoint(String hookName) {
    return glex.defineHookPoint(this, hookName, getAST());
  }

  // TODO AR <- PN Actually,the plan was to move both instantiate() methods to
  // HpFV.
  // But as the className may be unqualified, the package name is needed.
  // But, HpFV does not now anything about the template or its package

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
   * @param aliases the staticFunctions to set
   */
  public void setAliases(List<Macro> aliases) {
    this.aliases = newArrayList(aliases);
  }

  /**
   * @return aliases
   */
  List<Macro> getAliases() {
    return aliases;
  }

  public boolean existsHWC(String fileName) {
    return existsHWC(fileName, DEFAULT_FILE_EXTENSION);
  }

  public boolean existsHWC(String fileName, String extension) {
    checkArgument(!isNullOrEmpty(fileName));
    checkArgument(!isNullOrEmpty(extension));
    Path filePath = Paths.get(Names.getFileName(Names.getPathFromPackage(fileName), extension));
    return config.getHandcodedPath().exists(filePath);
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

}
