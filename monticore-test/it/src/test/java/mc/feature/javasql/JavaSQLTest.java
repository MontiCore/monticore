/* (c) https://github.com/MontiCore/monticore */

package mc.feature.javasql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.javasql.javasql.javasql._parser.JavaSQLParser;
import org.junit.jupiter.api.Test;

public class JavaSQLTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1() throws IOException {
    
    JavaSQLParser p = new JavaSQLParser();
    p.parseStart(new StringReader("a++,a=SELECT a FROM x ,i++"));
    
    Assertions.assertEquals(false, p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
