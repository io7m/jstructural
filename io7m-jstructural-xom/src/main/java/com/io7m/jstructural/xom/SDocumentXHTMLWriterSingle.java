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

import java.util.SortedMap;
import java.util.TreeMap;

import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SADocumentVisitor;
import com.io7m.jstructural.annotated.SADocumentWithParts;
import com.io7m.jstructural.annotated.SADocumentWithSections;
import com.io7m.jstructural.annotated.SAFormalItem;
import com.io7m.jstructural.annotated.SAFormalItemNumber;
import com.io7m.jstructural.annotated.SAFormalItemsByKindReadable;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAIDMapReadable;
import com.io7m.jstructural.annotated.SAIDTargetContent;
import com.io7m.jstructural.annotated.SAIDTargetContentVisitor;
import com.io7m.jstructural.annotated.SAParagraph;
import com.io7m.jstructural.annotated.SAParagraphNumber;
import com.io7m.jstructural.annotated.SAPart;
import com.io7m.jstructural.annotated.SAPartNumber;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASectionNumber;
import com.io7m.jstructural.annotated.SASectionVisitor;
import com.io7m.jstructural.annotated.SASectionWithParagraphs;
import com.io7m.jstructural.annotated.SASectionWithSubsections;
import com.io7m.jstructural.annotated.SASegmentNumber;
import com.io7m.jstructural.annotated.SASegmentNumberVisitor;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SASubsectionContent;
import com.io7m.jstructural.annotated.SASubsectionContentVisitor;
import com.io7m.jstructural.annotated.SASubsectionNumber;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;
import com.io7m.jstructural.core.SSectionContents;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * An XHTML writer that produces XHTML 1.0 Strict documents, as a single page.
 */

