/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen;

import com.google.common.base.Preconditions;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.List;

public class GeneratorHelper {

  public static final String AST_PREFIX = "AST";

  public static final String AST_PACKAGE_SUFFIX = "_ast";

  public static final String GET_SUFFIX_LIST = "List";

  public static final int STAR = -1;

  protected ASTCDDefinition cdDefinition;

  protected String packageName;

  protected String qualifiedName;

  // preserves order of appearance in the extends list of the grammar
  protected List<String> superGrammarCds = new ArrayList<>();

  protected CD4AnalysisGlobalScope symbolTable;

  protected ASTCDCompilationUnit topAst;

  public GeneratorHelper(ASTCDCompilationUnit topAst, CD4AnalysisGlobalScope symbolTable) {
    Preconditions.checkArgument(topAst.getCDDefinition() != null);

    this.topAst = topAst;

    cdDefinition = topAst.getCDDefinition();

    this.symbolTable = symbolTable;

    // Qualified Name
    qualifiedName = Names.getQualifiedName(topAst.getPackageList(), getCdName());

    // CD package
    packageName = getCdPackage(qualifiedName);

    // Create list of CDs for super grammars
    for (ASTMCImportStatement importSt : topAst.getMCImportStatementList()) {
      if (importSt.isStar()) {
        superGrammarCds.add(importSt.getQName());
      }
    }
  }

  public static String getPackageName(String packageName, String suffix) {
    return packageName.isEmpty() ? suffix : packageName
        + "." + suffix;
  }

  /**
   * @return packageName
   */
  public String getPackageName() {
    return this.packageName;
  }

  public String getCdName() {
    return cdDefinition.getName();
  }

  public static String getCdPackage(String qualifiedCdName) {
    return qualifiedCdName.toLowerCase();
  }
}
