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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Some;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A document with sections.
 */

public final class SADocumentWithParts extends SADocument
{
  private final Map<SAPartNumber, SAPart> numbered_parts;
  private final SNonEmptyList<SAPart>     parts;

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
   */

  public SADocumentWithParts(
    final SAIDMap in_ids,
    final SADocumentTitle in_title,
    final OptionType<SDocumentContents> in_contents,
    final OptionType<SDocumentStyle> in_style,
    final SNonEmptyList<SAPart> in_content,
    final List<SAFootnote> in_footnotes,
    final SAFormalItemsByKind in_formals)
  {
    super(in_ids, in_title, in_contents, in_style, in_footnotes, in_formals);
    this.parts = NullCheck.notNull(in_content, "Parts");

    this.numbered_parts = new HashMap<SAPartNumber, SAPart>();
    for (final SAPart p : this.parts.getElements()) {
      assert this.numbered_parts.containsKey(p.getNumber()) == false;
      this.numbered_parts.put(p.getNumber(), p);
    }
  }

  @Override public <A> A documentAccept(
    final SADocumentVisitor<A> v)
    throws Exception
  {
    return v.visitDocumentWithParts(this);
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
    final SADocumentWithParts other = (SADocumentWithParts) obj;
    return this.parts.equals(other.parts);
  }

  /**
   * @param n
   *          The part number
   * @return The part for the given part number, if any
   */

  public OptionType<SAPart> getPart(
    final SAPartNumber n)
  {
    NullCheck.notNull(n, "Part number");

    final List<SAPart> ps = this.parts.getElements();
    final int part_index = n.getActual() - 1;
    if (part_index >= ps.size()) {
      return Option.none();
    }

    final SAPart p = ps.get(part_index);
    assert p != null;
    return Option.some(p);
  }

  /**
   * @return The document sections
   */

  public SNonEmptyList<SAPart> getParts()
  {
    return this.parts;
  }

