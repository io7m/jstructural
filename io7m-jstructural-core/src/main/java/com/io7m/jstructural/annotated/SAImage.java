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

import java.net.URI;

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * An image.
 */

public final class SAImage implements
  SAParagraphContent,
  SALinkContent,
  SAListItemContent,
  SAFootnoteContent,
  SAFormalItemContent,
  SATableCellContent
{
  private final OptionType<Integer> height;
  private final String              text;
  private final OptionType<String>  type;
  private final URI                 uri;
  private final OptionType<Integer> width;

  SAImage(
    final URI in_uri,
    final OptionType<String> in_type,
    final OptionType<Integer> in_width,
    final OptionType<Integer> in_height,
    final String in_text)
  {
    this.uri = NullCheck.notNull(in_uri, "URI");
    this.type = NullCheck.notNull(in_type, "Type");
    this.width = NullCheck.notNull(in_width, "Width");
    this.height = NullCheck.notNull(in_height, "Height");
    this.text = NullCheck.notNull(in_text, "Text");
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
    final SAImage other = (SAImage) obj;
    return this.height.equals(other.height)
      && this.text.equals(other.text)
      && this.type.equals(other.type)
      && this.uri.equals(other.uri)
      && this.width.equals(other.width);
  }

  @Override public <A> A footnoteContentAccept(
    final SAFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A formalItemContentAccept(
    final SAFormalItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  /**
   * @return The image height specified, if any
   */

  public OptionType<Integer> getHeight()
  {
    return this.height;
  }

  /**
   * @return The image text
   */

  public String getText()
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

  /**
   * @return The image URI
   */

  public URI getURI()
  {
    return this.uri;
  }

  /**
   * @return The image width specified, if any
   */

  public OptionType<Integer> getWidth()
  {
    return this.width;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.height.hashCode();
    result = (prime * result) + this.text.hashCode();
    result = (prime * result) + this.type.hashCode();
    result = (prime * result) + this.uri.hashCode();
    result = (prime * result) + this.width.hashCode();
    return result;
  }

  @Override public <A> A linkContentAccept(
    final SALinkContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A listItemContentAccept(
    final SAListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A tableCellContentAccept(
    final SATableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }
}
