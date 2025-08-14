package de.monticore.interpreter.values;

import java.util.Optional;

public class ErrorMIValue implements MIFlowControlSignal {

  protected Optional<String> message;

  protected Optional<Exception> exception;

  public ErrorMIValue(String message) {
    this.message = Optional.of(message);
    this.exception = Optional.empty();
  }

  public ErrorMIValue(Exception exception) {
    this.message = Optional.empty();
    this.exception = Optional.of(exception);
  }

  @Override
  public boolean isError() {
    return true;
  }

  @Override
  public String asError() {
    return getMessage();
  }

  @Override
  public String printType() {
    return "Error";
  }

  @Override
  public String printValue() {
    return getMessage();
  }

  // helper

  protected String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    else {
      return exception.get().getClass().getTypeName()
          + " occured: " + exception.get().getMessage();
    }
  }

}
