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
 * A simple term.
 */

@Immutable public final class STerm implements
  SListItemContent,
  SParagraphContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new term with the given text.
   * 
   * @param text
   *          The text
   * @return A new term
   */

  public static STerm term(
    final SText text)
  {
    final OptionType<String> none = Option.none();
    return new STerm(text, none);
  }

  /**
   * Construct a new term with the given text and type attribute.
   * 
   * @param text
   *          The text
   * @param type
   *          The type attribute
   * @return A new term
   */

  public static STerm termTyped(
    final SText text,
    final String type)
  {
    final OptionType<String> some =
      Option.some(NullCheck.notNull(type, "Type"));
    return new STerm(text, some);
  }

  private final SText              text;
  private final OptionType<String> type;

  private STerm(
    final SText in_text,
    final OptionType<String> in_type)
  {
    this.text = NullCheck.notNull(in_text, "Text");
    this.type = NullCheck.notNull(in_type, "Type");
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
    final STerm other = (STerm) obj;
    return this.text.equals(other.text) && this.type.equals(other.type);
  }

  @Override public <A> A footnoteContentAccept(
    final SFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitTerm(this);
  }

  /**
   * @return The term's text
   */

  public SText getText()
  {
    return this.text;
  }

  /**
   * @return The term's type attribute
   */

  public OptionType<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.text.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final SListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitTerm(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitTerm(this);
  }

  @Override public <A> A tableCellContentAccept(
    final STableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitTerm(this);
  }
}
