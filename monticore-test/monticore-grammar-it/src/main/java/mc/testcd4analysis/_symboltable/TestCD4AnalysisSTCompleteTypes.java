/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCCollectionTypesFullPrettyPrinter;
import mc.testcd4analysis._ast.*;
import mc.testcd4analysis._visitor.TestCD4AnalysisVisitor2;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class TestCD4AnalysisSTCompleteTypes implements TestCD4AnalysisVisitor2 {

  protected Deque<? extends ITestCD4AnalysisScope> scopeStack;

  public TestCD4AnalysisSTCompleteTypes(Deque<? extends ITestCD4AnalysisScope> scopeStack){
    this.scopeStack = scopeStack;
  }

  @Override
  public void endVisit(ASTCDAttribute astAttribute){
    CDFieldSymbol fieldSymbol = astAttribute.getSymbol();
    ASTMCType astType = astAttribute.getMCType();
    final String typeName;
    if (astType instanceof ASTMCGenericType) {
      typeName = ((ASTMCGenericType) astType).printWithoutTypeArguments();
    } else {
      typeName = astAttribute.getMCType().printType(new MCCollectionTypesFullPrettyPrinter(new IndentPrinter()));
    }

    final CDTypeSymbolSurrogate typeReference;
    Optional<CDTypeSymbol> typeSymbol = astAttribute.getEnclosingScope().resolveCDType(typeName);
    if (typeSymbol.isPresent()) {
      typeReference = new CDTypeSymbolSurrogate(typeSymbol.get().getFullName());
    } else {
      typeReference = new CDTypeSymbolSurrogate(typeName);
    }
    typeReference.setEnclosingScope(scopeStack.peekLast());
    fieldSymbol.setType(typeReference);

    if (astAttribute.isPresentModifier()) {
      final ASTModifier astModifier = astAttribute.getModifier();

      fieldSymbol.setIsDerived(astModifier.isDerived());
      fieldSymbol.setIsStatic(astModifier.isStatic());
      fieldSymbol.setIsFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        fieldSymbol.setIsProtected(true);
      } else if (astModifier.isPrivate()) {
        fieldSymbol.setIsPrivate(true);
      } else {
        // public is default
        fieldSymbol.setIsPublic(true);
      }
    }
  }

  @Override
  public void endVisit(ASTCDMethod astMethod){
    CDMethOrConstrSymbol methodSymbol = astMethod.getSymbol();
    methodSymbol.setIsStatic(astMethod.getModifier().isStatic());
    setTypeOfMethod(methodSymbol, astMethod);
    if (!astMethod.getCDParameterList().isEmpty() && astMethod.getCDParameter(astMethod.getCDParameterList().size() - 1).isEllipsis()) {
      methodSymbol.setIsEllipsis(true);
    }
  }

  public void setTypeOfMethod(final CDMethOrConstrSymbol methodSymbol, ASTCDMethod astMethod) {
    String typeName = astMethod.getMCReturnType().printType(new MCCollectionTypesFullPrettyPrinter(new IndentPrinter()));
    final CDTypeSymbolSurrogate typeReference;
    Optional<CDTypeSymbol> typeSymbol = astMethod.getEnclosingScope().resolveCDType(typeName);
    if (typeSymbol.isPresent()) {
      typeReference = new CDTypeSymbolSurrogate(typeSymbol.get().getFullName());
    } else {
      typeReference = new CDTypeSymbolSurrogate(typeName);
    }
    typeReference.setEnclosingScope(scopeStack.peekLast());
    methodSymbol.setReturnType(typeReference);
  }

  @Override
  public void endVisit(ASTCDInterface astInterface){
    CDTypeSymbol interfaceSymbol = astInterface.getSymbol();
    interfaceSymbol.setIsInterface(true);
    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);

    addInterfacesToType(interfaceSymbol, astInterface.getInterfaceList());

    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);
  }

  public void addInterfacesToType(final CDTypeSymbol typeSymbol, final List<ASTMCObjectType> astInterfaces) {
    if (astInterfaces != null) {
      for (final ASTMCObjectType superInterface : astInterfaces) {
        final CDTypeSymbolSurrogate superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
        typeSymbol.getCdInterfacesList().add(superInterfaceSymbol);
      }

    }
  }

  CDTypeSymbolSurrogate createCDTypeSymbolFromReference(final ASTMCObjectType astmcObjectType) {
    String typeName = astmcObjectType.printType(new MCCollectionTypesFullPrettyPrinter(new IndentPrinter()));
    final CDTypeSymbolSurrogate typeReference;
    Optional<CDTypeSymbol> typeSymbol =((ITestCD4AnalysisScope) astmcObjectType.getEnclosingScope()).resolveCDType(typeName);
    if (typeSymbol.isPresent()) {
      typeReference = new CDTypeSymbolSurrogate(typeSymbol.get().getFullName());
    } else {
      typeReference = new CDTypeSymbolSurrogate(typeName);
    }
    typeReference.setEnclosingScope(scopeStack.peekLast());
    return typeReference;
  }

  @Override
  public void endVisit(ASTCDClass astClass){
    CDTypeSymbol symbol = astClass.getSymbol();
    symbol.setIsClass(true);

    if (astClass.isPresentSuperclass()) {
      ASTMCObjectType superC = astClass.getSuperclass();
      final CDTypeSymbolSurrogate superClassSymbol = createCDTypeSymbolFromReference(superC);
      symbol.setSuperClass(superClassSymbol);
    }

    addInterfacesToType(symbol, astClass.getInterfaceList());
  }

}
