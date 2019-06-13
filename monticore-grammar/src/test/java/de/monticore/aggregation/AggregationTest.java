package de.monticore.aggregation;

import de.monticore.aggregation.blah._ast.ASTBlahModel;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.BlahLanguage;
import de.monticore.aggregation.blah._symboltable.BlahScope;
import de.monticore.aggregation.blah._symboltable.BlahSymbolTableCreator;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.foo._ast.ASTBar;
import de.monticore.aggregation.foo._parser.FooParser;
import de.monticore.aggregation.foo._symboltable.BarSymbol;
import de.monticore.aggregation.foo._symboltable.FooLanguage;
import de.monticore.aggregation.foo._symboltable.FooScope;
import de.monticore.aggregation.foo._symboltable.FooSymbolTableCreator;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class AggregationTest {

 @BeforeClass
 public static void disableFailQuick() {
  Log.enableFailQuick(false);
 }


 @Test
 public void test() throws IOException {
  
  // infrastruktur aufbauen, modelle zum resolven einlesen, SymTab aufbauen, adapter schreiben, globalscope foo und blah verbinden
  // TransitiveAdapterResolvingFilter implementieren und im globscope registrieren,
  //
   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Blah/Blub Infrastruktur
    ******************************************************************************************************************
    */
 
  //Create global scope for our language combination
  BlahLanguage blahLang = new BlahLanguage();
  FooLanguage fooLanguage = new FooLanguage("FooLangName","foo") {};

  FooBlahGlobalScope globalScope = new FooBlahGlobalScope(new ModelPath(), fooLanguage);
 
  //Parse blah model
  BlahParser blahParser = new BlahParser();
  Optional<ASTBlahModel> blahModel = blahParser.parse_String(
          "blahmodel {" +
            "blubScope blubScope1 {" +
              "blubScope blubScope2 {" +
                "symbol blubSymbol2" +
              "}" +
              "symbol blubSymbol1"+
            "}" +
          "}"
  );
  
  // create symbol table for "blah"
  BlahSymbolTableCreator blahSymbolTableCreator = new BlahSymbolTableCreator(globalScope.getIBlahGS());
  BlahScope blahSymbolTable = blahSymbolTableCreator.createFromAST(blahModel.get());
  
  // check dummy symbol is present in local scope
  Optional<DummySymbol> blubSymbol1 = blahSymbolTable.resolveDummy("blubScope1.blubSymbol1");
  
  assertTrue(blubSymbol1.isPresent());
//
//

  // check dummy symbol is present in global scope
  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  Optional<BarSymbol> barSymbol = globalScope.resolveBar("blubScope1.blubSymbol1");
  
  assertTrue(barSymbol.isPresent());


   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Foo/Bar Infrastruktur
   ******************************************************************************************************************
   */
 
   //parse foo model
  FooParser fooParser = new FooParser();
  Optional<ASTBar> fooModel = fooParser.parse_String("bar { blubSymbol1() } name");
 
  // Check foo model is parsed
  assertTrue(fooModel.isPresent());
 
  // create symbol table for "foo"
  FooSymbolTableCreator fooSymbolTableCreator = new FooSymbolTableCreator(globalScope);
  FooScope fooScope = (FooScope) fooSymbolTableCreator.createFromAST(fooModel.get());
  
  // check Dummy symbol is resolvable
  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  //Optional<Symbol> k = fooScope.resolve(".blubSymbol1", DummyKind.KIND);
  //assertTrue(k.isPresent());

 }
 
 
}
