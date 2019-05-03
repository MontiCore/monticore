package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class TypeCD2JavaVisitor implements CD4AnalysisVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private ASTCDCompilationUnit compilationUnit;

  public TypeCD2JavaVisitor(ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  @Override
  public void visit(ASTSimpleReferenceType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = compilationUnit.getEnclosingScope().resolve(node.getName(0), CDTypeSymbol.KIND);
    if (typeSymbol.isPresent()) {
      String javaType = String.join(".", typeSymbol.get().getModelName().toLowerCase(), ASTConstants.AST_PACKAGE, typeSymbol.get().getName());
      node.setName(0, javaType);
    }
    node.setNameList(new ArrayList<>(Arrays.asList(node.getName(0).split(PACKAGE_SEPARATOR))));
  }

  //remove super classes from traversal, so their names get not replaced
  @Override
  public void traverse(ASTCDClass node) {
    if (node.getModifierOpt().isPresent()) {
      ((ASTModifier)node.getModifierOpt().get()).accept(this.getRealThis());
    }

    if (node.getTImplementsOpt().isPresent()) {
      ((ASTTImplements)node.getTImplementsOpt().get()).accept(this.getRealThis());
    }

    Iterator iter_cDMethods = node.getInterfaceList().iterator();

    while(iter_cDMethods.hasNext()) {
      ((ASTReferenceType)iter_cDMethods.next()).accept(this.getRealThis());
    }

    if (node.getStereotypeOpt().isPresent()) {
      ((ASTCDStereotype)node.getStereotypeOpt().get()).accept(this.getRealThis());
    }

    iter_cDMethods = node.getCDAttributeList().iterator();

    while(iter_cDMethods.hasNext()) {
      ((ASTCDAttribute)iter_cDMethods.next()).accept(this.getRealThis());
    }

    iter_cDMethods = node.getCDConstructorList().iterator();

    while(iter_cDMethods.hasNext()) {
      ((ASTCDConstructor)iter_cDMethods.next()).accept(this.getRealThis());
    }

    iter_cDMethods = node.getCDMethodList().iterator();

    while(iter_cDMethods.hasNext()) {
      ((ASTCDMethod)iter_cDMethods.next()).accept(this.getRealThis());
    }

  }

}
