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

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SNonEmptyList;
import net.jcip.annotations.Immutable;

/**
 * An unordered list element.
 */

@Immutable public final class SAListUnordered implements SAListItemContent,
  SAFootnoteContent,
  SAParagraphContent,
  SAFormalItemContent,
  SATableCellContent
{
  private final SNonEmptyList<SAListItem> items;
  private final OptionType<String>        type;

  SAListUnordered(
    final OptionType<String> in_type,
    final SNonEmptyList<SAListItem> in_items)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.items = NullCheck.notNull(in_items, "Items");
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
    final SAListUnordered other = (SAListUnordered) obj;
    return this.items.equals(other.items) && this.type.equals(other.type);
  }

  @Override public <A> A footnoteContentAccept(
    final SAFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitListUnordered(this);
  }

  @Override public <A> A formalItemContentAccept(
    final SAFormalItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitListUnordered(this);
  }

  /**
   * @return The list items
   */

  public SNonEmptyList<SAListItem> getItems()
  {
    return this.items;
  }

  /**
   * @return The type attribute
   */

  public OptionType<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.items.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final SAListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitListUnordered(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitListUnordered(this);
  }

  @Override public <A> A tableCellContentAccept(
    final SATableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitListUnordered(this);
  }
}
