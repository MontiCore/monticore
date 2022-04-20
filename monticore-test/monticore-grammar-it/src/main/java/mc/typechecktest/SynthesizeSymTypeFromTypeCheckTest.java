/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

public class SynthesizeSymTypeFromTypeCheckTest implements ISynthesize {

  protected TypeCheckTestTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public SynthesizeSymTypeFromTypeCheckTest(){
    init();
  }

  public void init() {
    traverser = TypeCheckTestMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCBasicTypes basicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    SynthesizeSymTypeFromMCCollectionTypes collectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    SynthesizeSymTypeFromMCSimpleGenericTypes simpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    basicTypes.setTypeCheckResult(typeCheckResult);
    collectionTypes.setTypeCheckResult(typeCheckResult);
    simpleGenericTypes.setTypeCheckResult(typeCheckResult);

    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
  }

  public TypeCheckTestTraverser getTraverser() {
    return traverser;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    init();
    qName.accept(traverser);
    return typeCheckResult.copy();
  }
}
