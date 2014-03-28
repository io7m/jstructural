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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SADocumentVisitor;
import com.io7m.jstructural.annotated.SADocumentWithParts;
import com.io7m.jstructural.annotated.SADocumentWithSections;
import com.io7m.jstructural.annotated.SAFootnote;
import com.io7m.jstructural.annotated.SAFormalItem;
import com.io7m.jstructural.annotated.SAFormalItemNumber;
import com.io7m.jstructural.annotated.SAFormalItemsByKindReadable;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAIDMapReadable;
import com.io7m.jstructural.annotated.SAIDTargetContentVisitor;
import com.io7m.jstructural.annotated.SAParagraph;
import com.io7m.jstructural.annotated.SAPart;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASectionNumber;
import com.io7m.jstructural.annotated.SASectionNumberPS;
import com.io7m.jstructural.annotated.SASectionNumberS;
import com.io7m.jstructural.annotated.SASectionNumberVisitor;
import com.io7m.jstructural.annotated.SASectionVisitor;
import com.io7m.jstructural.annotated.SASectionWithParagraphs;
import com.io7m.jstructural.annotated.SASectionWithSubsections;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SASubsectionContent;
import com.io7m.jstructural.annotated.SASubsectionContentVisitor;
import com.io7m.jstructural.annotated.SASubsectionNumber;
import com.io7m.jstructural.annotated.SASubsectionNumberPSS;
import com.io7m.jstructural.annotated.SASubsectionNumberSS;
import com.io7m.jstructural.annotated.SASubsectionNumberVisitor;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;
import com.io7m.jstructural.core.SSectionContents;

/**
 * An XHTML writer that produces XHTML 1.0 Strict documents, as a single page.
 */

