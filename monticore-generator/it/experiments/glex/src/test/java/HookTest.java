/* (c) https://github.com/MontiCore/monticore */

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hierautomata._ast.ASTStateMachine;
import hierautomata._parser.HierAutomataParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class HookTest {
  
  // setup the language infrastructure
  static ASTStateMachine ast;
  static GlobalExtensionManagement glex;
  static GeneratorEngine ge;

  private static final String REGEXP = "[\r\n]+";

  private static final String REPLACE = " ";
  
  @BeforeClass
  public static void init() throws IOException{
    // replace log by a sideffect free variant
    LogStub.init();
    HierAutomataParser parser = new HierAutomataParser() ;
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
  public void testSimple() {
    // Empty Dummy
    StringBuilder res = ge.generate("tpl4/Simple.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("TA ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefine() {
    // Two explicit hook points
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" A B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace1() {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new StringHookPoint("St"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" A St B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace2() {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new StringHookPoint("StA"));
    glex.bindHookPoint("HP2", new StringHookPoint("StB"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    //System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" A StA StB B ", res.toString().replaceAll(REGEXP, REPLACE));
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
    assertEquals(" A St B ", res.toString().replaceAll(REGEXP, REPLACE));
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
    assertEquals(" A 38 B ", res.toString().replaceAll(REGEXP, REPLACE));
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
    assertEquals(" A PingPong B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace6() {
    // Two explicit hook points
    glex.bindHookPoint("HP1", new TemplateHookPoint("tpl4/Hook1.ftl"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" A R PingPong T B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplace7() {
    // Demonstrate that hook points in hook templates are again executed
    glex.bindHookPoint("HP1", new TemplateHookPoint("tpl4/Hook1.ftl"));
    glex.bindHookPoint("HP3", new StringHookPoint("tpl4/Hook1.ftl"));
    StringBuilder res = ge.generate("tpl4/Define.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" A R PingPong tpl4/Hook1.ftl T B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplaceCode1() {
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
    assertEquals(" A C1 C2 B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testDefineReplaceCode2() {
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
    assertEquals(" A PingPong1 PingPong2 B ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testReplaceCodeWithArgs11() {
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
    assertEquals(" PingPong:3318 PingPong:3323 ", res.toString().replaceAll(REGEXP, REPLACE));
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testReplaceCodeWithArgs12() {
    // Using Template Hooks (with explicit extra arguments)
    TemplateHookPoint chp = new TemplateHookPoint("tpl4/Hook2.ftl", "PingPong") ;
    
    glex.bindHookPoint("NameHook", chp);
    StringBuilder res = ge.generate("tpl4/Define3.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" PingPong:18 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates1() {
    // Templates A -> B -> C+D
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 B1 C1 D1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2() {
    // Templates A -> B -> C+D
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4.F"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 F1 B1 C1 D1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

    // Only three variants of template names work
    // glex.bindHookPoint("P", new TemplateHookPoint("tpl4/F"));
    // doesnt' because the absence of "." leads to a path completion,
    // to "tpl4/A.tpl4/F".

  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2c() {
    // Templates A -> B -> C+D
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4/F.ftl"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 F1 B1 C1 D1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates2d() {
    // Templates A -> B -> C+D
    glex.bindHookPoint("P", new TemplateHookPoint("tpl4.F.ftl"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 F1 B1 C1 D1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }


  // --------------------------------------------------------------------
  @Test
  public void testHooksInTemplates3() {
    // Templates A -> B=E
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.E"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 E1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteReplace() {
    // Templates A -> B=F
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.E")); // no eff.
    glex.replaceTemplate("tpl4.B", new TemplateHookPoint("tpl4.F"));
    StringBuilder res = ge.generate("tpl4/A.ftl", ast);

    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals("A1 F1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteAndDecorateOldB() {
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
    assertEquals("A1 G1 F1 G1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testOverwriteAndDecorateOldAndNew() {
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
    assertEquals("A1 E1 F1 E1 ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testHookSignature1() {
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
    assertEquals(" FH1:FH2:18:FH2 ", res.toString().replaceAll(REGEXP, REPLACE));
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testDefaults() {
    // Using String Hook
    StringHookPoint chp = new StringHookPoint("HookPoint filled") ;
    
    // the hook in Define3 itself has also one argument
    glex.bindHookPoint("NameHook", chp);
    StringBuilder res = ge.generate("tpl4/Define4.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    // System.out.println(res.toString());
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" HookPoint filled " + "Hook empty ", res.toString().replaceAll(REGEXP, REPLACE));
  }

  // --------------------------------------------------------------------
  @Test
  public void testBindHookFromTemplate() {
    
    // the hook in Define3 itself has also one argument
    StringBuilder res = ge.generate("tpl4/Bind1.ftl", ast);
    
    // System.out.println("****RES::\n" + res + "\n****--------");
    // System.out.println("++++LOG::\n" + Log.getFindings() + "\n++++--------");
    // Stringvergleich: --------------------
    assertEquals("[]", Log.getFindings().toString());
    assertEquals(" // Content of (c) template"
    		 + " // Content of (c) template"
		 + " // Developed by SE RWTH ", res.toString().replaceAll(REGEXP, REPLACE));
  }


}

