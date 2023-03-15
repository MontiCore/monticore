/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._parser.CDBasisParser;
import de.monticore.cdbasis._symboltable.*;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResolvingTest {

  protected Optional<ASTCDCompilationUnit> parseAndTransform(String model){
    CDBasisParser parser = CDBasisMill.parser();
    try {
      Optional<ASTCDCompilationUnit> unit = parser.parse(model);
      CDBasisTraverser traverser = CDBasisMill.traverser();
      traverser.add4CDBasis(new CDBasisCombinePackagesTrafo());
      if(unit.isPresent()){
        unit.get().accept(traverser);
        return Optional.of(unit.get());
      }
      return Optional.empty();
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  protected ICDBasisArtifactScope buildSymbolTable(ASTCDCompilationUnit ast){
    CDBasisScopesGenitorDelegator scopesGenitorDelegator = CDBasisMill.scopesGenitorDelegator();
    ICDBasisArtifactScope as = scopesGenitorDelegator.createFromAST(ast);

    CDBasisTraverser traverser = CDBasisMill.traverser();
    traverser.add4CDBasis(new CDBasisSymbolTableCompleter());
    ast.accept(traverser);

    return as;
  }

  @Test
  public void testValidExample(){
    Optional<ASTCDCompilationUnit> a = parseAndTransform("src/test/resources/de/monticore/cdbasis/valid/A.cd");
    Optional<ASTCDCompilationUnit> b = parseAndTransform("src/test/resources/de/monticore/cdbasis/valid/B.cd");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    ICDBasisArtifactScope asB = buildSymbolTable(b.get());
    ICDBasisArtifactScope asA = buildSymbolTable(a.get());

    Optional<CDTypeSymbol> fooSymbol = asA.resolveCDType("Foo");
    assertTrue(fooSymbol.isPresent());

    ICDBasisScope fooScope = fooSymbol.get().getSpannedScope();
    Optional<CDTypeSymbol> type = fooScope.resolveCDType("B.Bar");
    assertTrue(type.isPresent());
  }

  @Test
  public void testSimpleInvalid(){
    Optional<ASTCDCompilationUnit> c = parseAndTransform("src/test/resources/de/monticore/cdbasis/invalid/C.cd");

    assertTrue(c.isPresent());

    ICDBasisArtifactScope asC = buildSymbolTable(c.get());

    Optional<CDTypeSymbol> fooSymbol = asC.resolveCDType("Foo");
    assertTrue(fooSymbol.isPresent());

    ICDBasisScope fooScope = fooSymbol.get().getSpannedScope();
    try {
      Optional<CDTypeSymbol> type = fooScope.resolveCDType("Bar");
      //if a type could be resolved: Test fails because Bar should be ambiguous
      assertFalse(type.isPresent());
    } catch(ResolvedSeveralEntriesForSymbolException e) {
      assertTrue(e.getMessage().startsWith("0xA4095"));
    }
  }

  @Test
  public void testInterModelInvalid(){
    Optional<ASTCDCompilationUnit> a = parseAndTransform("src/test/resources/de/monticore/cdbasis/invalid/A.cd");
    Optional<ASTCDCompilationUnit> b = parseAndTransform("src/test/resources/de/monticore/cdbasis/invalid/B.cd");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    ICDBasisArtifactScope asB = buildSymbolTable(b.get());
    ICDBasisArtifactScope asA = buildSymbolTable(a.get());

    Optional<CDTypeSymbol> fooSymbol = asA.resolveCDType("Foo");
    assertTrue(fooSymbol.isPresent());

    ICDBasisScope fooScope = fooSymbol.get().getSpannedScope();
    try {
      Optional<CDTypeSymbol> type = fooScope.resolveCDType("B.Bar");
      //if a type could be resolved: Test fails because B.Bar should be ambiguous
      assertFalse(type.isPresent());
    } catch(ResolvedSeveralEntriesForSymbolException e) {
      assertTrue(e.getMessage().startsWith("0xA4095"));
    }
  }


}
