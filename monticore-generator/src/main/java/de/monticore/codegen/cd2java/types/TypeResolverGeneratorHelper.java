/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.types;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
/**
 *  on 06.11.2016.
 */
public class TypeResolverGeneratorHelper extends GeneratorHelper {

  public TypeResolverGeneratorHelper(
      ASTCDCompilationUnit topAst,
      GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }

  public String getTypeResolver() {
    return getTypeResolver(getCdName());
  }

  public String getTypeResolver(String cdName) {
    return cdName + "TypeResolver";
  }
}
