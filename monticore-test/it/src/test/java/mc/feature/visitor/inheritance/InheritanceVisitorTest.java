/* (c) https://github.com/MontiCore/monticore */
package mc.feature.visitor.inheritance;

import mc.feature.visitors.a._visitor.AVisitor2;
import mc.feature.visitors.b._visitor.BVisitor2;
import mc.feature.visitors.c.CMill;
import mc.feature.visitors.c._symboltable.ICArtifactScope;
import mc.feature.visitors.c._symboltable.ICGlobalScope;
import mc.feature.visitors.c._symboltable.ICScope;
import mc.feature.visitors.b._symboltable.IBArtifactScope;
import mc.feature.visitors.b._symboltable.IBGlobalScope;
import mc.feature.visitors.b._symboltable.IBScope;
import mc.feature.visitors.a._symboltable.IAArtifactScope;
import mc.feature.visitors.a._symboltable.IAGlobalScope;
import mc.feature.visitors.a._symboltable.IAScope;
import mc.feature.visitors.c._visitor.CInheritanceHandler;
import mc.feature.visitors.c._visitor.CTraverser;
import mc.feature.visitors.c._visitor.CVisitor2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class InheritanceVisitorTest {

  private StringBuilder sb;

  @Test
  public void testInheritanceHandler() {
    sb = new StringBuilder();

    CTraverser traverser = CMill.traverser();
    CInheritanceHandler handler = new CInheritanceHandler();
    traverser.setCHandler(handler);
    traverser.add4A(new SimpleAVisitor());
    traverser.add4B(new SimpleBVisitor());
    traverser.add4C(new SimpleCVisitor());

    ICScope scope = CMill.scope();
    scope.accept(traverser);

    Assertions.assertEquals("ASBSCS", sb.toString());

    sb = new StringBuilder();

    ICGlobalScope globalScope = CMill.globalScope();
    globalScope.accept(traverser);

    Assertions.assertEquals("AGSASBGSBSCSCGS", sb.toString());

    sb = new StringBuilder();

    ICArtifactScope artifactScope = CMill.artifactScope();
    artifactScope.accept(traverser);

    Assertions.assertEquals("AASASBASBSCSCAS", sb.toString());
  }


  private class SimpleAVisitor implements AVisitor2 {
    @Override
    public void visit (IAScope node) {
      sb.append("AS");
    }

    @Override
    public void visit (IAArtifactScope node) {
      sb.append("AAS");
    }

    @Override
    public void visit (IAGlobalScope node) {
      sb.append("AGS");
    }
  }
  private class SimpleBVisitor implements BVisitor2 {
    @Override
    public void visit (IBScope node) {
      sb.append("BS");
    }

    @Override
    public void visit (IBArtifactScope node) {
      sb.append("BAS");
    }

    @Override
    public void visit (IBGlobalScope node) {
      sb.append("BGS");
    }
  }
  private class SimpleCVisitor implements CVisitor2 {
    @Override
    public void visit (ICScope node) {
      sb.append("CS");
    }

    @Override
    public void visit (ICArtifactScope node) {
      sb.append("CAS");
    }

    @Override
    public void visit (ICGlobalScope node) {
      sb.append("CGS");
    }
  }

}