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

package com.io7m.jstructural;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;

/**
 * A paragraph element.
 */

@Immutable public final class SParagraph implements SSubsectionContent
{
  /**
   * Construct a new paragraph.
   * 
   * @param content
   *          The paragraph content.
   * @return A new paragraph
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SParagraph paragraph(
    final @Nonnull SNonEmptyList<SParagraphContent> content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    return new SParagraph(no_type, content, no_id);
  }

  /**
   * Construct a new paragraph with an ID.
   * 
   * @param id
   *          The ID
   * @param content
   *          The paragraph content.
   * @return A new paragraph
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SParagraph paragraphID(
    final @Nonnull SID id,
    final @Nonnull SNonEmptyList<SParagraphContent> content)
    throws ConstraintError
  {
    final Option<SID> some_id = Option.some(id);
    final Option<String> no_type = Option.none();
    return new SParagraph(no_type, content, some_id);
  }

  /**
   * Construct a new paragraph with a type attribute.
   * 
   * @param type
   *          The type attribute
   * @param content
   *          The paragraph content.
   * @return A new paragraph
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SParagraph paragraphTyped(
    final @Nonnull String type,
    final @Nonnull SNonEmptyList<SParagraphContent> content)
    throws ConstraintError
  {
    final Option<String> some =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> none = Option.none();
    return new SParagraph(some, content, none);
  }

  /**
   * Construct a new paragraph with an ID and type attribute.
   * 
   * @param type
   *          The type attribute
   * @param id
   *          The ID
   * @param content
   *          The paragraph content.
   * @return A new paragraph
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SParagraph paragraphTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SNonEmptyList<SParagraphContent> content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id = Option.some(id);
    return new SParagraph(some_type, content, some_id);
  }

  private final @Nonnull SNonEmptyList<SParagraphContent> content;
  private final @Nonnull Option<SID>                      id;
  private final @Nonnull Option<String>                   type;

  private SParagraph(
    final @Nonnull Option<String> in_type,
    final @Nonnull SNonEmptyList<SParagraphContent> in_content,
    final @Nonnull Option<SID> in_id)
    throws ConstraintError
  {
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
    final SParagraph other = (SParagraph) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public @Nonnull SNonEmptyList<SParagraphContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The paragraph's ID.
   */

  public @Nonnull Option<SID> getID()
  {
    return this.id;
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
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final @Nonnull SSubsectionContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitParagraph(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SParagraph content=");
    builder.append(this.content);
    builder.append(" id=");
    builder.append(this.id);
    builder.append(" type=");
    builder.append(this.type);
    builder.append("]");
    return builder.toString();
  }
}
