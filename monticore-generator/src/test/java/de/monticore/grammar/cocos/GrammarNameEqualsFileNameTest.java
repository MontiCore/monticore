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

package de.monticore.grammar.cocos;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by
 *
 * @author KH
 */
public class GrammarNameEqualsFileNameTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testInvalidFilename(){
    Log.getFindings().clear();
    MCGrammarParser.parse(Paths.get("src/test/resources/cocos/invalid/A4003/A4003.mc4"));

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4003 The grammar name A4002 must not differ from the file name"
          + " A4003 of the grammar (without its file extension).", f.getMsg());
    }
  }

  @Test
  public void testInvalidPackage(){
    Log.getFindings().clear();
    MCGrammarParser.parse(Paths.get("src/test/resources/cocos/invalid/A4004/A4004.mc4"));

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4004 The package declaration cocos.invalid.A4003 of the grammar must not"
          + " differ from the package of the grammar file.", f.getMsg());
    }
  }
}
