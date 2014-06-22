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

package com.io7m.jstructural.core;

/**
 * List item content visitor.
 * 
 * @param <A>
 *          The type of values returned by the visitor
 */

public interface SListItemContentVisitor<A>
{
  /**
   * Visit a footnote element.
   * 
   * @param footnote
   *          The footnote element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitFootnote(
    final SFootnote footnote)
    throws Exception;

  /**
   * Visit an image element.
   * 
   * @param image
   *          The image element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitImage(
    final SImage image)
    throws Exception;

  /**
   * Visit a link element.
   * 
   * @param link
   *          The link element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitLink(
    final SLink link)
    throws Exception;

  /**
   * Visit a link element.
   * 
   * @param link
   *          The link element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitLinkExternal(
    final SLinkExternal link)
    throws Exception;

  /**
   * Visit an ordered list element.
   * 
   * @param list
   *          The list element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitListOrdered(
    final SListOrdered list)
    throws Exception;

  /**
   * Visit an unordered list element.
   * 
   * @param list
   *          The list element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitListUnordered(
    final SListUnordered list)
    throws Exception;

  /**
   * Visit a term element.
   * 
   * @param term
   *          The term element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitTerm(
    final STerm term)
    throws Exception;

  /**
   * Visit a text element.
   * 
   * @param text
   *          The text element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitText(
    final SText text)
    throws Exception;

  /**
   * Visit a verbatim element.
   * 
   * @param text
   *          The verbatim element
   * @return A value of type <code>A</code>
   * 
   * @throws Exception
   *           If required
   */

  A visitVerbatim(
    final SVerbatim text)
    throws Exception;
}
