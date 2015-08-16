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
   */

  public static SParagraph paragraph(
    final SNonEmptyList<SParagraphContent> content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
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
   */

  public static SParagraph paragraphID(
    final SID id,
    final SNonEmptyList<SParagraphContent> content)
  {
    final OptionType<SID> some_id = Option.some(id);
    final OptionType<String> no_type = Option.none();
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
   */

  public static SParagraph paragraphTyped(
    final String type,
    final SNonEmptyList<SParagraphContent> content)
  {
    final OptionType<String> some =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> none = Option.none();
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
   */

  public static SParagraph paragraphTypedID(
    final String type,
    final SID id,
    final SNonEmptyList<SParagraphContent> content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(id);
    return new SParagraph(some_type, content, some_id);
  }

  private final SNonEmptyList<SParagraphContent> content;
  private final OptionType<SID>                  id;
  private final OptionType<String>               type;

  private SParagraph(
    final OptionType<String> in_type,
    final SNonEmptyList<SParagraphContent> in_content,
    final OptionType<SID> in_id)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.content = NullCheck.notNull(in_content, "Content");
    this.id = NullCheck.notNull(in_id, "ID");
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
    final SParagraph other = (SParagraph) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public SNonEmptyList<SParagraphContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The paragraph's ID.
   */

  public OptionType<SID> getID()
  {
    return this.id;
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
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final SSubsectionContentVisitor<A> v)
    throws Exception
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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
