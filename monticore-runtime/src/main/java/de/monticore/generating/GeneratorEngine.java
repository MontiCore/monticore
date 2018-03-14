/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;

import de.monticore.ast.ASTNode;
import de.monticore.ast.ASTNodeNull;
import de.monticore.generating.templateengine.TemplateController;
import de.se_rwth.commons.logging.Log;

/**
 * Represents the whole generator engine component.
 * Clients usually need only this class when generating.
 *
 */
public class GeneratorEngine {

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
    tc.writeArgs(templateName, filePath, new ASTNodeNull(), Arrays.asList(templateArguments));
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
      Log.error("0xA4060 Template " + templateName + " cannot write the content");   
    }
  }
  
  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code> and writes the content into the <code>filePath</code>. Note:
   * Unless not absolute, the <code>filePath</code> is relative to the configured output directory
   * specified in the {@link de.monticore.generating.GeneratorSetup}.
   *
   * @param templateName the template to be processes
   * @param filePath the writer in which the content is to be written
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

}
