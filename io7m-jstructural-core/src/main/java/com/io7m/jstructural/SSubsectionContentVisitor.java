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
 * A subsection content visitor.
 * 
 * @param <A>
 *          The type of values returned by the visitor.
 */

public interface SSubsectionContentVisitor<A>
{
  /**
   * Visit a formal item.
   * 
   * @param formal
   *          The formal item element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitFormalItem(
    final @Nonnull SFormalItem formal)
    throws ConstraintError,
      Exception;

  /**
   * Visit a paragraph.
   * 
   * @param paragraph
   *          The paragraph element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitParagraph(
    final @Nonnull SParagraph paragraph)
    throws ConstraintError,
      Exception;
}
