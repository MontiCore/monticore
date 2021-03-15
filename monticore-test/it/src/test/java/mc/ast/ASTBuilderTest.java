/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import com.google.common.collect.Lists;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTTransition;
import mc.feature.javasql.javasql.javasql.JavaSQLMill;
import mc.feature.javasql.sql.sql.SQLMill;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

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
    
    // Create ASTTransition by Builder (unchecked)
    ASTTransition transition1 = FeatureDSLMill.transitionBuilder().uncheckedBuild();
    transition1.setFrom(from);
    transition1.setActivate(activate);
    transition1.setTo(to);
    // Compare these instances
    assertTrue(transition.deepEquals(transition1));
  }
  
  @Test
  public void createASTBuilderWithExtension() {
    int distinct = 5;
    List<String> selection = Lists.newArrayList();
    String table = "tableAttr";

   mc.feature.javasql.javasql.javasql._ast.ASTSelectStatement javasqlStatement = JavaSQLMill.selectStatementBuilder().
            setDistinct(distinct).
            setSelectionList(selection).
            setTable(table).
            build();

    assertEquals(distinct, javasqlStatement.getDistinct());
    assertEquals(selection, javasqlStatement.getSelectionList());
    assertEquals(table, javasqlStatement.getTable());

    mc.feature.javasql.javasql.javasql._ast.ASTSelectStatement javasqlStatement1 = JavaSQLMill.selectStatementBuilder().build();
    javasqlStatement1.setDistinct(distinct);
    javasqlStatement1.setSelectionList(selection);
    javasqlStatement1.setTable(table);

    assertTrue(javasqlStatement.deepEquals(javasqlStatement1));

    mc.feature.javasql.sql.sql._ast.ASTSelectStatement sqlStatement = SQLMill.selectStatementBuilder().
        setDistinct(distinct).
        setSelectionList(selection).
        setTable(table).
        build();

    mc.feature.javasql.sql.sql._ast.ASTSelectStatement sqlStatement1 = SQLMill.selectStatementBuilder().uncheckedBuild();
    sqlStatement1.setDistinct(distinct);
    sqlStatement1.setSelectionList(selection);
    sqlStatement1.setTable(table);

    assertTrue(sqlStatement.deepEquals(sqlStatement1));

    assertTrue(sqlStatement.getClass().isAssignableFrom(javasqlStatement.getClass()));
    assertFalse(javasqlStatement.getClass().isAssignableFrom(sqlStatement.getClass()));
  }
  
}