  @Override public OptionType<SASection> getSection(
    final SASectionNumber n)
  {
    NullCheck.notNull(n, "Section number");

    try {
      return n
        .sectionNumberAccept(new SASectionNumberVisitor<OptionType<SASection>>() {
          @Override public
            OptionType<SASection>
            visitSectionNumberWithoutPart(
              final SASectionNumberS p)
              throws Exception
          {
            return Option.none();
          }

          @Override public OptionType<SASection> visitSectionNumberWithPart(
            final SASectionNumberPS p)
            throws Exception
          {
            final OptionType<SAPart> part =
              SADocumentWithParts.this.getPart(new SAPartNumber(p.getPart()));
            if (part.isNone()) {
              return Option.none();
            }
            final Some<SAPart> some = (Some<SAPart>) part;
            return some.get().getSection(p);
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.parts.hashCode();
    return result;
  }

  @Override public SASegmentNumber segmentGetFirst()
  {
    return this.parts.getElements().get(0).getNumber();
  }

  @Override public OptionType<SASegmentNumber> segmentGetNext(
    final SASegmentNumber n)
  {
    try {
      final Map<SAPartNumber, SAPart> part_map = this.numbered_parts;

      return n
        .segmentNumberAccept(new SASegmentNumberVisitor<OptionType<SASegmentNumber>>() {

          /**
           * If the current number is a part number, then the next number must
           * be the first section of that part.
           */

          @Override public OptionType<SASegmentNumber> visitPartNumber(
            final SAPartNumber pn)
            throws Exception
          {
            final SAPart current = part_map.get(pn);
            final SASectionNumberPS next =
              new SASectionNumberPS(pn.getActual(), 1);

            final OptionType<SASection> section = current.getSection(next);
            assert section.isSome();
            return Option.some((SASegmentNumber) next);
          }

          /**
           * If the current number is a section number, then the next segment
           * is either the next section in the current part, or the next part.
           */

          @Override public OptionType<SASegmentNumber> visitSectionNumber(
            final SASectionNumber sn)
            throws Exception
          {
            return sn
              .sectionNumberAccept(new SASectionNumberVisitor<OptionType<SASegmentNumber>>() {

                /**
                 * In a document with parts, there cannot be section numbers
                 * without parts.
                 */

                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithoutPart(
                    final SASectionNumberS ss)
                    throws Exception
                {
                  throw new UnreachableCodeException();
                }

                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithPart(
                    final SASectionNumberPS sps)
                    throws Exception
                {
                  final SAPart p =
                    part_map.get(new SAPartNumber(sps.getPart()));
                  final List<SASection> sections =
                    p.getSections().getElements();

                  if (sps.getSection() >= sections.size()) {
                    final SAPartNumber nn =
                      new SAPartNumber(sps.getPart() + 1);
                    final SAPart q = part_map.get(nn);
                    if (q == null) {
                      return Option.none();
                    }
                    return Option.some((SASegmentNumber) nn);
                  }

                  final SASectionNumberPS ns =
                    new SASectionNumberPS(sps.getPart(), sps.getSection() + 1);
                  return Option.some((SASegmentNumber) ns);
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
      final Map<SAPartNumber, SAPart> part_map =
        SADocumentWithParts.this.numbered_parts;

      return n
        .segmentNumberAccept(new SASegmentNumberVisitor<OptionType<SASegmentNumber>>() {

          /**
           * If the current number is a part number, the previous segment is
           * either the start of the document, or the last section of the
           * previous part.
           */

          @Override public OptionType<SASegmentNumber> visitPartNumber(
            final SAPartNumber p)
            throws Exception
          {
            final int actual = p.getActual();
            if (actual == 1) {
              return Option.none();
            }

            final SAPartNumber q = new SAPartNumber(actual - 1);
            assert part_map.containsKey(q);
            final SAPart other = part_map.get(q);
            assert other != null;

            final List<SASection> sections =
              other.getSections().getElements();
            final SASection s = sections.get(sections.size() - 1);
            return Option.some((SASegmentNumber) s.getNumber());
          }

          /**
           * If the current number is a section number, the previous segment
           * is either the previous section, or the last section of the
           * previous part.
           */

          @Override public OptionType<SASegmentNumber> visitSectionNumber(
            final SASectionNumber s)
            throws Exception
          {
            return s
              .sectionNumberAccept(new SASectionNumberVisitor<OptionType<SASegmentNumber>>() {

                /**
                 * In a document with parts, there cannot be a section without
                 * a part number.
                 */

                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithoutPart(
                    final SASectionNumberS ss)
                    throws Exception
                {
                  throw new UnreachableCodeException();
                }

                /**
                 * If the current section number is 1, then the previous
                 * segment is the part segment.
                 */

                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithPart(
                    final SASectionNumberPS ss)
                    throws Exception
                {
                  if (ss.getSection() == 1) {
                    return Option.some((SASegmentNumber) new SAPartNumber(ss
                      .getPart()));
                  }

                  return Option.some((SASegmentNumber) new SASectionNumberPS(
                    ss.getPart(),
                    ss.getSection() - 1));
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
    try {
      return n
        .segmentNumberAccept(new SASegmentNumberVisitor<OptionType<SASegmentNumber>>() {
          @Override public OptionType<SASegmentNumber> visitPartNumber(
            final SAPartNumber p)
            throws Exception
          {
            return Option.none();
          }

          @Override public OptionType<SASegmentNumber> visitSectionNumber(
            final SASectionNumber s)
            throws Exception
          {
            return s
              .sectionNumberAccept(new SASectionNumberVisitor<OptionType<SASegmentNumber>>() {
                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithoutPart(
                    final SASectionNumberS ss)
                    throws Exception
                {
                  return Option.none();
                }

                @Override public
                  OptionType<SASegmentNumber>
                  visitSectionNumberWithPart(
                    final SASectionNumberPS sps)
                    throws Exception
                {
                  return Option.some((SASegmentNumber) new SAPartNumber(sps
                    .getPart()));
                }
              });
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }
}
