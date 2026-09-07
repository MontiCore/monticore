/* (c) https://github.com/MontiCore/monticore */

package mc.feature.javasql;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.javasql.javasql.javasql.JavaSQLMill;
import mc.feature.javasql.javasql.javasql._parser.JavaSQLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(JavaSQLMill.class)
public class JavaSQLTest {

  @Test
  public void test1() throws IOException {
    
    JavaSQLParser p = JavaSQLMill.parser();
    p.parseStart(new StringReader("a++,a=SELECT a FROM x ,i++"));
    
    assertFalse(p.hasErrors());
  }
}
