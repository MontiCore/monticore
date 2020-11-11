/* (c) https://github.com/MontiCore/monticore */

package mc.feature.javasql;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.javasql.javasql.javasql._parser.JavaSQLParser;

public class JavaSQLTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    JavaSQLParser p = new JavaSQLParser();
    p.parseStart(new StringReader("a++,a=SELECT a FROM x ,i++"));
    
    assertEquals(false, p.hasErrors());
    
  }
}
