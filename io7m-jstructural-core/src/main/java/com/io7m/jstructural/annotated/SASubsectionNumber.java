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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;

/**
 * An abstract subsection number.
 */

public abstract class SASubsectionNumber
{
  /**
   * Accept a subsection number visitor.
   * 
   * @param <T>
   *          The type of values returned by the visitor
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws ConstraintError
   *           If the visitor raises {@link ConstraintError}
   * @throws Exception
   *           If the visitor raises an {@link Exception}
   */

  public abstract <T> T subsectionNumberAccept(
    final @Nonnull SASubsectionNumberVisitor<T> v)
    throws ConstraintError,
      Exception;

  /**
   * @return A human-readable string representing the subsection number (such
   *         as "1.2.3")
   */

  public abstract @Nonnull String subsectionNumberFormat();
}
