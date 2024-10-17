/* (c) https://github.com/MontiCore/monticore */
package de.monticore.aggregation;

import de.monticore.aggregation.blah.BlahMill;
import de.monticore.aggregation.blah._ast.ASTBlahModel;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.BlahScopesGenitor;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.blah._symboltable.IBlahArtifactScope;
import de.monticore.aggregation.blah._visitor.BlahTraverser;
import de.monticore.aggregation.foo.FooMill;
import de.monticore.aggregation.foo._ast.ASTBar;
import de.monticore.aggregation.foo._parser.FooParser;
import de.monticore.aggregation.foo._symboltable.BarSymbol;
import de.monticore.aggregation.foo._symboltable.FooGlobalScope;
import de.monticore.aggregation.foo._symboltable.FooScopesGenitorDelegator;
import de.monticore.aggregation.foo._symboltable.IFooArtifactScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class AggregationTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    FooMill.reset();
    FooMill.init();
    BlahMill.reset();
    BlahMill.init();
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
    FooGlobalScope globalScope = (FooGlobalScope) FooMill.globalScope();
    
    //Parse blah model
    BlahParser blahParser = new BlahParser();
    Optional<ASTBlahModel> blahModel = blahParser.parse_String(
      "blahmodel {" +
        "blubScope blubScope1 {" +
        "blubScope blubScope2 {" +
        "symbol blubSymbol2" +
        "}" +
        "symbol blubSymbol1" +
        "}" +
        "}"
    );
    
    // create symbol table for "blah"
    BlahTraverser traverser = BlahMill.traverser();
    BlahScopesGenitor blahSymbolTableCreator = new BlahScopesGenitor();
    blahSymbolTableCreator.addToScopeStack(globalScope.getIBlahGS());
    traverser.add4Blah(blahSymbolTableCreator);
    traverser.setBlahHandler(blahSymbolTableCreator);
    
    IBlahArtifactScope blahSymbolTable = blahSymbolTableCreator.createFromAST(blahModel.get());
    blahSymbolTable.setName("blahmodel");
    
    // check dummy symbol is present in local scope
    Optional<DummySymbol> blubSymbol1 = blahSymbolTable.resolveDummy("blahmodel.blubScope1.blubSymbol1");
    
    Assertions.assertTrue(blubSymbol1.isPresent());
//
//
    
    // check dummy symbol is present in global scope
    Optional<BarSymbol> barSymbol = globalScope.resolveBar("blahmodel.blubScope1.blubSymbol1");
    
    Assertions.assertTrue(barSymbol.isPresent());


   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Foo/Bar Infrastruktur
   ******************************************************************************************************************
   */
    
    //parse foo model
    FooParser fooParser = new FooParser();
    Optional<ASTBar> fooModel = fooParser.parse_String("bar { blubSymbol1() } name");
    
    // Check foo model is parsed
    Assertions.assertTrue(fooModel.isPresent());
    
    // create symbol table for "foo"
    FooScopesGenitorDelegator fooSymbolTableCreator = FooMill.scopesGenitorDelegator();
    IFooArtifactScope fooScope = fooSymbolTableCreator.createFromAST(fooModel.get());
    
    // check symbol is resolvable
    Optional<BarSymbol> k = fooScope.resolveBar("name");
    Assertions.assertTrue(k.isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  
}
