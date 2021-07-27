/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.transformation.rule.translation;

import com.google.common.collect.Maps;
import de.monticore.io.paths.ModelPath;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.se_rwth.commons.logging.Log;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr.DSLWithOtherPropertiesThanAutomatonTRMill;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr._ast.*;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr._parser.DSLWithOtherPropertiesThanAutomatonTRParser;
import mc.testcases.tr.translation.DSLWithOtherPropertiesThanAutomatonRule2OD;
import mc.testcases.tr.translation.DSLWithOtherPropertiesThanAutomatonRule2ODVisitor;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._symboltable.ODRulesGlobalScope;
import de.monticore.tf.odrules._symboltable.ODRulesScopesGenitorDelegator;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules._ast.ASTODAttribute;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.rule2od.Variable2AttributeMap;
import de.monticore.tf.ruletranslation.Rule2ODState;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DSLWithOtherPropertiesThanAutomatonRule2ODVisitorTest {
  private static void createSymboltable(ASTODRule od) {
    ODRulesScopesGenitorDelegator symbolTable = ODRulesMill.scopesGenitorDelegator();
    symbolTable.createFromAST(od);
  }

  @BeforeClass
  public static void disableFailQuick() {
    DSLWithOtherPropertiesThanAutomatonTRMill.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testVisit_QualifiedName_Pat() throws IOException {
    // create input
    String pattern_String = "my.qualified.name";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTQualifiedName_Pat pattern = parser.parse_StringQualifiedName_Pat(pattern_String).get();
    assertFalse(parser.hasErrors());

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().handle(pattern);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());

    ASTODObject lhsObject = de.monticore.tf.odrules.util.Util.getODObject(lhs, "qualifiedName_1");
    assertNotNull(lhsObject);
    assertEquals(1, lhsObject.getAttributesList().size());
    ASTODAttribute attr_lhs = lhsObject.getAttributes(0);
    assertEquals("name", attr_lhs.getName());
    assertEquals("java.util.List<String>", attr_lhs.printType());
    assertTrue(attr_lhs.isPresentList());
    ASTArrayInit lhs_value = attr_lhs.getList();
    assertEquals(3, lhs_value.getVariableInitList().size());

    ASTODDefinition rhs = od.getRhs();
    assertEquals(1, rhs.getODObjectList().size());

    ASTODObject rhsObject = de.monticore.tf.odrules.util.Util.getODObject(rhs, "qualifiedName_1");
    assertNotNull(rhsObject);
    assertEquals(0, rhsObject.getAttributesList().size());

  }

  @Test
  public void testVisit_IFoo_Pat() throws IOException {
    // create input
    String pattern_String = "IFoo $IFOO";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTIFoo_Pat pattern = parser.parse_StringIFoo_Pat(pattern_String).orElse(null);
    assertFalse(parser.hasErrors());
    assertNotNull(pattern);

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().handle(pattern);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());

    ASTODObject lhsObject = de.monticore.tf.odrules.util.Util.getODObject(lhs, "$IFOO");
    assertNotNull(lhsObject);
    assertTrue(lhsObject.getAttributesList().isEmpty());

    ASTODDefinition rhs = od.getRhs();
    assertEquals(1, rhs.getODObjectList().size());

    ASTODObject rhsObject = de.monticore.tf.odrules.util.Util.getODObject(rhs, "$IFOO");
    assertNotNull(rhsObject);
    assertTrue(rhsObject.getAttributesList().isEmpty());
  }

  @Test
  public void testVisit_IFoo_Rep() throws IOException {
    // create input
    String replacement_String = "[[ IFoo $IFOO :- ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTIFoo_Rep replacement = parser.parse_StringIFoo_Rep(replacement_String).get();
    assertFalse(parser.hasErrors());

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().handle(replacement);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());

    ASTODObject lhsObject = de.monticore.tf.odrules.util.Util.getODObject(lhs, "$IFOO");
    assertNotNull(lhsObject);
    assertTrue(lhsObject.getAttributesList().isEmpty());

    ASTODDefinition rhs = od.getRhs();
    assertTrue(rhs.getODObjectList().isEmpty());
  }

  @Test
  public void testVisit_IFoo_Neg() throws IOException {
    // create input
    String negation_String = "not [[ IFoo $IFOO  ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTIFoo_Neg negation = parser.parse_StringIFoo_Neg(negation_String).get();
    assertFalse(parser.hasErrors());

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().visit(negation);
    testee.getTraverser().visit((ASTIFoo_Pat) negation.getIFoo());
    testee.getTraverser().endVisit(negation);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());

    ASTODObject lhsObject = de.monticore.tf.odrules.util.Util.getODObject(lhs, "$IFOO");
    assertNotNull(lhsObject);
    assertTrue(lhsObject.getAttributesList().isEmpty());
    assertTrue(lhsObject.hasStereotype(ODRuleStereotypes.NOT));
  }

  @Test
  public void testVisit_IFoo_List() throws IOException {
    // create input
    String list_String = "list [[ IFoo $IFOO ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTIFoo_List list = parser.parse_StringIFoo_List(list_String).get();
    assertFalse(parser.hasErrors());

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().handle(list);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());

    ASTODObject lhsObject = de.monticore.tf.odrules.util.Util.getODObject(lhs, "$IFOO");
    ASTODObject listParent = de.monticore.tf.odrules.util.Util.getODObject(lhs, lhs.getODObjectList().get(0).getName());
    assertNotNull(lhsObject);
    assertTrue(lhsObject.getAttributesList().isEmpty());
    assertTrue(listParent.hasStereotype(ODRuleStereotypes.LIST));
    assertTrue(listParent.getAllODObjects().contains(lhsObject));
  }

  @Test
  public void testVisit_IFoo_Opt() throws IOException {
    // create input
    String optional_String = "opt [[ IFoo $IFOO  ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = new DSLWithOtherPropertiesThanAutomatonTRParser();
    ASTIFoo_Opt optional = parser.parse_StringIFoo_Opt(optional_String).get();
    assertFalse(parser.hasErrors());

    // run test
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), Maps.newHashMap());
    DSLWithOtherPropertiesThanAutomatonRule2OD testee = new DSLWithOtherPropertiesThanAutomatonRule2OD(state);
    testee.getTraverser().visit(optional);
    testee.getTraverser().visit((ASTIFoo_Pat) optional.getIFoo());
    testee.getTraverser().endVisit(optional);

    // check result
    ASTODRule od = testee.getOD();
    createSymboltable(od);

    ASTODDefinition lhs = od.getLhs();
    assertEquals(1, lhs.getODObjectList().size());
    ASTODObject opt = (ASTODObject) lhs.getODObjectList().get(0);
    assertTrue(opt.hasStereotype(ODRuleStereotypes.OPTIONAL));
    assertEquals(1, opt.getInnerLinksList().size());
    assertNotNull(opt.getInnerLinksList().get(0));
    assertNotNull(opt.getInnerLinksList().get(0).getODObject());

    ASTODObject lhsObject = (ASTODObject) opt.getInnerLinksList().get(0).getODObject();
    assertTrue(lhsObject.getAttributesList().isEmpty());
  }
}
