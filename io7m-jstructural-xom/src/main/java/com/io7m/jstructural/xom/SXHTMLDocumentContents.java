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

import com.io7m.jstructural.annotated.SAPart;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.core.SNonEmptyList;

final class SXHTMLDocumentContents
{
  private final SLinkProvider callbacks;

  public SXHTMLDocumentContents(
    final SLinkProvider in_callbacks)
  {
    this.callbacks = in_callbacks;
  }

  public Element getTableOfContentsParts(
    final SNonEmptyList<SAPart> parts)
  {
    final Element dce =
      SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
        "contents",
        "document_contents", });

    for (final SAPart p : parts.getElements()) {
      final Element pe =
        SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, new String[] {
          "contents_item",
          "contents_item1",
          "contents_item_part", });

      final Element plink =
        SXHTML.linkRaw(this.callbacks.getPartLinkTarget(p.getNumber()));
      {
        final StringBuilder title = new StringBuilder();
        title.append(p.getNumber().getActual());
        title.append(". ");
        title.append(p.getTitle().getActual());
        plink.appendChild(title.toString());
      }
      pe.appendChild(plink);
      dce.appendChild(pe);

      final Element pce =
        SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
          "contents",
          "part_contents", });

      for (final SASection s : p.getSections().getElements()) {
        final Element se =
          SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, new String[] {
            "contents_item",
            "contents_item2",
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
      }

      pe.appendChild(pce);
    }

    return dce;
  }

  public Element getTableOfContentsSections(
    final SNonEmptyList<SASection> sections)
  {
    final Element dce =
      SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
        "contents",
        "document_contents", });

    for (final SASection s : sections.getElements()) {
      final Element pe =
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
      pe.appendChild(slink);
      dce.appendChild(pe);

      final Element pce =
        SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
          "contents",
          "section_contents", });

      pe.appendChild(pce);
    }

    return dce;
  }
}
