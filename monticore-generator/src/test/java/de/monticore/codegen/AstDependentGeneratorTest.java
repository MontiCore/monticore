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

package de.monticore.codegen;

import java.util.List;

import com.google.common.collect.Lists;

import de.monticore.MontiCoreConfiguration;
import de.monticore.codegen.cd2java.ast.AstGeneratorTest;

/**
 * The super class for all test classes that generate and compile code which
 * depends on the generated ast code
 *
 * @author Galina Volkova
 */
public abstract class AstDependentGeneratorTest extends GeneratorTest {
  
  protected AstGeneratorTest astTest;
  
  protected AstDependentGeneratorTest() {
    astTest = new AstGeneratorTest();
    astTest.setDoCompile(false);
  }
  
  protected String[] getCLIArguments(String grammar) {
    List<String> args = Lists.newArrayList(getGeneratorArguments());
    args.add(getConfigProperty(MontiCoreConfiguration.Options.GRAMMARS.toString()));
    args.add(grammar);
    return args.toArray(new String[0]);
  }

  @Override
  public void testCorrect(String model) {
    testCorrect(model, getPathToGeneratedCode(model));
  }
}
