/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jstructural.annotated;

/**
 * An abstract formal item number.
 */

public abstract class SAFormalItemNumber implements
  Comparable<SAFormalItemNumber>,
  SASubsectionContentNumber
{
  /**
   * Accept a formal item number visitor.
   * 
   * @param <T>
   *          The type of values returned by the visitor
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * 
   * @throws Exception
   *           If the visitor raises an {@link Exception}
   */

  public abstract <T> T formalItemNumberAccept(
    final SAFormalItemNumberVisitor<T> v)
    throws Exception;

  /**
   * @return A human-readable string representing the formal item number (such
   *         as "1.2.3.4")
   */

  public abstract String formalItemNumberFormat();

  /**
   * @return The formal item number
   */

  public abstract int getFormalItem();

  @Override public final <T> T subsectionContentNumberAccept(
    final SASubsectionContentNumberVisitor<T> v)
    throws Exception
  {
    return v.visitFormalItemNumber(this);
  }
}
