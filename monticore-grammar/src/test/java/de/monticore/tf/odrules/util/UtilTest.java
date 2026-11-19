/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.Splitters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(ODRulesMill.class)
public class UtilTest {

  @Test
  public void testListAccessorNameOfDerivedName() {
    // State* -> getStateList, the DSTL uses the name from the grammar
    assertEquals("State", Util.getListAccessorName("state", "a._ast.ASTState"));
    assertEquals("CatchClause", Util.getListAccessorName("catchClause", "a._ast.ASTCatchClause"));
    assertEquals("Address", Util.getListAccessorName("address", "a._ast.ASTAddress"));
    assertEquals("MCType", Util.getListAccessorName("mCType", "a._ast.ASTMCType"));
  }

  @Test
  public void testListAccessorNameOfDerivedAttributeName() {
    // handwritten rules may use the attribute name of the class diagram
    assertEquals("State", Util.getListAccessorName("states", "a._ast.ASTState"));
    assertEquals("CatchClause", Util.getListAccessorName("catchClauses", "a._ast.ASTCatchClause"));
    assertEquals("Address", Util.getListAccessorName("addresss", "a._ast.ASTAddress"));
    assertEquals("MCType", Util.getListAccessorName("mCTypes", "a._ast.ASTMCType"));
  }

  @Test
  public void testListAccessorNameOfUsageName() {
    // places:Placedeclaration* -> getPlacesList
    assertEquals("Places", Util.getListAccessorName("places", "a._ast.ASTPlacedeclaration"));
    assertEquals("Tags", Util.getListAccessorName("tags", "a._ast.ASTTargetElement"));
    assertEquals("Parts", Util.getListAccessorName("parts", "java.util.List<String>"));
    assertEquals("PhotoPath", Util.getListAccessorName("photoPath", "java.util.List<String>"));
    assertEquals("Transitions", Util.getListAccessorName("transitions", ""));
  }

  @Test
  public void testListAccessorNameOfTranslatedLink() {
    // contexts:Context* -> getContextsList, links translated by the DSTL carry the declared type
    ASTODObject target = createObject("a._ast.ASTContext");
    assertEquals("Contexts",
        Util.getListAccessorName(createLink("contexts", "a._ast.ASTContext"), target));
    assertEquals("Context",
        Util.getListAccessorName(createLink("context", "a._ast.ASTContext"), target));
  }

  @Test
  public void testListAccessorNameOfHandwrittenLink() {
    ASTODObject target = createObject("a._ast.ASTState");
    assertEquals("State", Util.getListAccessorName(createLink("states", ""), target));
    assertEquals("State", Util.getListAccessorName(createLink("state", ""), target));
    assertEquals("States", Util.getListAccessorName(createLink("states", ""), null));
  }

  protected ASTODLink createLink(String role, String genericType) {
    ASTODLink link = ODRulesMill.oDLinkBuilder().uncheckedBuild();
    link.setRightRole(role);
    if (!genericType.isEmpty()) {
      ASTStereoValue value = ODRulesMill.stereoValueBuilder().uncheckedBuild();
      value.setName("genericType");
      value.setContent(genericType);
      ASTStereotype stereotype = ODRulesMill.stereotypeBuilder().uncheckedBuild();
      stereotype.addValues(value);
      link.setStereotype(stereotype);
    }
    return link;
  }

  protected ASTODObject createObject(String type) {
    ASTODObject object = ODRulesMill.oDObjectBuilder().uncheckedBuild();
    object.setType(ODRulesMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ODRulesMill.mCQualifiedNameBuilder()
            .setPartsList(Splitters.DOT.splitToList(type)).build())
        .build());
    return object;
  }

}
