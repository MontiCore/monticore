/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources/";

  @Before
  public void setUpDecoratorTestCase() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(globalScope);
    globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }

    ASTCDCompilationUnit comp = ast.get();
    new CD4CodeAfterParseTrafo().transform(ast.get());

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(comp);
    comp.getEnclosingScope().setAstNode(comp);
    String packageName = Joiners.DOT.join(comp.getCDPackageList());
    scope.getLocalDiagramSymbols().forEach(s -> s.setPackageName(packageName));
    List<ImportStatement> imports = Lists.newArrayList();
    comp.getMCImportStatementList().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    scope.setImportsList(imports);
    scope.setPackageName(packageName);
    for (ASTMCImportStatement imp: comp.getMCImportStatementList()) {
      if (!CD4CodeMill.globalScope().resolveDiagram(imp.getQName()).isPresent()) {
        parse(imp.getMCQualifiedName().getPartsList().toArray(new String[imp.getMCQualifiedName().sizeParts()]));
      }
    }
    return comp;
  }

  /**
   * Collects all compilation units and calls the cd type completer for
   * completing the symbol table of each cd.
   *
   * @param gs The given global scope to extract all compilation units
   */
  // TODO (MB): Macht diese Methode für unsere Klassendiagramme sind?
  // So etwas kann nicht aufgelöst werden:
  // <<astType>> protected java.util.List<de.monticore.codegen.ast.automaton._ast.ASTState> states;

  /*
  public void completeCDTypes(ICD4AnalysisGlobalScope gs) {
    for (ICD4AnalysisScope scope : gs.getSubScopes()) {
      if (!scope.getDiagramSymbols().isEmpty()) {
        // artifact scopes with a diagram symbol always yield to the AST node of a compilation unit
        ASTCDCompilationUnit comp = (ASTCDCompilationUnit) scope.getAstNode();

        // complete types for CD
        CD4CodeSymbolTableCompleter v = new CD4CodeSymbolTableCompleter(comp);
        comp.accept(v.getTraverser());
      }
    }
  }

   */

  /**
   * Imports all super CDs based on a given compilation unit.
   *
   * @param comp Input compiluation unit
   */
  /*
  public void importCDs(ASTCDCompilationUnit comp) {
    for (ASTMCImportStatement imp : comp.getMCImportStatementList()) {
      String impName = String.join("/", imp.getMCQualifiedName().getPartsList());
      ASTCDCompilationUnit sub = parse(impName);
      importCDs(sub);
    }
  }
   */
}
