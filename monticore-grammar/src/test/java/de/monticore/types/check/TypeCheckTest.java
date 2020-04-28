// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for {@link TypeCheck}
 */
public class TypeCheckTest {

  TypeCheck tc = new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator(TypeSymbolsSymTabMill.typeSymbolsScopeBuilder().build(), new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter())));
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  @Test
  public void testIsOfTypeForAssign() throws IOException {
    //primitives
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    ASTExpression int1 = p.parse_StringExpression("3").get();
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();

    assertTrue(tc.isOfTypeForAssign(tc.typeOf(bool1), bool2));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(double1), int1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(bool1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(float1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(long1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(char1), char1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(char1), int1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(double1), bool1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(long1), float1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(float1), int1));

    TypeSymbolsScope scope = TypeSymbolsSymTabMill.typeSymbolsScopeBuilder()
        .setName("Phantasy2")
        .setEnclosingScope(null)
        .setExportingSymbols(true)
        .setAstNode(null)
        .build();

    //a FirstSemesterStudent is a Student and a Student is a Person
    TypeSymbol person = DefsTypeBasic.type("Person");
    scope.add(person);
    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope))
    );
    scope.add(student);
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope))
    );

    add2scope(scope, person);
    add2scope(scope, student);
    add2scope(scope, firstsemesterstudent);
    DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(scope, new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    tc = new TypeCheck(null, derLit);

    //non-primitives
    ASTExpression pers = p.parse_StringExpression("Person").get();
    ASTExpression stud = p.parse_StringExpression("Student").get();
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();

    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), stud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), fstud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(stud), fstud));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(stud), pers));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(fstud), pers));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(fstud), stud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), pers));

    assertFalse(tc.isOfTypeForAssign(tc.typeOf(int1), pers));
  }

  @Test
  public void testIsSubtype() throws IOException {
    //primitives
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    ASTExpression int1 = p.parse_StringExpression("3").get();
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();


    assertFalse(isSubtypeOf(tc.typeOf(bool1), tc.typeOf(bool2)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(double1)));
    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(bool1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(long1)));
    assertFalse(isSubtypeOf(tc.typeOf(char1), tc.typeOf(char1)));
    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(char1)));
    assertFalse(isSubtypeOf(tc.typeOf(bool1), tc.typeOf(double1)));
    assertFalse(isSubtypeOf(tc.typeOf(float1), tc.typeOf(long1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));

    TypeSymbolsScope scope = TypeSymbolsSymTabMill.typeSymbolsScopeBuilder()
        .setName("Phantasy2")
        .setEnclosingScope(null)
        .setExportingSymbols(true)
        .setAstNode(null)
        .build();

    //a FirstSemesterStudent is a Student and a Student is a Person
    TypeSymbol person = DefsTypeBasic.type("Person");
    scope.add(person);
    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope))
    );
    scope.add(student);
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope))
    );

    add2scope(scope, person);
    add2scope(scope, student);
    add2scope(scope, firstsemesterstudent);
    DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(scope, new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    tc = new TypeCheck(null, derLit);

    //non-primitives
    ASTExpression pers = p.parse_StringExpression("Person").get();
    ASTExpression stud = p.parse_StringExpression("Student").get();
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();

    assertTrue(isSubtypeOf(tc.typeOf(stud), tc.typeOf(pers)));
    assertTrue(isSubtypeOf(tc.typeOf(fstud), tc.typeOf(pers)));
    assertTrue(isSubtypeOf(tc.typeOf(fstud), tc.typeOf(stud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(stud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(fstud)));
    assertFalse(isSubtypeOf(tc.typeOf(stud), tc.typeOf(fstud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(pers)));

    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(pers)));
  }
}
