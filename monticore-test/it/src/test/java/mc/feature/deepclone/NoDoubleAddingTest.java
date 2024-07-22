/* (c) https://github.com/MontiCore/monticore */
package mc.feature.deepclone;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.deepclone.nodoubleadding._ast.ASTSupProd;
import mc.feature.deepclone.nodoubleadding._parser.NoDoubleAddingParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class NoDoubleAddingTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testNoDoubleListElements() throws IOException {
    //test that deepClone does not copy list elements twice
    NoDoubleAddingParser parser = new NoDoubleAddingParser();
    Optional<ASTSupProd> astSupProd = parser.parse_StringSupProd("Foo foo Name1 Name2 Name3");
    Assertions.assertTrue(astSupProd.isPresent());
    ASTSupProd clonedProd = astSupProd.get().deepClone();

    Assertions.assertEquals(3, clonedProd.sizeNames());
    Assertions.assertEquals("Name1", clonedProd.getName(0));
    Assertions.assertEquals("Name2", clonedProd.getName(1));
    Assertions.assertEquals("Name3", clonedProd.getName(2));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
