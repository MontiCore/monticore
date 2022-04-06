/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import mylang.DeriveFromMyLang;
import mylang.MyLangMill;
import mylang.SynthesizeFromMyLang;
import mylang._ast.ASTMyVar;
import mylang._parser.MyLangParser;
import org.junit.Test;


public class MyLangTest {
  
  @Test
  public void testMyLang() throws IOException {
    MyLangMill.init();
    BasicSymbolsMill.initializePrimitives();
    MyLangParser parser = MyLangMill.parser();
  
    Optional<ASTMyVar> varOpt = parser.parse_String("boolean x = 3 > 4");
    assertFalse(parser.hasErrors());
    assertTrue(varOpt.isPresent());
    
    MyLangMill.scopesGenitorDelegator().createFromAST(varOpt.get());
  
    TypeCalculator tc = new TypeCalculator(new SynthesizeFromMyLang(),
                                     new DeriveFromMyLang());
  
    ASTMyVar var = varOpt.get();
    ASTMCType type = var.getType();
    ASTExpression exp = var.getExp();
  
    // synthesize SymTypeExpression from type
    SymTypeExpression symType1 = tc.symTypeFromAST(type);
  
    // calculate SymTypeExpression for exp
    SymTypeExpression symType2 = tc.typeOf(exp);
    
    // check whether the type is boolean
    assertEquals("boolean",symType1.getTypeInfo().getName());
    assertTrue(TypeCheck.isBoolean(symType1));
    
    assertEquals("boolean",symType2.getTypeInfo().getName());
  
    assertTrue(TypeCheck.isBoolean(symType2));
    
    // check whether both types are compatible
    assertTrue(TypeCheck.compatible(symType1,symType2));
  
    // check whether the expression is of assignable type 
    assertTrue(tc.isOfTypeForAssign(symType1,exp));
    
  }
  
  
}
