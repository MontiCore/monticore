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
  
  public SetExpressionsInterpreter(IModelInterpreter realThis) {
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
      Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
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
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }
  
  @Override
  public MIValue interpret(ASTSetVariableDeclaration node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isFlowControlSignal()) return value;
    
    // should already be declared at start of SetComprehension
    storeVariable(node.getSymbol(), value);
    return new VoidMIValue();
  }
  
  private MIValue evaluateSetComprehensionItems(ASTSetComprehension node, int idx, Collection<Object> results) {
    if (idx >= node.getSetComprehensionItemList().size()) {
      MIValue result = node.getLeft().evaluate(getRealThis());
      if (result.isFlowControlSignal()) return result;
      results.add(InterpreterUtils.valueToObject(result));
      return new VoidMIValue();
    }
    ASTSetComprehensionItem item = node.getSetComprehensionItemList().get(idx);
    if (item.isPresentGeneratorDeclaration()) {
      ASTGeneratorDeclaration generatorDeclaration = item.getGeneratorDeclaration();
      MIValue collection = generatorDeclaration.getExpression().evaluate(getRealThis());
      if (collection.isFlowControlSignal()) return collection;
      
      Collection<Object> values = (Collection<Object>)collection.asObject();
      for (Object obj : values) {
        storeVariable(generatorDeclaration.getSymbol(),
            InterpreterUtils.objectToValue(obj));
        MIValue result = evaluateSetComprehensionItems(node, idx + 1, results);
        if (result.isFlowControlSignal()) return result;
      }
      return MIValueFactory.createValue(results);
      
    } else if (item.isPresentExpression()) {
      MIValue filterValue = item.getExpression().evaluate(getRealThis());
      if (filterValue.isFlowControlSignal()) return filterValue;
      if (!filterValue.isBoolean()) {
        String errorMsg = "0x57078 SetComprehensionItem of type Expression should return a Boolean. Got "
            + filterValue.printType() + " (" + filterValue.printValue() + ").";
        Log.error(errorMsg, item.getExpression().get_SourcePositionStart(), item.getExpression().get_SourcePositionEnd());
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
    
    String errorMsg = "0x57079 Encountered unexpected type of SetComprehensionItem.";
    Log.error(errorMsg, item.get_SourcePositionStart(), item.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
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
    pushScope(scope);
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (item.isPresentSetVariableDeclaration()) {
        ASTSetVariableDeclaration variableDeclaration = item.getSetVariableDeclaration();
        declareVariable(variableDeclaration.getSymbol(), Optional.empty());
      } else if (item.isPresentGeneratorDeclaration()) {
        ASTGeneratorDeclaration generatorDeclaration = item.getGeneratorDeclaration();
        declareVariable(generatorDeclaration.getSymbol(), Optional.empty());
      }
    }
    MIValue result = evaluateSetComprehensionItems(node, 0, results);
    popScope();
    if (result.isFlowControlSignal()) return result;
    return MIValueFactory.createValue(results);
  }
  
  @Override
  public MIValue interpret(ASTSetComprehensionItem node) {
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    } else if (node.isPresentSetVariableDeclaration()) {
      ASTSetVariableDeclaration variableDeclaration = node.getSetVariableDeclaration();
      MIValue value = variableDeclaration.getExpression().evaluate(getRealThis());
      if (value.isFlowControlSignal()) return value;
      getRealThis().declareVariable(variableDeclaration.getSymbol(), Optional.of(value));
      return new VoidMIValue();
    }
    
    String errorMsg = "0x57077 Unexpected type of ASTSetComprehensionItem";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }
}
