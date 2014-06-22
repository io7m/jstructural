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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A text element.
 */

public final class SText implements
  SParagraphContent,
  SLinkContent,
  SListItemContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new text element.
   * 
   * @param text
   *          The text
   * @return A new text element
   */

  public static SText text(
    final String text)
  {
    return new SText(text);
  }

  private final String text;

  private SText(
    final String in_text)
  {
    this.text = NullCheck.notNull(in_text, "Text");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SText other = (SText) obj;
    return this.text.equals(other.text);
  }

  @Override public <A> A footnoteContentAccept(
    final SFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  /**
   * @return The term's text
   */

  public String getText()
  {
    return this.text;
  }

  @Override public int hashCode()
  {
    return this.text.hashCode();
  }

  @Override public <A> A linkContentAccept(
    final SLinkContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A listItemContentAccept(
    final SListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A tableCellContentAccept(
    final STableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }
}
