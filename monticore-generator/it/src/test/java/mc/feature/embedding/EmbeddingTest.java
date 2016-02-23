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

package mc.feature.embedding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.embedding.outer.featureembedded._ast.ASTExt;
import mc.feature.embedding.outer.featureembedded._parser.FeatureembeddedParser;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter;
import mc.feature.embedding.outer.featureouterdsl._ast.ASTOuter3;

public class EmbeddingTest extends GeneratorIntegrationsTest {
  
  private ASTOuter createAST(String filename, Reader r) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = new FeatureembeddedParser();
    
    // Parse the input expression
    Optional<ASTOuter> ast = parser.parseOuter(r);
    
    return ast.get();
  }
  
  private ASTOuter3 createAST3(String filename, Reader r) throws IOException {
    
    // Create overall parser
    FeatureembeddedParser parser = new FeatureembeddedParser();
    
    // Parse the input expression
    Optional<ASTOuter3> ast = parser.parseOuter3(r);
    
    return ast.get();
  }
  
  @Test
  public void testEmbedding() throws IOException {
    
    StringReader s = new StringReader("out { test }");
    
    ASTOuter ast = createAST("hihi", s);
    
    assertEquals("test", ((ASTExt) ast.getInner()).getInner().getName());
    
  }

  @Test
  public void testEmbedding3() throws IOException {
    
    StringReader s = new StringReader("out  {test}  ");
    
    createAST3("Embedded - optional taken", s);
    
  }
  
  @Test
  public void testEmbedding4() throws IOException {
    
    StringReader s = new StringReader("out");
    
    createAST3("Embedded - optional not taken", s);
    
  }
  
}
