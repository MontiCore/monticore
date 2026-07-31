/* (c) https://github.com/MontiCore/monticore */

package mc.feature.javasql;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;

import mc.GeneratorIntegrationsTest;
import mc.feature.javasql.javasql.javasql._parser.JavaSQLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaSQLTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    JavaSQLParser p = new JavaSQLParser();
    p.parseStart(new StringReader("a++,a=SELECT a FROM x ,i++"));
    
    assertFalse(p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
