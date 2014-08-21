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

/**
 * A formal item.
 */

public final class SFormalItem implements SSubsectionContent
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
   */

  public static SFormalItem formalItem(
    final SFormalItemTitle in_title,
    final String in_kind,
    final SFormalItemContent in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    return new SFormalItem(in_title, in_kind, no_type, no_id, in_content);
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
   */

  public static SFormalItem formalItemTyped(
    final SFormalItemTitle in_title,
    final String in_kind,
    final String in_type,
    final SFormalItemContent in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(in_type, "Type"));
    final OptionType<SID> no_id = Option.none();
    return new SFormalItem(in_title, in_kind, some_type, no_id, in_content);
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
   * @param in_id
   *          The ID
   * @return A new formal item
   * @since 4.0.0
   */

  public static SFormalItem formalItemTypedWithID(
    final SFormalItemTitle in_title,
    final String in_kind,
    final String in_type,
    final SFormalItemContent in_content,
    final SID in_id)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(in_type, "Type"));
    final OptionType<SID> some_id =
      Option.some(NullCheck.notNull(in_id, "ID"));
    return new SFormalItem(in_title, in_kind, some_type, some_id, in_content);
  }

  /**
   * Construct a new formal item with the given type attribute.
   *
   * @param in_title
   *          The title
   * @param in_kind
   *          The kind of formal item
   * @param in_content
   *          The formal item content
   * @param in_id
   *          The ID
   * @return A new formal item
   * @since 4.0.0
   */

  public static SFormalItem formalItemWithID(
    final SFormalItemTitle in_title,
    final String in_kind,
    final SFormalItemContent in_content,
    final SID in_id)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id =
      Option.some(NullCheck.notNull(in_id, "ID"));
    return new SFormalItem(in_title, in_kind, no_type, some_id, in_content);
  }

  private final SFormalItemContent content;
  private final OptionType<SID>    id;
  private final String             kind;
  private final SFormalItemTitle   title;
  private final OptionType<String> type;

  private SFormalItem(
    final SFormalItemTitle in_title,
    final String in_kind,
    final OptionType<String> in_type,
    final OptionType<SID> in_id,
    final SFormalItemContent in_content)
  {
    this.title = NullCheck.notNull(in_title, "Title");
    this.kind = NullCheck.notNull(in_kind, "Kind");
    this.type = NullCheck.notNull(in_type, "Type");
    this.id = NullCheck.notNull(in_id, "ID");
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
    final SFormalItem other = (SFormalItem) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.kind.equals(other.kind)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The formal item content
   */

  public SFormalItemContent getContent()
  {
    return this.content;
  }

  /**
   * @return The ID of the element, if any.
   */

  public OptionType<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The kind of formal item
   */

  public String getKind()
  {
    return this.kind;
  }

  /**
   * @return The formal item title
   */

  public SFormalItemTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The formal item type attribute, if specified
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
    result = (prime * result) + this.kind.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final SSubsectionContentVisitor<A> v)
    throws Exception
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
    builder.append(" id=");
    builder.append(this.id);
    builder.append(" title=");
    builder.append(this.title);
    builder.append(" type=");
    builder.append(this.type);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
