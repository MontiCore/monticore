package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SuperSymbolHelper {

  private ASTCDCompilationUnit compilationUnit;

  private CDSymbol cdSymbol;

  protected static final String LOG_NAME = "SuperSymbolHelper";

  public SuperSymbolHelper(ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.cdSymbol = resolveCd(Names.getQualifiedName(compilationUnit.getPackageList(), compilationUnit.getCDDefinition().getName()));
  }

  public List<CDSymbol> getSuperCDs() {
    return getSuperCDs(cdSymbol);
  }

  private List<CDSymbol> getSuperCDs(CDSymbol cdSymbol) {
    List<CDSymbol> resolvedCds = new ArrayList<>();
    // imported cds
    for (String importedCdName : cdSymbol.getImports()) {
      Log.trace("Resolving the CD: " + importedCdName, LOG_NAME);
      CDSymbol importedCd = resolveCd(importedCdName);
      List<CDSymbol> recursivImportedCds = getAllCds(importedCd);
      for (CDSymbol recImport : recursivImportedCds) {
        //falls das Symbol noch nicht vorhanden ist, dann hinzufuegen
        if (!resolvedCds
            .stream()
            .filter(c -> c.getFullName().equals(recImport.getFullName()))
            .findAny()
            .isPresent()) {
          resolvedCds.add(recImport);
        }
      }
    }
    return resolvedCds;
  }

  private CDSymbol resolveCd(String symbolName) {
    Optional<CDSymbol> cdSymbol = compilationUnit.getEnclosingScope().resolve(symbolName, CDSymbol.KIND);
    if (!cdSymbol.isPresent()) {
      Log.error("0xA0487 The class diagram could not be resolved: " + symbolName);
    }
    return cdSymbol.get();
  }

  private List<CDSymbol> getAllCds(CDSymbol cd) {
    List<CDSymbol> resolvedCds = new ArrayList<>();
    // the cd itself
    resolvedCds.add(cd);
    resolvedCds.addAll(getSuperCDs(cd));
    return resolvedCds;
  }
}
