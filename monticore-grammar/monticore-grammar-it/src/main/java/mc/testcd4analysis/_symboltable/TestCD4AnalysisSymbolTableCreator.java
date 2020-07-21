/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.Names;
import mc.testcd4analysis._ast.*;

import java.util.*;

public class TestCD4AnalysisSymbolTableCreator extends TestCD4AnalysisSymbolTableCreatorTOP {

  public TestCD4AnalysisSymbolTableCreator(ITestCD4AnalysisScope enclosingScope) {
    super(enclosingScope);
  }

  public TestCD4AnalysisSymbolTableCreator(Deque<? extends ITestCD4AnalysisScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public TestCD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    TestCD4AnalysisArtifactScope artifactScope = new TestCD4AnalysisArtifactScope(Optional.empty(),
        Names.getQualifiedName(rootNode.getPackageList()), new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void initialize_CDAttribute(CDFieldSymbol fieldSymbol, ASTCDAttribute astAttribute) {
    ASTMCType astType = astAttribute.getMCType();
    final String typeName;
    if (astType instanceof ASTMCGenericType) {
      typeName = ((ASTMCGenericType) astType).printWithoutTypeArguments();
    } else if (astType instanceof ASTMCArrayType) {
      typeName = ((ASTMCArrayType) astType).printTypeWithoutBrackets();
    } else {
      typeName = astAttribute.getMCType().printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()));
    }

    final CDTypeSymbolSurrogate typeReference = new CDTypeSymbolSurrogate(typeName);
    typeReference.setEnclosingScope(getCurrentScope().get());
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
  public void initialize_CDMethod(CDMethOrConstrSymbol methodSymbol, ASTCDMethod astMethod) {
    setReturnTypeOfMethod(methodSymbol, astMethod);
    if (!astMethod.getCDParametersList().isEmpty()) {
      if (astMethod.getCDParameters(astMethod.getCDParametersList().size() - 1).isEllipsis()) {
        methodSymbol.setIsEllipsis(true);
      }
    }
  }

  public void setReturnTypeOfMethod(final CDMethOrConstrSymbol methodSymbol, ASTCDMethod astMethod) {
    final CDTypeSymbolSurrogate returnSymbol = new CDTypeSymbolSurrogate(
        ( astMethod.getMCReturnType().printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()))));
    returnSymbol.setEnclosingScope(getCurrentScope().get());
    methodSymbol.setReturnType(returnSymbol);
  }

  @Override
  public void initialize_CDInterface(CDTypeSymbol interfaceSymbol, ASTCDInterface astInterface) {
    interfaceSymbol.setIsInterface(true);
    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);

    addInterfacesToType(interfaceSymbol, astInterface.getInterfaceList());

    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);
  }

  @Override
  public void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    symbol.setIsClass(true);

    if (ast.isPresentSuperclass()) {
      ASTMCObjectType superC = ast.getSuperclass();
      final CDTypeSymbolSurrogate superClassSymbol = createCDTypeSymbolFromReference(superC);
      symbol.setSuperClass(superClassSymbol);
    }

    addInterfacesToType(symbol, ast.getInterfaceList());
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
    CDTypeSymbolSurrogate surrogate =  new CDTypeSymbolSurrogate(
        astmcObjectType.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())));
    surrogate.setEnclosingScope(getCurrentScope().get());
    return surrogate;
  }
}
