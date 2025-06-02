/* (c) https://github.com/MontiCore/monticore */
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.SymTypeObscure;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import systematicscopedeser.SystematicScopeDeSerMill;
import systematicscopedeser._symboltable.*;

/**
 * Test that the DeSer correctly handles inherited scope/symbols
 * as well as their subscopes
 */
public class SystematicDeSerTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    LogStub.clearFindings();

    SystematicScopeDeSerMill.init();
  }

  @Test
  public void test() {
    // Construct one of each symbol
    var compU = SystematicScopeDeSerMill.compilationUnitBuilder();

    // direct implementations of interface symbol scope MyScopeSymbolI
    var i1 = SystematicScopeDeSerMill.myScopeSymbolIImpl1Builder().setName("i1").build();
    compU.addMyScopeSymbolI(i1);
    var i2 = SystematicScopeDeSerMill.myScopeSymbolIImpl2Builder().setName("i2").build();
    compU.addMyScopeSymbolI(i2);
    var i3 = SystematicScopeDeSerMill.myScopeSymbolIImpl3Builder().setName("i3").build();
    compU.addMyScopeSymbolI(i3);

    // indirect implementations of interface MyScopeSymbolI2 extends MyScopeSymbolI
    var s1 = SystematicScopeDeSerMill.myScopeSymbolI2Impl1Builder().setName("s1").build();
    compU.addMyScopeSymbolI(s1);
    var s2 = SystematicScopeDeSerMill.myScopeSymbolI2Impl2Builder().setName("s2").build();
    compU.addMyScopeSymbolI(s2);
    var s3 = SystematicScopeDeSerMill.myScopeSymbolI2Impl3Builder().setName("s3").build();
    compU.addMyScopeSymbolI(s3);
    var m = SystematicScopeDeSerMill.testMethodBuilder().setName("m").build();
    compU.addMethod(m);
    var f = SystematicScopeDeSerMill.testFunctionBuilder().setName("f").build();
    compU.addFunction(f);

    var ast = compU.build();

    var scope = SystematicScopeDeSerMill.scopesGenitorDelegator().createFromAST(ast);

    // Set some SymTypeExpression information for the type field
    m.getSymbol().setType(new SymTypeObscure());
    f.getSymbol().setType(new SymTypeObscure());

    Assertions.assertEquals(8, scope.getSubScopes().size());
    // Test that the symbols are correct
    Assertions.assertEquals(MyScopeSymbolISymbol.class, i1.getSymbol().getClass());
    Assertions.assertEquals(MyScopeSymbolIImpl2Symbol.class, i2.getSymbol().getClass());
    Assertions.assertEquals(MyScopeSymbolIImpl3Symbol.class, i3.getSymbol().getClass());
    Assertions.assertEquals(MyScopeSymbolISymbol.class, s1.getSymbol().getClass());
    Assertions.assertEquals(MyScopeSymbolI2Impl2Symbol.class, s2.getSymbol().getClass());
    Assertions.assertEquals(MyScopeSymbolI2Impl3Symbol.class, s3.getSymbol().getClass());
    Assertions.assertEquals(MethodSymbol.class, m.getSymbol().getClass());
    Assertions.assertEquals(FunctionSymbol.class, f.getSymbol().getClass());

    var s2j = new SystematicScopeDeSerSymbols2Json();

    String serialized = s2j.serialize(scope);

    // And now, do similar tests on the serialized & deserialized symbol table

    var scope2 = s2j.deserialize(serialized);

    Assertions.assertEquals(8, scope2.getSubScopes().size());
    // Test that the symbols are present && correct
    Assertions.assertEquals(MyScopeSymbolISymbol.class, scope2.resolveMyScopeSymbolI("i1").get().getClass());
    Assertions.assertEquals(MyScopeSymbolIImpl2Symbol.class, scope2.resolveMyScopeSymbolIImpl2("i2").get().getClass());
    Assertions.assertEquals(MyScopeSymbolIImpl3Symbol.class, scope2.resolveMyScopeSymbolIImpl3("i3").get().getClass());
    Assertions.assertEquals(MyScopeSymbolISymbol.class, scope2.resolveMyScopeSymbolI("s1").get().getClass());
    Assertions.assertEquals(MyScopeSymbolI2Impl2Symbol.class, scope2.resolveMyScopeSymbolI2Impl2("s2").get().getClass());
    Assertions.assertEquals(MyScopeSymbolI2Impl3Symbol.class, scope2.resolveMyScopeSymbolI2Impl3("s3").get().getClass());
    Assertions.assertEquals(MethodSymbol.class, scope2.resolveMethod("m").get().getClass());
    Assertions.assertEquals(FunctionSymbol.class, scope2.resolveFunction("f").get().getClass());

    // Note: We will have findings in our log due to SymTypeObscure
  }


}
