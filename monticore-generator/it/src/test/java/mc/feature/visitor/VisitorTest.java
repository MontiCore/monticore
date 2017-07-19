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

package mc.feature.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.sub._ast.ASTB;
import mc.feature.visitor.sub._ast.ASTE;
import mc.feature.visitor.sub._parser.SubParser;
import mc.feature.visitor.sub._visitor.SubParentAwareVisitor;
import mc.feature.visitor.sub._visitor.SubVisitor;
import mc.feature.visitor.sup._ast.ASTA;
import mc.feature.visitor.sup._visitor.SupVisitor;

public class VisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConcreteVisitor() throws IOException {
    // Create AST
    SubParser p = new SubParser();
    Optional<ASTA> node = p.parseA(new StringReader("test1 test2"));
    assertFalse(p.hasErrors());
    assertTrue(node.isPresent());
    
    // Running Visitor
    SubConcreteVisitor v = new SubConcreteVisitor();
    
    v.handle(node.get());
    assertTrue(v.hasVisited());
    
    SupVisitor vSup = new SupVisitor() {};
    long errorCount = Log.getErrorCount();
    // expected error, because super visitor may not run on sub language
    vSup.handle(node.get());
    assertEquals(errorCount + 1, Log.getErrorCount());
  }
  
  @Test
  public void testParentAware() throws IOException {
    // Create AST
    SubParser p = new SubParser();
    // in b ist parent a
    Optional<ASTA> node = p.parseA(new StringReader("test1 test2"));
    assertFalse(p.hasErrors());
    assertTrue(node.isPresent());
    
    // Running Visitor
    final StringBuilder run = new StringBuilder();
    SubVisitor v = new SubParentAwareVisitor() {
      @Override
      public void visit(ASTA node) {
        run.append("A");
        if (getParent().isPresent()) {
          fail("The parent must not be present, but was set to " + getParent().get());
        }
      }
      
      @Override
      public void visit(ASTE node) {
        run.append("E");
        // TODO parentaware visitor does not support super grammars, yet, but
        // nobody uses it anyway.. its only a demonstrator
        // if (!getParent().isPresent()) {
        // fail("The parent must be present.");
        // }
        // if (!(getParent().get() instanceof ASTA)) {
        // fail("The parent must be present as A, but was " +
        // getParent().get());
        // }
      }
      
      @Override
      public void visit(ASTB node) {
        run.append("B");
        if (!getParent().isPresent()) {
          System.out.println(run);
          fail("The parent must be present.");
        }
        if (!(getParent().get() instanceof ASTE)) {
          fail("The parent must be present as E, but was " + getParent().get());
        }
      }
    };
    
    v.handle(node.get());
    assertEquals("AEB", run.toString());
  }
}
