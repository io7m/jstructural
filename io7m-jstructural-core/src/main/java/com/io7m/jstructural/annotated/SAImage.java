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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;

/**
 * An image.
 */

@Immutable public final class SAImage implements
  SAParagraphContent,
  SALinkContent,
  SAListItemContent,
  SAFootnoteContent,
  SAFormalItemContent,
  SATableCellContent
{
  private final @Nonnull Option<Integer> height;
  private final @Nonnull String          text;
  private final @Nonnull Option<String>  type;
  private final @Nonnull URI             uri;
  private final @Nonnull Option<Integer> width;

  SAImage(
    final @Nonnull URI in_uri,
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<Integer> in_width,
    final @Nonnull Option<Integer> in_height,
    final @Nonnull String in_text)
    throws ConstraintError
  {
    this.uri = Constraints.constrainNotNull(in_uri, "URI");
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.width = Constraints.constrainNotNull(in_width, "Width");
    this.height = Constraints.constrainNotNull(in_height, "Height");
    this.text = Constraints.constrainNotNull(in_text, "Text");
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
    final SAImage other = (SAImage) obj;
    return this.height.equals(other.height)
      && this.text.equals(other.text)
      && this.type.equals(other.type)
      && this.uri.equals(other.uri)
      && this.width.equals(other.width);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SAFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A formalItemContentAccept(
    final @Nonnull SAFormalItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  /**
   * @return The image height specified, if any
   */

  public @Nonnull Option<Integer> getHeight()
  {
    return this.height;
  }

  /**
   * @return The image text
   */

  public @Nonnull String getText()
  {
    return this.text;
  }

  /**
   * @return The term's type attribute
   */

  public @Nonnull Option<String> getType()
  {
    return this.type;
  }

  /**
   * @return The image URI
   */

  public @Nonnull URI getURI()
  {
    return this.uri;
  }

  /**
   * @return The image width specified, if any
   */

  public @Nonnull Option<Integer> getWidth()
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
    final @Nonnull SALinkContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SAListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SAParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull SATableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }
}
