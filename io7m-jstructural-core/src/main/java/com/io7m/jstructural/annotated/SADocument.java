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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;

/**
 * A document.
 */

public abstract class SADocument implements SASegmentsReadable
{
  private final @Nonnull Option<SDocumentContents> contents;
  private final @Nonnull List<SAFootnote>          footnotes;
  private final @Nonnull SAFormalItemsByKind       formals;
  private final @Nonnull SAIDMap                   ids;
  private final @Nonnull Option<SDocumentStyle>    style;
  private final @Nonnull SADocumentTitle           title;

  protected SADocument(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull SADocumentTitle in_title,
    final @Nonnull Option<SDocumentContents> in_contents,
    final @Nonnull Option<SDocumentStyle> in_style,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SAFormalItemsByKind in_formals)
    throws ConstraintError
  {
    this.ids = Constraints.constrainNotNull(in_ids, "ID mappings");
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.contents = Constraints.constrainNotNull(in_contents, "Contents");
    this.style = Constraints.constrainNotNull(in_style, "Style");
    this.footnotes = Constraints.constrainNotNull(in_footnotes, "Footnotes");
    this.formals = Constraints.constrainNotNull(in_formals, "Formals");
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
    final @Nonnull SADocumentVisitor<A> v)
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

  public final @Nonnull Option<SDocumentContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The document's footnotes
   */

  public final @Nonnull List<SAFootnote> getFootnotes()
  {
    return Collections.unmodifiableList(this.footnotes);
  }

  /**
   * @return The formal items by kind
   */

  public final @Nonnull SAFormalItemsByKindReadable getFormals()
  {
    return this.formals;
  }

  /**
   * @return The set of mappings from IDs to elements
   */

  public final @Nonnull SAIDMapReadable getIDMappings()
  {
    return this.ids;
  }

  /**
   * @param n
   *          The section number
   * @return The section with the given section number, if any.
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public abstract Option<SASection> getSection(
    final @Nonnull SASectionNumber n)
    throws ConstraintError;

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

  public final @Nonnull SADocumentTitle getTitle()
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
