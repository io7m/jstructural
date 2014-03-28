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
 * SFECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jstructural.annotated;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A formal item number visitor.
 * 
 * @param <T>
 *          The type of transformed formal item numbers
 */

public interface SAFormalItemNumberVisitor<T>
{
  /**
   * Visit a PSF formal item number.
   * 
   * @param f
   *          The number
   * @return A value of type <code>T</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  T visitFormalItemNumberPSF(
    final @Nonnull SAFormalItemNumberPSF f)
    throws ConstraintError,
      Exception;

  /**
   * Visit a PSSF formal item number.
   * 
   * @param f
   *          The number
   * @return A value of type <code>T</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  T visitFormalItemNumberPSSF(
    final @Nonnull SAFormalItemNumberPSSF f)
    throws ConstraintError,
      Exception;

  /**
   * Visit a SF formal item number.
   * 
   * @param f
   *          The number
   * @return A value of type <code>T</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  T visitFormalItemNumberSF(
    final @Nonnull SAFormalItemNumberSF f)
    throws ConstraintError,
      Exception;

  /**
   * Visit a SSF formal item number.
   * 
   * @param f
   *          The number
   * @return A value of type <code>T</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  T visitFormalItemNumberSSF(
    final @Nonnull SAFormalItemNumberSSF f)
    throws ConstraintError,
      Exception;
}
