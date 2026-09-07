/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.comments.commenttypestest.CommentTypesTestMill;
import mc.feature.comments.commenttypestest._parser.CommentTypesTestParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(CommentTypesTestMill.class)
public class CommentTypesTest {

  /**
   * This Test tests if xml comments are parsed correctly.
   * 
   * @throws IOException 
   */
  @Test
  public void testXMLComment() throws IOException  {
    CommentTypesTestParser p = CommentTypesTestMill.parser();
    p.parse_StringCStart("start <!-- comment \n --> marita");
    
    assertFalse(p.hasErrors());
  }
  
  /**
   * This Test tests if xml comments with including "--" are parsed correctly.
   * 
   */
  @Test
  public void testCComment_With__() throws IOException  {
    CommentTypesTestParser p = CommentTypesTestMill.parser();
    p.parse_StringCStart("start <!-- -- --> marita");
    
    assertFalse(p.hasErrors());
  }
  
  /**
   * This Test tests if tex comments are parsed correctly.
   * 
   */
  @Test
  public void testTexComment() throws IOException {
    CommentTypesTestParser p = CommentTypesTestMill.parser();
    p.parse_StringCStart("start % comment\n  marita");
    
    assertFalse(p.hasErrors());
  }
  
  /**
   * This Test tests if freemarker comments are parsed correctly.
   * 
   */
  @Test
  public void testFreeMarkerComment() throws IOException {
    CommentTypesTestParser p = CommentTypesTestMill.parser();
    p.parse_StringCStart("start <#-- comment \n --> marita");
    
    assertFalse(p.hasErrors());
  }
  
  /**
   * This Test tests if hash comments are parsed correctly.
   * 
   */
  @Test
  public void testHashComment() throws IOException {
    CommentTypesTestParser p = CommentTypesTestMill.parser();
    p.parse_StringCStart("start # comment \n marita");
    
    assertFalse(p.hasErrors());
  }
  
}
