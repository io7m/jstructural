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

package com.io7m.jstructural.xom;

import javax.annotation.Nonnull;

import nu.xom.Element;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Unit;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASectionVisitor;
import com.io7m.jstructural.annotated.SASectionWithParagraphs;
import com.io7m.jstructural.annotated.SASectionWithSubsections;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.core.SNonEmptyList;

final class SXHTMLPartContents
{
  private final @Nonnull SLinkProvider callbacks;

  public SXHTMLPartContents(
    final @Nonnull SLinkProvider in_callbacks)
  {
    this.callbacks = in_callbacks;
  }

  public @Nonnull Element getTableOfContentsSections(
    final @Nonnull SNonEmptyList<SASection> sections)
    throws ConstraintError,
      Exception
  {
    final SLinkProvider cb = this.callbacks;

    final Element pce =
      SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
        "contents",
        "part_contents_outer",
        "part_contents", });

    for (final SASection s : sections.getElements()) {
      final Element se =
        SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, new String[] {
          "contents_item",
          "contents_item1",
          "contents_item_section", });
      se.appendChild(cb.getSectionLink(s));
      pce.appendChild(se);

      s.sectionAccept(new SASectionVisitor<Unit>() {
        @Override public Unit visitSectionWithParagraphs(
          final @Nonnull SASectionWithParagraphs swp)
          throws ConstraintError,
            Exception
        {
          return Unit.unit();
        }

        @Override public Unit visitSectionWithSubsections(
          final @Nonnull SASectionWithSubsections sws)
          throws ConstraintError,
            Exception
        {
          final Element sce =
            SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
              "contents",
              "section_contents", });
          se.appendChild(sce);

          final SNonEmptyList<SASubsection> subsections =
            sws.getSubsections();

          for (final SASubsection ss : subsections.getElements()) {
            final Element sse =
              SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, new String[] {
                "contents_item",
                "contents_item2",
                "contents_item_subsection", });
            sse.appendChild(cb.getSubsectionLink(ss));
            sce.appendChild(sse);
          }
          return Unit.unit();
        }
      });
    }

    return pce;
  }
}
