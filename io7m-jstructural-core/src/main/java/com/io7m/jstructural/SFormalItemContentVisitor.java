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

package com.io7m.jstructural;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;

/**
 * Formal item content visitor.
 * 
 * @param <A>
 *          The type of values returned by the visitor
 */

public interface SFormalItemContentVisitor<A>
{
  /**
   * Visit a formal item list element.
   * 
   * @param list
   *          The formal item list element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitFormalItemList(
    final @Nonnull SFormalItemList list)
    throws ConstraintError,
      Exception;

  /**
   * Visit an image element.
   * 
   * @param image
   *          The image element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitImage(
    final @Nonnull SImage image)
    throws ConstraintError,
      Exception;

  /**
   * Visit an ordered list element.
   * 
   * @param list
   *          The list element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitListOrdered(
    final @Nonnull SListOrdered list)
    throws ConstraintError,
      Exception;

  /**
   * Visit an unordered list element.
   * 
   * @param list
   *          The list element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitListUnordered(
    final @Nonnull SListUnordered list)
    throws ConstraintError,
      Exception;

  /**
   * Visit a table element.
   * 
   * @param e
   *          The table element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitTable(
    final @Nonnull STable e)
    throws ConstraintError,
      Exception;

  /**
   * Visit a verbatim element.
   * 
   * @param text
   *          The verbatim element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitVerbatim(
    final @Nonnull SVerbatim text)
    throws ConstraintError,
      Exception;
}
