package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SuperSymbolHelper {

  private static final String LOG_NAME = "SuperSymbolHelper";

  public static List<CDSymbol> getSuperCDs(ASTCDCompilationUnit compilationUnit) {
    CDSymbol cdSymbol = resolveCd(compilationUnit);
    return getSuperCDs(cdSymbol, compilationUnit);
  }

  private static List<CDSymbol> getSuperCDs(CDSymbol cdSymbol, ASTCDCompilationUnit compilationUnit) {
    List<CDSymbol> resolvedCds = new ArrayList<>();
    // imported cds
    for (String importedCdName : cdSymbol.getImports()) {
      Log.trace("Resolving the CD: " + importedCdName, LOG_NAME);
      CDSymbol importedCd = resolveCd(importedCdName, compilationUnit);
      List<CDSymbol> recursivImportedCds = getAllCds(importedCd, compilationUnit);
      for (CDSymbol recImport : recursivImportedCds) {
        //falls das Symbol noch nicht vorhanden ist, dann hinzufuegen
        if (resolvedCds
            .stream()
            .noneMatch(c -> c.getFullName().equals(recImport.getFullName()))) {
          resolvedCds.add(recImport);
        }
      }
    }
    return resolvedCds;
  }

  private static CDSymbol resolveCd(ASTCDCompilationUnit compilationUnit) {
    String symbolName = Names.getQualifiedName(compilationUnit.getPackageList(), compilationUnit.getCDDefinition().getName());
    return resolveCd(symbolName, compilationUnit);
  }

  private static CDSymbol resolveCd(String symbolName, ASTCDCompilationUnit compilationUnit) {
    Optional<CDSymbol> cdSymbol = compilationUnit.getEnclosingScope().resolve(symbolName, CDSymbol.KIND);
    if (!cdSymbol.isPresent()) {
      Log.error("0xA0487 The class diagram could not be resolved: " + symbolName);
    }
    return cdSymbol.get();
  }

  private static List<CDSymbol> getAllCds(CDSymbol cd, ASTCDCompilationUnit compilationUnit) {
    List<CDSymbol> resolvedCds = new ArrayList<>();
    // the cd itself
    resolvedCds.add(cd);
    resolvedCds.addAll(getSuperCDs(cd, compilationUnit));
    return resolvedCds;
  }
}
