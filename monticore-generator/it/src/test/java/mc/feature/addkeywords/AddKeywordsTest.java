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

package mc.feature.addkeywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.addkeywords.addkeywords._ast.ASTD;
import mc.feature.addkeywords.addkeywords._ast.ASTE;
import mc.feature.addkeywords.addkeywords._parser.AddKeywordsParser;

public class AddKeywordsTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testB() throws IOException {
    
    helperb("3");
    helperb("keyword");
    helperb("key2");
    
  }
  
  private void helperb(String in) throws IOException {
    AddKeywordsParser b = new AddKeywordsParser();
    b.parseB(new StringReader(in));
        
    assertFalse(b.hasErrors());
  }
  
  @Test
  public void testC() throws IOException {
    
    helperc("15");
    helperc("keyword");
    helperc("key2");
    
  }
  
  private void helperc(String in) throws IOException {
    AddKeywordsParser b = new AddKeywordsParser();
    b.parseC(new StringReader(in));
    assertFalse(b.hasErrors());
  }
  
  @Test
  public void testD() throws IOException {
    
    helperd("1");
    helperd("keyword");
    helperd("key2");
    
    assertEquals(3, helperd("10 keyword 2").getName().size());
    assertEquals(3, helperd("2 2 3").getName().size());
    assertEquals(3, helperd("48 keyword key2").getName().size());
    
  }
  
  private ASTD helperd(String in) throws IOException {
    AddKeywordsParser createSimpleParser = new AddKeywordsParser();
    Optional<ASTD> parse = createSimpleParser.parseD(new StringReader(in));
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
    
    return parse.get();
  }
  
  @Test
  public void testE() throws IOException {
    
    helpere("1");
    helpere("keyword");
    helpere("key2");
    
    assertEquals(3, helpere("10 keyword 2").getINTs().size());
    assertEquals(3, helpere("2 2 3").getINTs().size());
    assertEquals(3, helpere("48 keyword key2").getINTs().size());
    
  }
  
  private ASTE helpere(String in) throws IOException {
    AddKeywordsParser createSimpleParser = new AddKeywordsParser();
    Optional<ASTE> parse = createSimpleParser.parseE(new StringReader(in));
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
    
    return parse.get();
  }
  
}
