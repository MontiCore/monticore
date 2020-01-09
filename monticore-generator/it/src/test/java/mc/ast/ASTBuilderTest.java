/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import com.google.common.collect.Lists;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTTransition;
import mc.feature.featuredsl._ast.FeatureDSLMill;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;
import mc.feature.javasql.javasql.javasql._ast.JavaSQLMill;
import mc.feature.javasql.javasql.javasql._ast.JavaSQLNodeFactory;
import mc.feature.javasql.sql.sql._ast.SQLMill;
import mc.feature.javasql.sql.sql._ast.SQLNodeFactory;
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
    
    // Create ASTTransition by NodeFactory
    ASTTransition transition1 = FeatureDSLNodeFactory.createASTTransition();
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

    // TODO: Die Builder müssen alle alle Setter überschreiben.

   mc.feature.javasql.javasql.javasql._ast.ASTSelectStatement javasqlStatement = JavaSQLMill.selectStatementBuilder().
            setDistinct(distinct).
            setSelectionList(selection).
            setTable(table).
            build();

    assertEquals(distinct, javasqlStatement.getDistinct());
    assertEquals(selection, javasqlStatement.getSelectionList());
    assertEquals(table, javasqlStatement.getTable());

    mc.feature.javasql.javasql.javasql._ast.ASTSelectStatement javasqlStatement1 = JavaSQLNodeFactory.createASTSelectStatement();
    javasqlStatement1.setDistinct(distinct);
    javasqlStatement1.setSelectionList(selection);
    javasqlStatement1.setTable(table);

    assertTrue(javasqlStatement.deepEquals(javasqlStatement1));

    mc.feature.javasql.sql.sql._ast.ASTSelectStatement sqlStatement = SQLMill.selectStatementBuilder().
        setDistinct(distinct).
        setSelectionList(selection).
        setTable(table).
        build();

    mc.feature.javasql.sql.sql._ast.ASTSelectStatement sqlStatement1 = SQLNodeFactory.createASTSelectStatement();
    sqlStatement1.setDistinct(distinct);
    sqlStatement1.setSelectionList(selection);
    sqlStatement1.setTable(table);

    assertTrue(sqlStatement.deepEquals(sqlStatement1));

    assertTrue(sqlStatement.getClass().isAssignableFrom(javasqlStatement.getClass()));
    assertFalse(javasqlStatement.getClass().isAssignableFrom(sqlStatement.getClass()));
  }
  
}
