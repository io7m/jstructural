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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

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
   */

  public static SListItem listItem(
    final SNonEmptyList<SListItemContent> content)
  {
    final OptionType<String> none = Option.none();
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
   */

  public static SListItem listItemTyped(
    final String type,
    final SNonEmptyList<SListItemContent> content)
  {
    final OptionType<String> some =
      Option.some(NullCheck.notNull(type, "Type"));
    return new SListItem(some, content);
  }

  private final SNonEmptyList<SListItemContent> content;
  private final OptionType<String>              type;

  private SListItem(
    final OptionType<String> in_type,
    final SNonEmptyList<SListItemContent> in_content)
  {
    this.type = NullCheck.notNull(in_type, "Type");
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
    final SListItem other = (SListItem) obj;
    return this.content.equals(other.content) && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public SNonEmptyList<SListItemContent> getContent()
  {
    return this.content;
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
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }
}
