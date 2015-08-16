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
 * A paragraph element.
 */

@Immutable public final class SAParagraph
  implements SASubsectionContent, SAIDTargetContent
{
  private final SNonEmptyList<SAParagraphContent> content;
  private final OptionType<SAID>                  id;
  private final SAParagraphNumber                 number;
  private final OptionType<String>                type;

  /**
   * Construct a new paragraph.
   *
   * @param in_number  The paragraph number
   * @param in_type    The type attribute
   * @param in_id      The ID
   * @param in_content The paragraph content.
   */

  public SAParagraph(
    final SAParagraphNumber in_number,
    final OptionType<String> in_type,
    final SNonEmptyList<SAParagraphContent> in_content,
    final OptionType<SAID> in_id)
  {
    this.number = NullCheck.notNull(in_number, "Number");
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
    final SAParagraph other = (SAParagraph) obj;
    return this.content.equals(other.content)
           && this.number.equals(other.number)
           && this.id.equals(other.id)
           && this.type.equals(other.type);
  }

  /**
   * @return The element content
   */

  public SNonEmptyList<SAParagraphContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The paragraph's ID.
   */

  public OptionType<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The paragraph number
   */

  public SAParagraphNumber getNumber()
  {
    return this.number;
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
    result = (prime * result) + this.number.hashCode();
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final SASubsectionContentVisitor<A> v)
    throws Exception
  {
    return v.visitParagraph(this);
  }

  @Override public <T> T targetContentAccept(
    final SAIDTargetContentVisitor<T> v)
    throws Exception
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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
