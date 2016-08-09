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

import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SDocumentVisitor;
import com.io7m.jstructural.core.SDocumentWithParts;
import com.io7m.jstructural.core.SDocumentWithSections;
import com.io7m.jstructural.core.SFootnote;
import com.io7m.jstructural.core.SFootnoteContent;
import com.io7m.jstructural.core.SFootnoteContentVisitor;
import com.io7m.jstructural.core.SFormalItem;
import com.io7m.jstructural.core.SFormalItemContentVisitor;
import com.io7m.jstructural.core.SFormalItemList;
import com.io7m.jstructural.core.SID;
import com.io7m.jstructural.core.SImage;
import com.io7m.jstructural.core.SLink;
import com.io7m.jstructural.core.SLinkContent;
import com.io7m.jstructural.core.SLinkContentVisitor;
import com.io7m.jstructural.core.SLinkExternal;
import com.io7m.jstructural.core.SListItem;
import com.io7m.jstructural.core.SListItemContent;
import com.io7m.jstructural.core.SListItemContentVisitor;
import com.io7m.jstructural.core.SListOrdered;
import com.io7m.jstructural.core.SListUnordered;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SParagraph;
import com.io7m.jstructural.core.SParagraphContent;
import com.io7m.jstructural.core.SParagraphContentVisitor;
import com.io7m.jstructural.core.SPart;
import com.io7m.jstructural.core.SSection;
import com.io7m.jstructural.core.SSectionVisitor;
import com.io7m.jstructural.core.SSectionWithParagraphs;
import com.io7m.jstructural.core.SSectionWithSubsections;
import com.io7m.jstructural.core.SSubsection;
import com.io7m.jstructural.core.SSubsectionContent;
import com.io7m.jstructural.core.SSubsectionContentVisitor;
import com.io7m.jstructural.core.STable;
import com.io7m.jstructural.core.STableBody;
import com.io7m.jstructural.core.STableCell;
import com.io7m.jstructural.core.STableCellContent;
import com.io7m.jstructural.core.STableCellContentVisitor;
import com.io7m.jstructural.core.STableColumnName;
import com.io7m.jstructural.core.STableHead;
import com.io7m.jstructural.core.STableRow;
import com.io7m.jstructural.core.STableSummary;
import com.io7m.jstructural.core.STerm;
import com.io7m.jstructural.core.SText;
import com.io7m.jstructural.core.SVerbatim;
import com.io7m.junreachable.UnreachableCodeException;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A document annotator.
 */

