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
 * A list item element.
 */

@Immutable public final class SListItem
{
  /**
   * Construct a new list item.
   * 
   * @param content
   *          The list item content.
   * @return A new list item
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SListItem listItem(
    final @Nonnull SNonEmptyList<SListItemContent> content)
    throws ConstraintError
  {
    final Option<String> none = Option.none();
    return new SListItem(none, content);
  }

  /**
   * Construct a new list item with a type attribute.
   * 
   * @param type
   *          The type attribute
   * @param content
   *          The list item content.
   * @return A new list item
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SListItem listItemTyped(
    final @Nonnull String type,
    final @Nonnull SNonEmptyList<SListItemContent> content)
    throws ConstraintError
  {
    final Option<String> some =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    return new SListItem(some, content);
  }

  private final @Nonnull SNonEmptyList<SListItemContent> content;
  private final @Nonnull Option<String>                  type;

  private SListItem(
    final @Nonnull Option<String> in_type,
    final @Nonnull SNonEmptyList<SListItemContent> in_content)
    throws ConstraintError
  {
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
    final SListItem other = (SListItem) obj;
    return this.content.equals(other.content) && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public @Nonnull SNonEmptyList<SListItemContent> getContent()
  {
    return this.content;
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
    result = (prime * result) + this.type.hashCode();
    return result;
  }
}
