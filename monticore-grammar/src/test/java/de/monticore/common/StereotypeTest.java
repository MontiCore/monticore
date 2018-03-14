/* (c) https://github.com/MontiCore/monticore */

package de.monticore.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.common.testcommon._parser.TestCommonParser;
import de.se_rwth.commons.logging.Log;

/**
 * @author Marita Breuer
 */
public class StereotypeTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  private ASTStereotype parseStereotype(String s) throws IOException {
    TestCommonParser parser = new TestCommonParser();
    Optional<ASTStereotype> ast = parser.parseStereotype(new StringReader(s));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    return ast.get();
  }
  
  @Test
  public void parseStereotype() {
    try {
      parseStereotype("<<s1=\"S1\">>");
      parseStereotype("<<s1=\"S1\", s2>>");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void parseNegativeStereotype() {
    try {
      TestCommonParser parser = new TestCommonParser();
      parser.parseStereotype(new StringReader("<<s1> >"));
      assertTrue(parser.hasErrors());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void parseGenericType() {
    // Check if handling of ">>" in generic tpyes is correct
    try {
      TestCommonParser parser = new TestCommonParser();
      parser.parseType(new StringReader("List<List<String>>"));
      assertFalse(parser.hasErrors());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
