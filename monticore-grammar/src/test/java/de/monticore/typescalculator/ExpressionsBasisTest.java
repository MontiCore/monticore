package de.monticore.typescalculator;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisLanguage;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testcommonexpressions._parser.TestCommonExpressionsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.util.ArrayList;

public class ExpressionsBasisTest {

  private ExpressionsBasisScope scope;

  private LiteralTypeCalculator literalsVisitor;

  private ArtifactScope artifactScope;

  @Before
  public void setup(){
    ExpressionsBasisLanguage expressionsBasisLanguage=new ExpressionsBasisLanguage("CommonExpressions","exp") {
      @Override
      public MCConcreteParser getParser() {
        return new TestCommonExpressionsParser();
      }
    };
    Log.enableFailQuick(false);

    this.scope=new ExpressionsBasisScope();
    this.artifactScope=new ArtifactScope("",new ArrayList<>());

    this.literalsVisitor=new CommonLiteralsTypesCalculator();

    EVariableSymbol symbol = new EVariableSymbol("varInt");
    MCTypeSymbol typeSymbol = new MCTypeSymbol("int");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varDouble");
    typeSymbol = new MCTypeSymbol("double");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    ExpressionsBasisScope ascope = new ExpressionsBasisScope();
    scope.addSubScope(ascope);
    ascope.setName("A");
    ExpressionsBasisScope bscope = new ExpressionsBasisScope();
    bscope.setName("B");
    ascope.addSubScope(bscope);
    ExpressionsBasisScope cscope = new ExpressionsBasisScope();
    cscope.setName("C");
    bscope.addSubScope(cscope);


    symbol = new EVariableSymbol("QName");
    ArrayList<String> name=new ArrayList<>();
    typeSymbol= new MCTypeSymbol("QName");
    ASTMCType type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    cscope.add(symbol);
  }

//  @Test
//  public void nameTest() throws IOException {
//    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
//    calc.setLiteralsVisitor(literalsVisitor);
//    calc.setScope(scope);
//    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
//    Optional<ASTExpression> o = p.parse_StringExpression("varInt");
//
//    assertTrue(o.isPresent());
//    o.get().accept(calc);
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
//
//  }
//
//  @Test
//  public void qualifiedNameTest() throws IOException{
//    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
//    calc.setLiteralsVisitor(literalsVisitor);
//    calc.setScope(scope);
//    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
//    Optional<ASTExpression> o = p.parse_StringExpression("A.B.C.QName");
//
//
//    assertTrue(o.isPresent());
//    o.get().accept(calc);
//    List<String> nameList = new ArrayList<>();
//    nameList.add("A");
//    nameList.add("B");
//    nameList.add("C");
//    nameList.add("QName");
//    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.calculateType(o.get())));
//
//  }
//
//  @Test
//  public void literalTest() throws IOException{
//    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
//    calc.setLiteralsVisitor(literalsVisitor);
//    calc.setScope(scope);
//    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
//    Optional<ASTExpression> o = p.parse_StringExpression("3");
//
//    assertTrue(o.isPresent());
//    o.get().accept(calc);
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
//
//
//    Optional<ASTExpression> q = p.parse_StringExpression("true");
//
//    assertTrue(q.isPresent());
//    q.get().accept(calc);
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));
//
//
//    Optional<ASTExpression> r = p.parse_StringExpression("4.5");
//    assertTrue(r.isPresent());
//    r.get().accept(calc);
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));
//
//    List<String> nameList = new ArrayList<>();
//    nameList.add("String");
//
//    Optional<ASTExpression> s = p.parse_StringExpression("\"Hallo\"");
//    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.getResult()));
//    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.calculateType(s.get())));
//
//  }
}
