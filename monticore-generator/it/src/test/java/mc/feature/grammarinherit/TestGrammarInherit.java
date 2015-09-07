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

package mc.feature.grammarinherit;

import static org.junit.Assert.assertEquals;
import groovyjarjarantlr.TokenStreamException;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.grammarinherit.sub.featuredslgrammarinherit._parser.FeatureDSLgrammarinheritParserFactory;
import mc.feature.grammarinherit.sub.featuredslgrammarinherit._parser.FileMCParser;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

public class TestGrammarInherit extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException, RecognitionException, TokenStreamException {
    
    StringReader s = new StringReader("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    FileMCParser p = FeatureDSLgrammarinheritParserFactory.createFileMCParser();
    p.parse(s);
    
    assertEquals(false, p.hasErrors());
    
  }
  
}
