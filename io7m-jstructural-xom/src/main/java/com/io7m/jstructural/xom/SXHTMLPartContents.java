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

import nu.xom.Element;

import com.io7m.jfunctional.Unit;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASectionVisitor;
import com.io7m.jstructural.annotated.SASectionWithParagraphs;
import com.io7m.jstructural.annotated.SASectionWithSubsections;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.core.SNonEmptyList;

final class SXHTMLPartContents
{
  private final SLinkProvider callbacks;

  SXHTMLPartContents(
    final SLinkProvider in_callbacks)
  {
    this.callbacks = in_callbacks;
  }

  public Element getTableOfContentsSections(
    final SNonEmptyList<SASection> sections)
    throws Exception
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

      final Element slink =
        SXHTML.linkRaw(this.callbacks.getSectionLinkTarget(s.getNumber()));
      {
        final StringBuilder title = new StringBuilder();
        title.append(s.getNumber().sectionNumberFormat());
        title.append(". ");
        title.append(s.getTitle().getActual());
        slink.appendChild(title.toString());
      }
      se.appendChild(slink);
      pce.appendChild(se);

      s.sectionAccept(new SASectionVisitor<Unit>() {
        @Override public Unit visitSectionWithParagraphs(
          final SASectionWithParagraphs swp)
          throws Exception
        {
          return Unit.unit();
        }

        @Override public Unit visitSectionWithSubsections(
          final SASectionWithSubsections sws)
          throws Exception
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

            final Element sslink =
              SXHTML.linkRaw(cb.getSubsectionLinkTarget(ss.getNumber()));
            {
              final StringBuilder title = new StringBuilder();
              title.append(ss.getNumber().subsectionNumberFormat());
              title.append(". ");
              title.append(ss.getTitle().getActual());
              sslink.appendChild(title.toString());
            }

            sse.appendChild(sslink);
            sce.appendChild(sse);
          }
          return Unit.unit();
        }
      });
    }

    return pce;
  }
}
