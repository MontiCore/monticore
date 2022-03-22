/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageTraverser;

import java.util.Optional;

public class SynthesizeSymTypeFromMyOwnLanguage implements ISynthesize {

  protected SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  protected SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes;
  protected SynthesizeSymTypeFromUnitTypes symTypeFromUnitTypes;
  protected TypeCheckResult typeCheckResult = new TypeCheckResult();
  private MyOwnLanguageTraverser traverser;

  public SynthesizeSymTypeFromMyOwnLanguage(){
    init();
  }

  public MyOwnLanguageTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MyOwnLanguageTraverser traverser) {
    this.traverser = traverser;
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

  public void init() {
    typeCheckResult = new TypeCheckResult();
    traverser = MyOwnLanguageMill.traverser();

    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes();
    symTypeFromUnitTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4UnitTypes(symTypeFromUnitTypes);
    traverser.setUnitTypesHandler(symTypeFromUnitTypes);

    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCollectionTypes(symTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(symTypeFromMCCollectionTypes);

    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
  }
}
