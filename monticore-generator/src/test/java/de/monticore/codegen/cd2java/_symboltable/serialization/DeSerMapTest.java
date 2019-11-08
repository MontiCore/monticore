/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

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
    return DeSerMap.getDeserializationImplementation(parse(attr), "deserializeFoo()", "symbolJson", addPrimitiveTypeSymbols());
  }

  protected TypeSymbolsScope addPrimitiveTypeSymbols(){
    TypeSymbolsScope scope = new TypeSymbolsScope();
    scope.add(new TypeSymbol("String"));
    scope.add(new TypeSymbol("Boolean"));
    scope.add(new TypeSymbol("Long"));
    scope.add(new TypeSymbol("Float"));
    scope.add(new TypeSymbol("Double"));
    scope.add(new TypeSymbol("Integer"));
    scope.add(new TypeSymbol("java.lang.String"));
    scope.add(new TypeSymbol("java.lang.Boolean"));
    scope.add(new TypeSymbol("java.lang.Long"));
    scope.add(new TypeSymbol("java.lang.Float"));
    scope.add(new TypeSymbol("java.lang.Double"));
    scope.add(new TypeSymbol("java.lang.Integer"));
    return scope;
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
