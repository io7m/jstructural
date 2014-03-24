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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;

/**
 * A formal item.
 */

@Immutable public final class SFormalItem implements SSubsectionContent
{
  /**
   * Construct a new formal item with the given type attribute.
   * 
   * @param in_title
   *          The title
   * @param in_kind
   *          The kind of formal item
   * @param in_content
   *          The formal item content
   * @return A new formal item
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SFormalItem formalItem(
    final @Nonnull SFormalItemTitle in_title,
    final @Nonnull String in_kind,
    final @Nonnull SFormalItemContent in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    return new SFormalItem(in_title, in_kind, no_type, in_content);
  }

  /**
   * Construct a new formal item with the given type attribute.
   * 
   * @param in_title
   *          The title
   * @param in_kind
   *          The kind of formal item
   * @param in_type
   *          The type attribute
   * @param in_content
   *          The formal item content
   * @return A new formal item
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SFormalItem formalItemTyped(
    final @Nonnull SFormalItemTitle in_title,
    final @Nonnull String in_kind,
    final @Nonnull String in_type,
    final @Nonnull SFormalItemContent in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(in_type, "Type"));
    return new SFormalItem(in_title, in_kind, some_type, in_content);
  }

  private final @Nonnull SFormalItemContent content;

  private final @Nonnull String             kind;
  private final @Nonnull SFormalItemTitle   title;
  private final @Nonnull Option<String>     type;
  private SFormalItem(
    final @Nonnull SFormalItemTitle in_title,
    final @Nonnull String in_kind,
    final @Nonnull Option<String> in_type,
    final @Nonnull SFormalItemContent in_content)
    throws ConstraintError
  {
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
    final SFormalItem other = (SFormalItem) obj;
    return this.content.equals(other.content)
      && this.kind.equals(other.kind)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The formal item content
   */

  public @Nonnull SFormalItemContent getContent()
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
   * @return The formal item title
   */

  public @Nonnull SFormalItemTitle getTitle()
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
    final @Nonnull SSubsectionContentVisitor<A> v)
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
