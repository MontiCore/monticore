/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

enum JsonTokenKind {
  STRING, MEMBER_NAME, NUMBER, BOOLEAN, BEGIN_ARRAY, END_ARRAY, BEGIN_OBJECT, END_OBJECT, NULL, COMMA, WHITESPACE;
}
