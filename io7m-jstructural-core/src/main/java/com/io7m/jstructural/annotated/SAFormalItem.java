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
import com.io7m.jranges.RangeCheck;

/**
 * A formal item.
 */

public final class SAFormalItem implements
  SASubsectionContent,
  SAIDTargetContent
{
  private final OptionType<SAID>    id;
  private final SAFormalItemContent content;
  private final int                 formal_number;
  private final String              kind;
  private final SAFormalItemNumber  number;
  private final SAFormalItemTitle   title;
  private final OptionType<String>  type;

  SAFormalItem(
    final SAFormalItemNumber in_number,
    final SAFormalItemTitle in_title,
    final String in_kind,
    final OptionType<String> in_type,
    final SAFormalItemContent in_content,
    final int in_formal_number,
    final OptionType<SAID> in_id)
  {
    this.number = NullCheck.notNull(in_number, "Number");
    this.title = NullCheck.notNull(in_title, "Title");
    this.kind = NullCheck.notNull(in_kind, "Kind");
    this.type = NullCheck.notNull(in_type, "Type");
    this.content = NullCheck.notNull(in_content, "Content");
    this.id = NullCheck.notNull(in_id, "ID");

    this.formal_number =
      (int) RangeCheck.checkIncludedIn(
        in_formal_number,
        "Formal item number",
        RangeCheck.POSITIVE_INTEGER,
        "Valid formal item number range");
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
    final SAFormalItem other = (SAFormalItem) obj;
    return this.content.equals(other.content)
      && this.number.equals(other.number)
      && this.kind.equals(other.kind)
      && this.title.equals(other.title)
      && this.type.equals(other.type)
      && this.id.equals(other.id)
      && (this.formal_number == other.formal_number);
  }

  /**
   * @return The formal item content
   */

  public SAFormalItemContent getContent()
  {
    return this.content;
  }

  /**
   * @return The formal item number.
   */

  public int getFormalNumber()
  {
    return this.formal_number;
  }

  /**
   * @return The kind of formal item
   */

  public String getKind()
  {
    return this.kind;
  }

  /**
   * @return The formal item number
   */

  public SAFormalItemNumber getNumber()
  {
    return this.number;
  }

  /**
   * @return The formal item title
   */

  public SAFormalItemTitle getTitle()
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
    result = (prime * result) + this.formal_number;
    return result;
  }

  @Override public <A> A subsectionContentAccept(
    final SASubsectionContentVisitor<A> v)
    throws Exception
  {
    return v.visitFormalItem(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAFormalItem content=");
    builder.append(this.content);
    builder.append(" kind=");
    builder.append(this.kind);
    builder.append(" number=");
    builder.append(this.number);
    builder.append(" title=");
    builder.append(this.title);
    builder.append(" type=");
    builder.append(this.type);
    builder.append(" id=");
    builder.append(this.id);
    builder.append(" formal_number=");
    builder.append(this.formal_number);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }

  @Override public <T> T targetContentAccept(
    final SAIDTargetContentVisitor<T> v)
    throws Exception
  {
    return v.visitFormalItem(this);
  }
}
