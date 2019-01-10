package de.monticore.aggregation;

import de.monticore.aggregation.blah._ast.ASTBlub;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.BlahLanguage;
import de.monticore.aggregation.blah._symboltable.BlahScope;
import de.monticore.aggregation.blah._symboltable.BlahSymbolTableCreator;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.foo._ast.ASTBar;
import de.monticore.aggregation.foo._parser.FooParser;
import de.monticore.aggregation.foo._symboltable.FooLanguage;
import de.monticore.aggregation.foo._symboltable.FooScope;
import de.monticore.aggregation.foo._symboltable.FooSymbolTableCreator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

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

   final ResolvingConfiguration blahResolverConfiguration = new ResolvingConfiguration();
   blahResolverConfiguration.addDefaultFilters(blahLang.getResolvingFilters());

   GlobalScope globalScope = new GlobalScope(new ModelPath(), blahLang, blahResolverConfiguration);

   BlahSymbolTableCreator blahSymbolTableCreator = new BlahSymbolTableCreator(blahResolverConfiguration,globalScope);
   BlahScope blahSymbolTable = (BlahScope) blahSymbolTableCreator.createFromAST(blahModel.get());


   Optional<DummySymbol> blubSymbol1 = blahSymbolTable.resolveDummy("blubSymbol1");

   System.out.println(blubSymbol1.isPresent());


   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Foo/Bar Infrastruktur
   ******************************************************************************************************************
   */
   FooLanguage fooLanguage = new FooLanguage("FooLangName","foo") {};
   Optional<ASTBar> fooModel = fooParser.parse_String("bar { blubSymbol1() }");

   final ResolvingConfiguration fooResolvingConfiguration = new ResolvingConfiguration();
   fooResolvingConfiguration.addDefaultFilters(fooLanguage.getResolvingFilters());

   GlobalScope fooGlobalScope = new GlobalScope(new ModelPath(), fooLanguage,fooResolvingConfiguration);
   FooSymbolTableCreator fooSymbolTableCreator = new FooSymbolTableCreator(fooResolvingConfiguration,fooGlobalScope);

   FooScope fooScope = (FooScope) fooSymbolTableCreator.createFromAST(fooModel.get());

   System.out.println(fooModel.isPresent());


  }


}
