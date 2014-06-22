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

import java.net.URI;

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;

/**
 * An image.
 */

public final class SImage implements
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
   */

  public static SImage image(
    final URI uri,
    final String text)
  {
    final OptionType<Integer> no_height = Option.none();
    final OptionType<String> no_type = Option.none();
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
   */

  @SuppressWarnings({ "boxing", "null" }) public static SImage imageHeight(
    final URI uri,
    final int height,
    final String text)
  {
    final OptionType<Integer> some_height =
      Option.some((int) RangeCheck.checkIncludedIn(
        height,
        "Height",
        RangeCheck.POSITIVE_INTEGER,
        "Valid height range"));

    final OptionType<Integer> no_width = Option.none();
    final OptionType<String> no_type = Option.none();
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
   */

  public static SImage imageTyped(
    final URI uri,
    final String type,
    final String text)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));

    final OptionType<Integer> no_height = Option.none();
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
   */

  @SuppressWarnings({ "boxing", "null" }) public static
    SImage
    imageTypedHeight(
      final URI uri,
      final String type,
      final int height,
      final String text)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<Integer> some_height =
      Option.some((int) RangeCheck.checkIncludedIn(
        height,
        "Height",
        RangeCheck.POSITIVE_INTEGER,
        "Valid height range"));

    final OptionType<Integer> no_width = Option.none();
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
   */

  @SuppressWarnings({ "boxing", "null" }) public static
    SImage
    imageTypedWidth(
      final URI uri,
      final String type,
      final int width,
      final String text)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<Integer> some_width =
      Option.some((int) RangeCheck.checkIncludedIn(
        width,
        "Width",
        RangeCheck.POSITIVE_INTEGER,
        "Valid width range"));

    final OptionType<Integer> no_height = Option.none();
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
   */

  @SuppressWarnings({ "boxing", "null" }) public static
    SImage
    imageTypedWidthHeight(
      final URI uri,
      final String type,
      final int width,
      final int height,
      final String text)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<Integer> some_width =
      Option.some((int) RangeCheck.checkIncludedIn(
        width,
        "Width",
        RangeCheck.POSITIVE_INTEGER,
        "Valid width range"));
    final OptionType<Integer> some_height =
      Option.some((int) RangeCheck.checkIncludedIn(
        height,
        "Height",
        RangeCheck.POSITIVE_INTEGER,
        "Valid height range"));

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
   */

  @SuppressWarnings({ "boxing", "null" }) public static SImage imageWidth(
    final URI uri,
    final int width,
    final String text)
  {
    final OptionType<Integer> some_width =
      Option.some((int) RangeCheck.checkIncludedIn(
        width,
        "Width",
        RangeCheck.POSITIVE_INTEGER,
        "Valid width range"));

    final OptionType<Integer> no_height = Option.none();
    final OptionType<String> no_type = Option.none();
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
   */

  @SuppressWarnings({ "boxing", "null" }) public static
    SImage
    imageWidthHeight(
      final URI uri,
      final int width,
      final int height,
      final String text)
  {
    final OptionType<Integer> some_width =
      Option.some((int) RangeCheck.checkIncludedIn(
        width,
        "Width",
        RangeCheck.POSITIVE_INTEGER,
        "Valid width range"));
    final OptionType<Integer> some_height =
      Option.some((int) RangeCheck.checkIncludedIn(
        height,
        "Height",
        RangeCheck.POSITIVE_INTEGER,
        "Valid height range"));

    final OptionType<String> no_type = Option.none();
    return new SImage(uri, no_type, some_width, some_height, text);
  }

  private final OptionType<Integer> height;
  private final String              text;
  private final OptionType<String>  type;
  private final URI                 uri;
  private final OptionType<Integer> width;

  private SImage(
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
    final SImage other = (SImage) obj;
    return this.height.equals(other.height)
      && this.text.equals(other.text)
      && this.type.equals(other.type)
      && this.uri.equals(other.uri)
      && this.width.equals(other.width);
  }

  @Override public <A> A footnoteContentAccept(
    final SFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A formalItemContentAccept(
    final SFormalItemContentVisitor<A> v)
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
    final SLinkContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A listItemContentAccept(
    final SListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }

  @Override public <A> A tableCellContentAccept(
    final STableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitImage(this);
  }
}
