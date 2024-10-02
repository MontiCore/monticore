/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantgroups;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantgroups.constantgroupswithnames.ConstantGroupsWithNamesMill;
import mc.feature.constantgroups.constantgroupswithnames._ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstantsGroupsWithNamesTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    // CG1
    Optional<ASTCG1> cg1_1 = ConstantGroupsWithNamesMill.parser().parse_StringCG1("cg1");
    assertTrue(cg1_1.isPresent());
    assertTrue( cg1_1.get().isCg());

    Optional<ASTCG1> cg1_2 = ConstantGroupsWithNamesMill.parser().parse_StringCG1("cg2");
    assertTrue(cg1_2.isPresent());
    assertTrue( cg1_2.get().isCg());

    // CG2
    Optional<ASTCG2> cg2_1 = ConstantGroupsWithNamesMill.parser().parse_StringCG2("cg1");
    assertTrue(cg2_1.isPresent());
    assertTrue( cg2_1.get().isCg());

    Optional<ASTCG2> cg2_2 = ConstantGroupsWithNamesMill.parser().parse_StringCG2("cg2");
    assertTrue(cg2_2.isPresent());
    assertTrue( cg2_2.get().isCg());

    // CG3
    Optional<ASTCG3> cg3_1 = ConstantGroupsWithNamesMill.parser().parse_StringCG3("a");
    assertTrue(cg3_1.isPresent());
    assertTrue( cg3_1.get().isCg());

    Optional<ASTCG3> cg3_2 = ConstantGroupsWithNamesMill.parser().parse_StringCG3("cg1");
    assertTrue(cg3_2.isPresent());
    assertTrue( cg3_2.get().isCg());

    Optional<ASTCG3> cg3_3 = ConstantGroupsWithNamesMill.parser().parse_StringCG3("cg2");
    assertTrue(cg3_3.isPresent());
    assertTrue( cg3_3.get().isCg());

    // CG_i
    Optional<ASTCG_i> cgi_1 = ConstantGroupsWithNamesMill.parser().parse_StringCG_i("a");
    assertTrue(cg3_1.isPresent());
    assertEquals(ASTConstantsConstantGroupsWithNames.A, cgi_1.get().getCg());

    Optional<ASTCG_i> cgi_2 = ConstantGroupsWithNamesMill.parser().parse_StringCG_i("cg1");
    assertTrue(cg3_2.isPresent());
    assertEquals(ASTConstantsConstantGroupsWithNames.A1, cgi_2.get().getCg());

    Optional<ASTCG_i> cgi_3 = ConstantGroupsWithNamesMill.parser().parse_StringCG_i("cg2");
    assertTrue(cg3_3.isPresent());
    assertEquals(ASTConstantsConstantGroupsWithNames.A2, cgi_3.get().getCg());
  }

}
