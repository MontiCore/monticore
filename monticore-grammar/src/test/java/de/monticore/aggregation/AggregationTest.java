package de.monticore.aggregation;

import com.google.common.collect.Lists;
import de.monticore.aggregation.blah._ast.ASTBlahModel;
import de.monticore.aggregation.blah._ast.ASTBlub;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.*;
import de.monticore.aggregation.foo._ast.ASTBar;
import de.monticore.aggregation.foo._parser.FooParser;
import de.monticore.aggregation.foo._symboltable.FooLanguage;
import de.monticore.aggregation.foo._symboltable.FooScope;
import de.monticore.aggregation.foo._symboltable.FooSymbolTableCreator;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodKind;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
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
  BlahLanguage blahLang = new BlahLanguage("BlahLangName","blah"){};
  FooLanguage fooLanguage = new FooLanguage("FooLangName","foo") {};
  final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();

  resolvingConfiguration.addDefaultFilters(blahLang.getResolvingFilters());


  Dummy2EMethodResolvingFilter dummy2EMethodResolvingFilter = new Dummy2EMethodResolvingFilter(DummySymbol.KIND);
  resolvingConfiguration.addDefaultFilter(dummy2EMethodResolvingFilter);


  resolvingConfiguration.addDefaultFilters(fooLanguage.getResolvingFilters());
 
  GlobalScope globalScope = new GlobalScope(new ModelPath(), Lists.newArrayList(blahLang,fooLanguage), resolvingConfiguration);
 
  //Parse blah model
  BlahParser blahParser = new BlahParser();
  Optional<ASTBlahModel> blahModel = blahParser.parse_String(
          "blahmodel {" +
                  "blubScope blubScope1 {" +
                  "blubSymbol1" +

                  "}" +
                  "}"
  );
  
  // create symbol table for "blah"
  BlahSymbolTableCreator blahSymbolTableCreator = new BlahSymbolTableCreator(resolvingConfiguration,globalScope);
  BlahScope blahSymbolTable = (BlahScope) blahSymbolTableCreator.createFromAST(blahModel.get());
  
  // check dummy symbol is present in local scope
  Optional<DummySymbol> blubSymbol1 = blahSymbolTable.resolveDummy("blubSymbol1");
  
  assertTrue(blubSymbol1.isPresent());



  // check dummy symbol is present in global scope
  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  blubSymbol1 = globalScope.resolve("blubScope1.blubSymbol1", DummySymbol.KIND);
  
  assertTrue(blubSymbol1.isPresent());


   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Foo/Bar Infrastruktur
   ******************************************************************************************************************
   */
 
   //parse foo model
  FooParser fooParser = new FooParser();
  Optional<ASTBar> fooModel = fooParser.parse_String("bar { blubSymbol1() }");
 
  // Check foo model is parsed
  assertTrue(fooModel.isPresent());
 
  // create symbol table for "foo"
  FooSymbolTableCreator fooSymbolTableCreator = new FooSymbolTableCreator(resolvingConfiguration,globalScope);
  FooScope fooScope = (FooScope) fooSymbolTableCreator.createFromAST(fooModel.get());
  
  // check Dummy symbol is resolvable
  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  //Optional<Symbol> k = fooScope.resolve(".blubSymbol1", DummyKind.KIND);
  //assertTrue(k.isPresent());

  Optional<Symbol> a = fooScope.resolve("blubScope1.blubSymbol1", EMethodSymbol.KIND);

  assertTrue(a.isPresent());

  Optional<EMethodSymbol> a2 = fooScope.resolveEMethod("blubScope1.blubSymbol1");

  assertTrue(a2.isPresent());

 }
 
 
}
