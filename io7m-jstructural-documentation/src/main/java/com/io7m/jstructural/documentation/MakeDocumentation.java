package com.io7m.jstructural.documentation;

import com.io7m.jstructural.tools.JSCMain;
import com.io7m.junreachable.UnreachableCodeException;

final class MakeDocumentation
{
  private MakeDocumentation()
  {
    throw new UnreachableCodeException();
  }

  public static void main(
    final String[] args)
    throws Throwable
  {
    JSCMain.run(JSCMain.getLog(false), args);
  }
}
