/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createWildcard;
import static de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory.createList;
import static de.monticore.types3.util.DefsTypesForTests._carSymType;
import static de.monticore.types3.util.DefsTypesForTests._linkedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._personSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedString;
import static de.monticore.types3.util.DefsTypesForTests._voidSymType;
import static de.monticore.types3.util.DefsTypesForTests.function;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.type;
import static de.monticore.types3.util.DefsTypesForTests.typeVariable;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static de.monticore.types3.util.DefsVariablesForTests._carVarSym;
import static de.monticore.types3.util.DefsVariablesForTests._personVarSym;

public class CallGenericFunctionsTest
    extends AbstractTypeVisitorTest {

  @BeforeEach
  public void init() {
    setupValues();
    setupGenericFunctions();
    SymTypeRelations.init();
  }

  protected void setupGenericFunctions() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    // <E> E getTarget();
    TypeVarSymbol getTargetVar = typeVariable("E");
    FunctionSymbol getTargetSym = inScope(gs, function(
        "getTarget",
        createTypeVariable(getTargetVar)
    ));
    getTargetSym.getSpannedScope().add(getTargetVar);

    // <E> List<E> getTargetList();
    TypeVarSymbol getTargetListVar = typeVariable("E");
    FunctionSymbol getTargetListSym = inScope(gs, function(
        "getTargetList", createList(
            createTypeVariable(getTargetListVar)
        )
    ));
    getTargetListSym.getSpannedScope().add(getTargetListVar);

    // <E> List<? extends E> getExtendsTargetList();
    TypeVarSymbol getExtendsTargetListVar = typeVariable("E");
    FunctionSymbol getExtendsTargetListSym = inScope(gs, function(
        "getExtendsTargetList", createList(
            createWildcard(true, createTypeVariable(getExtendsTargetListVar))
        )
    ));
    getExtendsTargetListSym.getSpannedScope().add(getExtendsTargetListVar);

    // <E> List<? super E> getSuperTargetList();
    TypeVarSymbol getSuperTargetListVar = typeVariable("E");
    FunctionSymbol getSuperTargetListSym = inScope(gs, function(
        "getSuperTargetList", createList(
            createWildcard(false, createTypeVariable(getSuperTargetListVar))
        )
    ));
    getSuperTargetListSym.getSpannedScope().add(getSuperTargetListVar);

    // List<?> getWildList();
    FunctionSymbol getWildListSym = inScope(gs, function(
        "getWildList", createList(createWildcard())
    ));

    // <E> LinkedList<E> getTargetLinkedList();
    TypeVarSymbol getTargetLinkedListVar = typeVariable("E");
    FunctionSymbol getTargetLinkedListSym = inScope(gs, function(
        "getTargetLinkedList", createGenerics(
            _linkedListSymType.getTypeInfo(),
            createTypeVariable(getTargetLinkedListVar)
        )
    ));
    getTargetLinkedListSym.getSpannedScope().add(getTargetLinkedListVar);

    // <T> List<T> asList(T.... a); // from Arrays::asList
    TypeVarSymbol asListVar = typeVariable("T");
    FunctionSymbol asListSym = inScope(gs, function(
        "asList", createList(createTypeVariable(asListVar)),
        List.of(createTypeVariable(asListVar)), true
    ));
    asListSym.getSpannedScope().add(asListVar);

    // <T> T id(T t);
    TypeVarSymbol idVar = typeVariable("T");
    FunctionSymbol idSym = inScope(gs, function(
        "id", createTypeVariable(idVar), createTypeVariable(idVar)
    ));
    idSym.getSpannedScope().add(idVar);

    // <T> () -> T idRet(T t);
    TypeVarSymbol idRetVar = typeVariable("T");
    FunctionSymbol idRetSym = inScope(gs, function(
        "idRet",
        createFunction(createTypeVariable(idRetVar)),
        createTypeVariable(idRetVar)
    ));
    idRetSym.getSpannedScope().add(idRetVar);
  }

  @Test
  public void deriveFromGenericsPerReturnType() throws IOException {
    checkExpr("getTarget", "() -> int", "() -> int");
    checkExpr("getTarget()", "int", "int");
    checkExpr("getTarget()", "List<int>", "List<int>");
    // note: this may seem unintuitive
    // (getting wildcards for getTarget but not for getTargetList)
    // but this aligns with the behavior of the Oracle JDK 11.0.12
    // reason is that the type variable in List<T> cannot be set to a wildcard
    checkExpr("getTarget()", "List<? extends int>", "List<? extends int>");
    checkExpr("getTarget()", "List<? super int>", "List<? super int>");
    checkExpr("getTargetList()", "List<int>", "List<int>");
    checkExpr("getTargetList()", "List<? extends int>", "List<int>");
    checkExpr("getTargetList()", "List<? super int>", "List<int>");
    checkExpr("getExtendsTargetList()", "List<? extends int>", "List<? extends int>");
    checkExpr("getSuperTargetList()", "List<? super int>", "List<? super int>");
    checkExpr("getTargetLinkedList()", "List<int>", "java.util.LinkedList<int>");
    checkExpr("getTargetLinkedList()", "List<? extends int>", "java.util.LinkedList<int>");
    checkExpr("getTargetLinkedList()", "List<? super int>", "java.util.LinkedList<int>");
    assertNoFindings();
  }

  @Test
  public void deriveFromGenericsPerReturnTypeInvalid() throws IOException {
    checkErrorExpr("getExtendsTargetList()", "List<int>", "0xFD451");
    checkErrorExpr("getExtendsTargetList()", "List<? super int>", "0xFD451");
    checkErrorExpr("getSuperTargetList()", "List<int>", "0xFD451");
    checkErrorExpr("getSuperTargetList()", "List<? extends int>", "0xFD451");
  }

  @Test
  public void deriveFromGenericsPerIndirectReturnType() throws IOException {
    checkExpr("id(varint)", "int", "int");
    checkExpr("id(varshort)", "int", "short");
    checkExpr("id(varint)", "int"); // ?
    // getTarget with 1 id
    checkExpr("id(getTarget)", "() -> int", "() -> int");
    checkExpr("id(getTarget())", "int", "int");
    checkExpr("id(getTarget)()", "int", "int");
    // getTarget with 2 ids
    checkExpr("id(id(getTarget))", "() -> int", "() -> int");
    checkExpr("id(id(getTarget()))", "int", "int");
    checkExpr("id(id(getTarget)())", "int", "int");
    checkExpr("id(id(getTarget))()", "int", "int");
    assertNoFindings();
  }

  @Test
  public void deriveFromGenericsPerIndirectReturnTypeInvalid() throws IOException {
    checkErrorExpr("id(varint)", "short", "0xFD451");
  }

  @Test
  public void deriveFromGenericsConditionalExprs() throws IOException {
    checkExpr("(varboolean ? getTarget : getTarget)",
        "() -> int", "() -> int"
    );
    checkExpr("(varboolean ? getTarget : getTarget)()",
        "int", "int"
    );
    checkExpr("(varboolean ? getTarget : getTargetList)",
        "() -> List<int>", "() -> List<int>"
    );
    checkExpr("id(varboolean ? id(getTarget) : getTargetList)()",
        "List<int>", "List<int>"
    );
    checkExpr("(varboolean ? getTargetLinkedList : getTargetList)()",
        "List<int>", "List<int>"
    );
  }

  /**
   * example from Java Spec 21 18.5.1
   */
  @Test
  public void deriveFromAsListJavaSpec21Example_18_5_1() throws IOException {
    // Integer/Double extends Number implements Comparable<Integer/Double>
    // However, we replace Integer and Double with Person and Car
    // to not rely on Java-esque subtyping for boxed primitives
    // (which is not our default behavior)
    IBasicSymbolsScope gs = BasicSymbolsMill.globalScope();
    TypeSymbol numberSym = inScope(gs, type("Number"));
    TypeVarSymbol comparableVar = typeVariable("T");
    TypeSymbol comparableSym =
        inScope(gs, type("Comparable", List.of(), List.of(comparableVar)));
    _personSymType.getTypeInfo().addSuperTypes(createTypeObject(numberSym));
    _personSymType.getTypeInfo().addSuperTypes(createGenerics(comparableSym, _personSymType));
    _carSymType.getTypeInfo().addSuperTypes(createTypeObject(numberSym));
    _carSymType.getTypeInfo().addSuperTypes(createGenerics(comparableSym, _carSymType));
    checkExpr("asList(" + _personVarSym.getFullName()
            + ", " + _carVarSym.getFullName() + ")",
        "List<Number>",
        "List<Number>"
    );
    // in Java, we would get the "partial instantiation"
    // (during the method applicability test)
    // "List<Number & Comparable<? extends Number & Comparable<?>>>"
    // here, as we support union types, we make use of them:
    checkExpr("asList(" + _personVarSym.getFullName()
            + ", " + _carVarSym.getFullName() + ")",
        "List<(Car | Person)>"
    );
  }

  /**
   * Based on the IBM article
   * "Java theory and practice: Going wild with generics, Part 1"
   * tests wildcard capture
   */
  @Test
  public void goingWildWithGenerics_Example() throws IOException {
    IBasicSymbolsScope gs = BasicSymbolsMill.globalScope();
    //public interface Box<T> {
    //  public T get();
    //  public void put(T element);
    //}
    TypeVarSymbol boxVarSym = typeVariable("T");
    TypeSymbol boxSym = inScope(gs, type(
        "Box", List.of(), List.of(boxVarSym)
    ));
    inScope(boxSym.getSpannedScope(), function(
        "get", createTypeVariable(boxVarSym)
    ));
    inScope(boxSym.getSpannedScope(), function(
        "put", _voidSymType, createTypeVariable(boxVarSym)
    ));
    // Box<?> box;
    inScope(gs, variable("box", createGenerics(boxSym, createWildcard())));
    // <V> void reboxHelper(Box<V> box) { box.put(box.get()); }
    TypeVarSymbol reboxHelperVarSym = typeVariable("V");
    FunctionSymbol reboxHelperSym = inScope(gs, function(
        "reboxHelper", _voidSymType,
        createGenerics(boxSym, createTypeVariable(reboxHelperVarSym))
    ));
    reboxHelperSym.getSpannedScope().add(reboxHelperVarSym);
    // <U> Box<U> make();
    TypeVarSymbol makeVarSym = typeVariable("U");
    FunctionSymbol makeSym = inScope(gs, function("make",
        createGenerics(boxSym, createTypeVariable(makeVarSym))
    ));
    makeSym.getSpannedScope().add(makeVarSym);
    // Box<String> strBox;
    inScope(gs, variable("strBox", createGenerics(boxSym, _unboxedString)));
    // in Java, this would be Object, not "?"
    // or a free type variable due to capture conversion
    checkExpr("box.get()", "?");
    checkErrorExpr("box.put(box.get())", "0xFD444");
    checkExpr("reboxHelper(box)", "void");
    checkExpr("strBox = make()", "Box<String>");
    checkExpr("box = make()", "Box<?>");
  }
}
