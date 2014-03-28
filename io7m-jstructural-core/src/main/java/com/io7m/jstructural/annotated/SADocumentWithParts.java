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

import java.util.List;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * A document with sections.
 */

public final class SADocumentWithParts extends SADocument
{
  private final @Nonnull SNonEmptyList<SAPart> parts;

  /**
   * Construct a new document with parts.
   * 
   * @param in_ids
   *          The set of mappings from IDs to elements
   * @param in_title
   *          The title
   * @param in_contents
   *          Whether or not the document has a table of contents
   * @param in_style
   *          The style
   * @param in_content
   *          The list of parts
   * @param in_footnotes
   *          The list of footnotes
   * @param in_formals
   *          The formal items
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SADocumentWithParts(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull SADocumentTitle in_title,
    final @Nonnull Option<SDocumentContents> in_contents,
    final @Nonnull Option<SDocumentStyle> in_style,
    final @Nonnull SNonEmptyList<SAPart> in_content,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SAFormalItemsByKind in_formals)
    throws ConstraintError
  {
    super(in_ids, in_title, in_contents, in_style, in_footnotes, in_formals);
    this.parts = Constraints.constrainNotNull(in_content, "Parts");
  }

  @Override public <A> A documentAccept(
    final @Nonnull SADocumentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitDocumentWithParts(this);
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SADocumentWithParts other = (SADocumentWithParts) obj;
    return this.parts.equals(other.parts);
  }

  /**
   * @return The document sections
   */

  public @Nonnull SNonEmptyList<SAPart> getParts()
  {
    return this.parts;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.parts.hashCode();
    return result;
  }
}
