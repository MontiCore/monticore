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

package de.monticore.codegen.cd2java.ast_emf;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public static String getQualifiedENodeName() {
    return "de.monticore.emf._ast.ASTEnode";
  }
  
  public static String getEmfRuntimePackage() {
    return "de.monticore.emf._ast";
  }
  
  public static String getSuperClass(ASTCDClass clazz) {
    if (!clazz.getSuperclass().isPresent()) {
      return "de.monticore.emf.ASTEObjectImplNode";
    }
    return clazz.printSuperClass();
  }
}
