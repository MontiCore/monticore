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

package mc.ast;

import static org.junit.Assert.assertTrue;
import mc.GeneratorIntegrationsTest;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.io.paths.IterablePath;


/**
 * Test for generation of AST classes if there a handwritten class exists on the target path 
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class HandwrittenASTTest extends GeneratorIntegrationsTest {
  
  @Test
  public void createASTIfHWCExists() {
    IterablePath targetPath = IterablePath.empty(); // TODO GV
    
    // MontiCore generator should generate ast file "ASTXTOP" instead of "ASTX" if there was a handwritten class with the given name 
 //   assertTrue(TransformationHelper.existsHandwrittenClass(targetPath, "mc.feature._ast.ASTClassRule"));
    // TODO GV
//    assertFalse(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTClassRule"));
//    assertTrue(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTClassRuleTOP"));
//    assertTrue(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTClassRuleList"));
    
//    assertTrue(TransformationHelper.existsHandwrittenClass(targetPath, "mc.feature._ast.ASTInterfaceRule"));
    // TODO GV
//    assertFalse(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTInterfaceRule"));
//    assertTrue(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTInterfaceRuleTOP"));
//    assertTrue(helper.existsGeneratedFile(targetPath, "mc.feature._ast.ASTInterfaceRuleList"));
    
  }
}
