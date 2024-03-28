// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.library;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

/**
 * Adds symbols for streams
 */
public class StreamType {

  protected static final String STREAM_TYPE_NAME = "Stream";

  public void addStreamType() {
    TypeVarSymbol typeVarSymbol = BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build();
    IBasicSymbolsScope spannedScope = BasicSymbolsMill.scope();
    spannedScope.setName("");

    TypeSymbol typeSymbol = BasicSymbolsMill.typeSymbolBuilder()
        .setName(STREAM_TYPE_NAME)
        .setEnclosingScope(BasicSymbolsMill.globalScope())
        .setSpannedScope(spannedScope)
        .build();
    typeSymbol.addTypeVarSymbol(typeVarSymbol);

    BasicSymbolsMill.globalScope().add(typeSymbol);
    BasicSymbolsMill.globalScope().addSubScope(spannedScope);
    addStreamFunctions(typeSymbol, BasicSymbolsMill.globalScope());
  }

  protected void addStreamFunctions(TypeSymbol streamSymbol, IBasicSymbolsScope enclosingScope) {
    addFunctionEmpty(streamSymbol, enclosingScope);
    addFunctionAppendFirst(streamSymbol, enclosingScope);
    addFunctionConc(streamSymbol, enclosingScope);
    addFunctionLen(streamSymbol, enclosingScope);
    addFunctionFirst(streamSymbol, enclosingScope);
    addFunctionDropFirst(streamSymbol, enclosingScope);
    addFunctionNth(streamSymbol, enclosingScope);
    addFunctionTake(streamSymbol, enclosingScope);
    addFunctionDrop(streamSymbol, enclosingScope);
    addFunctionTimesElement(streamSymbol, enclosingScope);
    addFunctionTimesStream(streamSymbol, enclosingScope);
    addFunctionMap(streamSymbol, enclosingScope);
    addFunctionIterate(streamSymbol, enclosingScope);
    addFunctionFilter(streamSymbol, enclosingScope);
    addFunctionTakeWhile(streamSymbol, enclosingScope);
    addFunctionDropWhile(streamSymbol, enclosingScope);
    addFunctionRcDups(streamSymbol, enclosingScope);
  }

  // Functions

  protected void addFunctionEmpty(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("emptyStream", enclosingScope);
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionAppendFirst(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("appendFirst", enclosingScope);
    addParameter(function, "first", createTypeVarExpression("T"));
    addParameter(function, "tail", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionConc(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    // to discuss: conc(streamOfInts, streamOfDoubles) allowed and returning a stream of doubles?
    FunctionSymbol function = createFunction("conc", enclosingScope);
    addParameter(function, "head", createStreamTypeExpression(streamType, "T"));
    addParameter(function, "tail", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionLen(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("len", enclosingScope);
    // len must not require a type for T, such that "len(<>)" may be called
    addParameter(function, "list", SymTypeExpressionFactory.createGenerics(
        BasicSymbolsMill.typeSymbolBuilder().setName(STREAM_TYPE_NAME).build()
    ));
    function.setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
  }

  protected void addFunctionFirst(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("first", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createTypeVarExpression("T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionDropFirst(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("dropFirst", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionNth(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("nth", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    addParameter(function, "n", SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
    function.setType(createTypeVarExpression("T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionTake(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("take", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    addParameter(function, "n", SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionDrop(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("drop", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    addParameter(function, "n", SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionTimesElement(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("times", enclosingScope);
    addParameter(function, "elem", createTypeVarExpression("T"));
    addParameter(function, "n", SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionTimesStream(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("times", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    addParameter(function, "n", SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionMap(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("map", enclosingScope);
    addParameter(function, "function", SymTypeExpressionFactory.createFunction(
        createTypeVarExpression("U"),
        createTypeVarExpression("T")
    ));
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "U"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("U").build());
  }

  protected void addFunctionIterate(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("iterate", enclosingScope);
    addParameter(function, "function", SymTypeExpressionFactory.createFunction(
        createTypeVarExpression("T"),
        createTypeVarExpression("T")
    ));
    addParameter(function, "elem", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionFilter(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("filter", enclosingScope);
    addParameter(function, "predicate", SymTypeExpressionFactory.createFunction(
        createTypeVarExpression("T"),
        createTypeVarExpression("Bool")
    ));
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionTakeWhile(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("takeWhile", enclosingScope);
    addParameter(function, "predicate", SymTypeExpressionFactory.createFunction(
        createTypeVarExpression("T"),
        createTypeVarExpression("Bool")
    ));
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionDropWhile(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("dropWhile", enclosingScope);
    addParameter(function, "predicate", SymTypeExpressionFactory.createFunction(
        createTypeVarExpression("T"),
        createTypeVarExpression("Bool")
    ));
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  protected void addFunctionRcDups(TypeSymbol streamType, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = createFunction("rcDups", enclosingScope);
    addParameter(function, "list", createStreamTypeExpression(streamType, "T"));
    function.setType(createStreamTypeExpression(streamType, "T"));
    function.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder().setName("T").build());
  }

  // Helper

  protected SymTypeExpression createTypeVarExpression(String name) {
    return SymTypeExpressionFactory.createTypeVariable(
        BasicSymbolsMill.typeVarSymbolBuilder().setName(name).build());
  }

  protected SymTypeExpression createStreamTypeExpression(TypeSymbol streamType, String name) {
    return SymTypeExpressionFactory.createGenerics(
        streamType,
        createTypeVarExpression(name)
    );
  }

  protected FunctionSymbol createFunction(String name, IBasicSymbolsScope enclosingScope) {
    FunctionSymbol function = BasicSymbolsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(enclosingScope)
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
    enclosingScope.add(function);
    return function;
  }

  protected void addParameter(FunctionSymbol function, String name, SymTypeExpression type) {
    function.getSpannedScope().add(
        BasicSymbolsMill.variableSymbolBuilder()
            .setName(name)
            .setEnclosingScope(function.getSpannedScope())
            .setType(type)
            .build()
    );
  }

}
