// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.setexpressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.values.MIValue;
import de.monticore.interpreter.values.MIValueFactory;
import de.monticore.interpreter.values.MIValueObject;
import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetCollectionItem;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._ast.ASTSetValueItem;
import de.monticore.ocl.setexpressions._ast.ASTSetValueRange;
import de.monticore.ocl.setexpressions._ast.ASTSetVariableDeclaration;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsInheritanceHandler;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsBoolean;
import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsDouble;
import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsInt;

/**
 * Interpreter Visitor for SetExpressions
 */
public class SetExpressionsInterpreter
    extends SetExpressionsInheritanceHandler {

  protected InterpreterData iData;

  public SetExpressionsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  protected Stack<MIFrameLayoutForBasicSymbols> getScopeLayoutStack() {
    return iData.getFrameLayoutStack();
  }

  @Override
  public void traverse(ASTSetValueItem node) {
    node.getExpression().accept(getTraverser());
  }

  /**
   * will create a calculation that returns a {@link PrimitiveIterator.OfInt}.
   *
   * @param node the value range
   */
  @Override
  public void traverse(ASTSetValueRange node) {
    SymTypeExpression lowerType = TypeCheck3.typeOf(node.getLowerBound());
    SymTypeExpression upperType = TypeCheck3.typeOf(node.getUpperBound());
    Optional<SymTypeExpression> rangeType =
        SymTypeRelations.leastUpperBound(lowerType, upperType);
    if (rangeType.isEmpty()
        || rangeType.get().isObscureType()
        || !rangeType.get().isPrimitive()
        || !rangeType.get().asPrimitive().isIntegralType()
    ) {
      throw new IllegalStateException(
          "0x57076 internal error:"
              + " Failed to get common integral type of SetValueRange."
      );
    }
    node.getLowerBound().accept(getTraverser());
    MICalculationInt lowerCalc = iData.popCalculation().asCalculationInt();
    node.getUpperBound().accept(getTraverser());
    MICalculationInt upperCalc = iData.popCalculation().asCalculationInt();

    iData.putCalculation((MICalculationValue) frame -> {
      final int lowerValue = lowerCalc.calculate(frame);
      final int upperValue = upperCalc.calculate(frame);
      // Using PrimitiveIterator.ofInt to avoid (un)boxing (in some cases)
      // int[] may be an alternative
      final PrimitiveIterator.OfInt values = IntStream.rangeClosed(lowerValue, upperValue).iterator();
      return new MIValueObject(values);
    });
  }

  @Override
  public void traverse(ASTSetEnumeration node) {
    SymTypeExpression exprType = TypeCheck3.typeOf(node);
    SymTypeExpression elemType = MCCollectionSymTypeRelations.getCollectionElementType(exprType);
    final String sourcePosAsString = node.get_SourcePositionStart().toString();
    final List<MICalculation> itemCalcs = new ArrayList<>();
    for (ASTSetCollectionItem setItem : node.getSetCollectionItemList()) {
      setItem.accept(getTraverser());
      itemCalcs.add(iData.popCalculation());
    }

    if (isStoredAsBoolean(elemType)) {
      final Supplier<Collection<Boolean>> collectionConstructor =
          node.isList() ? ArrayList::new : LinkedHashSet::new;
      final List<MICalculationBoolean> booleanCalcs = itemCalcs.stream()
          .map(MICalculation::asCalculationBoolean)
          .toList();
      iData.putCalculation((MICalculationValue) frame -> {
        final Collection<Boolean> result = collectionConstructor.get();
        for (MICalculationBoolean calc : booleanCalcs) {
          result.add(calc.calculate(frame));
        }
        return new MIValueObject(result);
      });
    }
    else if (isStoredAsInt(elemType)) {
      final Supplier<Collection<Integer>> collectionConstructor =
          node.isList() ? ArrayList::new : LinkedHashSet::new;
      iData.putCalculation((MICalculationValue) frame -> {
        final Collection<Integer> result = collectionConstructor.get();
        for (MICalculation calc : itemCalcs) {
          if (calc.isCalculationInt()) {
            result.add(calc.asCalculationInt().calculate(frame));
          }
          else {
            final MIValue value = calc.asCalculationValue().calculate(frame);
            if (value.asNativeObject() instanceof PrimitiveIterator.OfInt iteratorOfInt) {
              while (iteratorOfInt.hasNext()) {
                // this boxes anyway,
                // but we can use the PrimitiveIterator
                // to avoid type compatibility checks.
                // Iterator being faster than temporary collection (assumed)
                result.add(iteratorOfInt.nextInt());
              }
            }
            else {
              throw new IllegalStateException(
                  "0xF1006 internal error: "
                      + "Encountered unexpected value type " + value.printType()
                      + " during interpretation of ASTSetEnumeration."
                      + " " + sourcePosAsString
              );
            }
          }
        }
        return new MIValueObject(result);
      });
    }
    else if (isStoredAsDouble(elemType)) {
      final Supplier<Collection<Double>> collectionConstructor =
          node.isList() ? ArrayList::new : LinkedHashSet::new;
      iData.putCalculation((MICalculationValue) frame -> {
        final Collection<Double> result = collectionConstructor.get();
        for (MICalculation calc : itemCalcs) {
          if (calc.isCalculationDouble() || calc.isCalculationInt()) {
            result.add(calc.asCalculationDouble().calculate(frame));
          }
          else {
            final MIValue value = calc.asCalculationValue().calculate(frame);
            if (value instanceof PrimitiveIterator.OfInt iteratorOfInt) {
              while (iteratorOfInt.hasNext()) {
                // using PrimitiveIterator to avoid unboxing of Integer
                result.add((double) iteratorOfInt.nextInt());
              }
            }
            else {
              throw new IllegalStateException(
                  "0xF1007 internal error: "
                      + "Encountered unexpected value type " + value.printType()
                      + " during interpretation of ASTSetEnumeration."
                      + " " + sourcePosAsString
              );
            }
          }
        }
        return new MIValueObject(result);
      });
    }
    else {
      final Supplier<Collection<Object>> collectionConstructor =
          node.isList() ? ArrayList::new : LinkedHashSet::new;
      final List<MICalculationValue> valueCalcs = itemCalcs.stream()
          .map(MICalculation::asCalculationValue)
          .toList();
      iData.putCalculation((MICalculationValue) frame -> {
        final Collection<Object> result = collectionConstructor.get();
        for (MICalculationValue calc : valueCalcs) {
          result.add(calc.calculate(frame).asNativeObject());
        }
        return new MIValueObject(result);
      });
    }
  }

  @Override
  public void traverse(ASTGeneratorDeclaration node) {
    String errorMsg = "0x57080 ASTGeneratorDeclaration should not be evaluated directly.";
    throw new IllegalCallerException(errorMsg);
  }

  @Override
  public void traverse(ASTSetComprehension node) {
    // create a new scope layout
    // this has to be done prior to creating the MICalculations
    final MIFrameLayoutForBasicSymbols frameLayout =
        getScopeLayoutStack().push(
            getScopeLayoutStack().isEmpty() ?
                new MIFrameLayoutForBasicSymbols() :
                new MIFrameLayoutForBasicSymbols(getScopeLayoutStack().peek())
        );
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      VariableSymbol varSym = null;
      if (item.isPresentGeneratorDeclaration()) {
        varSym = item.getGeneratorDeclaration().getSymbol();
      }
      else if (item.isPresentSetVariableDeclaration()) {
        item.getSetVariableDeclaration().getSymbol();
      }
      if (varSym != null) {
        frameLayout.declareVariable(varSym);
      }
    }

    node.getLeft().accept(getTraverser());
    final MICalculation leftCalc = iData.popCalculation();
    final List<SetComprehensionItemCalculation> itemCalcs = new ArrayList<>();
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (item.isPresentGeneratorDeclaration()) {
        itemCalcs.add(getCalculationOfGenerator(item.getGeneratorDeclaration(), frameLayout));
      }
      else if (item.isPresentSetVariableDeclaration()) {
        itemCalcs.add(getCalculationOfVariableDeclaration(item.getSetVariableDeclaration(), frameLayout));
      }
      else {
        itemCalcs.add(getCalculationOfFilter(item.getExpression(), frameLayout));
      }
    }

    final Supplier<Collection<Object>> collectionConstructor =
        node.isList() ? ArrayList::new : LinkedHashSet::new;

    iData.putCalculation((MICalculationValue) outerFrame -> {
      final MIFrame comprehensionFrame = new MIFrame(frameLayout, outerFrame);
      final Collection<Object> collection = collectionConstructor.get();
      MICalculationVoid comprehensionEvaluator = f -> collection.add(
          leftCalc.asCalculationValue().calculate(f).asNativeObject()
      );
      // calculations are build up from right to left,
      // to be executed from left to right
      for (int i = itemCalcs.size() - 1; i >= 0; i--) {
        final SetComprehensionItemCalculation item = itemCalcs.get(i);
        final MICalculationVoid tmpCalc = comprehensionEvaluator;
        comprehensionEvaluator = frame -> item.calculate(frame, tmpCalc);
      }
      comprehensionEvaluator.calculate(comprehensionFrame);
      return new MIValueObject(collection);
    });
    getScopeLayoutStack().pop();
  }

  /**
   * used to chain SetComprehensionItems together
   */
  @FunctionalInterface
  protected interface SetComprehensionItemCalculation {
    void calculate(MIFrame frame, MICalculationVoid innerCalculation);
  }

  protected SetComprehensionItemCalculation getCalculationOfGenerator(
      ASTGeneratorDeclaration generator,
      MIFrameLayoutForBasicSymbols frameLayout
  ) {
    Preconditions.checkNotNull(generator);
    Preconditions.checkNotNull(generator.getSymbol());
    MISetter relativeSetter =
        frameLayout.getVariableSetter(generator.getSymbol());
    generator.getExpression().accept(getTraverser());
    final MICalculationValue genExprCalc =
        iData.popCalculation().asCalculationValue();

    return (frame, innerCalc) -> {
      final MIValueObject collectionValue = (MIValueObject) genExprCalc.calculate(frame);
      final Collection<Object> collection = collectionValue.unsafeCast();
      for (Object elem : collection) {
        final MIValue elemValue = MIValueFactory.createMIValueOfNativeObject(elem);
        relativeSetter.set(frame, elemValue);
        innerCalc.calculate(frame);
      }
    };
  }

  protected SetComprehensionItemCalculation getCalculationOfFilter(
      ASTExpression expr,
      MIFrameLayoutForBasicSymbols frameLayout
  ) {
    Preconditions.checkNotNull(expr);
    expr.accept(getTraverser());
    final MICalculationBoolean filterCalc =
        iData.popCalculation().asCalculationBoolean();
    return (frame, innerCalc) -> {
      if (filterCalc.calculate(frame)) {
        innerCalc.calculate(frame);
      }
    };
  }

  protected SetComprehensionItemCalculation getCalculationOfVariableDeclaration(
      ASTSetVariableDeclaration varDecl,
      MIFrameLayoutForBasicSymbols frameLayout
  ) {
    Preconditions.checkNotNull(varDecl);
    Preconditions.checkNotNull(varDecl.getSymbol());
    Preconditions.checkNotNull(varDecl.getSymbol().getType());
    varDecl.getExpression().accept(getTraverser());
    final MICalculation exprCalc = iData.popCalculation();
    final MICalculationVoid calcAndStore = frameLayout.getCalcAndStore(
        varDecl.getSymbol(),
        exprCalc
    );
    return (frame, innerCalc) -> {
      calcAndStore.calculate(frame);
      innerCalc.calculate(frame);
    };
  }

}
