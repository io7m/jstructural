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
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.core.SNonEmptyList;

final class SXHTMLSectionContents
{
  private final @Nonnull SLinkProvider callbacks;

  public SXHTMLSectionContents(
    final @Nonnull SLinkProvider in_callbacks)
  {
    this.callbacks = in_callbacks;
  }

  public @Nonnull Element getTableOfContents(
    final @Nonnull SNonEmptyList<SASubsection> subsections)
    throws ConstraintError
  {
    final Element sce =
      SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, new String[] {
        "contents",
        "section_contents_outer",
        "section_contents", });

    for (final SASubsection s : subsections.getElements()) {
      final Element se =
        SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, new String[] {
          "contents_item",
          "contents_item1",
          "contents_item_subsection", });
      se.appendChild(this.callbacks.getSubsectionLink(s));
      sce.appendChild(se);
    }

    return sce;
  }
}
