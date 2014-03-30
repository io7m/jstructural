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

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SADocumentTitle;
import com.io7m.jstructural.annotated.SADocumentVisitor;
import com.io7m.jstructural.annotated.SADocumentWithParts;
import com.io7m.jstructural.annotated.SADocumentWithSections;
import com.io7m.jstructural.annotated.SAFormalItemNumber;
import com.io7m.jstructural.annotated.SAFormalItemsByKindReadable;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAIDMapReadable;
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
import com.io7m.jstructural.annotated.SASegmentsReadable;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SASubsectionContent;
import com.io7m.jstructural.annotated.SASubsectionNumber;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;
import com.io7m.jstructural.core.SSectionContents;

/**
 * An XHTML writer that produces XHTML 1.0 Strict documents, creating new
 * pages on parts and sections.
 */

@SuppressWarnings("synthetic-access") public final class SDocumentXHTMLWriterMulti implements
  SDocumentXHTMLWriter
{
  private static final @Nonnull String                  FRONT_PAGE;
  private static final @Nonnull Option<SASegmentNumber> NO_NUMBER;

  static {
    FRONT_PAGE = "index-m." + SXHTML.OUTPUT_FILE_SUFFIX;
  }

  static {
    NO_NUMBER = Option.none();
  }

  private static @Nonnull String getFormalItemLinkTarget(
    final @Nonnull SAFormalItemNumber f)
    throws Exception,
      ConstraintError
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getFormalItemFile(f));
    b.append("#");
    b.append(SXHTMLAnchors.getFormalItemAnchorID(f));
    return b.toString();
  }

  private static @Nonnull String getParagraphLinkTarget(
    final @Nonnull SAParagraphNumber n)
    throws ConstraintError,
      Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getParagraphFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getParagraphAnchorID(n));
    return b.toString();
  }

  private static @Nonnull String getPartLinkTarget(
    final @Nonnull SAPartNumber n)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getPartFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getPartAnchorID(n));
    return b.toString();
  }

  private static @Nonnull String getSectionLinkTarget(
    final @Nonnull SASectionNumber n)
    throws ConstraintError,
      Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getSectionFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getSectionAnchorID(n));
    return b.toString();
  }

  private static @Nonnull String getSegmentLinkTarget(
    final @Nonnull SASegmentNumber s)
    throws ConstraintError,
      Exception
  {
    return s.segmentNumberAccept(new SASegmentNumberVisitor<String>() {
      @Override public String visitPartNumber(
        final @Nonnull SAPartNumber n)
        throws ConstraintError,
          Exception
      {
        return SDocumentXHTMLWriterMulti.getPartLinkTarget(n);
      }

      @Override public String visitSectionNumber(
        final @Nonnull SASectionNumber n)
        throws ConstraintError,
          Exception
      {
        return SDocumentXHTMLWriterMulti.getSectionLinkTarget(n);
      }
    });
  }

  private static @Nonnull String getSubsectionLinkTarget(
    final @Nonnull SASubsectionNumber n)
    throws ConstraintError,
      Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getSubsectionFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getSubsectionAnchorID(n));
    return b.toString();
  }

  private static @Nonnull Element navigationBar(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SADocument document,
    final @Nonnull Option<SASegmentNumber> segment,
    final boolean top)
    throws ConstraintError,
      Exception
  {
    final String[] en_classes = new String[2];
    en_classes[0] = "navbar";
    en_classes[1] = top ? "navbar_top" : "navbar_bottom";
    final Element en =
      SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, en_classes);

    final String[] et_classes = new String[1];
    et_classes[0] = "navbar_table";
    final Element etn =
      SXHTML.elementWithClasses("table", SXHTML.NO_TYPE, et_classes);
    etn.addAttribute(new Attribute("summary", null, "Navigation bar"));

    if (top) {
      en.appendChild(etn);
      en.appendChild(SDocumentXHTMLWriterMulti.navigationBarHR());
      etn.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleRow(
        document,
        segment));
      etn.appendChild(SDocumentXHTMLWriterMulti.navigationBarLinkRow(
        document,
        link_provider,
        segment));
    } else {
      en.appendChild(SDocumentXHTMLWriterMulti.navigationBarHR());
      en.appendChild(etn);
      etn.appendChild(SDocumentXHTMLWriterMulti.navigationBarLinkRow(
        document,
        link_provider,
        segment));
      etn.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleRow(
        document,
        segment));
    }

    return en;
  }

  private static @Nonnull Element navigationBarHR()
  {
    final String[] ehr_classes = new String[1];
    ehr_classes[0] = "hr";
    return SXHTML.elementWithClasses("hr", SXHTML.NO_TYPE, ehr_classes);
  }

  private static @Nonnull Element navigationBarLinkRow(
    final @Nonnull SASegmentsReadable segments,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError
  {
    final Element er = new Element("tr", SXHTML.XHTML_URI.toString());

    er.appendChild(SDocumentXHTMLWriterMulti
      .navigationBarLinkRowCellPrevious(segments, link_provider, current));
    er.appendChild(SDocumentXHTMLWriterMulti.navigationBarLinkRowCellUp(
      segments,
      link_provider,
      current));
    er.appendChild(SDocumentXHTMLWriterMulti.navigationBarLinkRowCellNext(
      segments,
      link_provider,
      current));

    return er;
  }

  private static @Nonnull Element navigationBarLinkRowCellNext(
    final @Nonnull SASegmentsReadable segments,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError
  {
    final String[] c = new String[1];
    c[0] = "navbar_next_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> next =
        segments.segmentGetNext(current_some.value);

      if (next.isSome()) {
        final Some<SASegmentNumber> next_some = (Some<SASegmentNumber>) next;
        final Element elink =
          SXHTML.linkRaw(link_provider.getSegmentLinkTarget(next_some.value));
        elink.appendChild("Next");
        etc.appendChild(elink);
      }
    } else {
      final SASegmentNumber first = segments.segmentGetFirst();
      final Element elink =
        SXHTML.linkRaw(link_provider.getSegmentLinkTarget(first));
      elink.appendChild("Next");
      etc.appendChild(elink);
    }

    return etc;
  }

  private static @Nonnull Element navigationBarLinkRowCellPrevious(
    final @Nonnull SASegmentsReadable segments,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError
  {
    final String[] c = new String[1];
    c[0] = "navbar_prev_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> previous =
        segments.segmentGetPrevious(current_some.value);

      final Element elink;
      if (previous.isSome()) {
        final Some<SASegmentNumber> previous_some =
          (Some<SASegmentNumber>) previous;
        elink =
          SXHTML.linkRaw(link_provider
            .getSegmentLinkTarget(previous_some.value));
      } else {
        elink = SXHTML.linkRaw(SDocumentXHTMLWriterMulti.FRONT_PAGE);
      }

      elink.appendChild("Previous");
      etc.appendChild(elink);
    }
    return etc;
  }

  private static @Nonnull Element navigationBarLinkRowCellUp(
    final @Nonnull SASegmentsReadable segments,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError
  {
    final String[] c = new String[1];
    c[0] = "navbar_up_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> up =
        segments.segmentGetUp(current_some.value);

      final Element elink;
      if (up.isSome()) {
        final Some<SASegmentNumber> up_some = (Some<SASegmentNumber>) up;
        elink =
          SXHTML.linkRaw(link_provider.getSegmentLinkTarget(up_some.value));
      } else {
        elink = SXHTML.linkRaw(SDocumentXHTMLWriterMulti.FRONT_PAGE);
      }

      elink.appendChild("Up");
      etc.appendChild(elink);
    }

    return etc;
  }

  private static @Nonnull String navigationBarTitleForSegment(
    final @Nonnull SADocument document,
    final @Nonnull SASegmentNumber n)
    throws ConstraintError,
      Exception
  {
    return n.segmentNumberAccept(new SASegmentNumberVisitor<String>() {
      @Override public String visitPartNumber(
        final @Nonnull SAPartNumber pn)
        throws ConstraintError,
          Exception
      {
        return document.documentAccept(new SADocumentVisitor<String>() {
          @Override public String visitDocumentWithParts(
            final @Nonnull SADocumentWithParts dwp)
            throws ConstraintError,
              Exception
          {
            final Option<SAPart> p = dwp.getPart(pn);
            if (p.isSome()) {
              final Some<SAPart> some = (Some<SAPart>) p;
              final StringBuilder sb = new StringBuilder();
              sb.append(pn.getActual());
              sb.append(". ");
              sb.append(some.value.getTitle().getActual());
              return sb.toString();
            }
            throw new UnreachableCodeException();
          }

          @Override public String visitDocumentWithSections(
            final @Nonnull SADocumentWithSections dws)
            throws ConstraintError,
              Exception
          {
            throw new UnreachableCodeException();
          }
        });
      }

      @Override public String visitSectionNumber(
        final @Nonnull SASectionNumber sn)
        throws ConstraintError,
          Exception
      {
        final Option<SASection> s = document.getSection(sn);
        if (s.isSome()) {
          final Some<SASection> some = (Some<SASection>) s;
          final StringBuilder sb = new StringBuilder();
          sb.append(sn.sectionNumberFormat());
          sb.append(". ");
          sb.append(some.value.getTitle().getActual());
          return sb.toString();
        }
        throw new UnreachableCodeException();
      }
    });
  }

  private static @Nonnull Element navigationBarTitleRow(
    final @Nonnull SADocument document,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError,
      Exception
  {
    final Element er = new Element("tr", SXHTML.XHTML_URI.toString());

    er.appendChild(SDocumentXHTMLWriterMulti
      .navigationBarTitleRowCellPrevious(document, current));
    er.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleRowCellUp(
      document,
      current));
    er.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleRowCellNext(
      document,
      current));

    return er;
  }

  private static @Nonnull Element navigationBarTitleRowCellNext(
    final @Nonnull SADocument document,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError,
      Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_next_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> next =
        document.segmentGetNext(current_some.value);

      if (next.isSome()) {
        final Some<SASegmentNumber> next_some = (Some<SASegmentNumber>) next;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, next_some.value));
      }
    } else {
      final SASegmentNumber first = document.segmentGetFirst();
      etc.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleForSegment(
        document,
        first));
    }

    return etc;
  }

  private static @Nonnull Element navigationBarTitleRowCellPrevious(
    final @Nonnull SADocument document,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError,
      Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_prev_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> previous =
        document.segmentGetPrevious(current_some.value);

      if (previous.isSome()) {
        final Some<SASegmentNumber> previous_some =
          (Some<SASegmentNumber>) previous;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, previous_some.value));
      } else {
        etc.appendChild(document.getTitle().getActual());
      }
    }
    return etc;
  }

  private static @Nonnull Element navigationBarTitleRowCellUp(
    final @Nonnull SADocument document,
    final @Nonnull Option<SASegmentNumber> current)
    throws ConstraintError,
      Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_up_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final Option<SASegmentNumber> up =
        document.segmentGetUp(current_some.value);

      if (up.isSome()) {
        final Some<SASegmentNumber> up_some = (Some<SASegmentNumber>) up;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, up_some.value));
      } else {
        etc.appendChild(document.getTitle().getActual());
      }
    }

    return etc;
  }

  private static void part(
    final @Nonnull SortedMap<String, Document> documents,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SDocumentXHTMLWriterCallbacks callbacks,
    final @Nonnull SXHTMLPartContents part_contents,
    final @Nonnull SXHTMLSectionContents section_contents,
    final @Nonnull SADocument doc,
    final @Nonnull SAPart p)
    throws ConstraintError,
      Exception
  {
    final SAPartNumber number = p.getNumber();
    final SADocumentTitle title = doc.getTitle();
    final Option<SDocumentStyle> style = doc.getStyle();

    final StringBuilder tb = new StringBuilder();
    tb.append(title.getActual());
    tb.append(": ");
    tb.append(number.getActual());
    tb.append(". ");
    tb.append(p.getTitle().getActual());

    final SXHTMLPage page = SXHTML.newPage(tb.toString(), style);
    final Element body = page.getBodyContainer();

    callbacks.onHead(page.getHead());
    callbacks.onBodyStart(body);
    final Some<SASegmentNumber> some = Option.some((SASegmentNumber) number);
    body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      doc,
      some,
      true));

    final Element part_main = SXHTML.partContainer(p.getTitle());
    body.appendChild(part_main);

    final SNonEmptyList<SASection> sections = p.getSections();

    p.getContents().mapPartial(
      new PartialFunction<SPartContents, Unit, ConstraintError>() {
        @Override public Unit call(
          final @Nonnull SPartContents _)
          throws ConstraintError
        {
          try {
            part_main.appendChild(part_contents
              .getTableOfContentsSections(sections));
            return Unit.unit();
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }
      });

    body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      doc,
      some,
      false));
    callbacks.onBodyEnd(body);

    final String name = SXHTMLAnchors.getPartFile(number);
    assert documents.containsKey(name) == false;
    documents.put(name, page.getDocument());

    for (final SASection s : p.getSections().getElements()) {
      SDocumentXHTMLWriterMulti.section(
        documents,
        link_provider,
        callbacks,
        section_contents,
        doc,
        s);
    }
  }

  private static void section(
    final @Nonnull SortedMap<String, Document> documents,
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SDocumentXHTMLWriterCallbacks callbacks,
    final @Nonnull SXHTMLSectionContents section_contents,
    final @Nonnull SADocument document,
    final @Nonnull SASection s)
    throws ConstraintError,
      Exception
  {
    final SASectionNumber number = s.getNumber();
    final SADocumentTitle title = document.getTitle();
    final Option<SDocumentStyle> style = document.getStyle();

    final StringBuilder tb = new StringBuilder();
    tb.append(title.getActual());
    tb.append(": ");
    tb.append(number.sectionNumberFormat());
    tb.append(". ");
    tb.append(s.getTitle().getActual());

    final SXHTMLPage page = SXHTML.newPage(tb.toString(), style);
    final Element body = page.getBodyContainer();

    callbacks.onHead(page.getHead());
    callbacks.onBodyStart(body);
    final Some<SASegmentNumber> some = Option.some((SASegmentNumber) number);
    body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      document,
      some,
      true));

    final Element section_main = SXHTML.sectionContainer(s);
    body.appendChild(section_main);

    s.sectionAccept(new SASectionVisitor<Unit>() {
      @Override public Unit visitSectionWithParagraphs(
        final @Nonnull SASectionWithParagraphs swp)
        throws ConstraintError,
          Exception
      {
        for (final SASubsectionContent c : swp
          .getSectionContent()
          .getElements()) {
          section_main.appendChild(SXHTML.subsectionContent(
            link_provider,
            document.getFormals(),
            c));
        }

        return Unit.unit();
      }

      @Override public Unit visitSectionWithSubsections(
        final @Nonnull SASectionWithSubsections sws)
        throws ConstraintError,
          Exception
      {
        final SNonEmptyList<SASubsection> subsections = sws.getSubsections();

        s.getContents().mapPartial(
          new PartialFunction<SSectionContents, Unit, ConstraintError>() {
            @Override public Unit call(
              final SSectionContents x)
              throws ConstraintError
            {
              try {
                section_main.appendChild(section_contents
                  .getTableOfContents(subsections));
                return Unit.unit();
              } catch (final Exception e) {
                throw new UnreachableCodeException(e);
              }
            }
          });

        for (final SASubsection ss : sws.getSubsections().getElements()) {
          section_main.appendChild(SXHTML.subsection(
            link_provider,
            document.getFormals(),
            ss));
        }

        return Unit.unit();
      }
    });

    body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      document,
      some,
      false));
    callbacks.onBodyEnd(body);

    final String name = SXHTMLAnchors.getSectionFile(number);
    assert documents.containsKey(name) == false;
    documents.put(name, page.getDocument());
  }

  /**
   * Construct a new XHTML writer.
   */

  public SDocumentXHTMLWriterMulti()
  {

  }

  @Override public SortedMap<String, Document> writeDocuments(
    final @Nonnull SDocumentXHTMLWriterCallbacks callbacks,
    final @Nonnull SADocument doc)
    throws ConstraintError
  {
    try {
      Constraints.constrainNotNull(callbacks, "Callbacks");
      Constraints.constrainNotNull(doc, "Document");

      final SLinkProvider link_provider = new SLinkProvider() {
        @Override public String getFormalItemLinkTarget(
          final @Nonnull SAFormalItemNumber f)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterMulti.getFormalItemLinkTarget(f);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

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
                  return SDocumentXHTMLWriterMulti
                    .getParagraphLinkTarget(paragraph.getNumber());
                }

                @Override public String visitPart(
                  final @Nonnull SAPart part)
                  throws ConstraintError,
                    Exception
                {
                  return SDocumentXHTMLWriterMulti.getPartLinkTarget(part
                    .getNumber());
                }

                @Override public String visitSection(
                  final @Nonnull SASection section)
                  throws ConstraintError,
                    Exception
                {
                  return SDocumentXHTMLWriterMulti
                    .getSectionLinkTarget(section.getNumber());
                }

                @Override public String visitSubsection(
                  final @Nonnull SASubsection subsection)
                  throws ConstraintError,
                    Exception
                {
                  return SDocumentXHTMLWriterMulti
                    .getSubsectionLinkTarget(subsection.getNumber());
                }
              });
          } catch (final Exception x) {
            throw new UnreachableCodeException(x);
          }
        }

        @Override public String getPartLinkTarget(
          final @Nonnull SAPartNumber p)
          throws ConstraintError
        {
          return SDocumentXHTMLWriterMulti.getPartLinkTarget(p);
        }

        @Override public String getSectionLinkTarget(
          final @Nonnull SASectionNumber s)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterMulti.getSectionLinkTarget(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSegmentLinkTarget(
          final @Nonnull SASegmentNumber segment)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterMulti.getSegmentLinkTarget(segment);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSubsectionLinkTarget(
          final @Nonnull SASubsectionNumber s)
          throws ConstraintError
        {
          try {
            return SDocumentXHTMLWriterMulti.getSubsectionLinkTarget(s);
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

      final SortedMap<String, Document> documents =
        new TreeMap<String, Document>();

      doc.documentAccept(new SADocumentVisitor<Unit>() {
        @Override public Unit visitDocumentWithParts(
          final @Nonnull SADocumentWithParts dwp)
          throws ConstraintError,
            Exception
        {
          final SXHTMLPage page =
            SXHTML.newPage(doc.getTitle().getActual(), doc.getStyle());
          final Element body = page.getBodyContainer();

          callbacks.onHead(page.getHead());
          callbacks.onBodyStart(body);
          body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            true));

          final SNonEmptyList<SAPart> parts = dwp.getParts();
          body.appendChild(SXHTML.documentTitle(dwp));

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

          body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            false));
          callbacks.onBodyEnd(body);

          assert documents.containsKey(SDocumentXHTMLWriterMulti.FRONT_PAGE) == false;
          documents.put(
            SDocumentXHTMLWriterMulti.FRONT_PAGE,
            page.getDocument());

          for (final SAPart p : parts.getElements()) {
            SDocumentXHTMLWriterMulti.part(
              documents,
              link_provider,
              callbacks,
              part_contents,
              section_contents,
              doc,
              p);
          }

          return Unit.unit();
        }

        @Override public Unit visitDocumentWithSections(
          final @Nonnull SADocumentWithSections dws)
          throws ConstraintError,
            Exception
        {
          final SXHTMLPage page =
            SXHTML.newPage(doc.getTitle().getActual(), doc.getStyle());
          final Element body = page.getBodyContainer();

          final SNonEmptyList<SASection> sections = dws.getSections();

          callbacks.onHead(page.getHead());
          callbacks.onBodyStart(body);
          body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            true));

          body.appendChild(SXHTML.documentTitle(dws));

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

          body.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            false));
          callbacks.onBodyEnd(body);

          assert documents.containsKey(SDocumentXHTMLWriterMulti.FRONT_PAGE) == false;
          documents.put(
            SDocumentXHTMLWriterMulti.FRONT_PAGE,
            page.getDocument());
          return Unit.unit();
        }
      });

      return documents;
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }
}
