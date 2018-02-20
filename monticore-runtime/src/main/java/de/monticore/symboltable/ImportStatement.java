/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ImportStatement {

  private final String statement;
  private final boolean isStar;

  public ImportStatement(String statement, boolean isStar) {
    checkArgument(!isNullOrEmpty(statement), "An import statement must not be null or empty");
    checkArgument(!statement.endsWith("."), "An import statement must not end with a dot.");

    this.statement = statement;
    this.isStar = isStar;
  }

  /**
   * @return statement
   */
  public String getStatement() {
    return this.statement;
  }

  /**
   * @return isStar
   */
  public boolean isStar() {
    return this.isStar;
  }

}
