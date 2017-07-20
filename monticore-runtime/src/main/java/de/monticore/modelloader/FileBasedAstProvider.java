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

package de.monticore.modelloader;

import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

/**
 * Builds ASTs by going straight to the hard disk and reading in the model.
 *
 * @author Sebastian Oberhoff, Pedram Mir Seyed Nazari
 */
public final class FileBasedAstProvider<T extends ASTNode> implements AstProvider {

  private final ModelingLanguage modelingLanguage;

  public FileBasedAstProvider(ModelingLanguage modelingLanguage) {
    this.modelingLanguage = modelingLanguage;
  }

  @Override public T getRootNode(ModelCoordinate modelCoordinate) {
    Optional<T> ast = Optional.empty();

    try {
      Log.debug("Start parsing model " + modelCoordinate + ".",
          ModelingLanguageModelLoader.class.getSimpleName());

      Reader reader = new InputStreamReader(modelCoordinate.getLocation().openStream());
      ast = (Optional<T>) modelingLanguage.getParser().parse(reader);

      if (ast.isPresent()) {
        Log.debug("Parsed model " + modelCoordinate + " successfully.",
            ModelingLanguageModelLoader.class.getSimpleName());
      }
      else {
        Log.error("0xA1025 Could not parse model '" + modelCoordinate + "' of the grammar "
            + "language " + modelingLanguage.getName() + ". There seem to be syntactical errors.");
      }
    }
    catch (IOException e) {
      Log.error("0xA1026 I/O problem while parsing model '" + modelCoordinate + "' of the grammar "
          + "language " + modelingLanguage.getName(), e);
    }

    return ast.get();
  }
}
