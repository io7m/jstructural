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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.junreachable.UnreachableCodeException;
import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A document with sections.
 */

@Immutable public final class SADocumentWithSections extends SADocument
{
  private final Map<SASectionNumber, SASection> numbered_sections;
  private final SNonEmptyList<SASection>        sections;

  /**
   * Construct a new document with sections.
   *
   * @param in_ids       The set of mappings from IDs to elements
   * @param in_title     The title
   * @param in_contents  Whether or not the document has a table of contents
   * @param in_style     The style
   * @param in_content   The list of sections
   * @param in_footnotes The list of footnotes
   * @param in_formals   The formal items
   */

  public SADocumentWithSections(
    final SAIDMap in_ids,
    final SADocumentTitle in_title,
    final OptionType<SDocumentContents> in_contents,
    final OptionType<SDocumentStyle> in_style,
    final SNonEmptyList<SASection> in_content,
    final List<SAFootnote> in_footnotes,
    final SAFormalItemsByKind in_formals)
  {
    super(in_ids, in_title, in_contents, in_style, in_footnotes, in_formals);
    this.sections = NullCheck.notNull(in_content, "Content");

    this.numbered_sections = new HashMap<SASectionNumber, SASection>();
    for (final SASection s : this.sections.getElements()) {
      assert this.numbered_sections.containsKey(s.getNumber()) == false;
      this.numbered_sections.put(s.getNumber(), s);
    }
  }

  @Override public <A> A documentAccept(
    final SADocumentVisitor<A> v)
    throws Exception
  {
    return v.visitDocumentWithSections(this);
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SADocumentWithSections other = (SADocumentWithSections) obj;
    return this.sections.equals(other.sections);
  }

  @Override public OptionType<SASection> getSection(
    final SASectionNumber n)
  {
    NullCheck.notNull(n, "Number");
    if (this.numbered_sections.containsKey(n)) {
      final SASection r = this.numbered_sections.get(n);
      assert r != null;
      return Option.some(r);
    }
    return Option.none();
  }

  /**
   * @return The document sections
   */

  public SNonEmptyList<SASection> getSections()
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

  @Override public OptionType<SASegmentNumber> segmentGetNext(
    final SASegmentNumber n)
  {
    try {
      final List<SASection> section_list = this.sections.getElements();

      return n.segmentNumberAccept(
        new SASegmentNumberVisitor<OptionType<SASegmentNumber>>()
        {
          @Override public OptionType<SASegmentNumber> visitPartNumber(
            final SAPartNumber pn)
            throws Exception
          {
            throw new UnreachableCodeException();
          }

          @Override public OptionType<SASegmentNumber> visitSectionNumber(
            final SASectionNumber pn)
            throws Exception
          {
            return pn.sectionNumberAccept(
              new SASectionNumberVisitor<OptionType<SASegmentNumber>>()
              {
                @Override
                public OptionType<SASegmentNumber>
                visitSectionNumberWithoutPart(
                  final SASectionNumberS p)
                  throws Exception
                {
                  if (p.getSection() >= section_list.size()) {
                    return Option.none();
                  }

                  final SASectionNumberS next =
                    new SASectionNumberS(p.getSection() + 1);
                  return Option.some((SASegmentNumber) next);
                }

                @Override
                public OptionType<SASegmentNumber> visitSectionNumberWithPart(
                  final SASectionNumberPS p)
                  throws Exception
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

  @Override public OptionType<SASegmentNumber> segmentGetPrevious(
    final SASegmentNumber n)
  {
    try {
      return n.segmentNumberAccept(
        new SASegmentNumberVisitor<OptionType<SASegmentNumber>>()
        {
          @Override public OptionType<SASegmentNumber> visitPartNumber(
            final SAPartNumber pn)
            throws Exception
          {
            throw new UnreachableCodeException();
          }

          @Override public OptionType<SASegmentNumber> visitSectionNumber(
            final SASectionNumber pn)
            throws Exception
          {
            return pn.sectionNumberAccept(
              new SASectionNumberVisitor<OptionType<SASegmentNumber>>()
              {
                @Override
                public OptionType<SASegmentNumber>
                visitSectionNumberWithoutPart(
                  final SASectionNumberS p)
                  throws Exception
                {
                  if (p.getSection() == 1) {
                    return Option.none();
                  }

                  final SASectionNumberS prev =
                    new SASectionNumberS(p.getSection() - 1);
                  return Option.some((SASegmentNumber) prev);

                }

                @Override
                public OptionType<SASegmentNumber> visitSectionNumberWithPart(
                  final SASectionNumberPS p)
                  throws Exception
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

  @Override public OptionType<SASegmentNumber> segmentGetUp(
    final SASegmentNumber n)
  {
    return Option.none();
  }
}
