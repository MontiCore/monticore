/* (c) https://github.com/MontiCore/monticore */

package mc.feature.addkeywords;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.addkeywords.addkeywords.AddKeywordsMill;
import mc.feature.addkeywords.addkeywords._ast.ASTD;
import mc.feature.addkeywords.addkeywords._ast.ASTE;
import mc.feature.addkeywords.addkeywords._parser.AddKeywordsParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AddKeywordsMill.class)
public class AddKeywordsTest {
  
  @ParameterizedTest
  @ValueSource( strings = {
      "3", "keyword", "key2"
  })
  public void testB(String in) throws IOException {
    AddKeywordsParser b = AddKeywordsMill.parser();
    b.parse_StringB(in);
        
    assertFalse(b.hasErrors());
  }
  
  @ParameterizedTest
  @ValueSource( strings = {
      "15", "keyword", "key2"
  })
  public void testC(String in) throws IOException {
    AddKeywordsParser b = AddKeywordsMill.parser();
    b.parse_StringC(in);
    assertFalse(b.hasErrors());
  }
  
  @ParameterizedTest
  @ValueSource( strings = {
      "1", "keyword", "key2"
  })
  public void testD(String value) throws IOException {
    AddKeywordsParser createSimpleParser = AddKeywordsMill.parser();
    Optional<ASTD> parse = createSimpleParser.parse_StringD(value);
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
  }
  
  @ParameterizedTest
  @ValueSource( strings = {
      "10 keyword 2", "2 2 3", "48 keyword key2"
  })
  public void testD3(String value) throws IOException {
    AddKeywordsParser createSimpleParser = AddKeywordsMill.parser();
    Optional<ASTD> parse = createSimpleParser.parse_StringD(value);
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
    
    assertEquals(3, parse.get().getNameList().size());
  }
  
  @ParameterizedTest
  @ValueSource( strings = {
      "1", "keyword", "key2"
  })
  public void testE(String value) throws IOException {
    AddKeywordsParser createSimpleParser = AddKeywordsMill.parser();
    Optional<ASTE> parse = createSimpleParser.parse_StringE(value);
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
  }
  
  @ParameterizedTest
  @ValueSource( strings = {
      "10 keyword 2", "2 2 3", "48 keyword key2"
  })
  public void testE3(String value) throws IOException {
    AddKeywordsParser createSimpleParser = AddKeywordsMill.parser();
    Optional<ASTE> parse = createSimpleParser.parse_StringE(value);
    assertTrue(parse.isPresent());
    assertFalse(createSimpleParser.hasErrors());
    
    assertEquals(3, parse.get().getINTList().size());
  }
}
