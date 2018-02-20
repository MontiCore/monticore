/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTTransition;
import mc.feature.featuredsl._ast.FeatureDSLMill;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

public class ASTBuilderTest extends GeneratorIntegrationsTest {
  
  @Test
  public void createASTBuilder() {
    String from = "fromAttr";
    String activate = "activateAttr";
    String to = "toAttr";
    
    // Create ASTTransition by Builder
    ASTTransition transition = FeatureDSLMill.transitionBuilder().
        setFrom(from).
        setActivate(activate).
        setTo(to).build();
    
    assertEquals(from, transition.getFrom());
    assertEquals(activate, transition.getActivate());
    assertEquals(to, transition.getTo());
    
    // Create ASTTransition by NodeFactory
    ASTTransition transition1 = FeatureDSLNodeFactory.createASTTransition(from, activate, to);
    // Compare these instances
    assertTrue(transition.deepEquals(transition1));
  }
  
  @Test
  public void createASTBuilderWithExtension() {
    int distinct = 5;
    List<String> selection = Lists.newArrayList();
    String table = "tableAttr";
// TODO GV
//    ASTSelectStatement javasqlStatement = mc.feature.javasql.javasql._ast.ASTSelectStatement.getBuilder().
//       distinct(distinct).
//       selection(selection).
//       table(table).
//       build();
//       
//    assertEquals(distinct, javasqlStatement.getDistinct());
//    assertEquals(selection, javasqlStatement.getSelection());
//    assertEquals(table, javasqlStatement.getTable());
//    
//    ASTSelectStatement javasqlStatement1 = JavaSQLNodeFactory.createASTSelectStatement(distinct, selection, table);
//    
//    assertTrue(javasqlStatement.deepEquals(javasqlStatement1));
//    
//    ASTSelectStatement sqlStatement = mc.feature.javasql.sql._ast.ASTSelectStatement.getBuilder().
//        distinct(distinct).
//        selection(selection).
//        table(table).
//        build();
//    
//    ASTSelectStatement sqlStatement1 = SQLNodeFactory.createASTSelectStatement(distinct, selection, table);
//    
//    assertTrue(sqlStatement.deepEquals(sqlStatement1));
//    
//    assertTrue(sqlStatement.getClass().isAssignableFrom(javasqlStatement.getClass()));
//    assertFalse(javasqlStatement.getClass().isAssignableFrom(sqlStatement.getClass()));
    
    
  }
  
}
