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

package com.io7m.jstructural.xom;

import javax.annotation.Nonnull;

import nu.xom.Element;

/**
 * A set of callbacks allowing for document customisation.
 */

public interface SDocumentXHTMLWriterCallbacks
{
  /**
   * Called upon closing of the XHTML body element.
   * 
   * @param body
   *          The XHTML body element
   */

  void onBodyEnd(
    final @Nonnull Element body);

  /**
   * Called upon creation of the XHTML body element.
   * 
   * @param body
   *          The XHTML body element
   */

  void onBodyStart(
    final @Nonnull Element body);

  /**
   * Called upon creation of the XHTML head element.
   * 
   * @param head
   *          The XHTML head element
   */

  void onHead(
    final @Nonnull Element head);
}
