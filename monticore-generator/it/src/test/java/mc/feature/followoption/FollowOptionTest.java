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

package mc.feature.followoption;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import mc.GeneratorIntegrationsTest;
import mc.feature.followoption.followoption._parser.FollowOptionParser;

public class FollowOptionTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    //-- extractfile gen/FollowOptionTest.x
    FollowOptionParser simpleAParser = new FollowOptionParser();
    simpleAParser.setParserTarget(ParserExecution.EOF);
    simpleAParser.parseA(new StringReader("test ,"));
    assertEquals(false, simpleAParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
  }
    
  @Test
  public void test2() throws IOException {    
    //-- extractfile gen/FollowOptionTest.x

    FollowOptionParser simpleBParser = new FollowOptionParser();
    simpleBParser.setParserTarget(ParserExecution.EOF);
    simpleBParser.parseB(new StringReader("test ,"));
    assertEquals(true, simpleBParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
  }
  
  /**
   * Test assures that follow option is necessary, as this test fails to produce
   * correct behavior due to missing follow option
   * 
   */
  @Test
  public void test3() throws IOException {
    
    FollowOptionParser simpleParser = new FollowOptionParser();
    
    simpleParser.setParserTarget(ParserExecution.EOF);
    simpleParser.parseB(new StringReader(","));
    
    assertEquals(true, simpleParser.hasErrors());
  }

  @Test
  public void test4() throws IOException {
    
    FollowOptionParser simpleAParser = new FollowOptionParser();
    
    simpleAParser.setParserTarget(ParserExecution.EOF);
    simpleAParser.parseA(new StringReader("test ."));

    assertEquals(true, simpleAParser.hasErrors());
  }
}
