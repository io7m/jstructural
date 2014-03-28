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
 * A paragraph element.
 */

@Immutable public final class SAParagraph implements
  SASubsectionContent,
  SAIDTargetContent
{
  private final @Nonnull SNonEmptyList<SAParagraphContent> content;
  private final @Nonnull Option<SAID>                      id;
  private final @Nonnull SAParagraphNumber                 number;
  private final @Nonnull Option<String>                    type;

  /**
   * Construct a new paragraph.
   * 
   * @param in_number
   *          The paragraph number
   * @param in_type
   *          The type attribute
   * @param in_id
   *          The ID
   * @param in_content
   *          The paragraph content.
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SAParagraph(
    final @Nonnull SAParagraphNumber in_number,
    final @Nonnull Option<String> in_type,
    final @Nonnull SNonEmptyList<SAParagraphContent> in_content,
    final @Nonnull Option<SAID> in_id)
    throws ConstraintError
  {
    this.number = Constraints.constrainNotNull(in_number, "Number");
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.content = Constraints.constrainNotNull(in_content, "Content");
    this.id = Constraints.constrainNotNull(in_id, "ID");
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
    final SAParagraph other = (SAParagraph) obj;
    return this.content.equals(other.content)
      && this.number.equals(other.number)
      && this.id.equals(other.id)
      && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public @Nonnull SNonEmptyList<SAParagraphContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The paragraph's ID.
   */

  public @Nonnull Option<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The paragraph number
   */

  public @Nonnull SAParagraphNumber getNumber()
  {
    return this.number;
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
    result = (prime * result) + this.number.hashCode();
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final @Nonnull SASubsectionContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitParagraph(this);
  }

  @Override public <T> T targetContentAccept(
    final @Nonnull SAIDTargetContentVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitParagraph(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAParagraph content=");
    builder.append(this.content);
    builder.append(" id=");
    builder.append(this.id);
    builder.append(" type=");
    builder.append(this.type);
    builder.append(" number=");
    builder.append(this.number);
    builder.append("]");
    return builder.toString();
  }
}
