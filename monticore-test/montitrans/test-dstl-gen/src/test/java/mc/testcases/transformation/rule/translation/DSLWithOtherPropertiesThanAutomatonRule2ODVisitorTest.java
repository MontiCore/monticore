/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.transformation.rule.translation;

import com.google.common.collect.Maps;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODAttribute;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._symboltable.ODRulesScopesGenitorDelegator;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.rule2od.Variable2AttributeMap;
import de.monticore.tf.ruletranslation.Rule2ODState;
import de.se_rwth.commons.logging.Log;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr.DSLWithOtherPropertiesThanAutomatonTRMill;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr._ast.*;
import mc.testcases.tr.dslwithotherpropertiesthanautomatontr._parser.DSLWithOtherPropertiesThanAutomatonTRParser;
import mc.testcases.tr.translation.DSLWithOtherPropertiesThanAutomatonRule2OD;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(DSLWithOtherPropertiesThanAutomatonTRMill.class)
public class DSLWithOtherPropertiesThanAutomatonRule2ODVisitorTest {
  private static void createSymboltable(ASTODRule od) {
    ODRulesScopesGenitorDelegator symbolTable = ODRulesMill.scopesGenitorDelegator();
    symbolTable.createFromAST(od);
  }

  @Test
  public void testVisit_QualifiedName_Pat() throws IOException {
    // create input
    String pattern_String = "my.qualified.name";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    
    Optional<ASTQualifiedName_Pat> patternOpt = parser.parse_StringQualifiedName_Pat(pattern_String);
    assertTrue(patternOpt.isPresent());
    assertFalse(parser.hasErrors());
    
    ASTQualifiedName_Pat pattern = patternOpt.get();

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
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    Optional<ASTIFoo_Pat> patternOpt = parser.parse_StringIFoo_Pat(pattern_String);
    assertTrue(patternOpt.isPresent());
    assertFalse(parser.hasErrors());
    ASTIFoo_Pat pattern = patternOpt.get();

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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisit_IFoo_Rep() throws IOException {
    // create input
    String replacement_String = "[[ IFoo $IFOO :- ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    Optional<ASTIFoo_Rep> replacementOpt = parser.parse_StringIFoo_Rep(replacement_String);
    assertTrue(replacementOpt.isPresent());
    assertFalse(parser.hasErrors());
    
    ASTIFoo_Rep replacement = replacementOpt.get();

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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisit_IFoo_Neg() throws IOException {
    // create input
    String negation_String = "not [[ IFoo $IFOO  ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    Optional<ASTIFoo_Neg> negationOpt = parser.parse_StringIFoo_Neg(negation_String);
    assertTrue(negationOpt.isPresent());
    assertFalse(parser.hasErrors());
    
    ASTIFoo_Neg negation = negationOpt.get();

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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisit_IFoo_List() throws IOException {
    // create input
    String list_String = "list [[ IFoo $IFOO ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    Optional<ASTIFoo_List> listOpt = parser.parse_StringIFoo_List(list_String);
    assertTrue(listOpt.isPresent());
    assertFalse(parser.hasErrors());
    
    ASTIFoo_List list = listOpt.get();

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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisit_IFoo_Opt() throws IOException {
    // create input
    String optional_String = "opt [[ IFoo $IFOO  ]]";
    DSLWithOtherPropertiesThanAutomatonTRParser parser = DSLWithOtherPropertiesThanAutomatonTRMill.parser();
    Optional<ASTIFoo_Opt> optionalOpt = parser.parse_StringIFoo_Opt(optional_String);
    assertTrue(optionalOpt.isPresent());
    assertFalse(parser.hasErrors());
    
    ASTIFoo_Opt optional = optionalOpt.get();

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
    assertTrue(Log.getFindings().isEmpty());
  }
}
