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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Joiner;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;
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

  public GeneratorEngine(GeneratorSetup generatorSetup) {
    Log.errorIfNull(generatorSetup);
    this.setup = generatorSetup;
  }

  /**
   * Processes the template <code>templateName</code> with the given <code>templateArguments</code>
   * and returns the content as String.
   *
   * @param templateName the template to be processes
   * @param templateArguments additional template arguments (if needed).
   */
  public String generate(String templateName,
                         Object... templateArguments)
  {
    checkArgument(!isNullOrEmpty(templateName));
    TemplateController tc = setup.getNewTemplateController(templateName);
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }

// TODO (BR, 12/17): Es fehlt eine Methode
// public String generate(String templateName,
//                ASTNode node,
//                Object... templateArguments)

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
  public void generate(String templateName,
      Path filePath,
      ASTNode node,
      Object... templateArguments)
  {
    Log.errorIfNull(node);
    checkArgument(!isNullOrEmpty(templateName));
    Log.errorIfNull(filePath);

    TemplateController tc = setup.getNewTemplateController(templateName);
    tc.writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
  }

}
