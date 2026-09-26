/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMCTypeFacade;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@TestWithMCLanguage(MCSimpleGenericTypesMill.class)
public class MCSimpleGenericsTypeFacadeTest extends MCTypeFacadeTest {
  @Override
  protected MCTypeFacade doCreateTypeFacade() {
    MCSimpleGenericTypesMill.init();
    return super.doCreateTypeFacade();
  }

  @Test
  public void assertThatCorrectFacade() {
    Assertions.assertInstanceOf(MCSimpleGenericTypesMCTypeFacade.class, this.mcTypeFacade);
  }

  @Test
  public void testCustomInnerTypeArgument() {
    // Without the custom type facade, printType() is used which causes
    /* type = parser.parse_TypeString("IElem<MyInnerClass>");
       MCTypeFacade.getInstance().createListTypeOf(type) */
    // To be a qualifiedType("IElem<MyInnerClass>"), which is just incorrect
    ASTMCType myType = MCSimpleGenericTypesMill.mCBasicGenericTypeBuilder()
            .addName("IElem")
            .addMCTypeArgument(
                    MCTypeFacade.getInstance().createBasicTypeArgumentOf("MyInnerClass"))
            .build();
    ASTMCType collection = MCTypeFacade.getInstance().createCollectionTypeOf(myType);

    Assertions.assertInstanceOf(ASTMCBasicGenericType.class, collection);
    Assertions.assertEquals("Collection", ((ASTMCBasicGenericType) collection).getName(0));
    Assertions.assertInstanceOf(ASTMCCustomTypeArgument.class, ((ASTMCBasicGenericType) collection).getMCTypeArgument(0));
    Assertions.assertTrue(((ASTMCBasicGenericType) collection).getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(myType.deepEquals(((ASTMCBasicGenericType) collection).getMCTypeArgument(0).getMCTypeOpt().get()));
  }

  // Other tests are extended from the MCTypeFacadeTest
}
