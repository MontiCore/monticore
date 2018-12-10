/* (c) Monticore license: https://github.com/MontiCore/monticore */
package hierautomaton;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.TemplateStringHookPoint;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hierautomaton._ast.ASTStateMachine;
import hierautomaton._parser.HierAutomatonParser;


public class HookTest {
    
  // setup the language infrastructure
  static ASTStateMachine ast;
  static GlobalExtensionManagement glex;
  static GeneratorEngine ge;
  
  @BeforeClass
  public static void init() throws IOException {
    // replace log by a sideffect free variant
    LogStub.init();
    HierAutomatonParser parser = new HierAutomatonParser() ;
    String model = "src/test/resources/example/HierarchyPingPong.aut";
    Optional<ASTStateMachine> optStateMachine = parser.parse(model);

    if(parser.hasErrors() || !optStateMachine.isPresent()) {
    	throw new IOException();
    }
    ast = optStateMachine.get();
    if(ast == null) {
    	throw new IOException();
    }
  }
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    GeneratorSetup s = new GeneratorSetup();
    s.setTracing(false);
    glex = s.getGlex();
    ge = new GeneratorEngine(s);
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testSimple() throws IOException {
    // Empty Dummy
    StringBuilder res = ge.generate("tpl4/Simple.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("TA\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefine() throws IOException {
    // Two explicit hook points
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\n\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace1() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new StringHookPoint("St"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nSt\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace2() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new StringHookPoint("StA"));
    glex.bindHookPoint("HP2", new StringHookPoint("StB"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nStA\nStB\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace3() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new TemplateStringHookPoint("St"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nSt\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace4() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new TemplateStringHookPoint("${13+25}"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\n38\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace5() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new TemplateStringHookPoint("${ast.name}"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nPingPong\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace6() throws IOException {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new TemplateHookPoint("tpl4/Hook1.ftl"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nR\nPingPong\n\nT\n\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace7() throws IOException {
    // Demonstrate that hook points in hook templates are again executed
    glex.bindHookPoint("HP1", new TemplateHookPoint("tpl4/Hook1.ftl"));
    glex.bindHookPoint("HP3", new StringHookPoint("tpl4/Hook1.ftl"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nR\nPingPong\ntpl4/Hook1.ftl\nT\n\n\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplaceCode1() throws IOException {
    // Using Code Hooks (with Memory)
    CodeHookPoint chp = new CodeHookPoint() { 
        int i = 0; 
        public String processValue(TemplateController tc, ASTNode ast)
          {  i++;  return "C" + i; }
        // unused in this test
        public String processValue(TemplateController tc, List<Object> args)
	{  i++;  return "D" + i; }
        public String processValue(TemplateController tc, ASTNode ast, 
							  List<Object> args)
	{  i++;  return "E" + i; }
    };
    glex.bindHookPoint("HP1", chp);
    glex.bindHookPoint("HP2", chp);
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nC1\nC2\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplaceCode2() throws IOException {
    // Using Code Hooks (with ast access)
    CodeHookPoint chp = new CodeHookPoint() { 
        int i = 0; 
        public String processValue(TemplateController tc, ASTNode ast)
          {  i++; return ((ASTStateMachine)ast).getName() + i;}
        // unused in this test
        public String processValue(TemplateController tc, List<Object> args)
	{  i++; return "ERROR " + i; }
        public String processValue(TemplateController tc, ASTNode ast, 
							  List<Object> args)
	{  i++; return "ERROR2 " + i; }
    };
    glex.bindHookPoint("HP1", chp);
    glex.bindHookPoint("HP2", chp);
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nA\nPingPong1\nPingPong2\nB\n", res.toString());
  }


  // --------------------------------------------------------------------
// TODO BUG XXX MB:
// MB: Das funktioniert jetzt, ist also erledigt
// test geht nicht, weil zB ${glex.defineHookPoint(tc,"NameHook",ast,18)}
// nicht möglich ist und deshalb keine Argumente weiter gereicht werden können
// das wäre aber hilfreich
  @Test
  public void testReplaceCodeWithArgs11() throws IOException {
    // Using Code Hooks (with explicit extra arguments)
    CodeHookPoint chp = new CodeHookPoint() {
      
      // unused in this test
      public String processValue(TemplateController tc, ASTNode ast) {
        return "unused";
      }
      
      // unused in this test
      public String processValue(TemplateController tc, List<Object> args) {
        return "unused";
      }
      
      //
      public String processValue(TemplateController tc, ASTNode ast,
          List<Object> args) {
        int a2 = ((BigDecimal) args.get(0)).intValue();
        return ((ASTStateMachine) ast).getName() + ":" + (3300 + a2);
      }
    };
    
    glex.bindHookPoint("NameHook", chp);
    StringBuilder res = ge.generate("tpl4/Define2.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\nPingPong:3318\nPingPong:3323\n\n", res.toString());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testReplaceCodeWithArgs12() throws IOException {
    // Using Template Hooks (with explicit extra arguments)
    TemplateHookPoint chp = new TemplateHookPoint("tpl4/Hook2.ftl", "PingPong") ;
    
    glex.bindHookPoint("NameHook", chp);
    StringBuilder res = ge.generate("tpl4/Define3.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\n\nPingPong:18\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates1() throws IOException {
    // Templates A -> B -> C+D 
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\n\nB1\nC1\n\nD1\n\n\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2() throws IOException {
    // Templates A -> B -> C+D
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4.F"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\nF1\n\nB1\nC1\n\nD1\n\n\n", res.toString());
  }

    // Only three variants of template names work
    // glex.bindHookPoint("P", new TemplateHookPoint("tpl4/F"));
    // doesnt' because the absence of "." leads to a path completion,
    // to "tpl4/A.tpl4/F".

  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2c() throws IOException {
    // Templates A -> B -> C+D 
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4/F.ftl"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\nF1\n\nB1\nC1\n\nD1\n\n\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2d() throws IOException {
    // Templates A -> B -> C+D
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4.F.ftl"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\nF1\n\nB1\nC1\n\nD1\n\n\n", res.toString());
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates3() throws IOException {
    // Templates A -> B=E  
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.E"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\n\nE1\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteReplace() throws IOException {
    // Templates A -> B=F
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.E")); // no eff.
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.F"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\n\nF1\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteAndDecorateOldB() throws IOException {
    // Templates A -> B=F
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.E")); // no eff.
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.F"));
    glex.setBeforeTemplate("tpl4.B", new TemplateHookPoint("tpl4.E")); // no eff.
    glex.setBeforeTemplate("tpl4.B", new TemplateHookPoint("tpl4.G"));
    glex.setAfterTemplate("tpl4.B", new TemplateHookPoint("tpl4.E")); // no eff.
    glex.setAfterTemplate("tpl4.B", new TemplateHookPoint("tpl4.G"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\n\nG1\nF1\nG1\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteAndDecorateOldAndNew() throws IOException {
    // Templates A -> B=F
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.F"));
    glex.setBeforeTemplate("tpl4.B", new TemplateHookPoint("tpl4.E"));
    glex.setBeforeTemplate("tpl4.E", new TemplateHookPoint("tpl4.G")); // no eff.
    glex.setAfterTemplate("tpl4.B", new TemplateHookPoint("tpl4.E"));
    glex.setAfterTemplate("tpl4.E", new TemplateHookPoint("tpl4.G")); // no eff.
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1\n\nE1\nF1\nE1\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testHookSignature1() throws IOException {
    // Using Template Hooks (with 2 explicit extra arguments)
    TemplateHookPoint chp = new TemplateHookPoint("tpl4/HookDoubleArgs.ftl",
    							"FH1", "FH2") ;
    
    // the hook in Define3 itself has also one argument
    glex.bindHookPoint("NameHook", chp);
    StringBuilder res = ge.generate("tpl4/Define3.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\n\nFH1:FH2:18:FH2\n\n", res.toString());
  }

  // --------------------------------------------------------------------
  @Test
  public void testBindHookFromTemplate() throws IOException {
    
    // the hook in Define3 itself has also one argument
    StringBuilder res = ge.generate("tpl4/Bind1.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("\n\n\n// Content of (c) template\n\n\n"
    		 + "\n\n// Content of (c) template\n\n"
		 + "\n// Developed by SE RWTH\n\n", res.toString());
  }


}

