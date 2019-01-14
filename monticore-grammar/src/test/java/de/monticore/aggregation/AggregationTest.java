package de.monticore.aggregation;

import com.google.common.collect.Lists;
import de.monticore.aggregation.blah._ast.ASTBlub;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.*;
import de.monticore.aggregation.foo._ast.ASTBar;
import de.monticore.aggregation.foo._parser.FooParser;
import de.monticore.aggregation.foo._symboltable.FooLanguage;
import de.monticore.aggregation.foo._symboltable.FooScope;
import de.monticore.aggregation.foo._symboltable.FooSymbolTableCreator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class AggregationTest {



 @Test
  public void test() throws IOException {

    BlahParser blahParser = new BlahParser();
    FooParser fooParser = new FooParser();

    //blahParser

    // infrastruktur aufbauen, modelle zum resolven einlesen, SymTab aufbauen, adapter schreiben, globalscope foo und blah verbinden
    // TransitiveAdapterResolvingFilter implementieren und im globscope registrieren,
    //
   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Blah/Blub Infrastruktur
    ******************************************************************************************************************
    */
 
  BlahLanguage blahLang = new BlahLanguage("BlahLangName","blah"){};
  Optional<ASTBlub> blahModel = blahParser.parse_String("blub { blubSymbol1 }");
  FooLanguage fooLanguage = new FooLanguage("FooLangName","foo") {};
  Optional<ASTBar> fooModel = fooParser.parse_String("bar { blubSymbol1() }");
 
  final ResolvingConfiguration blahResolverConfiguration = new ResolvingConfiguration();
  blahResolverConfiguration.addDefaultFilters(blahLang.getResolvingFilters());
  blahResolverConfiguration.addDefaultFilters(fooLanguage.getResolvingFilters());
 
   GlobalScope globalScope = new GlobalScope(new ModelPath(), Lists.newArrayList(blahLang,fooLanguage), blahResolverConfiguration);

   BlahSymbolTableCreator blahSymbolTableCreator = new BlahSymbolTableCreator(blahResolverConfiguration,globalScope);
   BlahScope blahSymbolTable = (BlahScope) blahSymbolTableCreator.createFromAST(blahModel.get());

   Optional<DummySymbol> blubSymbol1 = blahSymbolTable.resolveDummy("blubSymbol1");
 
  assertTrue(blubSymbol1.isPresent());
 
  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  blubSymbol1 = globalScope.resolve("blub.blubSymbol1", DummyKind.KIND);
 
  assertTrue(blubSymbol1.isPresent());


   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Foo/Bar Infrastruktur
   ******************************************************************************************************************
   */
  

   FooSymbolTableCreator fooSymbolTableCreator = new FooSymbolTableCreator(blahResolverConfiguration,globalScope);

   FooScope fooScope = (FooScope) fooSymbolTableCreator.createFromAST(fooModel.get());

   System.out.println(fooModel.isPresent());

  // TODO soll das so? Scopes ohne Namen müssen mit Punkt navigiert werde
  Optional<Symbol> k = fooScope.resolve(".blubSymbol1", DummyKind.KIND);
  assertTrue(k.isPresent());

  //System.out.println(k.get());
  }


}