@SuppressWarnings("synthetic-access") public final class SDocumentXHTMLWriterSingle implements
  SDocumentXHTMLWriter
{
  @SuppressWarnings("boxing") private static @Nonnull Element getPartLink(
    final @Nonnull SAPart part)
  {
    final int number = part.getNumber();
    final Element e = SXHTML.linkRaw("#" + SXHTML.getPartAnchorID(number));
    final String text =
      String.format("%d. %s", number, part.getTitle().getActual());
    e.appendChild(text);
    return e;
  }

  @SuppressWarnings({ "boxing" }) private static Element getSectionLink(
    final SASection s)
    throws ConstraintError,
      Exception
  {
    final SASectionNumber n = s.getNumber();
    return n.sectionNumberAccept(new SASectionNumberVisitor<Element>() {
      @Override public Element visitSectionNumberWithoutPart(
        final @Nonnull SASectionNumberS p)
        throws ConstraintError,
          Exception
      {
        final int sn = p.getSection();
        final String text =
          String.format("%d. %s", sn, s.getTitle().getActual());
        final Element e = SXHTML.linkRaw("#" + SXHTML.getSectionAnchorID(p));
        e.appendChild(text);
        return e;
      }

      @Override public Element visitSectionNumberWithPart(
        final @Nonnull SASectionNumberPS p)
        throws ConstraintError,
          Exception
      {
        final int sn = p.getSection();
        final int pn = p.getPart();
        final String text =
          String.format("%d.%d %s", pn, sn, s.getTitle().getActual());
        final Element e = SXHTML.linkRaw("#" + SXHTML.getSectionAnchorID(p));
        e.appendChild(text);
        return e;
      }
    });
  }

  @SuppressWarnings("boxing") private static @Nonnull
    Element
    getSubsectionLink(
      final @Nonnull SASubsection s)
      throws ConstraintError,
        Exception
  {
    final SASubsectionNumber n = s.getTitle().getNumber();
    return n.subsectionNumberAccept(new SASubsectionNumberVisitor<Element>() {
      @Override public Element visitSubsectionNumberPSS(
        final @Nonnull SASubsectionNumberPSS p)
        throws ConstraintError,
          Exception
      {
        final int pn = p.getPart();
        final int sn = p.getSection();
        final int ssn = p.getSubsection();
        final String text =
          String.format("%d.%d.%d %s", pn, sn, ssn, s.getTitle().getActual());

        final Element e =
          SXHTML.linkRaw("#" + SXHTML.getSubsectionAnchorID(p));
        e.appendChild(text);
        return e;
      }

      @Override public Element visitSubsectionNumberSS(
        final @Nonnull SASubsectionNumberSS p)
        throws ConstraintError,
          Exception
      {
        final int sn = p.getSection();
        final int ssn = p.getSubsection();
        final String text =
          String.format("%d.%d %s", sn, ssn, s.getTitle().getActual());
        final Element e =
          SXHTML.linkRaw("#" + SXHTML.getSubsectionAnchorID(p));
        e.appendChild(text);
        return e;
      }
    });
  }

  private static @Nonnull Element part(
    final @Nonnull SXHTMLPartContents part_contents_writer,
    final @Nonnull SXHTMLSectionContents section_contents_writer,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAPart part)
    throws ConstraintError,
      Exception
  {
    final Element e = SXHTML.partContainer(part.getTitle());

    part.getContents().mapPartial(
      new PartialFunction<SPartContents, Unit, ConstraintError>() {
        @Override public Unit call(
          final @Nonnull SPartContents x)
          throws ConstraintError
        {
          try {
            e.appendChild(part_contents_writer
              .getTableOfContentsSections(part.getSections()));
            return Unit.unit();
          } catch (final Exception z) {
            throw new UnreachableCodeException(z);
          }
        }
      });

    for (final SASection s : part.getSections().getElements()) {
      e.appendChild(SDocumentXHTMLWriterSingle.section(
        section_contents_writer,
        link_provider,
        formals,
        s));
    }

    return e;
  }

  private static @Nonnull Element section(
    final @Nonnull SXHTMLSectionContents section_contents_writer,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SASection s)
    throws ConstraintError,
      Exception
  {
    final Element e = SXHTML.sectionContainer(s);

    s.sectionAccept(new SASectionVisitor<Unit>() {
      @Override public Unit visitSectionWithParagraphs(
        final SASectionWithParagraphs swp)
        throws ConstraintError,
          Exception
      {
        SDocumentXHTMLWriterSingle.sectionWithParagraphs(
          link_provider,
          formals,
          swp,
          e);
        return Unit.unit();
      }

      @Override public Unit visitSectionWithSubsections(
        final SASectionWithSubsections sws)
        throws ConstraintError,
          Exception
      {
        SDocumentXHTMLWriterSingle.sectionWithSubsections(
          section_contents_writer,
          link_provider,
          formals,
          sws,
          e);
        return Unit.unit();
      }
    });

    return e;
  }

  private static void sectionWithParagraphs(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SASectionWithParagraphs swp,
    final @Nonnull Element e)
    throws ConstraintError,
      Exception
  {
    final SNonEmptyList<SASubsectionContent> content =
      swp.getSectionContent();

    for (final SASubsectionContent ss : content.getElements()) {
      e.appendChild(ss
        .subsectionContentAccept(new SASubsectionContentVisitor<Element>() {
          @Override public Element visitFormalItem(
            final @Nonnull SAFormalItem formal)
            throws ConstraintError,
              Exception
          {
            return SXHTML.formalItem(link_provider, formals, formal);
          }

          @Override public Element visitParagraph(
            final @Nonnull SAParagraph paragraph)
            throws ConstraintError,
              Exception
          {
            return SXHTML.paragraph(link_provider, formals, paragraph);
          }
        }));
    }
  }

  private static void sectionWithSubsections(
    final @Nonnull SXHTMLSectionContents section_contents_writer,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SASectionWithSubsections sws,
    final @Nonnull Element e)
    throws ConstraintError,
      Exception
  {
    final SNonEmptyList<SASubsection> subsections = sws.getSubsections();

    sws.getContents().mapPartial(
      new PartialFunction<SSectionContents, Unit, ConstraintError>() {
        @Override public Unit call(
          final @Nonnull SSectionContents x)
          throws ConstraintError
        {
          e.appendChild(section_contents_writer
            .getTableOfContents(subsections));
          return Unit.unit();
        }
      });

    for (final SASubsection ss : subsections.getElements()) {
      e.appendChild(SXHTML.subsection(link_provider, formals, ss));
    }
  }

  /**
   * Construct a new XHTML writer.
   */

  public SDocumentXHTMLWriterSingle()
  {

  }

  @Override public List<Document> writeDocuments(
    final @Nonnull SDocumentXHTMLWriterCallbacks callbacks,
    final @Nonnull SADocument doc)
    throws ConstraintError
  {
    try {
      Constraints.constrainNotNull(callbacks, "Callbacks");
      Constraints.constrainNotNull(doc, "Document");

      final SLinkProvider link_provider = new SLinkProvider() {
        @Override public String getLinkTargetForID(
          final @Nonnull SAID id)
          throws ConstraintError
        {
          try {
            final SAIDMapReadable map = doc.getIDMappings();
            return map.get(id).targetContentAccept(
              new SAIDTargetContentVisitor<String>() {
                @Override public String visitParagraph(
                  final @Nonnull SAParagraph paragraph)
                  throws ConstraintError,
                    Exception
                {
                  return "#"
                    + SXHTML.getParagraphAnchorID(paragraph.getNumber());
                }

                @Override public String visitPart(
                  final @Nonnull SAPart part)
                  throws ConstraintError,
                    Exception
                {
                  return "#" + SXHTML.getPartAnchorID(part.getNumber());
                }

                @Override public String visitSection(
                  final @Nonnull SASection section)
                  throws ConstraintError,
                    Exception
                {
                  return "#" + SXHTML.getSectionAnchorID(section.getNumber());
                }

                @Override public String visitSubsection(
                  final @Nonnull SASubsection subsection)
                  throws ConstraintError,
                    Exception
                {
                  return "#"
                    + SXHTML.getSubsectionAnchorID(subsection
                      .getTitle()
                      .getNumber());
                }
              });
          } catch (final Exception x) {
            throw new UnreachableCodeException(x);
          }
        }

        @Override public Element getPartLink(
          final @Nonnull SAPart p)
          throws ConstraintError
        {
          return SDocumentXHTMLWriterSingle.getPartLink(p);
        }

        @Override public Element getSectionLink(
          final @Nonnull SASection s)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterSingle.getSectionLink(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public Element getSubsectionLink(
          final @Nonnull SASubsection s)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterSingle.getSubsectionLink(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public Element getFormalItemLink(
          final @Nonnull SAFormalItem f)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterSingle.getFormalItemLink(f);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }
      };

      final SXHTMLDocumentContents doc_contents =
        new SXHTMLDocumentContents(link_provider);
      final SXHTMLPartContents part_contents =
        new SXHTMLPartContents(link_provider);
      final SXHTMLSectionContents section_contents =
        new SXHTMLSectionContents(link_provider);
      final SAFormalItemsByKindReadable formals = doc.getFormals();

      final SXHTMLPage page =
        SXHTML.newPage(doc.getTitle().getActual(), doc.getStyle());
      callbacks.onHead(page.getHead());
      final Element body = page.getBodyContainer();
      callbacks.onBodyStart(body);

      body.appendChild(SXHTML.documentTitle(doc));

      doc.documentAccept(new SADocumentVisitor<Unit>() {
        @Override public Unit visitDocumentWithParts(
          final @Nonnull SADocumentWithParts document)
          throws ConstraintError,
            Exception
        {
          final SNonEmptyList<SAPart> parts = document.getParts();

          doc.getContents().mapPartial(
            new PartialFunction<SDocumentContents, Unit, ConstraintError>() {
              @Override public Unit call(
                final @Nonnull SDocumentContents x)
                throws ConstraintError
              {
                body.appendChild(doc_contents.getTableOfContentsParts(parts));
                return Unit.unit();
              }
            });

          for (final SAPart part : parts.getElements()) {
            body.appendChild(SDocumentXHTMLWriterSingle.part(
              part_contents,
              section_contents,
              link_provider,
              formals,
              part));
          }
          return Unit.unit();
        }

        @Override public Unit visitDocumentWithSections(
          final @Nonnull SADocumentWithSections document)
          throws ConstraintError,
            Exception
        {
          final SNonEmptyList<SASection> sections = document.getSections();

          doc.getContents().mapPartial(
            new PartialFunction<SDocumentContents, Unit, ConstraintError>() {
              @Override public Unit call(
                final @Nonnull SDocumentContents x)
                throws ConstraintError
              {
                body.appendChild(doc_contents
                  .getTableOfContentsSections(sections));
                return Unit.unit();
              }
            });

          for (final SASection s : sections.getElements()) {
            body.appendChild(SDocumentXHTMLWriterSingle.section(
              section_contents,
              link_provider,
              formals,
              s));
          }

          return Unit.unit();
        }
      });

      SDocumentXHTMLWriterSingle.footnotes(link_provider, formals, doc, body);

      callbacks.onBodyEnd(body);

      final List<Document> list = new ArrayList<Document>();
      list.add(page.getDocument());
      return list;
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  static void footnotes(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SADocument doc,
    final @Nonnull Element body)
    throws ConstraintError,
      Exception
  {
    final List<SAFootnote> footnotes = doc.getFootnotes();
    if (footnotes.size() > 0) {
      final String[] classes = new String[1];
      classes[0] = "footnotes";
      final Element e =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      e.appendChild(new Element("hr", SXHTML.XHTML_URI.toString()));

      for (final SAFootnote f : footnotes) {
        e.appendChild(SXHTML.footnoteContent(link_provider, formals, f));
      }

      body.appendChild(e);
    }
  }

  private static @Nonnull Element getFormalItemLink(
    final @Nonnull SAFormalItem f)
    throws ConstraintError,
      Exception
  {
    final SAFormalItemNumber n = f.getNumber();
    final Element e = SXHTML.linkRaw("#" + SXHTML.getFormalItemAnchorID(n));
    final String text =
      String.format("%s %s", n.formalItemNumberFormat(), f
        .getTitle()
        .getActual());
    e.appendChild(text);
    return e;
  }
}
