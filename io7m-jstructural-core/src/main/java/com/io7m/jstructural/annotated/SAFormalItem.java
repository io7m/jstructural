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

/**
 * A formal item.
 */

@Immutable public final class SAFormalItem implements SASubsectionContent
{
  private final @Nonnull SAFormalItemContent content;
  private final @Nonnull String              kind;
  private final @Nonnull SAFormalItemTitle   title;
  private final @Nonnull Option<String>      type;
  private final @Nonnull SAParagraphNumber   number;

  SAFormalItem(
    final @Nonnull SAParagraphNumber in_number,
    final @Nonnull SAFormalItemTitle in_title,
    final @Nonnull String in_kind,
    final @Nonnull Option<String> in_type,
    final @Nonnull SAFormalItemContent in_content)
    throws ConstraintError
  {
    this.number = Constraints.constrainNotNull(in_number, "Number");
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.kind = Constraints.constrainNotNull(in_kind, "Kind");
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.content = Constraints.constrainNotNull(in_content, "Content");
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
    final SAFormalItem other = (SAFormalItem) obj;
    return this.content.equals(other.content)
      && this.number.equals(other.number)
      && this.kind.equals(other.kind)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The formal item content
   */

  public @Nonnull SAFormalItemContent getContent()
  {
    return this.content;
  }

  /**
   * @return The kind of formal item
   */

  public @Nonnull String getKind()
  {
    return this.kind;
  }

  /**
   * @return The formal item number (note that formal items share numbers with
   *         paragraphs)
   */

  public @Nonnull SAParagraphNumber getNumber()
  {
    return this.number;
  }

  /**
   * @return The formal item title
   */

  public @Nonnull SAFormalItemTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The formal item type attribute, if specified
   */

  public @Nonnull Option<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.kind.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final @Nonnull SASubsectionContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFormalItem(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SFormalItem content=");
    builder.append(this.content);
    builder.append(" kind=");
    builder.append(this.kind);
    builder.append(" title=");
    builder.append(this.title);
    builder.append(" type=");
    builder.append(this.type);
    builder.append("]");
    return builder.toString();
  }
}
