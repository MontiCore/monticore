package de.monticore.interpreter.values;

public class ErrorMIValue implements MIValue {

  Throwable throwable;

  public ErrorMIValue(String message) {
    this(new RuntimeException(message));
  }

  public ErrorMIValue(Throwable throwable) {
    this.throwable = throwable;
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
  public Throwable asNativeObject() {
    return throwable;
  }

  @Override
  public String asString() {
    return String.valueOf(throwable);
  }

  @Override
  public boolean checkEqualityOperator(MIValue other) {
    return other.isError() && throwable == other.asNativeObject();
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
    return throwable.getClass().getTypeName()
        + " occured: " + throwable.getMessage();
  }

}
