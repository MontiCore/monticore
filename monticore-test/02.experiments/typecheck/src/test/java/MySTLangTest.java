/* (c) https://github.com/MontiCore/monticore */

import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mystlang.MySTLangMill;
import mystlang._symboltable.MySTLangSymbols2Json;
import mystlang._visitor.MySTLangTraverser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class MySTLangTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  /**
   * Test if a symbol table can be serialized & deserialized,
   * and if the enclosing symbols are present afterward.
   */
  @Test
  public void testMyLang() throws IOException {
    MySTLangMill.init();
    BasicSymbolsMill.initializePrimitives();

    var opt = MySTLangMill.parser().parse_String("class MyClass { public int f() }");

    Assertions.assertTrue(opt.isPresent());

    var s1 = MySTLangMill.scopesGenitorDelegator().createFromAST(opt.get());

    // Initialize TC3
    MySTLangTraverser traverser = MySTLangMill.traverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    // but be lazy and only for ExpressionBasis
    ExpressionBasisCTTIVisitor basicExpressions = new ExpressionBasisCTTIVisitor();
    basicExpressions.setType4Ast(type4Ast);
    basicExpressions.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(basicExpressions);
    traverser.setExpressionsBasisHandler(basicExpressions);

    new MapBasedTypeCheck3(traverser, type4Ast, ctx4Ast)
            .setThisAsDelegate();

    // set the return type of our function  - manually because its easier
    initType(opt.get().getMyFunction(0).getSymbol());


    // Serialize symbol table
    String symAsString = new MySTLangSymbols2Json().serialize(s1);

    // unload the previous ST
    MySTLangMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();

    // and load the previously deserialized symbol table
    var s2 = new MySTLangSymbols2Json().deserialize(symAsString);
    MySTLangMill.globalScope().addSubScope(s2);

    // Ensure the symbol can be resolved
    Assertions.assertTrue(s2.resolveFunction("MyClass.f").isPresent());
    // and its enclosing scope is present
    Assertions.assertNotNull(s2.resolveFunction("MyClass.f").get().getEnclosingScope());
    // as well as the next enclosing scope
    Assertions.assertEquals(s2, s2.resolveFunction("MyClass.f").get().getEnclosingScope().getEnclosingScope());
    // Finally, we check if the TC would be able to resolve the "int" type here (the enclosing scope must be present for that)
    Assertions.assertTrue(s2.resolveFunction("MyClass.f").get().getSpannedScope().resolveType("int").isPresent());
  }

  protected void initType(FunctionSymbol f) {
    f.setType(TypeCheck3.typeOf(MySTLangMill.nameExpressionBuilder().setName("int").build()));
  }
  
  
}
