/*
 * ******************************************************************************
 * MontiCore Language Workbench
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


package de.monticore.symboltable.names;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Provides default implementation for {@link QualifiedNamesCalculator}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class CommonQualifiedNamesCalculator implements QualifiedNamesCalculator {

  @Override
  public Set<String> calculateQualifiedNames(String name, String packageName, List<ImportStatement> imports) {
    final Set<String> potentialSymbolNames = new LinkedHashSet<>();

    // the simple name (in default package)
    potentialSymbolNames.add(name);

    // if name is already qualified, no further (potential) names exist.
    if (Names.getQualifier(name).isEmpty()) {
      // maybe the model belongs to the same package
      if (!packageName.isEmpty()) {
        potentialSymbolNames.add(packageName + "." + name);
      }

      for (ImportStatement importStatement : imports) {
        if (importStatement.isStar()) {
          potentialSymbolNames.add(importStatement.getStatement() + "." + name);
        }
        else if (Names.getSimpleName(importStatement.getStatement()).equals(name)) {
          potentialSymbolNames.add(importStatement.getStatement());
        }
      }
    }
    Log.trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
        CommonQualifiedNamesCalculator.class.getSimpleName());

    return potentialSymbolNames;
  }
}
