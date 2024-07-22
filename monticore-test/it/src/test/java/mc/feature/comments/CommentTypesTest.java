/* (c) https://github.com/MontiCore/monticore */

package mc.feature.comments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.comments.commenttypestest._parser.CommentTypesTestParser;

public class CommentTypesTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * This Test tests if xml comments are parsed correctly.
   * 
   * @throws IOException 
   */
  @Test
  public void testXMLComment() throws IOException  {
    StringReader r = new StringReader("start <!-- comment \n --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * This Test tests if xml comments with including "--" are parsed correctly.
   * 
   */
  @Test
  public void testCComment_With__() throws IOException  {
    StringReader r = new StringReader("start <!-- -- --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * This Test tests if tex comments are parsed correctly.
   * 
   */
  @Test
  public void testTexComment() throws IOException {
    StringReader r = new StringReader("start % comment\n  marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * This Test tests if freemarker comments are parsed correctly.
   * 
   */
  @Test
  public void testFreeMarkerComment() throws IOException {
    StringReader r = new StringReader("start <#-- comment \n --> marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * This Test tests if hash comments are parsed correctly.
   * 
   */
  @Test
  public void testHashComment() throws IOException {
    StringReader r = new StringReader("start # comment \n marita");
    
    CommentTypesTestParser p = new CommentTypesTestParser();    
    p.parseCStart(r);
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
