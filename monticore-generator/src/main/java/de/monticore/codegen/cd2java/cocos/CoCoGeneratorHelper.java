/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;

public class CoCoGeneratorHelper extends GeneratorHelper {
  
  public CoCoGeneratorHelper(ASTCDCompilationUnit topAst, CD4AnalysisGlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public String getCoCoPackage() {
    return getCoCoPackage(getPackageName());
  }
  
  public static String getCoCoPackage(String packageName) {
    return getPackageName(packageName, getCoCoPackageSuffix());
  }
  
  public static String getCoCoPackageSuffix() {
    return GeneratorHelper.COCOS_PACKAGE_SUFFIX;
  }
  
  public String getVisitorType() {
    return VisitorGeneratorHelper.getVisitorType(getCdName());
  }
  
  /**
   * @param cDName
   * @return type name of the language's visitor interface
   */
  public static String getCheckerType(String cDName) {
    return cDName + "CoCoChecker";
  }
  
  /**
   * Gets the full-qualified java name of the checker. E.g., input a cd with
   * qualified name "a.b.c.D" the result is "a.b.c.d._coco.DCoCoChecker".
   * 
   * @param cd the class diagram to get the visitor interface for.
   * @return the full-qualified java name of the visitor interface.
   */
  public String getQualifiedCheckerType(CDDefinitionSymbol cd) {
    return getQualifiedCheckerType(cd.getFullName());
  }
  
  /**
   * @param packageName
   * @param cdName
   * @return full-qualified name of the language's checker
   */
  public static String getQualifiedCheckerType(String packageName, String cdName) {
    return getPackageName(packageName, getCoCoPackageSuffix()) + "."
        + getCheckerType(cdName);
  }
  
  /**
   * Gets the full-qualified type of the language's checker. For example, input
   * "a.b.c.D" results in output "a.b.c.d._coco.DCoCoChecker"
   * 
   * @param qualifiedLanguageName
   * @return the languages full-qualified checker
   */
  public static String getQualifiedCheckerType(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedCheckerType(packageName, cdName);
  }
  
  public static String getQualifiedCoCoType(CDDefinitionSymbol cd, CDTypeSymbol type) {
    return getCoCoPackage(getCdPackage(cd.getFullName())) + "."
        + getCoCoType(cd, type.getName());
  }
  
  public static String getCoCoType(CDDefinitionSymbol cd, String aSTType) {
    return cd.getName() + aSTType + "CoCo";
  }
  
  public static boolean isCurrentDiagram(CDDefinitionSymbol cd, CDDefinitionSymbol currentCd) {
    return cd.getFullName().equals(currentCd.getFullName());
  }
  
  /**
   * @return type name of the language's inheritance visitor interface
   */
  public String getInheritanceVisitorType() {
    return VisitorGeneratorHelper.getInheritanceVisitorType(getCdName());
  }
}