@SuppressWarnings("synthetic-access") public final class SDocumentXHTMLWriterSingle implements
  SDocumentXHTMLWriter
{
  private static String getFormalItemLinkTarget(
    final SAFormalItemNumber f)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append("#");
    b.append(SXHTMLAnchors.getFormalItemAnchorID(f));
    return b.toString();
  }

  private static String getParagraphLinkTarget(
    final SAParagraphNumber n)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append("#");
    b.append(SXHTMLAnchors.getParagraphAnchorID(n));
    return b.toString();
  }

  private static String getPartLinkTarget(
    final SAPartNumber p)
  {
    final StringBuilder b = new StringBuilder();
    b.append("#");
    b.append(SXHTMLAnchors.getPartAnchorID(p));
    return b.toString();
  }

  private static String getSectionLinkTarget(
    final SASectionNumber s)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append("#");
    b.append(SXHTMLAnchors.getSectionAnchorID(s));
    return b.toString();
  }

  private static String getSegmentLinkTarget(
    final SASegmentNumber s)
    throws Exception
  {
    return s.segmentNumberAccept(new SASegmentNumberVisitor<String>() {
      @Override public String visitPartNumber(
        final SAPartNumber n)
        throws Exception
      {
        return SDocumentXHTMLWriterSingle.getPartLinkTarget(n);
      }

      @Override public String visitSectionNumber(
        final SASectionNumber n)
        throws Exception
      {
        return SDocumentXHTMLWriterSingle.getSectionLinkTarget(n);
      }
    });
  }

  private static String getSubsectionLinkTarget(
    final SASubsectionNumber s)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append("#");
    b.append(SXHTMLAnchors.getSubsectionAnchorID(s));
    return b.toString();
  }

  private static Element part(
    final SXHTMLPartContents part_contents_writer,
    final SXHTMLSectionContents section_contents_writer,
    final SLinkProvider link_provider,
    final SAFormalItemsByKindReadable formals,
    final SAPart part)
    throws Exception
  {
    final Element e = SXHTML.partContainer(part.getTitle());

    part.getContents().map(new FunctionType<SPartContents, Unit>() {
      @Override public Unit call(
        final SPartContents x)
      {
        try {
          e.appendChild(part_contents_writer.getTableOfContentsSections(part
            .getSections()));
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

  private static Element section(
    final SXHTMLSectionContents section_contents_writer,
    final SLinkProvider link_provider,
    final SAFormalItemsByKindReadable formals,
    final SASection s)
    throws Exception
  {
    final Element e = SXHTML.sectionContainer(s);

    s.sectionAccept(new SASectionVisitor<Unit>() {
      @Override public Unit visitSectionWithParagraphs(
        final SASectionWithParagraphs swp)
        throws Exception
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
        throws Exception
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
    final SLinkProvider link_provider,
    final SAFormalItemsByKindReadable formals,
    final SASectionWithParagraphs swp,
    final Element e)
    throws Exception
  {
    final SNonEmptyList<SASubsectionContent> content =
      swp.getSectionContent();

    for (final SASubsectionContent ss : content.getElements()) {
      e.appendChild(ss
        .subsectionContentAccept(new SASubsectionContentVisitor<Element>() {
          @Override public Element visitFormalItem(
            final SAFormalItem formal)
            throws Exception
          {
            return SXHTML.formalItem(link_provider, formals, formal);
          }

          @Override public Element visitParagraph(
            final SAParagraph paragraph)
            throws Exception
          {
            return SXHTML.paragraph(link_provider, formals, paragraph);
          }
        }));
    }
  }

  private static void sectionWithSubsections(
    final SXHTMLSectionContents section_contents_writer,
    final SLinkProvider link_provider,
    final SAFormalItemsByKindReadable formals,
    final SASectionWithSubsections sws,
    final Element e)
    throws Exception
  {
    final SNonEmptyList<SASubsection> subsections = sws.getSubsections();

    sws.getContents().map(new FunctionType<SSectionContents, Unit>() {
      @Override public Unit call(
        final SSectionContents x)
      {
        e.appendChild(section_contents_writer.getTableOfContents(subsections));
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

  @Override public SortedMap<String, Document> writeDocuments(
    final SDocumentXHTMLWriterCallbacks callbacks,
    final SADocument doc)
  {
    try {
      NullCheck.notNull(callbacks, "Callbacks");
      NullCheck.notNull(doc, "Document");

      final SLinkProvider link_provider = new SLinkProvider() {
        @Override public String getFormalItemLinkTarget(
          final SAFormalItemNumber f)
        {
          try {
            return SDocumentXHTMLWriterSingle.getFormalItemLinkTarget(f);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getLinkTargetForID(
          final SAID id)
        {
          try {
            final SAIDMapReadable map = doc.getIDMappings();
            final SAIDTargetContent content = map.get(id);
            assert content != null;

            return content
              .targetContentAccept(new SAIDTargetContentVisitor<String>() {
                @Override public String visitParagraph(
                  final SAParagraph paragraph)
                  throws Exception
                {
                  return SDocumentXHTMLWriterSingle
                    .getParagraphLinkTarget(paragraph.getNumber());
                }

                @Override public String visitPart(
                  final SAPart part)
                  throws Exception
                {
                  return SDocumentXHTMLWriterSingle.getPartLinkTarget(part
                    .getNumber());
                }

                @Override public String visitSection(
                  final SASection section)
                  throws Exception
                {
                  return SDocumentXHTMLWriterSingle
                    .getSectionLinkTarget(section.getNumber());
                }

                @Override public String visitSubsection(
                  final SASubsection subsection)
                  throws Exception
                {
                  return SDocumentXHTMLWriterSingle
                    .getSubsectionLinkTarget(subsection.getNumber());
                }
              });
          } catch (final Exception x) {
            throw new UnreachableCodeException(x);
          }
        }

        @Override public String getPartLinkTarget(
          final SAPartNumber p)
        {
          return SDocumentXHTMLWriterSingle.getPartLinkTarget(p);
        }

        @Override public String getSectionLinkTarget(
          final SASectionNumber s)
        {
          try {
            return SDocumentXHTMLWriterSingle.getSectionLinkTarget(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSegmentLinkTarget(
          final SASegmentNumber s)
        {
          try {
            return SDocumentXHTMLWriterSingle.getSegmentLinkTarget(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSubsectionLinkTarget(
          final SASubsectionNumber s)
        {
          try {
            return SDocumentXHTMLWriterSingle.getSubsectionLinkTarget(s);
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

      final Element container = page.getBodyContainer();
      final Element rbody = callbacks.onBodyStart(container);
      SXHTMLReparent.reparentBodyNode(container, rbody);
      container.appendChild(SXHTML.documentTitle(doc));

      doc.documentAccept(new SADocumentVisitor<Unit>() {
        @Override public Unit visitDocumentWithParts(
          final SADocumentWithParts document)
          throws Exception
        {
          final SNonEmptyList<SAPart> parts = document.getParts();

          doc.getContents().map(new FunctionType<SDocumentContents, Unit>() {
            @Override public Unit call(
              final SDocumentContents x)
            {
              container.appendChild(doc_contents
                .getTableOfContentsParts(parts));
              return Unit.unit();
            }
          });

          for (final SAPart part : parts.getElements()) {
            container.appendChild(SDocumentXHTMLWriterSingle.part(
              part_contents,
              section_contents,
              link_provider,
              formals,
              part));
          }
          return Unit.unit();
        }

        @Override public Unit visitDocumentWithSections(
          final SADocumentWithSections document)
          throws Exception
        {
          final SNonEmptyList<SASection> sections = document.getSections();

          doc.getContents().map(new FunctionType<SDocumentContents, Unit>() {
            @Override public Unit call(
              final SDocumentContents x)
            {
              container.appendChild(doc_contents
                .getTableOfContentsSections(sections));
              return Unit.unit();
            }
          });

          for (final SASection s : sections.getElements()) {
            container.appendChild(SDocumentXHTMLWriterSingle.section(
              section_contents,
              link_provider,
              formals,
              s));
          }

          return Unit.unit();
        }
      });

      SXHTML.footnotes(link_provider, formals, doc.getFootnotes(), container);
      callbacks.onBodyEnd(container);

      final SortedMap<String, Document> documents =
        new TreeMap<String, Document>();
      documents.put("index.xhtml", page.getDocument());
      return documents;
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }
}
