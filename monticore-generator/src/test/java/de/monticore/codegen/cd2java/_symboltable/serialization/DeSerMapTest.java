/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols._symboltable.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit)
 * @version , 08.11.2019
 * @since TODO
 */
public class DeSerMapTest {

  @Test
  public void foo(){
    HookPoint res = getHookPointFor("boolean foo;");
    assertTrue(res instanceof StringHookPoint);
  }

  protected HookPoint getHookPointFor(String attr){
    return DeSerMap.getDeserializationImplementation(parse(attr), "deserializeFoo()",
        "symbolJson", DeSerMap.initializePrimitiveTypesGlobalScope());
  }

  protected ASTCDAttribute parse(String attr){
    CD4AnalysisParser parser = new CD4AnalysisParser();
    try {
      ASTCDAttribute astcdAttribute = parser.parse_StringCDAttribute(attr).orElse(null);
      assertNotNull(astcdAttribute);
      return astcdAttribute;
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }


}
