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
 * The type of inline content.
 *
 * @param <T> The type of data associated with the AST
 */

public interface SInlineAnyContentType<T> extends SContentType<T>
{
  @Override
  default ContentKind contentKind()
  {
    return ContentKind.CONTENT_INLINE_ANY;
  }

  /**
   * @return The precise kind of inline content
   */

  InlineKind inlineKind();

  /**
   * The precise kind of inline content.
   */

  enum InlineKind
  {
    /**
     * @see STextType
     */

    INLINE_TEXT,

    /**
     * @see STermType
     */

    INLINE_TERM,

    /**
     * @see SImageType
     */

    INLINE_IMAGE,

    /**
     * @see SLinkType
     */

    INLINE_LINK,

    /**
     * @see SLinkExternalType
     */

    INLINE_LINK_EXTERNAL,

    /**
     * @see SFootnoteReferenceType
     */

    INLINE_FOOTNOTE_REFERENCE,

    /**
     * @see SFormalItemReferenceType
     */

    INLINE_FORMAL_ITEM_REFERENCE,

    /**
     * @see SVerbatimType
     */

    INLINE_VERBATIM,

    /**
     * @see SListOrderedType
     */

    INLINE_LIST_ORDERED,

    /**
     * @see SListUnorderedType
     */

    INLINE_LIST_UNORDERED,

    /**
     * @see STableType
     */

    INLINE_TABLE,
  }
}
