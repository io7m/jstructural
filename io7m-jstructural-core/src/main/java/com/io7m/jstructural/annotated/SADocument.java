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
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import net.jcip.annotations.Immutable;

import java.util.Collections;
import java.util.List;

/**
 * A document.
 */

@Immutable public abstract class SADocument implements SASegmentsReadable
{
  private final OptionType<SDocumentContents> contents;
  private final List<SAFootnote>              footnotes;
  private final SAFormalItemsByKind           formals;
  private final SAIDMap                       ids;
  private final OptionType<SDocumentStyle>    style;
  private final SADocumentTitle               title;

  protected SADocument(
    final SAIDMap in_ids,
    final SADocumentTitle in_title,
    final OptionType<SDocumentContents> in_contents,
    final OptionType<SDocumentStyle> in_style,
    final List<SAFootnote> in_footnotes,
    final SAFormalItemsByKind in_formals)
  {
    this.ids = NullCheck.notNull(in_ids, "ID mappings");
    this.title = NullCheck.notNull(in_title, "Title");
    this.contents = NullCheck.notNull(in_contents, "Contents");
    this.style = NullCheck.notNull(in_style, "Style");
    this.footnotes = NullCheck.notNull(in_footnotes, "Footnotes");
    this.formals = NullCheck.notNull(in_formals, "Formals");
  }

  /**
   * Accept a document visitor.
   *
   * @param v   The visitor
   * @param <A> The type of values returned by the visitor
   *
   * @return The value returned by the visitor
   *
   * @throws Exception If the visitor raises an {@link Exception}
   */

  public abstract <A> A documentAccept(
    final SADocumentVisitor<A> v)
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
    final SADocument other = (SADocument) obj;
    return this.contents.equals(other.contents)
           && this.footnotes.equals(other.footnotes)
           && this.formals.equals(other.formals)
           && this.ids.equals(other.ids)
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
   * @return The document's footnotes
   */

  public final List<SAFootnote> getFootnotes()
  {
    final List<SAFootnote> r = Collections.unmodifiableList(this.footnotes);
    assert r != null;
    return r;
  }

  /**
   * @return The formal items by kind
   */

  public final SAFormalItemsByKindReadable getFormals()
  {
    return this.formals;
  }

  /**
   * @return The set of mappings from IDs to elements
   */

  public final SAIDMapReadable getIDMappings()
  {
    return this.ids;
  }

  /**
   * @param n The section number
   *
   * @return The section with the given section number, if any.
   */

  public abstract OptionType<SASection> getSection(
    final SASectionNumber n);

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

  public final SADocumentTitle getTitle()
  {
    return this.title;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.footnotes.hashCode();
    result = (prime * result) + this.formals.hashCode();
    result = (prime * result) + this.ids.hashCode();
    result = (prime * result) + this.style.hashCode();
    result = (prime * result) + this.title.hashCode();
    return result;
  }
}
