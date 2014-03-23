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

package com.io7m.jstructural;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;

/**
 * An image.
 */

@Immutable public final class SImage implements
  SParagraphContent,
  SLinkContent,
  SListItemContent,
  SFootnoteContent,
  SFormalItemContent,
  STableCellContent
{
  /**
   * Construct a image with the given URI.
   * 
   * @param uri
   *          The URI
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SImage image(
    final @Nonnull URI uri,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Option<Integer> no_height = Option.none();
    final Option<String> no_type = Option.none();
    return new SImage(uri, no_type, no_height, no_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and height.
   * 
   * @param uri
   *          The URI
   * @param height
   *          The height
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull SImage imageHeight(
    final @Nonnull URI uri,
    final int height,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Some<Integer> some_height =
      Option.some(Constraints.constrainRange(
        height,
        1,
        Integer.MAX_VALUE,
        "Height"));

    final Option<Integer> no_width = Option.none();
    final Option<String> no_type = Option.none();
    return new SImage(uri, no_type, no_width, some_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute.
   * 
   * @param uri
   *          The URI
   * @param type
   *          The type attribute
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SImage imageTyped(
    final @Nonnull URI uri,
    final @Nonnull String type,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));

    final Option<Integer> no_height = Option.none();
    return new SImage(uri, some_type, no_height, no_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and height.
   * 
   * @param uri
   *          The URI
   * @param type
   *          The type attribute
   * @param height
   *          The height
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull SImage imageTypedHeight(
    final @Nonnull URI uri,
    final @Nonnull String type,
    final int height,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Some<Integer> some_height =
      Option.some(Constraints.constrainRange(
        height,
        1,
        Integer.MAX_VALUE,
        "Height"));

    final Option<Integer> no_width = Option.none();
    return new SImage(uri, some_type, no_width, some_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and width.
   * 
   * @param uri
   *          The URI
   * @param type
   *          The type attribute
   * @param width
   *          The width
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull SImage imageTypedWidth(
    final @Nonnull URI uri,
    final @Nonnull String type,
    final int width,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Some<Integer> some_width =
      Option.some(Constraints.constrainRange(
        width,
        1,
        Integer.MAX_VALUE,
        "Width"));

    final Option<Integer> no_height = Option.none();
    return new SImage(uri, some_type, some_width, no_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and width/height.
   * 
   * @param uri
   *          The URI
   * @param type
   *          The type attribute
   * @param width
   *          The width
   * @param height
   *          The height
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull
    SImage
    imageTypedWidthHeight(
      final @Nonnull URI uri,
      final @Nonnull String type,
      final int width,
      final int height,
      final @Nonnull String text)
      throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Some<Integer> some_width =
      Option.some(Constraints.constrainRange(
        width,
        1,
        Integer.MAX_VALUE,
        "Width"));
    final Some<Integer> some_height =
      Option.some(Constraints.constrainRange(
        height,
        1,
        Integer.MAX_VALUE,
        "Height"));

    return new SImage(uri, some_type, some_width, some_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and width.
   * 
   * @param uri
   *          The URI
   * @param width
   *          The width
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull SImage imageWidth(
    final @Nonnull URI uri,
    final int width,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Some<Integer> some_width =
      Option.some(Constraints.constrainRange(
        width,
        1,
        Integer.MAX_VALUE,
        "Width"));

    final Option<Integer> no_height = Option.none();
    final Option<String> no_type = Option.none();
    return new SImage(uri, no_type, some_width, no_height, text);
  }

  /**
   * Construct a image with the given URI, type attribute, and width/height.
   * 
   * @param uri
   *          The URI
   * @param width
   *          The width
   * @param height
   *          The height
   * @param text
   *          The image text
   * @return A new image
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  @SuppressWarnings("boxing") public static @Nonnull SImage imageWidthHeight(
    final @Nonnull URI uri,
    final int width,
    final int height,
    final @Nonnull String text)
    throws ConstraintError
  {
    final Some<Integer> some_width =
      Option.some(Constraints.constrainRange(
        width,
        1,
        Integer.MAX_VALUE,
        "Width"));
    final Some<Integer> some_height =
      Option.some(Constraints.constrainRange(
        height,
        1,
        Integer.MAX_VALUE,
        "Height"));

    final Option<String> no_type = Option.none();
    return new SImage(uri, no_type, some_width, some_height, text);
  }

  private final @Nonnull Option<Integer> height;

  private final @Nonnull String          text;

  private final @Nonnull Option<String>  type;
  private final @Nonnull URI             uri;
  private final @Nonnull Option<Integer> width;

  private SImage(
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
    final SImage other = (SImage) obj;
    return this.height.equals(other.height)
      && this.text.equals(other.text)
      && this.type.equals(other.type)
      && this.uri.equals(other.uri)
      && this.width.equals(other.width);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A formalItemContentAccept(
    final @Nonnull SFormalItemContentVisitor<A> v)
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
    final @Nonnull SLinkContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull STableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitImage(this);
  }
}
