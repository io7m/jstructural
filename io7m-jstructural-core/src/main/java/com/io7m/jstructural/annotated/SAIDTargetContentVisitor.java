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
 * A visitor that can visit elements that can be assigned IDs.
 * 
 * @param <T>
 *          The type of values returned by the visitor
 */

public interface SAIDTargetContentVisitor<T>
{
  /**
   * Visit a paragraph element.
   * 
   * @param paragraph
   *          The paragraph element
   * @return A value of type <code>T</code>
   * 
   * @throws Exception
   *           If required
   */

  T visitParagraph(
    final SAParagraph paragraph)
    throws Exception;

  /**
   * Visit a part element.
   * 
   * @param part
   *          The part element
   * @return A value of type <code>T</code>
   * 
   * @throws Exception
   *           If required
   */

  T visitPart(
    final SAPart part)
    throws Exception;

  /**
   * Visit a section element.
   * 
   * @param section
   *          The section element
   * @return A value of type <code>T</code>
   * 
   * @throws Exception
   *           If required
   */

  T visitSection(
    final SASection section)
    throws Exception;

  /**
   * Visit a subsection element.
   * 
   * @param subsection
   *          The subsection element
   * @return A value of type <code>T</code>
   * 
   * @throws Exception
   *           If required
   */

  T visitSubsection(
    final SASubsection subsection)
    throws Exception;
}
