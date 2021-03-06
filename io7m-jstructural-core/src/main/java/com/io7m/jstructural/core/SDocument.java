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

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

/**
 * A document.
 */

@Immutable public abstract class SDocument
{
  private final OptionType<SDocumentContents> contents;
  private final OptionType<SDocumentStyle>    style;
  private final SDocumentTitle                title;

  protected SDocument(
    final SDocumentTitle in_title,
    final OptionType<SDocumentContents> in_contents,
    final OptionType<SDocumentStyle> in_style)
  {
    this.title = NullCheck.notNull(in_title, "Title");
    this.contents = NullCheck.notNull(in_contents, "Contents");
    this.style = NullCheck.notNull(in_style, "Style");
  }

  /**
   * Accept a document visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * 
   * @throws Exception
   *           If the visitor raises an {@link Exception}
   * @param <D>
   *          The type of transformed {@link SDocument}s
   */

  public abstract <D> D documentAccept(
    final SDocumentVisitor<D> v)
    throws Exception;

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
    final SDocument other = (SDocument) obj;
    return this.contents.equals(other.contents)
      && this.style.equals(other.style)
      && this.title.equals(other.title);
  }

  /**
   * @return The document contents
   */

  public final OptionType<SDocumentContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The document style
   */

  public final OptionType<SDocumentStyle> getStyle()
  {
    return this.style;
  }

  /**
   * @return The document title
   */

  public final SDocumentTitle getTitle()
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
