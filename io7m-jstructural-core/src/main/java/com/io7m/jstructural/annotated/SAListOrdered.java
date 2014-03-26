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
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * An ordered list element.
 */

@Immutable public final class SAListOrdered implements
  SAListItemContent,
  SAFootnoteContent,
  SAParagraphContent,
  SAFormalItemContent,
  SATableCellContent
{
  private final @Nonnull SNonEmptyList<SAListItem> items;
  private final @Nonnull Option<String>            type;

  SAListOrdered(
    final @Nonnull Option<String> in_type,
    final @Nonnull SNonEmptyList<SAListItem> in_items)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.items = Constraints.constrainNotNull(in_items, "Items");
  }

  @Override public boolean equals(
    final Object obj)
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
    final SAListOrdered other = (SAListOrdered) obj;
    return this.items.equals(other.items) && this.type.equals(other.type);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SAFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitListOrdered(this);
  }

  @Override public <A> A formalItemContentAccept(
    final @Nonnull SAFormalItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitListOrdered(this);
  }

  /**
   * @return The list items
   */

  public @Nonnull SNonEmptyList<SAListItem> getItems()
  {
    return this.items;
  }

  /**
   * @return The type attribute
   */

  public @Nonnull Option<String> getType()
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
    final @Nonnull SAListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitListOrdered(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SAParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitListOrdered(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull SATableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitListOrdered(this);
  }
}
