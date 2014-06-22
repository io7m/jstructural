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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SADocumentTitle;
import com.io7m.jstructural.annotated.SADocumentVisitor;
import com.io7m.jstructural.annotated.SADocumentWithParts;
import com.io7m.jstructural.annotated.SADocumentWithSections;
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
import com.io7m.jstructural.annotated.SASegmentsReadable;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SASubsectionContent;
import com.io7m.jstructural.annotated.SASubsectionNumber;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;
import com.io7m.jstructural.core.SSectionContents;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * An XHTML writer that produces XHTML 1.0 Strict documents, creating new
 * pages on parts and sections.
 */

@SuppressWarnings("synthetic-access") public final class SDocumentXHTMLWriterMulti implements
  SDocumentXHTMLWriter
{
  private static final String                      FRONT_PAGE;
  private static final OptionType<SASegmentNumber> NO_NUMBER;

  static {
    FRONT_PAGE = "index-m." + SXHTML.OUTPUT_FILE_SUFFIX;
  }

  static {
    NO_NUMBER = Option.none();
  }

  private static String getFormalItemLinkTarget(
    final SAFormalItemNumber f)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getFormalItemFile(f));
    b.append("#");
    b.append(SXHTMLAnchors.getFormalItemAnchorID(f));
    return b.toString();
  }

  private static String getParagraphLinkTarget(
    final SAParagraphNumber n)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getParagraphFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getParagraphAnchorID(n));
    return b.toString();
  }

  private static String getPartLinkTarget(
    final SAPartNumber n)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getPartFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getPartAnchorID(n));
    return b.toString();
  }

  private static String getSectionLinkTarget(
    final SASectionNumber n)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getSectionFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getSectionAnchorID(n));
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
        return SDocumentXHTMLWriterMulti.getPartLinkTarget(n);
      }

      @Override public String visitSectionNumber(
        final SASectionNumber n)
        throws Exception
      {
        return SDocumentXHTMLWriterMulti.getSectionLinkTarget(n);
      }
    });
  }

  private static String getSubsectionLinkTarget(
    final SASubsectionNumber n)
    throws Exception
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTMLAnchors.getSubsectionFile(n));
    b.append("#");
    b.append(SXHTMLAnchors.getSubsectionAnchorID(n));
    return b.toString();
  }

  private static Element navigationBar(
    final SLinkProvider link_provider,
    final SADocument document,
    final OptionType<SASegmentNumber> segment,
    final boolean top)
    throws Exception
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

  private static Element navigationBarHR()
  {
    final String[] ehr_classes = new String[1];
    ehr_classes[0] = "hr";
    return SXHTML.elementWithClasses("hr", SXHTML.NO_TYPE, ehr_classes);
  }

  private static Element navigationBarLinkRow(
    final SASegmentsReadable segments,
    final SLinkProvider link_provider,
    final OptionType<SASegmentNumber> current)
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

  private static Element navigationBarLinkRowCellNext(
    final SASegmentsReadable segments,
    final SLinkProvider link_provider,
    final OptionType<SASegmentNumber> current)
  {
    final String[] c = new String[1];
    c[0] = "navbar_next_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> next =
        segments.segmentGetNext(current_some.get());

      if (next.isSome()) {
        final Some<SASegmentNumber> next_some = (Some<SASegmentNumber>) next;
        final Element elink =
          SXHTML.linkRaw(link_provider.getSegmentLinkTarget(next_some.get()));
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

  private static Element navigationBarLinkRowCellPrevious(
    final SASegmentsReadable segments,
    final SLinkProvider link_provider,
    final OptionType<SASegmentNumber> current)
  {
    final String[] c = new String[1];
    c[0] = "navbar_prev_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> previous =
        segments.segmentGetPrevious(current_some.get());

      final Element elink;
      if (previous.isSome()) {
        final Some<SASegmentNumber> previous_some =
          (Some<SASegmentNumber>) previous;
        elink =
          SXHTML.linkRaw(link_provider.getSegmentLinkTarget(previous_some
            .get()));
      } else {
        elink = SXHTML.linkRaw(SDocumentXHTMLWriterMulti.FRONT_PAGE);
      }

      elink.appendChild("Previous");
      etc.appendChild(elink);
    }
    return etc;
  }

  private static Element navigationBarLinkRowCellUp(
    final SASegmentsReadable segments,
    final SLinkProvider link_provider,
    final OptionType<SASegmentNumber> current)
  {
    final String[] c = new String[1];
    c[0] = "navbar_up_file_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> up =
        segments.segmentGetUp(current_some.get());

      final Element elink;
      if (up.isSome()) {
        final Some<SASegmentNumber> up_some = (Some<SASegmentNumber>) up;
        elink =
          SXHTML.linkRaw(link_provider.getSegmentLinkTarget(up_some.get()));
      } else {
        elink = SXHTML.linkRaw(SDocumentXHTMLWriterMulti.FRONT_PAGE);
      }

      elink.appendChild("Up");
      etc.appendChild(elink);
    }

    return etc;
  }

  private static String navigationBarTitleForSegment(
    final SADocument document,
    final SASegmentNumber n)
    throws Exception
  {
    return n.segmentNumberAccept(new SASegmentNumberVisitor<String>() {
      @Override public String visitPartNumber(
        final SAPartNumber pn)
        throws Exception
      {
        return document.documentAccept(new SADocumentVisitor<String>() {
          @Override public String visitDocumentWithParts(
            final SADocumentWithParts dwp)
            throws Exception
          {
            final OptionType<SAPart> p = dwp.getPart(pn);
            if (p.isSome()) {
              final Some<SAPart> some = (Some<SAPart>) p;
              final StringBuilder sb = new StringBuilder();
              sb.append(pn.getActual());
              sb.append(". ");
              sb.append(some.get().getTitle().getActual());
              return sb.toString();
            }
            throw new UnreachableCodeException();
          }

          @Override public String visitDocumentWithSections(
            final SADocumentWithSections dws)
            throws Exception
          {
            throw new UnreachableCodeException();
          }
        });
      }

      @Override public String visitSectionNumber(
        final SASectionNumber sn)
        throws Exception
      {
        final OptionType<SASection> s = document.getSection(sn);
        if (s.isSome()) {
          final Some<SASection> some = (Some<SASection>) s;
          final StringBuilder sb = new StringBuilder();
          sb.append(sn.sectionNumberFormat());
          sb.append(". ");
          sb.append(some.get().getTitle().getActual());
          return sb.toString();
        }
        throw new UnreachableCodeException();
      }
    });
  }

  private static Element navigationBarTitleRow(
    final SADocument document,
    final OptionType<SASegmentNumber> current)
    throws Exception
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

  private static Element navigationBarTitleRowCellNext(
    final SADocument document,
    final OptionType<SASegmentNumber> current)
    throws Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_next_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> next =
        document.segmentGetNext(current_some.get());

      if (next.isSome()) {
        final Some<SASegmentNumber> next_some = (Some<SASegmentNumber>) next;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, next_some.get()));
      }
    } else {
      final SASegmentNumber first = document.segmentGetFirst();
      etc.appendChild(SDocumentXHTMLWriterMulti.navigationBarTitleForSegment(
        document,
        first));
    }

    return etc;
  }

  private static Element navigationBarTitleRowCellPrevious(
    final SADocument document,
    final OptionType<SASegmentNumber> current)
    throws Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_prev_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> previous =
        document.segmentGetPrevious(current_some.get());

      if (previous.isSome()) {
        final Some<SASegmentNumber> previous_some =
          (Some<SASegmentNumber>) previous;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, previous_some.get()));
      } else {
        etc.appendChild(document.getTitle().getActual());
      }
    }
    return etc;
  }

  private static Element navigationBarTitleRowCellUp(
    final SADocument document,
    final OptionType<SASegmentNumber> current)
    throws Exception
  {
    final String[] c = new String[1];
    c[0] = "navbar_up_title_cell";
    final Element etc = SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, c);

    if (current.isSome()) {
      final Some<SASegmentNumber> current_some =
        (Some<SASegmentNumber>) current;
      final OptionType<SASegmentNumber> up =
        document.segmentGetUp(current_some.get());

      if (up.isSome()) {
        final Some<SASegmentNumber> up_some = (Some<SASegmentNumber>) up;
        etc.appendChild(SDocumentXHTMLWriterMulti
          .navigationBarTitleForSegment(document, up_some.get()));
      } else {
        etc.appendChild(document.getTitle().getActual());
      }
    }

    return etc;
  }

  private static void part(
    final SortedMap<String, Document> documents,
    final SLinkProvider link_provider,
    final SDocumentXHTMLWriterCallbacks callbacks,
    final SXHTMLPartContents part_contents,
    final SXHTMLSectionContents section_contents,
    final SADocument doc,
    final SAPart p)
    throws Exception
  {
    final SAPartNumber number = p.getNumber();
    final SADocumentTitle title = doc.getTitle();
    final OptionType<SDocumentStyle> style = doc.getStyle();

    final StringBuilder tb = new StringBuilder();
    tb.append(title.getActual());
    tb.append(": ");
    tb.append(number.getActual());
    tb.append(". ");
    tb.append(p.getTitle().getActual());

    final SXHTMLPage page = SXHTML.newPage(tb.toString(), style);

    callbacks.onHead(page.getHead());

    final Element container = page.getBodyContainer();
    final Element rbody = callbacks.onBodyStart(container);
    SXHTMLReparent.reparentBodyNode(container, rbody);

    final OptionType<SASegmentNumber> some =
      Option.some((SASegmentNumber) number);
    container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      doc,
      some,
      true));

    final Element part_main = SXHTML.partContainer(p.getTitle());
    container.appendChild(part_main);

    final SNonEmptyList<SASection> sections = p.getSections();

    p.getContents().map(new FunctionType<SPartContents, Unit>() {
      @Override public Unit call(
        final SPartContents _)
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

    container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      doc,
      some,
      false));
    callbacks.onBodyEnd(container);

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
    final SortedMap<String, Document> documents,
    final SLinkProvider link_provider,
    final SDocumentXHTMLWriterCallbacks callbacks,
    final SXHTMLSectionContents section_contents,
    final SADocument document,
    final SASection s)
    throws Exception
  {
    final SASectionNumber number = s.getNumber();
    final SADocumentTitle title = document.getTitle();
    final OptionType<SDocumentStyle> style = document.getStyle();

    final StringBuilder tb = new StringBuilder();
    tb.append(title.getActual());
    tb.append(": ");
    tb.append(number.sectionNumberFormat());
    tb.append(". ");
    tb.append(s.getTitle().getActual());

    final SXHTMLPage page = SXHTML.newPage(tb.toString(), style);

    callbacks.onHead(page.getHead());

    final Element container = page.getBodyContainer();
    final Element rbody = callbacks.onBodyStart(container);
    SXHTMLReparent.reparentBodyNode(container, rbody);

    final OptionType<SASegmentNumber> some =
      Option.some((SASegmentNumber) number);
    container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      document,
      some,
      true));

    final Element section_main = SXHTML.sectionContainer(s);
    container.appendChild(section_main);

    s.sectionAccept(new SASectionVisitor<Unit>() {
      @Override public Unit visitSectionWithParagraphs(
        final SASectionWithParagraphs swp)
        throws Exception
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
        final SASectionWithSubsections sws)
        throws Exception
      {
        final SNonEmptyList<SASubsection> subsections = sws.getSubsections();

        s.getContents().map(new FunctionType<SSectionContents, Unit>() {
          @Override public Unit call(
            final SSectionContents x)
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

    SXHTML.footnotes(
      link_provider,
      document.getFormals(),
      s.getFootnotes(),
      container);

    container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
      link_provider,
      document,
      some,
      false));

    callbacks.onBodyEnd(container);

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
            return SDocumentXHTMLWriterMulti.getFormalItemLinkTarget(f);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getLinkTargetForID(
          final SAID id)
        {
          try {
            final SAIDMapReadable map = doc.getIDMappings();
            final SAIDTargetContent k = map.get(id);
            assert k != null;
            return k
              .targetContentAccept(new SAIDTargetContentVisitor<String>() {
                @Override public String visitParagraph(
                  final SAParagraph paragraph)
                  throws Exception
                {
                  return SDocumentXHTMLWriterMulti
                    .getParagraphLinkTarget(paragraph.getNumber());
                }

                @Override public String visitPart(
                  final SAPart part)
                  throws Exception
                {
                  return SDocumentXHTMLWriterMulti.getPartLinkTarget(part
                    .getNumber());
                }

                @Override public String visitSection(
                  final SASection section)
                  throws Exception
                {
                  return SDocumentXHTMLWriterMulti
                    .getSectionLinkTarget(section.getNumber());
                }

                @Override public String visitSubsection(
                  final SASubsection subsection)
                  throws Exception
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
          final SAPartNumber p)
        {
          return SDocumentXHTMLWriterMulti.getPartLinkTarget(p);
        }

        @Override public String getSectionLinkTarget(
          final SASectionNumber s)
        {
          try {
            return SDocumentXHTMLWriterMulti.getSectionLinkTarget(s);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSegmentLinkTarget(
          final SASegmentNumber segment)
        {
          try {
            return SDocumentXHTMLWriterMulti.getSegmentLinkTarget(segment);
          } catch (final Exception e) {
            throw new UnreachableCodeException(e);
          }
        }

        @Override public String getSubsectionLinkTarget(
          final SASubsectionNumber s)
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
          final SADocumentWithParts dwp)
          throws Exception
        {
          final SXHTMLPage page =
            SXHTML.newPage(doc.getTitle().getActual(), doc.getStyle());

          callbacks.onHead(page.getHead());

          final Element container = page.getBodyContainer();
          final Element rbody = callbacks.onBodyStart(container);
          SXHTMLReparent.reparentBodyNode(container, rbody);

          container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            true));

          final SNonEmptyList<SAPart> parts = dwp.getParts();
          container.appendChild(SXHTML.documentTitle(dwp));

          doc.getContents().map(new FunctionType<SDocumentContents, Unit>() {
            @Override public Unit call(
              final SDocumentContents x)
            {
              container.appendChild(doc_contents
                .getTableOfContentsParts(parts));
              return Unit.unit();
            }
          });

          container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            false));
          callbacks.onBodyEnd(container);

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
          final SADocumentWithSections dws)
          throws Exception
        {
          final SXHTMLPage page =
            SXHTML.newPage(doc.getTitle().getActual(), doc.getStyle());

          final SNonEmptyList<SASection> sections = dws.getSections();

          callbacks.onHead(page.getHead());

          final Element container = page.getBodyContainer();
          final Element rbody = callbacks.onBodyStart(container);
          SXHTMLReparent.reparentBodyNode(container, rbody);

          container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            true));

          container.appendChild(SXHTML.documentTitle(dws));

          doc.getContents().map(new FunctionType<SDocumentContents, Unit>() {
            @Override public Unit call(
              final SDocumentContents x)
            {
              container.appendChild(doc_contents
                .getTableOfContentsSections(sections));
              return Unit.unit();
            }
          });

          container.appendChild(SDocumentXHTMLWriterMulti.navigationBar(
            link_provider,
            doc,
            SDocumentXHTMLWriterMulti.NO_NUMBER,
            false));
          callbacks.onBodyEnd(container);

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
