/* (c) https://github.com/MontiCore/monticore */
package mc.feature.deepclone;

import mc.feature.deepclone.nodoubleadding._ast.ASTSupProd;
import mc.feature.deepclone.nodoubleadding._parser.NoDoubleAddingParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class NoDoubleAddingTest {

  @Test
  public void testNoDoubleListElements() throws IOException {
    //test that deepClone does not copy list elements twice
    NoDoubleAddingParser parser = new NoDoubleAddingParser();
    Optional<ASTSupProd> astSupProd = parser.parse_StringSupProd("Foo foo Name1 Name2 Name3");
    assertTrue(astSupProd.isPresent());
    ASTSupProd clonedProd = astSupProd.get().deepClone();

    assertEquals(3, clonedProd.sizeNames());
    assertEquals("Name1", clonedProd.getName(0));
    assertEquals("Name2", clonedProd.getName(1));
    assertEquals("Name3", clonedProd.getName(2));
  }
}
