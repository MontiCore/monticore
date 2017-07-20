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

package de.monticore.codegen.cd2java.types;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
/**
 *  on 06.11.2016.
 */
public class TypeResolverGeneratorHelper extends GeneratorHelper {

  public TypeResolverGeneratorHelper(
      ASTCDCompilationUnit topAst,
      GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }

  public String getTypeResolver() {
    return getTypeResolver(getCdName());
  }

  public String getTypeResolver(String cdName) {
    return cdName + "TypeResolver";
  }
}
