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

package mc.feature.followoption;

import static org.junit.Assert.assertEquals;
import groovyjarjarantlr.TokenStreamException;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.followoption.followoption._parser.AMCParser;
import mc.feature.followoption.followoption._parser.BMCParser;
import mc.feature.followoption.followoption._parser.FollowOptionParserFactory;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;

public class FollowOptionTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    //-- extractfile gen/FollowOptionTest.x
    AMCParser simpleAParser = FollowOptionParserFactory.createAMCParser();
    simpleAParser.setParserTarget(ParserExecution.EOF);
    simpleAParser.parse(new StringReader("test ,"));
    assertEquals(false, simpleAParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
  }
    
  @Test
  public void test2() throws IOException {    
    //-- extractfile gen/FollowOptionTest.x

    BMCParser simpleBParser = FollowOptionParserFactory.createBMCParser();    
    simpleBParser.setParserTarget(ParserExecution.EOF);
    simpleBParser.parse(new StringReader("test ,"));
    assertEquals(true, simpleBParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
  }
  
  /**
   * Test assures that follow option is necessary, as this test fails to produce
   * correct behavior due to missing follow option
   * 
   * @throws RecognitionException
   * @throws TokenStreamException
   */
  @Test
  public void test3() throws IOException {
    
    BMCParser simpleParser = FollowOptionParserFactory.createBMCParser();
    
    simpleParser.setParserTarget(ParserExecution.EOF);
    simpleParser.parse(new StringReader(","));
    
    assertEquals(true, simpleParser.hasErrors());
  }

  @Test
  public void test4() throws IOException {
    
    AMCParser simpleParser = FollowOptionParserFactory.createAMCParser();
    
    simpleParser.setParserTarget(ParserExecution.EOF);
    simpleParser.parse(new StringReader("test ."));

    assertEquals(true, simpleParser.hasErrors());
  }
}
