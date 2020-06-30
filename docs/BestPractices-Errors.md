<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Understanding Errors, Defining Errors

[[_TOC_]]

Errors happen.
Some happen because of faults in the code (we call that internal errors),
some happen because we haven't well explained how to use MontiCore and 
how to use the generated code.

Here we try to add infromation how to handle occuring errors.
We use the eror code for an easier identification. Error code start with 
`0x`and use 5(!) hex characters and thus should be at the same time 
memorizable (because not completely unknown, but still not so common that
they could be taken for something else).

## **Handling Errors 0x.....** 

### How to use **Expressions** (0xA0129) 

* `Expression` is a predefined nonterminal in the MontiCore basic grammars. 
  Because of the infix notation of some operators and similar challenges,
  it is usually not possible to use a subset of the expressions only. 
  For example use of `ConditionalExpression` may lead to a parser generation 
  error (i.e. `0xA0129`).
* Solutions:
  1. Use nonterminal `Expression` and forbid all unwanted alternatives through 
     context conditions.
  2. Think of allowing more general expressions?
  3. If especially the syntax of `if . then . else .` shall be reused, 
     why not defining this in a new nonterminal and ignoring that the same
     syntactic constructs were already available in another production.
* Defined by: CKi, BR.
  
 
    
## Further Information

* [Overview Best Practices](BestPractices.md)
* [MontiCore project](../README.md) - MontiCore
* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

