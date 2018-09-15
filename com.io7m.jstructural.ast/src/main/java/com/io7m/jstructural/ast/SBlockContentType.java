/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.ast;

/**
 * The type of block content.
 *
 * @param <T> The type of data associated with the AST
 */

public interface SBlockContentType<T> extends SContentType<T>
{
  @Override
  default ContentKind contentKind()
  {
    return ContentKind.CONTENT_BLOCK;
  }

  /**
   * @return The precise kind of block content
   */

  BlockKind blockKind();

  /**
   * The precise kind of block content.
   */

  enum BlockKind
  {
    /**
     * @see SSubsectionContentType
     */

    BLOCK_SUBSECTION_CONTENT,

    /**
     * @see SSubsectionType
     */

    BLOCK_SUBSECTION,

    /**
     * @see SSectionType
     */

    BLOCK_SECTION,

    /**
     * @see SDocumentType
     */

    BLOCK_DOCUMENT,
  }
}
