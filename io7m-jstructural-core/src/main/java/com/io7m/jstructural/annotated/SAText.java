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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

/**
 * A text element.
 */

@Immutable public final class SAText implements SAParagraphContent,
  SALinkContent,
  SAListItemContent,
  SAFootnoteContent,
  SATableCellContent
{
  private final String text;

  /**
   * Construct text.
   *
   * @param in_text The text
   */

  public SAText(
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
    final SAText other = (SAText) obj;
    return this.text.equals(other.text);
  }

  @Override public <A> A footnoteContentAccept(
    final SAFootnoteContentVisitor<A> v)
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
    final SALinkContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A listItemContentAccept(
    final SAListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A tableCellContentAccept(
    final SATableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitText(this);
  }
}