@Immutable
public final class SAnnotator
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SAnnotator.class);
  }

  private final SDocument document;

  private SAnnotator(
    final SDocument d)
  {
    this.document = NullCheck.notNull(d, "Document");
  }

  /**
   * Annotate the given document.
   *
   * @param d The document
   *
   * @return An annotated document
   */

  public static SADocument document(
    final SDocument d)
  {
    return new SAnnotator(d).process();
  }

  private static SAFootnote transformFootnote(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASubsectionContentNumber number,
    final SFootnote footnote)
    throws Exception
  {
    final List<SAFootnoteContent> content_r =
      new ArrayList<SAFootnoteContent>();

    for (final SFootnoteContent c : footnote.getContent().getElements()) {
      final SAFootnoteContent rc = c.footnoteContentAccept(
        new FootnoteContentAnnotator(
          ids, footnotes, number));
      content_r.add(rc);
    }

    final SNonEmptyList<SAFootnoteContent> in_content =
      SNonEmptyList.newList(content_r);
    final SAFootnote f = new SAFootnote(footnotes.size(), in_content);
    footnotes.add(f);
    return f;
  }

  private static SAFormalItemContent transformFormalItemContent(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SFormalItem formal,
    final SAFormalItemNumber f_number)
    throws Exception
  {
    return formal.getContent().formalItemContentAccept(
      new FormalItemContentAnnotator(ids, footnotes, f_number));
  }

  private static SAFormalItemList transformFormalItemList(
    final SFormalItemList list)
  {
    return new SAFormalItemList(list.getKind());
  }

  private static SAImage transformImage(
    final SImage image)
  {
    return new SAImage(
      image.getURI(),
      image.getType(),
      image.getWidth(),
      image.getHeight(),
      image.getText());
  }

  private static SALink transformLink(
    final SLink link)
    throws Exception
  {
    final List<SALinkContent> content_r = new ArrayList<SALinkContent>();
    for (final SLinkContent c : link.getContent().getElements()) {
      final SALinkContent rc = c.linkContentAccept(new LinkContentAnnotator());
      content_r.add(rc);
    }

    final SNonEmptyList<SALinkContent> in_content =
      SNonEmptyList.newList(content_r);
    return new SALink(link.getTarget(), in_content);
  }

  private static SALinkExternal transformLinkExternal(
    final SLinkExternal link)
    throws Exception
  {
    final List<SALinkContent> content_r = new ArrayList<SALinkContent>();
    for (final SLinkContent c : link.getContent().getElements()) {
      final SALinkContent rc = c.linkContentAccept(new LinkContentAnnotator());
      content_r.add(rc);
    }

    final SNonEmptyList<SALinkContent> in_content =
      SNonEmptyList.newList(content_r);
    return new SALinkExternal(link.getTarget(), in_content);
  }

  private static SAListOrdered transformListOrdered(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASubsectionContentNumber number,
    final SListOrdered list)
    throws Exception
  {
    final ListItemContentAnnotator lic =
      new ListItemContentAnnotator(ids, footnotes, number);

    final List<SAListItem> items_r = new ArrayList<SAListItem>();
    for (final SListItem i : list.getItems().getElements()) {

      final List<SAListItemContent> ai_content_r =
        new ArrayList<SAListItemContent>();
      for (final SListItemContent ic : i.getContent().getElements()) {
        final SAListItemContent aic = ic.listItemContentAccept(lic);
        ai_content_r.add(aic);
      }

      final SNonEmptyList<SAListItemContent> ai_content =
        SNonEmptyList.newList(ai_content_r);
      final SAListItem ai = new SAListItem(i.getType(), ai_content);
      items_r.add(ai);
    }

    final OptionType<String> in_type = list.getType();
    final SNonEmptyList<SAListItem> in_items = SNonEmptyList.newList(items_r);

    return new SAListOrdered(in_type, in_items);
  }

  private static SAListUnordered transformListUnordered(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASubsectionContentNumber number,
    final SListUnordered list)
    throws Exception
  {
    final ListItemContentAnnotator lic =
      new ListItemContentAnnotator(ids, footnotes, number);

    final List<SAListItem> items_r = new ArrayList<SAListItem>();
    for (final SListItem i : list.getItems().getElements()) {

      final List<SAListItemContent> ai_content_r =
        new ArrayList<SAListItemContent>();
      for (final SListItemContent ic : i.getContent().getElements()) {
        final SAListItemContent aic = ic.listItemContentAccept(lic);
        ai_content_r.add(aic);
      }

      final SNonEmptyList<SAListItemContent> ai_content =
        SNonEmptyList.newList(ai_content_r);
      final SAListItem ai = new SAListItem(i.getType(), ai_content);
      items_r.add(ai);
    }

    final OptionType<String> in_type = list.getType();
    final SNonEmptyList<SAListItem> in_items = SNonEmptyList.newList(items_r);

    return new SAListUnordered(in_type, in_items);
  }

  private static SNonEmptyList<SAParagraphContent> transformParagraphContent(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SParagraph paragraph,
    final SAParagraphNumber p_number)
    throws Exception
  {
    final List<SAParagraphContent> content_r =
      new ArrayList<SAParagraphContent>();
    final ParagraphContentAnnotator annot =
      new ParagraphContentAnnotator(ids, footnotes, p_number);
    for (final SParagraphContent c : paragraph.getContent().getElements()) {
      final SAParagraphContent ca = c.paragraphContentAccept(annot);
      content_r.add(ca);
    }

    return SNonEmptyList.newList(content_r);
  }

  private static SAFormalItem transformSectionFormalItem(
    final SAIDMap ids,
    final SAFormalItemsByKind formals,
    final List<SAFootnote> footnotes,
    final SASectionNumber number,
    final SFormalItem formal,
    final int formal_number)
    throws Exception
  {
    final OptionType<SAID> id = formal.getID().map(new SAIDMapper());

    final SAFormalItem saf = number.sectionNumberAccept(
      new SASectionNumberVisitor<SAFormalItem>()
      {
        @Override
        public SAFormalItem visitSectionNumberWithoutPart(
          final SASectionNumberS sn)
          throws Exception
        {
          final SAFormalItemNumber f_number =
            new SAFormalItemNumberSF(sn.getSection(), formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids, footnotes, formal, f_number);

          return new SAFormalItem(
            f_number,
            title,
            formal.getKind(),
            formal.getType(),
            in_content,
            formal_number,
            id);
        }

        @Override
        public SAFormalItem visitSectionNumberWithPart(
          final SASectionNumberPS sn)
          throws Exception
        {
          final SAFormalItemNumber f_number = new SAFormalItemNumberPSF(
            sn.getPart(), sn.getSection(), formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids, footnotes, formal, f_number);

          return new SAFormalItem(
            f_number,
            title,
            formal.getKind(),
            formal.getType(),
            in_content,
            formal_number,
            id);
        }
      });

    id.map(new SAIDLinkCreator(ids, saf));
    formals.put(saf.getKind(), saf);
    return saf;
  }

  private static SAParagraph transformSectionParagraph(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASectionNumber number,
    final SParagraph paragraph,
    final int paragraph_number)
    throws Exception
  {
    final OptionType<SAID> id = paragraph.getID().map(new SAIDMapper());

    final SAParagraph sap = number.sectionNumberAccept(
      new SASectionNumberVisitor<SAParagraph>()
      {
        @Override
        public SAParagraph visitSectionNumberWithoutPart(
          final SASectionNumberS sn)
          throws Exception
        {
          final SAParagraphNumber p_number =
            new SAParagraphNumberSP(sn.getSection(), paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids, footnotes, paragraph, p_number);

          return new SAParagraph(
            p_number, paragraph.getType(), in_content, id);
        }

        @Override
        public SAParagraph visitSectionNumberWithPart(
          final SASectionNumberPS sn)
          throws Exception
        {
          final SAParagraphNumber p_number = new SAParagraphNumberPSP(
            sn.getPart(), sn.getSection(), paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids, footnotes, paragraph, p_number);

          return new SAParagraph(
            p_number, paragraph.getType(), in_content, id);
        }
      });

    id.map(new SAIDLinkCreator(ids, sap));
    return sap;
  }

  private static SASection transformSectionWithParagraphs(
    final SAIDMap ids,
    final SAFormalItemsByKind formals,
    final List<SAFootnote> footnotes,
    final SSectionWithParagraphs s,
    final SASectionNumber number)
    throws Exception
  {
    final SASectionTitle title =
      new SASectionTitle(number, s.getTitle().getActual());

    final List<SASubsectionContent> content_r =
      new ArrayList<SASubsectionContent>();
    final AtomicInteger paragraph_no = new AtomicInteger(1);
    final AtomicInteger formal_no = new AtomicInteger(1);
    final int footnotes_before = footnotes.size();

    for (final SSubsectionContent p : s.getSectionContent().getElements()) {
      final SASubsectionContent r = p.subsectionContentAccept(
        new SSubsectionContentVisitor<SASubsectionContent>()
        {
          @Override
          public SASubsectionContent visitFormalItem(
            final SFormalItem formal)
            throws Exception
          {
            return SAnnotator.transformSectionFormalItem(
              ids,
              formals,
              footnotes,
              number,
              formal,
              formal_no.getAndIncrement());
          }

          @Override
          public SASubsectionContent visitParagraph(
            final SParagraph paragraph)
            throws Exception
          {
            return SAnnotator.transformSectionParagraph(
              ids,
              footnotes,
              number,
              paragraph,
              paragraph_no.getAndIncrement());
          }
        });

      content_r.add(r);
    }

    final OptionType<SAID> id = s.getID().map(new SAIDMapper());

    final List<SAFootnote> footnotes_here = new ArrayList<SAFootnote>();
    final int footnotes_now = footnotes.size();
    if ((footnotes_now - footnotes_before) > 0) {
      for (int index = footnotes_before; index < footnotes_now; ++index) {
        footnotes_here.add(footnotes.get(index));
      }
    }

    final SNonEmptyList<SASubsectionContent> content =
      SNonEmptyList.newList(content_r);
    return new SASectionWithParagraphs(
      number, s.getType(), id, title, s.getContents(), content, footnotes_here);
  }

  private static SASection transformSectionWithSubsections(
    final SAIDMap ids,
    final SAFormalItemsByKind formals,
    final List<SAFootnote> footnotes,
    final SASectionNumber number,
    final SSectionWithSubsections s)
    throws Exception
  {
    final SASectionTitle title =
      new SASectionTitle(number, s.getTitle().getActual());

    final int footnotes_before = footnotes.size();
    final List<SASubsection> subsections_r = new ArrayList<SASubsection>();
    for (final SSubsection ss : s.getSubsections().getElements()) {
      assert ss != null;
      final SASubsection sa = SAnnotator.transformSubsection(
        ids, formals, footnotes, number, ss, subsections_r.size() + 1);
      sa.getID().map(new SAIDLinkCreator(ids, sa));
      subsections_r.add(sa);
    }
    final SNonEmptyList<SASubsection> subsections =
      SNonEmptyList.newList(subsections_r);

    final List<SAFootnote> footnotes_here = new ArrayList<SAFootnote>();
    final int footnotes_now = footnotes.size();
    if ((footnotes_now - footnotes_before) > 0) {
      for (int index = footnotes_before; index < footnotes_now; ++index) {
        footnotes_here.add(footnotes.get(index));
      }
    }

    final OptionType<SAID> id = s.getID().map(new SAIDMapper());
    return new SASectionWithSubsections(
      number,
      s.getType(),
      id,
      title,
      s.getContents(),
      subsections,
      footnotes_here);
  }

  private static SASubsection transformSubsection(
    final SAIDMap ids,
    final SAFormalItemsByKind formals,
    final List<SAFootnote> footnotes,
    final SASectionNumber number,
    final SSubsection subsection,
    final int subsection_number)
    throws Exception
  {
    final SASubsectionNumber s_number =
      SAnnotator.transformSubsectionNumber(number, subsection_number);

    final SASubsectionTitle title =
      new SASubsectionTitle(s_number, subsection.getTitle().getActual());

    final List<SASubsectionContent> results_r =
      new ArrayList<SASubsectionContent>();

    final AtomicInteger paragraph_no = new AtomicInteger(1);
    final AtomicInteger formal_no = new AtomicInteger(1);

    for (final SSubsectionContent c : subsection.getContent().getElements()) {
      final SASubsectionContent ca = c.subsectionContentAccept(
        new SSubsectionContentVisitor<SASubsectionContent>()
        {
          @Override
          public SASubsectionContent visitFormalItem(
            final SFormalItem formal)
            throws Exception
          {
            return SAnnotator.transformSubsectionFormalItem(
              ids,
              formals,
              footnotes,
              s_number,
              formal,
              formal_no.getAndIncrement());
          }

          @Override
          public SASubsectionContent visitParagraph(
            final SParagraph paragraph)
            throws Exception
          {
            return SAnnotator.transformSubsectionParagraph(
              ids,
              footnotes,
              s_number,
              paragraph,
              paragraph_no.getAndIncrement());
          }
        });
      results_r.add(ca);
    }

    final SNonEmptyList<SASubsectionContent> in_content =
      SNonEmptyList.newList(results_r);

    final OptionType<SAID> id = subsection.getID().map(new SAIDMapper());
    return new SASubsection(
      s_number, subsection.getType(), id, title, in_content);
  }

  private static SAFormalItem transformSubsectionFormalItem(
    final SAIDMap ids,
    final SAFormalItemsByKind formals,
    final List<SAFootnote> footnotes,
    final SASubsectionNumber s_number,
    final SFormalItem formal,
    final int formal_number)
    throws Exception
  {
    final OptionType<SAID> id = formal.getID().map(new SAIDMapper());

    final SAFormalItem saf = s_number.subsectionNumberAccept(
      new SASubsectionNumberVisitor<SAFormalItem>()
      {
        @Override
        public SAFormalItem visitSubsectionNumberPSS(
          final SASubsectionNumberPSS p)
          throws Exception
        {
          final SAFormalItemNumber f_number = new SAFormalItemNumberPSSF(
            p.getPart(), p.getSection(), p.getSubsection(), formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids, footnotes, formal, f_number);

          return new SAFormalItem(
            f_number,
            title,
            formal.getKind(),
            formal.getType(),
            in_content,
            formal_number,
            id);
        }

        @Override
        public SAFormalItem visitSubsectionNumberSS(
          final SASubsectionNumberSS p)
          throws Exception
        {
          final SAFormalItemNumber f_number = new SAFormalItemNumberSSF(
            p.getSection(), p.getSubsection(), formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids, footnotes, formal, f_number);

          return new SAFormalItem(
            f_number,
            title,
            formal.getKind(),
            formal.getType(),
            in_content,
            formal_number,
            id);
        }
      });

    id.map(new SAIDLinkCreator(ids, saf));
    formals.put(saf.getKind(), saf);
    return saf;
  }

  private static SASubsectionNumber transformSubsectionNumber(
    final SASectionNumber number,
    final int subsection_number)
    throws Exception
  {
    return number.sectionNumberAccept(
      new SASectionNumberVisitor<SASubsectionNumber>()
      {
        @Override
        public SASubsectionNumber visitSectionNumberWithoutPart(
          final SASectionNumberS p)
          throws Exception
        {
          return new SASubsectionNumberSS(p.getSection(), subsection_number);
        }

        @Override
        public SASubsectionNumber visitSectionNumberWithPart(
          final SASectionNumberPS p)
          throws Exception
        {
          return new SASubsectionNumberPSS(
            p.getPart(), p.getSection(), subsection_number);
        }
      });
  }

  private static SAParagraph transformSubsectionParagraph(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASubsectionNumber number,
    final SParagraph paragraph,
    final int paragraph_number)
    throws Exception
  {
    final OptionType<SAID> id = paragraph.getID().map(new SAIDMapper());

    final SAParagraph sap = number.subsectionNumberAccept(
      new SASubsectionNumberVisitor<SAParagraph>()
      {
        @Override
        public SAParagraph visitSubsectionNumberPSS(
          final SASubsectionNumberPSS ssn)
          throws Exception
        {
          final SAParagraphNumber p_number = new SAParagraphNumberPSSP(
            ssn.getPart(),
            ssn.getSection(),
            ssn.getSubsection(),
            paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids, footnotes, paragraph, p_number);

          return new SAParagraph(
            p_number, paragraph.getType(), in_content, id);
        }

        @Override
        public SAParagraph visitSubsectionNumberSS(
          final SASubsectionNumberSS ssn)
          throws Exception
        {
          final SAParagraphNumber p_number = new SAParagraphNumberSSP(
            ssn.getSection(), ssn.getSubsection(), paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids, footnotes, paragraph, p_number);

          return new SAParagraph(
            p_number, paragraph.getType(), in_content, id);
        }
      });

    id.map(new SAIDLinkCreator(ids, sap));
    return sap;
  }

  private static SATable transformTable(
    final SAIDMap ids,
    final List<SAFootnote> footnotes,
    final SASubsectionContentNumber number,
    final STable t)
    throws Exception
  {
    final SATableSummary in_summary =
      SAnnotator.transformTableSummary(t.getSummary());

    final OptionType<SATableHead> in_header = t.getHeader().mapPartial(
      new PartialFunctionType<STableHead, SATableHead, Exception>()
      {
        @Override
        public SATableHead call(
          final STableHead x)
          throws Exception
        {
          return SAnnotator.transformTableHead(x);
        }
      });

    final SATableBody in_body =
      SAnnotator.transformTableBody(ids, footnotes, number, t.getBody());
    return new SATable(in_summary, in_header, in_body);
  }

  private static SATableBody transformTableBody(
    final SAIDMap in_ids,
    final List<SAFootnote> in_footnotes,
    final SASubsectionContentNumber in_number,
    final STableBody body)
    throws Exception
  {
    final List<SATableRow> rows_r = new ArrayList<SATableRow>();
    for (final STableRow r : body.getRows().getElements()) {
      assert r != null;
      rows_r.add(
        SAnnotator.transformTableRow(
          in_ids, in_footnotes, in_number, r));
    }

    final SNonEmptyList<SATableRow> rows = SNonEmptyList.newList(rows_r);
    return new SATableBody(rows);
  }

  private static SATableCell transformTableCell(
    final SAIDMap in_ids,
    final List<SAFootnote> in_footnotes,
    final SASubsectionContentNumber in_number,
    final STableCell c)
    throws Exception
  {
    final List<SATableCellContent> content_r =
      new ArrayList<SATableCellContent>();
    for (final STableCellContent cc : c.getContent()) {
      assert cc != null;
      content_r.add(
        SAnnotator.transformTableCellContent(
          in_ids, in_footnotes, in_number, cc));
    }

    return new SATableCell(content_r);
  }

  private static SATableCellContent transformTableCellContent(
    final SAIDMap in_ids,
    final List<SAFootnote> in_footnotes,
    final SASubsectionContentNumber in_number,
    final STableCellContent cc)
    throws Exception
  {
    return cc.tableCellContentAccept(
      new TableCellContentAnnotator(
        in_ids, in_footnotes, in_number));
  }

  private static SATableColumnName transformTableColumnName(
    final STableColumnName n)
  {
    return new SATableColumnName(n.getText());
  }

  private static SATableHead transformTableHead(
    final STableHead x)
  {
    final List<SATableColumnName> names_r = new ArrayList<SATableColumnName>();
    for (final STableColumnName n : x.getHeader().getElements()) {
      assert n != null;
      names_r.add(SAnnotator.transformTableColumnName(n));
    }

    final SNonEmptyList<SATableColumnName> names =
      SNonEmptyList.newList(names_r);
    return new SATableHead(names);
  }

  private static SATableRow transformTableRow(
    final SAIDMap in_ids,
    final List<SAFootnote> in_footnotes,
    final SASubsectionContentNumber in_number,
    final STableRow r)
    throws Exception
  {
    final List<SATableCell> cells_r = new ArrayList<SATableCell>();
    for (final STableCell c : r.getColumns().getElements()) {
      assert c != null;
      cells_r.add(
        SAnnotator.transformTableCell(
          in_ids, in_footnotes, in_number, c));
    }

    final SNonEmptyList<SATableCell> cells = SNonEmptyList.newList(cells_r);
    return new SATableRow(cells);
  }

  private static SATableSummary transformTableSummary(
    final STableSummary summary)
  {
    return new SATableSummary(summary.getText());
  }

  private static SATerm transformTerm(
    final STerm term)
  {
    return new SATerm(new SAText(term.getText().getText()), term.getType());
  }

  private static SAText transformText(
    final SText text)
  {
    return new SAText(text.getText());
  }

  private static SAVerbatim transformVerbatim(
    final SVerbatim text)
  {
    return new SAVerbatim(text.getText(), text.getType());
  }

  private SADocument process()
  {
    try {
      return this.document.documentAccept(new DocumentAnnotator());
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static final class DocumentAnnotator
    implements SDocumentVisitor<SADocument>
  {
    private final List<SAFootnote> footnotes;
    private final SAFormalItemsByKind formals;
    private final SAIDMap ids;

    private DocumentAnnotator()
    {
      this.ids = new SAIDMap();
      this.footnotes = new ArrayList<SAFootnote>();
      this.formals = new SAFormalItemsByKind();
    }

    public SAPart part(
      final SPart p,
      final SAPartNumber part_no)
      throws Exception
    {
      final OptionType<SAID> id = p.getID().map(new SAIDMapper());
      final SAPartTitle title =
        new SAPartTitle(part_no, p.getTitle().getActual());

      final List<SASection> sections_r = new ArrayList<SASection>();
      for (final SSection s : p.getSections().getElements()) {
        final SASectionNumberPS number =
          new SASectionNumberPS(part_no.getActual(), sections_r.size() + 1);

        final SASection sp = s.sectionAccept(
          new PartSectionAnnotator(
            this.ids, this.formals, this.footnotes, number));

        sp.getID().map(new SAIDLinkCreator(this.ids, sp));
        sections_r.add(sp);
      }

      final SNonEmptyList<SASection> sections =
        SNonEmptyList.newList(sections_r);
      return new SAPart(
        part_no, p.getType(), id, title, p.getContents(), sections);
    }

    @Override
    public SADocument visitDocumentWithParts(
      final SDocumentWithParts dp)
      throws Exception
    {
      final List<SAPart> parts_r = new ArrayList<SAPart>();
      for (final SPart p : dp.getParts().getElements()) {
        assert p != null;
        final SAPart q = this.part(p, new SAPartNumber(parts_r.size() + 1));
        q.getID().map(new SAIDLinkCreator(this.ids, q));
        parts_r.add(q);
      }
      final SNonEmptyList<SAPart> parts = SNonEmptyList.newList(parts_r);

      final SADocumentTitle title =
        SADocumentTitle.documentTitle(dp.getTitle().getActual());

      return new SADocumentWithParts(
        this.ids,
        title,
        dp.getContents(),
        dp.getStyle(),
        parts,
        this.footnotes,
        this.formals);
    }

    @Override
    public SADocument visitDocumentWithSections(
      final SDocumentWithSections ds)
      throws Exception
    {
      final List<SASection> sections_r = new ArrayList<SASection>();
      for (final SSection s : ds.getSections().getElements()) {
        final SASection ss = s.sectionAccept(
          new NoPartSectionAnnotator(
            this.ids, this.formals, this.footnotes, sections_r.size() + 1));

        ss.getID().map(new SAIDLinkCreator(this.ids, ss));
        sections_r.add(ss);
      }
      final SNonEmptyList<SASection> sections =
        SNonEmptyList.newList(sections_r);

      final SADocumentTitle title =
        SADocumentTitle.documentTitle(ds.getTitle().getActual());

      return new SADocumentWithSections(
        this.ids,
        title,
        ds.getContents(),
        ds.getStyle(),
        sections,
        this.footnotes,
        this.formals);
    }
  }

  private static final class FootnoteContentAnnotator
    implements SFootnoteContentVisitor<SAFootnoteContent>
  {
    private final List<SAFootnote> footnotes;
    private final SAIDMap ids;
    private final SASubsectionContentNumber number;

    private FootnoteContentAnnotator(
      final SAIDMap in_ids,
      final List<SAFootnote> in_footnotes,
      final SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SAFootnoteContent visitFootnote(
      final SFootnote footnote)
      throws Exception
    {
      return SAnnotator.transformFootnote(
        this.ids, this.footnotes, this.number, footnote);
    }

    @Override
    public SAFootnoteContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SAFootnoteContent visitLink(
      final SLink link)
      throws Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override
    public SAFootnoteContent visitLinkExternal(
      final SLinkExternal link)
      throws Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override
    public SAFootnoteContent visitListOrdered(
      final SListOrdered list)
      throws Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAFootnoteContent visitListUnordered(
      final SListUnordered list)
      throws Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAFootnoteContent visitTerm(
      final STerm term)
      throws Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override
    public SAFootnoteContent visitText(
      final SText text)
      throws Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override
    public SAFootnoteContent visitVerbatim(
      final SVerbatim text)
      throws Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class FormalItemContentAnnotator
    implements SFormalItemContentVisitor<SAFormalItemContent>
  {
    private final List<SAFootnote> footnotes;
    private final SAIDMap ids;
    private final SAFormalItemNumber number;

    private FormalItemContentAnnotator(
      final SAIDMap in_ids,
      final List<SAFootnote> in_footnotes,
      final SAFormalItemNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SAFormalItemContent visitFormalItemList(
      final SFormalItemList list)
      throws Exception
    {
      return SAnnotator.transformFormalItemList(list);
    }

    @Override
    public SAFormalItemContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SAFormalItemContent visitListOrdered(
      final SListOrdered list)
      throws Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAFormalItemContent visitListUnordered(
      final SListUnordered list)
      throws Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAFormalItemContent visitTable(
      final STable t)
      throws Exception
    {
      return SAnnotator.transformTable(
        this.ids, this.footnotes, this.number, t);
    }

    @Override
    public SAFormalItemContent visitVerbatim(
      final SVerbatim text)
      throws Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class LinkContentAnnotator
    implements SLinkContentVisitor<SALinkContent>
  {
    private LinkContentAnnotator()
    {

    }

    @Override
    public SALinkContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SALinkContent visitText(
      final SText text)
      throws Exception
    {
      return SAnnotator.transformText(text);
    }
  }

  private static final class ListItemContentAnnotator
    implements SListItemContentVisitor<SAListItemContent>
  {
    private final List<SAFootnote> footnotes;
    private final SAIDMap ids;
    private final SASubsectionContentNumber number;

    private ListItemContentAnnotator(
      final SAIDMap in_ids,
      final List<SAFootnote> in_footnotes,
      final SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SAListItemContent visitFootnote(
      final SFootnote footnote)
      throws Exception
    {
      return SAnnotator.transformFootnote(
        this.ids, this.footnotes, this.number, footnote);
    }

    @Override
    public SAListItemContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SAListItemContent visitLink(
      final SLink link)
      throws Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override
    public SAListItemContent visitLinkExternal(
      final SLinkExternal link)
      throws Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override
    public SAListItemContent visitListOrdered(
      final SListOrdered list)
      throws Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAListItemContent visitListUnordered(
      final SListUnordered list)
      throws Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAListItemContent visitTerm(
      final STerm term)
      throws Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override
    public SAListItemContent visitText(
      final SText text)
      throws Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override
    public SAListItemContent visitVerbatim(
      final SVerbatim text)
      throws Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class NoPartSectionAnnotator
    implements SSectionVisitor<SASection>
  {
    private final List<SAFootnote> footnotes;
    private final SAFormalItemsByKind formals;
    private final SAIDMap ids;
    private final SASectionNumberS number;

    private NoPartSectionAnnotator(
      final SAIDMap in_ids,
      final SAFormalItemsByKind in_formals,
      final List<SAFootnote> in_footnotes,
      final int section_no)
    {
      this.ids = in_ids;
      this.formals = in_formals;
      this.number = new SASectionNumberS(section_no);
      this.footnotes = in_footnotes;
    }

    @Override
    public SASection visitSectionWithParagraphs(
      final SSectionWithParagraphs s)
      throws Exception
    {
      return SAnnotator.transformSectionWithParagraphs(
        this.ids, this.formals, this.footnotes, s, this.number);
    }

    @Override
    public SASection visitSectionWithSubsections(
      final SSectionWithSubsections s)
      throws Exception
    {
      return SAnnotator.transformSectionWithSubsections(
        this.ids, this.formals, this.footnotes, this.number, s);
    }
  }

  private static final class ParagraphContentAnnotator
    implements SParagraphContentVisitor<SAParagraphContent>
  {
    private final List<SAFootnote> footnotes;
    private final SAIDMap ids;
    private final SAParagraphNumber number;

    private ParagraphContentAnnotator(
      final SAIDMap in_ids,
      final List<SAFootnote> in_footnotes,
      final SAParagraphNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SAParagraphContent visitFootnote(
      final SFootnote footnote)
      throws Exception
    {
      return SAnnotator.transformFootnote(
        this.ids, this.footnotes, this.number, footnote);
    }

    @Override
    public SAParagraphContent visitFormalItemList(
      final SFormalItemList list)
      throws Exception
    {
      return SAnnotator.transformFormalItemList(list);
    }

    @Override
    public SAParagraphContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SAParagraphContent visitLink(
      final SLink link)
      throws Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override
    public SAParagraphContent visitLinkExternal(
      final SLinkExternal link)
      throws Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override
    public SAParagraphContent visitListOrdered(
      final SListOrdered list)
      throws Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAParagraphContent visitListUnordered(
      final SListUnordered list)
      throws Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SAParagraphContent visitTable(
      final STable table)
      throws Exception
    {
      return SAnnotator.transformTable(
        this.ids, this.footnotes, this.number, table);
    }

    @Override
    public SAParagraphContent visitTerm(
      final STerm term)
      throws Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override
    public SAParagraphContent visitText(
      final SText text)
      throws Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override
    public SAParagraphContent visitVerbatim(
      final SVerbatim text)
      throws Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class PartSectionAnnotator
    implements SSectionVisitor<SASection>
  {
    private final List<SAFootnote> footnotes;
    private final SAFormalItemsByKind formals;
    private final SAIDMap ids;
    private final SASectionNumberPS number;

    private PartSectionAnnotator(
      final SAIDMap in_ids,
      final SAFormalItemsByKind in_formals,
      final List<SAFootnote> in_footnotes,
      final SASectionNumberPS in_number)
    {
      this.ids = in_ids;
      this.formals = in_formals;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SASection visitSectionWithParagraphs(
      final SSectionWithParagraphs s)
      throws Exception
    {
      return SAnnotator.transformSectionWithParagraphs(
        this.ids, this.formals, this.footnotes, s, this.number);
    }

    @Override
    public SASection visitSectionWithSubsections(
      final SSectionWithSubsections s)
      throws Exception
    {
      return SAnnotator.transformSectionWithSubsections(
        this.ids, this.formals, this.footnotes, this.number, s);
    }
  }

  private static final class SAIDLinkCreator implements FunctionType<SAID, Unit>
  {
    private final SAIDTargetContent content;
    private final SAIDMap ids;

    private SAIDLinkCreator(
      final SAIDMap map,
      final SAIDTargetContent c)
    {
      this.ids = map;
      this.content = c;
    }

    @Override
    public Unit call(
      final SAID x)
    {
      this.ids.put(x, this.content);
      return Unit.unit();
    }
  }

  private static final class SAIDMapper implements FunctionType<SID, SAID>
  {
    private SAIDMapper()
    {
      // Nothing
    }

    @Override
    public SAID call(
      final SID x)
    {
      return new SAID(x.getActual());
    }
  }

  private static final class TableCellContentAnnotator
    implements STableCellContentVisitor<SATableCellContent>
  {
    private final List<SAFootnote> footnotes;
    private final SAIDMap ids;
    private final SASubsectionContentNumber number;

    private TableCellContentAnnotator(
      final SAIDMap in_ids,
      final List<SAFootnote> in_footnotes,
      final SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override
    public SATableCellContent visitFootnote(
      final SFootnote footnote)
      throws Exception
    {
      return SAnnotator.transformFootnote(
        this.ids, this.footnotes, this.number, footnote);
    }

    @Override
    public SATableCellContent visitImage(
      final SImage image)
      throws Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override
    public SATableCellContent visitLink(
      final SLink link)
      throws Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override
    public SATableCellContent visitLinkExternal(
      final SLinkExternal link)
      throws Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override
    public SATableCellContent visitListOrdered(
      final SListOrdered list)
      throws Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SATableCellContent visitListUnordered(
      final SListUnordered list)
      throws Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids, this.footnotes, this.number, list);
    }

    @Override
    public SATableCellContent visitTerm(
      final STerm term)
      throws Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override
    public SATableCellContent visitText(
      final SText text)
      throws Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override
    public SATableCellContent visitVerbatim(
      final SVerbatim text)
      throws Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }
}
