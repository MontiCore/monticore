package de.monticore.ocl.setexpressions._visitor;

import de.monticore.interpreter.*;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class SetExpressionsInterpreter extends SetExpressionsInterpreterTOP {
  
  public SetExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  public SetExpressionsInterpreter() {
    super();
  }
  
  @Override
  public MIValue interpret(ASTSetValueItem node) {
    return node.getExpression().evaluate(getRealThis());
  }
  
  @Override
  public MIValue interpret(ASTSetValueRange node) {
    MIValue lowerValue = node.getLowerBound().evaluate(getRealThis());
    MIValue upperValue = node.getUpperBound().evaluate(getRealThis());
    SymTypeExpression lowerType = TypeCheck3.typeOf(node.getLowerBound());
    SymTypeExpression upperType = TypeCheck3.typeOf(node.getUpperBound());
    Optional<SymTypeExpression> rangeType = SymTypeRelations.leastUpperBound(lowerType, upperType);
    if (rangeType.isEmpty() || rangeType.get().isObscureType()
        || !rangeType.get().isPrimitive() || !rangeType.get().asPrimitive().isIntegralType()) {
      String errorMsg = "0x57076 Failed to get common type of SetValueRange.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    List<Object> values = new ArrayList<>();
    for (long l = lowerValue.asLong(); l <= upperValue.asLong(); l++) {
      values.add(InterpreterUtils.convertToPrimitiveExplicit(BasicSymbolsMill.LONG,
          rangeType.get().asPrimitive().getPrimitiveName(), MIValueFactory.createValue(l)));
    }
    
    return MIValueFactory.createValue(values);
  }
  
  @Override
  public MIValue interpret(ASTSetEnumeration node) {
    Collection<Object> result;
    if (node.isList()) {
      result = new ArrayList<>();
    } else {
      result = new HashSet<>();
    }
    
    for (ASTSetCollectionItem setItem : node.getSetCollectionItemList()) {
      MIValue element = setItem.evaluate(getRealThis());
      if (setItem instanceof ASTSetValueItem) {
        result.add(InterpreterUtils.valueToObject(element));
      } else if (setItem instanceof ASTSetValueRange){
        for (MIValue value : (Collection<MIValue>)element.asObject()) {
          result.add(InterpreterUtils.valueToObject(value));
        }
      }
    }
    return MIValueFactory.createValue(result);
  }
  
  @Override
  public MIValue interpret(ASTGeneratorDeclaration node) {
    String errorMsg = "0x57080 ASTGeneratorDeclaration should not be evaluated directly.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  @Override
  public MIValue interpret(ASTSetVariableDeclaration node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isError()) return value;
    
    // should already be declared at start of SetComprehension
    getRealThis().storeVariable(node.getSymbol(), value);
    return new VoidMIValue();
  }
  
  private MIValue evaluateSetComprehensionItems(ASTSetComprehension node, int idx, Collection<Object> results) {
    if (idx >= node.getSetComprehensionItemList().size()) {
      MIValue result = node.getLeft().evaluate(getRealThis());
      if (result.isError()) return result;
      results.add(InterpreterUtils.valueToObject(result));
      return new VoidMIValue();
    }
    ASTSetComprehensionItem item = node.getSetComprehensionItemList().get(idx);
    if (item.isPresentGeneratorDeclaration()) {
      ASTGeneratorDeclaration generatorDeclaration = item.getGeneratorDeclaration();
      MIValue collection = generatorDeclaration.getExpression().evaluate(getRealThis());
      if (collection.isError()) return collection;
      
      Collection<Object> values = (Collection<Object>)collection.asObject();
      for (Object obj : values) {
        getRealThis().storeVariable(generatorDeclaration.getSymbol(),
            InterpreterUtils.objectToValue(obj));
        MIValue result = evaluateSetComprehensionItems(node, idx + 1, results);
        if (result.isError()) return result;
      }
      return MIValueFactory.createValue(results);
      
    } else if (item.isPresentExpression()) {
      MIValue filterValue = item.getExpression().evaluate(getRealThis());
      if (filterValue.isError()) return filterValue;
      if (!filterValue.isBoolean()) {
        String errorMsg = "0x57078 SetComprehensionItem of type Expression should return a Boolean. Got "
            + filterValue.printType() + " (" + filterValue.printValue() + ").";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }
      if (!filterValue.asBoolean()) {
        return new VoidMIValue();
      }
      return evaluateSetComprehensionItems(node, idx+1, results);
      
    } else if (item.isPresentSetVariableDeclaration()) {
      item.getSetVariableDeclaration().evaluate(getRealThis());
      return evaluateSetComprehensionItems(node, idx+1, results);
    }
    return new ErrorMIValue("0x57079 Encountered unexpected type of SetComprehensionItem.");
  }
  
  @Override
  public MIValue interpret(ASTSetComprehension node) {
    Collection<Object> results;
    if (node.isList()) {
      results = new ArrayList<>();
    } else {
      results = new HashSet<>();
    }
    
    MIScope scope = new MIScope(getRealThis().getCurrentScope());
    getRealThis().pushScope(scope);
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (item.isPresentSetVariableDeclaration()) {
        ASTSetVariableDeclaration variableDeclaration = item.getSetVariableDeclaration();
        getRealThis().declareVariable(variableDeclaration.getSymbol(), null);
      } else if (item.isPresentGeneratorDeclaration()) {
        ASTGeneratorDeclaration generatorDeclaration = item.getGeneratorDeclaration();
        getRealThis().declareVariable(generatorDeclaration.getSymbol(), null);
      }
    }
    MIValue result = evaluateSetComprehensionItems(node, 0, results);
    getRealThis().popScope();
    if (result.isError()) return result;
    return MIValueFactory.createValue(results);
  }
  
  @Override
  public MIValue interpret(ASTSetComprehensionItem node) {
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    } else if (node.isPresentSetVariableDeclaration()) {
      ASTSetVariableDeclaration variableDeclaration = node.getSetVariableDeclaration();
      MIValue value = variableDeclaration.getExpression().evaluate(getRealThis());
      if (value.isError()) return value;
      getRealThis().declareVariable(variableDeclaration.getSymbol(), value);
      return new VoidMIValue();
    }
    
    String errorMsg = "0x57077 Unexpected type of ASTSetComprehensionItem";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
}
