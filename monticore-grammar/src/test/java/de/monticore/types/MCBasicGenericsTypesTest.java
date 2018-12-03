package de.monticore.types;


import de.monticore.types.mcbasicgenericstypes._ast.ASTMCListType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMCOptionalType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMCSetType;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;
import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MCBasicGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"List<a.A>","Optional<String>","Set<String>","Map<String,String>","List<socnet.Person>"};

    for (String testType : types) {
      MCBasicGenericsTypesTestParser mcBasicTypesParser = new MCBasicGenericsTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCReferenceType);

      ASTMCReferenceType t = (ASTMCReferenceType) type.get();
      t.accept( new MCBasicGenericsTypesVisitor() {
        public void visit(ASTMCListType t) {
          assertTrue(true);
          t.getMCTypeArgument().accept(new MCBasicGenericsTypesVisitor() {
            @Override
            public void visit(ASTMCType node) {
              if (!(node instanceof ASTMCQualifiedReferenceType)) {
                fail("Found not String");
              }
            }
          });
        }
      });
    }

  }


  private class CheckTypeVisitor implements MCBasicGenericsTypesVisitor {

  }

}
