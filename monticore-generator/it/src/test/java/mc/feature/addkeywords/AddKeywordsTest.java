/* (c) https://github.com/MontiCore/monticore */

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
    
    assertEquals(3, helperd("10 keyword 2").getNameList().size());
    assertEquals(3, helperd("2 2 3").getNameList().size());
    assertEquals(3, helperd("48 keyword key2").getNameList().size());
    
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
    
    assertEquals(3, helpere("10 keyword 2").getINTList().size());
    assertEquals(3, helpere("2 2 3").getINTList().size());
    assertEquals(3, helpere("48 keyword key2").getINTList().size());
    
  }
  
  private ASTE helpere(String in) throws IOException {
    AddKeywordsParser createSimpleParser = new AddKeywordsParser();
    Optional<ASTE> parse = createSimpleParser.parseE(new StringReader(in));
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
    
    return parse.get();
  }
  
}
