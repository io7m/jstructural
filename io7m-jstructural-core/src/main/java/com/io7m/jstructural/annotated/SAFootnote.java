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
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.Ranges;
import com.io7m.jstructural.core.SNonEmptyList;
import net.jcip.annotations.Immutable;

/**
 * A footnote element.
 */

@Immutable public final class SAFootnote implements SAParagraphContent,
  SAListItemContent,
  SAFootnoteContent,
  SATableCellContent
{
  private final SNonEmptyList<SAFootnoteContent> content;
  private final int                              number;

  SAFootnote(
    final int in_number,
    final SNonEmptyList<SAFootnoteContent> in_content)
  {
    this.number = RangeCheck.checkIncludedInInteger(
      in_number,
      "Footnote number",
      Ranges.NATURAL_INTEGER,
      "Valid footnote number range");

    this.content = NullCheck.notNull(in_content, "Content");
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
    final SAFootnote other = (SAFootnote) obj;
    if (!this.content.equals(other.content)) {
      return false;
    }
    return this.number == other.number;
  }

  @Override public <A> A footnoteContentAccept(
    final SAFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitFootnote(this);
  }

  /**
   * @return The element content
   */

  public SNonEmptyList<SAFootnoteContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The footnote number, unique over a document
   */

  public int getNumber()
  {
    return this.number;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.number;
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final SAListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A tableCellContentAccept(
    final SATableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitFootnote(this);
  }
}
