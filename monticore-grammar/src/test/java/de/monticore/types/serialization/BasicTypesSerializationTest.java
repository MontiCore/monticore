/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.ast.ASTNode;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class BasicTypesSerializationTest {
  
  TypesSerializer typesSerializer;
  
  MCBasicTypesTestParser mcBasicTypesParser;
  
  @Before
  public void init() {
    typesSerializer = new TypesSerializer();
    mcBasicTypesParser = new MCBasicTypesTestParser();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testQualifiedType() throws IOException {
    Optional<ASTMCQualifiedType> stringOpt = mcBasicTypesParser
        .parse_StringMCQualifiedType("java.lang.String");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  @Test
  public void testPrimitiveType() throws IOException {
    Optional<ASTMCPrimitiveType> stringOpt = mcBasicTypesParser
        .parse_StringMCPrimitiveType("double");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  @Test
  public void testVoidType() throws IOException {
    Optional<ASTMCVoidType> stringOpt = mcBasicTypesParser
        .parse_StringMCVoidType("void");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  @Test
  public void testReturnType() throws IOException {
    Optional<ASTMCReturnType> stringOpt = mcBasicTypesParser
        .parse_StringMCReturnType("java.util.Double");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  @Test
  public void testQualifiedName() throws IOException {
    Optional<ASTMCQualifiedName> stringOpt = mcBasicTypesParser
        .parse_StringMCQualifiedName("java.lang.String");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  @Test
  public void testImportStatement() throws IOException {
    Optional<ASTMCImportStatement> stringOpt = mcBasicTypesParser
        .parse_StringMCImportStatement("import java.lang.*;");
    String serialized = testSerialize(stringOpt);
    System.out.println(serialized);
    testDeserialize(serialized);
  }
  
  protected <T extends ASTNode> String testSerialize(Optional<T> obj) throws IOException {
    assertTrue(obj.isPresent());
    Optional<String> serialized = typesSerializer.serialize(obj.get());
    assertTrue(serialized.isPresent());
    assertTrue(serialized.get().length() > 0);
    return serialized.get();
  }
  
  protected ASTNode testDeserialize(String type) throws IOException {
    Optional<ASTNode> deserialized = typesSerializer.deserialize(type);
    assertTrue(deserialized.isPresent());
    return deserialized.get();
  }
}
