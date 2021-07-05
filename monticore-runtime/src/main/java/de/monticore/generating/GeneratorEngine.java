/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Represents the whole generator engine component.
 * Clients usually need only this class when generating.
 *
 */
public class GeneratorEngine {
  
  public static final String DEFAULT_FILE_EXTENSION = ".java";
  
  public static final String TOP_NAME_EXTENSION = "TOP";

  /**
   * Contains all configuration data
   */
  private GeneratorSetup setup;

  public GeneratorEngine(GeneratorSetup gs) {
    Log.errorIfNull(gs);
    this.setup = gs;
  }

  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param filePath the file path in which the content is to be written
   * @param node the ast node
   * @param templateArguments additional template arguments (if needed).
   */
  public void generate(String templateName, Path filePath,
                       ASTNode node, Object... templateArguments) {
    Log.errorIfNull(node);
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(filePath);

    TemplateController tc = setup.getNewTemplateController(templateName);
    tc.writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
  }
  
  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param filePath the file path in which the content is to be written
   * @param templateArguments additional template arguments (if needed).
   */
  public void generateNoA(String templateName, Path filePath, Object... templateArguments) {
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(filePath);

    TemplateController tc = setup.getNewTemplateController(templateName);
    ASTCNode dummyAST = new ASTCNode(){

      @Override public IScope getEnclosingScope() {
        return null;
      }

      @Override public ASTNode deepClone() {
        return this;
      }
    };

    tc.writeArgs(templateName, filePath, dummyAST, Arrays.asList(templateArguments));
  }

  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param writer the writer in which the content is to be written
   * @param node the ast node
   * @param templateArguments additional template arguments (if needed).
   */
  public void generate(String templateName, Writer writer,
                ASTNode node, Object... templateArguments) {
    Log.errorIfNull(node);
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(writer);

    TemplateController tc = setup.getNewTemplateController(templateName);
    StringBuilder sb = tc.includeArgs(templateName, node, Arrays.asList(templateArguments));
    try {
      writer.write(sb.toString());
    }
    catch (IOException e) {
      Log.error("0xA4110 Template " + templateName + " cannot write the content");
    }
  }
  
  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param writer the writer in which the content is to be written
   * @param templateArguments additional template arguments (if needed).
   */
  public void generateNoA(String templateName, Writer writer, Object... templateArguments) {
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(writer);

    TemplateController tc = setup.getNewTemplateController(templateName);
    StringBuilder sb = tc.includeArgs(templateName, Arrays.asList(templateArguments));
    try {
      writer.write(sb.toString());
    }
    catch (IOException e) {
      Log.error("0xA4089 Template " + templateName + " cannot write the content");
    }
  }


  /**
   * Processes the template <code>templateName</code> with the given <code>templateArguments</code>
   * and returns the content as StringBuilder.
   *
   * @param templateName the template to be processes
   * @param node the ast node
   * @param templateArguments additional template arguments (if needed).
   */
  public StringBuilder generate(String templateName, ASTNode node, Object... templateArguments) {
    checkArgument(!isNullOrEmpty(templateName));
    TemplateController tc = setup.getNewTemplateController(templateName);
    return tc.includeArgs(templateName, node, Arrays.asList(templateArguments));
  }
  
  /**
   * Processes the template <code>templateName</code> with the given <code>templateArguments</code>
   * and returns the content as StringBuilder.
   *
   * @param templateName the template to be processes
   * @param templateArguments additional template arguments (if needed).
   */
  public StringBuilder generateNoA(String templateName, Object... templateArguments) {
    checkArgument(!isNullOrEmpty(templateName));
    TemplateController tc = setup.getNewTemplateController(templateName);
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }
  
  /**
   * Check whewther an handwritten version of that class already exits and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenClass(MCPath targetPath, String qualifiedName) {
    String hwFile = Names.getPathFromPackage(qualifiedName)+ DEFAULT_FILE_EXTENSION;
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path,
        Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return result;
  }

}
