/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.generating;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.FileWriter;
import java.io.IOException;
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
  public void generate(String templateName, Path filePath, Object... templateArguments) {
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
// TODO MB: wäre nicht java.io.Writer besser
// Wir wollen ja auch in Strings schreiben können
  public void generate(String templateName, FileWriter writer,
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
// TODO MB: wäre nicht java.io.Writer besser
  public void generate(String templateName, FileWriter writer, Object... templateArguments) {
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
  public StringBuilder generate(String templateName, Object... templateArguments) {
    checkArgument(!isNullOrEmpty(templateName));
    TemplateController tc = setup.getNewTemplateController(templateName);
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }

}
