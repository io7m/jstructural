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
 * List item content visitor.
 * 
 * @param <A>
 *          The type of values returned by the visitor
 */

public interface SAListItemContentVisitor<A>
{
  /**
   * Visit a footnote element.
   * 
   * @param footnote
   *          The footnote element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitFootnote(
    final @Nonnull SAFootnote footnote)
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
    final @Nonnull SAImage image)
    throws ConstraintError,
      Exception;

  /**
   * Visit a link element.
   * 
   * @param link
   *          The link element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitLink(
    final @Nonnull SALink link)
    throws ConstraintError,
      Exception;

  /**
   * Visit a link element.
   * 
   * @param link
   *          The link element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitLinkExternal(
    final @Nonnull SALinkExternal link)
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
    final @Nonnull SAListOrdered list)
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
    final @Nonnull SAListUnordered list)
    throws ConstraintError,
      Exception;

  /**
   * Visit a term element.
   * 
   * @param term
   *          The term element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitTerm(
    final @Nonnull SATerm term)
    throws ConstraintError,
      Exception;

  /**
   * Visit a text element.
   * 
   * @param text
   *          The text element
   * @return A value of type <code>A</code>
   * @throws ConstraintError
   *           If required
   * @throws Exception
   *           If required
   */

  A visitText(
    final @Nonnull SAText text)
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
    final @Nonnull SAVerbatim text)
    throws ConstraintError,
      Exception;
}
