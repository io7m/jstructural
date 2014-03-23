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
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;

/**
 * A document.
 */

public abstract class SDocument
{
  /**
   * The XML URI for structural documents.
   */

  public static final @Nonnull URI                 XML_URI;

  static {
    try {
      XML_URI = new URI("http://www.io7m.com/schemas/structural/1.0.0");
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private final @Nonnull Option<SDocumentContents> contents;
  private final @Nonnull Option<SDocumentStyle>    style;
  private final @Nonnull SDocumentTitle            title;

  protected SDocument(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull Option<SDocumentContents> in_contents,
    final @Nonnull Option<SDocumentStyle> in_style)
    throws ConstraintError
  {
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.contents = Constraints.constrainNotNull(in_contents, "Contents");
    this.style = Constraints.constrainNotNull(in_style, "Style");
  }

  /**
   * Accept a document visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws ConstraintError
   *           If the visitor raises {@link ConstraintError}
   * @throws Exception
   *           If the visitor raises an {@link Exception}
   * @param <A>
   *          The type of values returned by the visitor
   */

  public abstract <A> A documentAccept(
    final @Nonnull SDocumentVisitor<A> v)
    throws ConstraintError,
      Exception;

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
    final SDocument other = (SDocument) obj;
    return this.contents.equals(other.contents)
      && this.style.equals(other.style)
      && this.title.equals(other.title);
  }

  /**
   * @return The document contents
   */

  public final @Nonnull Option<SDocumentContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The document style
   */

  public final @Nonnull Option<SDocumentStyle> getStyle()
  {
    return this.style;
  }

  /**
   * @return The document title
   */

  public final @Nonnull SDocumentTitle getTitle()
  {
    return this.title;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.style.hashCode();
    result = (prime * result) + this.title.hashCode();
    return result;
  }
}
