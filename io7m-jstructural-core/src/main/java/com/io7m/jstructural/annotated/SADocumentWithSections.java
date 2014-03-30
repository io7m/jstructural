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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * A document with sections.
 */

public final class SADocumentWithSections extends SADocument
{
  private final @Nonnull Map<SASectionNumber, SASection> numbered_sections;
  private final @Nonnull SNonEmptyList<SASection>        sections;

  /**
   * Construct a new document with sections.
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
   *          The list of sections
   * @param in_footnotes
   *          The list of footnotes
   * @param in_formals
   *          The formal items
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SADocumentWithSections(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull SADocumentTitle in_title,
    final @Nonnull Option<SDocumentContents> in_contents,
    final @Nonnull Option<SDocumentStyle> in_style,
    final @Nonnull SNonEmptyList<SASection> in_content,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SAFormalItemsByKind in_formals)
    throws ConstraintError
  {
    super(in_ids, in_title, in_contents, in_style, in_footnotes, in_formals);
    this.sections = Constraints.constrainNotNull(in_content, "Content");

    this.numbered_sections = new HashMap<SASectionNumber, SASection>();
    for (final SASection s : this.sections.getElements()) {
      assert this.numbered_sections.containsKey(s.getNumber()) == false;
      this.numbered_sections.put(s.getNumber(), s);
    }
  }

  @Override public <A> A documentAccept(
    final @Nonnull SADocumentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitDocumentWithSections(this);
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
    final SADocumentWithSections other = (SADocumentWithSections) obj;
    return this.sections.equals(other.sections);
  }

  @Override public Option<SASection> getSection(
    final @Nonnull SASectionNumber n)
    throws ConstraintError
  {
    Constraints.constrainNotNull(n, "Number");
    if (this.numbered_sections.containsKey(n)) {
      return Option.some(this.numbered_sections.get(n));
    }
    return Option.none();
  }

  /**
   * @return The document sections
   */

  public @Nonnull SNonEmptyList<SASection> getSections()
  {
    return this.sections;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.sections.hashCode();
    return result;
  }

  @Override public SASegmentNumber segmentGetFirst()
  {
    return this.sections.getElements().get(0).getNumber();
  }

  @Override public Option<SASegmentNumber> segmentGetNext(
    final @Nonnull SASegmentNumber n)
    throws ConstraintError
  {
    try {
      final List<SASection> section_list = this.sections.getElements();

      return n
        .segmentNumberAccept(new SASegmentNumberVisitor<Option<SASegmentNumber>>() {
          @Override public Option<SASegmentNumber> visitPartNumber(
            final @Nonnull SAPartNumber pn)
            throws ConstraintError,
              Exception
          {
            throw new UnreachableCodeException();
          }

          @Override public Option<SASegmentNumber> visitSectionNumber(
            final @Nonnull SASectionNumber pn)
            throws ConstraintError,
              Exception
          {
            return pn
              .sectionNumberAccept(new SASectionNumberVisitor<Option<SASegmentNumber>>() {
                @Override public
                  Option<SASegmentNumber>
                  visitSectionNumberWithoutPart(
                    final @Nonnull SASectionNumberS p)
                    throws ConstraintError,
                      Exception
                {
                  if (p.getSection() >= section_list.size()) {
                    return Option.none();
                  }

                  final SASectionNumberS next =
                    new SASectionNumberS(p.getSection() + 1);
                  return Option.some((SASegmentNumber) next);
                }

                @Override public
                  Option<SASegmentNumber>
                  visitSectionNumberWithPart(
                    final @Nonnull SASectionNumberPS p)
                    throws ConstraintError,
                      Exception
                {
                  throw new UnreachableCodeException();
                }
              });
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public Option<SASegmentNumber> segmentGetPrevious(
    final @Nonnull SASegmentNumber n)
    throws ConstraintError
  {
    try {
      return n
        .segmentNumberAccept(new SASegmentNumberVisitor<Option<SASegmentNumber>>() {
          @Override public Option<SASegmentNumber> visitPartNumber(
            final @Nonnull SAPartNumber pn)
            throws ConstraintError,
              Exception
          {
            throw new UnreachableCodeException();
          }

          @Override public Option<SASegmentNumber> visitSectionNumber(
            final @Nonnull SASectionNumber pn)
            throws ConstraintError,
              Exception
          {
            return pn
              .sectionNumberAccept(new SASectionNumberVisitor<Option<SASegmentNumber>>() {
                @Override public
                  Option<SASegmentNumber>
                  visitSectionNumberWithoutPart(
                    final @Nonnull SASectionNumberS p)
                    throws ConstraintError,
                      Exception
                {
                  if (p.getSection() == 1) {
                    return Option.none();
                  }

                  final SASectionNumberS prev =
                    new SASectionNumberS(p.getSection() - 1);
                  return Option.some((SASegmentNumber) prev);

                }

                @Override public
                  Option<SASegmentNumber>
                  visitSectionNumberWithPart(
                    final @Nonnull SASectionNumberPS p)
                    throws ConstraintError,
                      Exception
                {
                  throw new UnreachableCodeException();
                }
              });
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public Option<SASegmentNumber> segmentGetUp(
    final @Nonnull SASegmentNumber n)
    throws ConstraintError
  {
    return Option.none();
  }
}
