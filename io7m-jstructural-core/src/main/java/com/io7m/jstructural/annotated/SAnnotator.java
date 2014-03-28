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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jlog.Log;
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

/**
 * A document annotator.
 */

@SuppressWarnings("synthetic-access") public final class SAnnotator
{
  private static final class DocumentAnnotator implements
    SDocumentVisitor<SADocument>
  {
    private final @Nonnull List<SAFootnote>    footnotes;
    private final @Nonnull SAIDMap             ids;
    private final @Nonnull SAFormalItemsByKind formals;

    public DocumentAnnotator(
      final @Nonnull Log log)
    {
      this.ids = new SAIDMap(log);
      this.footnotes = new ArrayList<SAFootnote>();
      this.formals = new SAFormalItemsByKind(log);
    }

    public @Nonnull SAPart part(
      final @Nonnull SPart p,
      final int part_no)
      throws ConstraintError,
        Exception
    {
      final Option<SAID> id = p.getID().mapPartial(new SAIDMapper());
      final SAPartTitle title =
        new SAPartTitle(part_no, p.getTitle().getActual());

      final List<SASection> sections_r = new ArrayList<SASection>();
      for (final SSection s : p.getSections().getElements()) {
        final SASectionNumberPS number =
          new SASectionNumberPS(part_no, sections_r.size() + 1);

        final SASection sp =
          s.sectionAccept(new PartSectionAnnotator(
            this.ids,
            this.formals,
            this.footnotes,
            number));

        sp.getID().mapPartial(new SAIDLinkCreator(this.ids, sp));
        sections_r.add(sp);
      }

      final SNonEmptyList<SASection> sections =
        SNonEmptyList.newList(sections_r);
      return new SAPart(
        part_no,
        p.getType(),
        id,
        title,
        p.getContents(),
        sections);
    }

    @Override public SADocument visitDocumentWithParts(
      final @Nonnull SDocumentWithParts dp)
      throws ConstraintError,
        Exception
    {
      final List<SAPart> parts_r = new ArrayList<SAPart>();
      for (final SPart p : dp.getParts().getElements()) {
        final SAPart q = this.part(p, parts_r.size() + 1);
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

    @Override public SADocument visitDocumentWithSections(
      final @Nonnull SDocumentWithSections ds)
      throws ConstraintError,
        Exception
    {
      final List<SASection> sections_r = new ArrayList<SASection>();
      for (final SSection s : ds.getSections().getElements()) {
        final SASection ss =
          s.sectionAccept(new NoPartSectionAnnotator(
            this.ids,
            this.formals,
            this.footnotes,
            sections_r.size() + 1));

        ss.getID().mapPartial(new SAIDLinkCreator(this.ids, ss));
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

  private static final class FootnoteContentAnnotator implements
    SFootnoteContentVisitor<SAFootnoteContent>
  {
    private final @Nonnull List<SAFootnote>          footnotes;
    private final @Nonnull SAIDMap                   ids;
    private final @Nonnull SASubsectionContentNumber number;

    public FootnoteContentAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SAFootnoteContent visitFootnote(
      final SFootnote footnote)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFootnote(
        this.ids,
        this.footnotes,
        this.number,
        footnote);
    }

    @Override public SAFootnoteContent visitImage(
      final SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SAFootnoteContent visitLink(
      final SLink link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override public SAFootnoteContent visitLinkExternal(
      final SLinkExternal link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override public SAFootnoteContent visitListOrdered(
      final SListOrdered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAFootnoteContent visitListUnordered(
      final SListUnordered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAFootnoteContent visitTerm(
      final STerm term)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override public SAFootnoteContent visitText(
      final SText text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override public SAFootnoteContent visitVerbatim(
      final SVerbatim text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class FormalItemContentAnnotator implements
    SFormalItemContentVisitor<SAFormalItemContent>
  {
    private final @Nonnull List<SAFootnote>   footnotes;
    private final @Nonnull SAIDMap            ids;
    private final @Nonnull SAFormalItemNumber number;

    public FormalItemContentAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SAFormalItemNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SAFormalItemContent visitFormalItemList(
      final @Nonnull SFormalItemList list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFormalItemList(list);
    }

    @Override public SAFormalItemContent visitImage(
      final @Nonnull SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SAFormalItemContent visitListOrdered(
      final @Nonnull SListOrdered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAFormalItemContent visitListUnordered(
      final @Nonnull SListUnordered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAFormalItemContent visitTable(
      final @Nonnull STable t)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTable(
        this.ids,
        this.footnotes,
        this.number,
        t);
    }

    @Override public SAFormalItemContent visitVerbatim(
      final @Nonnull SVerbatim text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class LinkContentAnnotator implements
    SLinkContentVisitor<SALinkContent>
  {
    public LinkContentAnnotator()
    {

    }

    @Override public SALinkContent visitImage(
      final @Nonnull SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SALinkContent visitText(
      final @Nonnull SText text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformText(text);
    }
  }

  private static final class ListItemContentAnnotator implements
    SListItemContentVisitor<SAListItemContent>
  {
    private final @Nonnull List<SAFootnote>          footnotes;
    private final @Nonnull SAIDMap                   ids;
    private final @Nonnull SASubsectionContentNumber number;

    public ListItemContentAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SAListItemContent visitFootnote(
      final @Nonnull SFootnote footnote)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFootnote(
        this.ids,
        this.footnotes,
        this.number,
        footnote);
    }

    @Override public SAListItemContent visitImage(
      final @Nonnull SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SAListItemContent visitLink(
      final @Nonnull SLink link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override public SAListItemContent visitLinkExternal(
      final @Nonnull SLinkExternal link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override public SAListItemContent visitListOrdered(
      final @Nonnull SListOrdered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAListItemContent visitListUnordered(
      final @Nonnull SListUnordered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAListItemContent visitTerm(
      final @Nonnull STerm term)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override public SAListItemContent visitText(
      final @Nonnull SText text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override public SAListItemContent visitVerbatim(
      final @Nonnull SVerbatim text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class NoPartSectionAnnotator implements
    SSectionVisitor<SASection>
  {
    private final @Nonnull List<SAFootnote>    footnotes;
    private final @Nonnull SAIDMap             ids;
    private final @Nonnull SASectionNumberS    number;
    private final @Nonnull SAFormalItemsByKind formals;

    public NoPartSectionAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull SAFormalItemsByKind in_formals,
      final @Nonnull List<SAFootnote> in_footnotes,
      final int section_no)
      throws ConstraintError
    {
      this.ids = in_ids;
      this.formals = in_formals;
      this.number = new SASectionNumberS(section_no);
      this.footnotes = in_footnotes;
    }

    @Override public SASection visitSectionWithParagraphs(
      final @Nonnull SSectionWithParagraphs s)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformSectionWithParagraphs(
        this.ids,
        this.formals,
        this.footnotes,
        s,
        this.number);
    }

    @Override public SASection visitSectionWithSubsections(
      final @Nonnull SSectionWithSubsections s)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformSectionWithSubsections(
        this.ids,
        this.formals,
        this.footnotes,
        this.number,
        s);
    }
  }

  private static final class ParagraphContentAnnotator implements
    SParagraphContentVisitor<SAParagraphContent>
  {
    private final @Nonnull List<SAFootnote>  footnotes;
    private final @Nonnull SAIDMap           ids;
    private final @Nonnull SAParagraphNumber number;

    public ParagraphContentAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SAParagraphNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SAParagraphContent visitFootnote(
      final @Nonnull SFootnote footnote)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFootnote(
        this.ids,
        this.footnotes,
        this.number,
        footnote);
    }

    @Override public SAParagraphContent visitFormalItemList(
      final @Nonnull SFormalItemList list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFormalItemList(list);
    }

    @Override public SAParagraphContent visitImage(
      final @Nonnull SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SAParagraphContent visitLink(
      final @Nonnull SLink link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override public SAParagraphContent visitLinkExternal(
      final @Nonnull SLinkExternal link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override public SAParagraphContent visitListOrdered(
      final @Nonnull SListOrdered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAParagraphContent visitListUnordered(
      final @Nonnull SListUnordered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SAParagraphContent visitTable(
      final @Nonnull STable table)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTable(
        this.ids,
        this.footnotes,
        this.number,
        table);
    }

    @Override public SAParagraphContent visitTerm(
      final @Nonnull STerm term)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override public SAParagraphContent visitText(
      final @Nonnull SText text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override public SAParagraphContent visitVerbatim(
      final @Nonnull SVerbatim text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  private static final class PartSectionAnnotator implements
    SSectionVisitor<SASection>
  {
    private final @Nonnull List<SAFootnote>    footnotes;
    private final @Nonnull SAIDMap             ids;
    private final @Nonnull SASectionNumberPS   number;
    private final @Nonnull SAFormalItemsByKind formals;

    public PartSectionAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull SAFormalItemsByKind in_formals,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SASectionNumberPS in_number)
    {
      this.ids = in_ids;
      this.formals = in_formals;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SASection visitSectionWithParagraphs(
      final @Nonnull SSectionWithParagraphs s)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformSectionWithParagraphs(
        this.ids,
        this.formals,
        this.footnotes,
        s,
        this.number);
    }

    @Override public SASection visitSectionWithSubsections(
      final @Nonnull SSectionWithSubsections s)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformSectionWithSubsections(
        this.ids,
        this.formals,
        this.footnotes,
        this.number,
        s);
    }
  }

  private static final class SAIDLinkCreator implements
    PartialFunction<SAID, Unit, ConstraintError>
  {
    private final @Nonnull SAIDTargetContent content;
    private final @Nonnull SAIDMap           ids;

    public SAIDLinkCreator(
      final @Nonnull SAIDMap map,
      final @Nonnull SAIDTargetContent c)
    {
      this.ids = map;
      this.content = c;
    }

    @Override public Unit call(
      final SAID x)
      throws ConstraintError
    {
      this.ids.put(x, this.content);
      return Unit.unit();
    }
  }

  private static final class SAIDMapper implements
    PartialFunction<SID, SAID, ConstraintError>
  {
    public SAIDMapper()
    {
      // Nothing
    }

    @Override public SAID call(
      final @Nonnull SID x)
      throws ConstraintError
    {
      return new SAID(x.getActual());
    }
  }

  private static final class TableCellContentAnnotator implements
    STableCellContentVisitor<SATableCellContent>
  {
    private final @Nonnull List<SAFootnote>          footnotes;
    private final @Nonnull SAIDMap                   ids;
    private final @Nonnull SASubsectionContentNumber number;

    public TableCellContentAnnotator(
      final @Nonnull SAIDMap in_ids,
      final @Nonnull List<SAFootnote> in_footnotes,
      final @Nonnull SASubsectionContentNumber in_number)
    {
      this.ids = in_ids;
      this.footnotes = in_footnotes;
      this.number = in_number;
    }

    @Override public SATableCellContent visitFootnote(
      final @Nonnull SFootnote footnote)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformFootnote(
        this.ids,
        this.footnotes,
        this.number,
        footnote);
    }

    @Override public SATableCellContent visitImage(
      final @Nonnull SImage image)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformImage(image);
    }

    @Override public SATableCellContent visitLink(
      final @Nonnull SLink link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLink(link);
    }

    @Override public SATableCellContent visitLinkExternal(
      final @Nonnull SLinkExternal link)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformLinkExternal(link);
    }

    @Override public SATableCellContent visitListOrdered(
      final @Nonnull SListOrdered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListOrdered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SATableCellContent visitListUnordered(
      final @Nonnull SListUnordered list)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformListUnordered(
        this.ids,
        this.footnotes,
        this.number,
        list);
    }

    @Override public SATableCellContent visitTerm(
      final @Nonnull STerm term)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformTerm(term);
    }

    @Override public SATableCellContent visitText(
      final @Nonnull SText text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformText(text);
    }

    @Override public SATableCellContent visitVerbatim(
      final @Nonnull SVerbatim text)
      throws ConstraintError,
        Exception
    {
      return SAnnotator.transformVerbatim(text);
    }
  }

  /**
   * Annotate the given document.
   * 
   * @param log
   *          A log handle
   * @param d
   *          The document
   * @return An annotated document
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SADocument document(
    final @Nonnull Log log,
    final @Nonnull SDocument d)
    throws ConstraintError
  {
    return new SAnnotator(log, d).process();
  }

  private static @Nonnull SAFootnote transformFootnote(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionContentNumber number,
    final @Nonnull SFootnote footnote)
    throws ConstraintError,
      Exception
  {
    final List<SAFootnoteContent> content_r =
      new ArrayList<SAFootnoteContent>();

    for (final SFootnoteContent c : footnote.getContent().getElements()) {
      final SAFootnoteContent rc =
        c.footnoteContentAccept(new FootnoteContentAnnotator(
          ids,
          footnotes,
          number));
      content_r.add(rc);
    }

    final SNonEmptyList<SAFootnoteContent> in_content =
      SNonEmptyList.newList(content_r);
    final SAFootnote f = new SAFootnote(footnotes.size(), in_content);
    footnotes.add(f);
    return f;
  }

  private static @Nonnull SAFormalItemContent transformFormalItemContent(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SFormalItem formal,
    final @Nonnull SAFormalItemNumber f_number)
    throws ConstraintError,
      Exception
  {
    return formal.getContent().formalItemContentAccept(
      new FormalItemContentAnnotator(ids, footnotes, f_number));
  }

  private static @Nonnull SAFormalItemList transformFormalItemList(
    final @Nonnull SFormalItemList list)
    throws ConstraintError
  {
    return new SAFormalItemList(list.getKind());
  }

  private static @Nonnull SAImage transformImage(
    final @Nonnull SImage image)
    throws ConstraintError
  {
    return new SAImage(
      image.getURI(),
      image.getType(),
      image.getWidth(),
      image.getHeight(),
      image.getText());
  }

  private static @Nonnull SALink transformLink(
    final @Nonnull SLink link)
    throws ConstraintError,
      Exception
  {
    final List<SALinkContent> content_r = new ArrayList<SALinkContent>();
    for (final SLinkContent c : link.getContent().getElements()) {
      final SALinkContent rc =
        c.linkContentAccept(new LinkContentAnnotator());
      content_r.add(rc);
    }

    final SNonEmptyList<SALinkContent> in_content =
      SNonEmptyList.newList(content_r);
    return new SALink(link.getTarget(), in_content);
  }

  private static @Nonnull SALinkExternal transformLinkExternal(
    final @Nonnull SLinkExternal link)
    throws ConstraintError,
      Exception
  {
    final List<SALinkContent> content_r = new ArrayList<SALinkContent>();
    for (final SLinkContent c : link.getContent().getElements()) {
      final SALinkContent rc =
        c.linkContentAccept(new LinkContentAnnotator());
      content_r.add(rc);
    }

    final SNonEmptyList<SALinkContent> in_content =
      SNonEmptyList.newList(content_r);
    return new SALinkExternal(link.getTarget(), in_content);
  }

  private static @Nonnull SAListOrdered transformListOrdered(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionContentNumber number,
    final @Nonnull SListOrdered list)
    throws ConstraintError,
      Exception
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

    final Option<String> in_type = list.getType();
    final SNonEmptyList<SAListItem> in_items = SNonEmptyList.newList(items_r);

    return new SAListOrdered(in_type, in_items);
  }

  private static @Nonnull SAListUnordered transformListUnordered(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionContentNumber number,
    final @Nonnull SListUnordered list)
    throws ConstraintError,
      Exception
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

    final Option<String> in_type = list.getType();
    final SNonEmptyList<SAListItem> in_items = SNonEmptyList.newList(items_r);

    return new SAListUnordered(in_type, in_items);
  }

  private static SNonEmptyList<SAParagraphContent> transformParagraphContent(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SParagraph paragraph,
    final @Nonnull SAParagraphNumber p_number)
    throws ConstraintError,
      Exception
  {
    final List<SAParagraphContent> content_r =
      new ArrayList<SAParagraphContent>();
    final ParagraphContentAnnotator annot =
      new ParagraphContentAnnotator(ids, footnotes, p_number);
    for (final SParagraphContent c : paragraph.getContent().getElements()) {
      final SAParagraphContent ca = c.paragraphContentAccept(annot);
      content_r.add(ca);
    }

    final SNonEmptyList<SAParagraphContent> in_content =
      SNonEmptyList.newList(content_r);
    return in_content;
  }

  private static @Nonnull SAFormalItem transformSectionFormalItem(
    final @Nonnull SAIDMap ids,
    final @Nonnull SAFormalItemsByKind formals,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASectionNumber number,
    final @Nonnull SFormalItem formal,
    final int formal_number)
    throws ConstraintError,
      Exception
  {
    final SAFormalItem saf =
      number.sectionNumberAccept(new SASectionNumberVisitor<SAFormalItem>() {
        @Override public SAFormalItem visitSectionNumberWithoutPart(
          final SASectionNumberS sn)
          throws ConstraintError,
            Exception
        {
          final SAFormalItemNumber f_number =
            new SAFormalItemNumberSF(sn.getSection(), formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids,
              footnotes,
              formal,
              f_number);

          return new SAFormalItem(f_number, title, formal.getKind(), formal
            .getType(), in_content, formal_number);
        }

        @Override public SAFormalItem visitSectionNumberWithPart(
          final SASectionNumberPS sn)
          throws ConstraintError,
            Exception
        {
          final SAFormalItemNumber f_number =
            new SAFormalItemNumberPSF(
              sn.getPart(),
              sn.getSection(),
              formal_number);
          final SAFormalItemTitle title =
            new SAFormalItemTitle(f_number, formal.getTitle().getActual());

          final SAFormalItemContent in_content =
            SAnnotator.transformFormalItemContent(
              ids,
              footnotes,
              formal,
              f_number);

          return new SAFormalItem(f_number, title, formal.getKind(), formal
            .getType(), in_content, formal_number);
        }
      });

    formals.put(saf.getKind(), saf);
    return saf;
  }

  private static @Nonnull SAParagraph transformSectionParagraph(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASectionNumber number,
    final @Nonnull SParagraph paragraph,
    final int paragraph_number)
    throws ConstraintError,
      Exception
  {
    final Option<SAID> id = paragraph.getID().mapPartial(new SAIDMapper());

    final SAParagraph sap =
      number.sectionNumberAccept(new SASectionNumberVisitor<SAParagraph>() {
        @Override public SAParagraph visitSectionNumberWithoutPart(
          final SASectionNumberS sn)
          throws ConstraintError,
            Exception
        {
          final SAParagraphNumber p_number =
            new SAParagraphNumberSP(sn.getSection(), paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids,
              footnotes,
              paragraph,
              p_number);

          return new SAParagraph(
            p_number,
            paragraph.getType(),
            in_content,
            id);
        }

        @Override public SAParagraph visitSectionNumberWithPart(
          final SASectionNumberPS sn)
          throws ConstraintError,
            Exception
        {
          final SAParagraphNumber p_number =
            new SAParagraphNumberPSP(
              sn.getPart(),
              sn.getSection(),
              paragraph_number);

          final SNonEmptyList<SAParagraphContent> in_content =
            SAnnotator.transformParagraphContent(
              ids,
              footnotes,
              paragraph,
              p_number);

          return new SAParagraph(
            p_number,
            paragraph.getType(),
            in_content,
            id);
        }
      });

    id.mapPartial(new SAIDLinkCreator(ids, sap));
    return sap;
  }

  private static SASection transformSectionWithParagraphs(
    final @Nonnull SAIDMap ids,
    final @Nonnull SAFormalItemsByKind formals,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SSectionWithParagraphs s,
    final @Nonnull SASectionNumber number)
    throws ConstraintError,
      Exception
  {
    final SASectionTitle title =
      new SASectionTitle(number, s.getTitle().getActual());

    final List<SASubsectionContent> content_r =
      new ArrayList<SASubsectionContent>();
    final AtomicInteger paragraph_no = new AtomicInteger(1);
    final AtomicInteger formal_no = new AtomicInteger(1);

    for (final SSubsectionContent p : s.getSectionContent().getElements()) {
      final SASubsectionContent r =
        p
          .subsectionContentAccept(new SSubsectionContentVisitor<SASubsectionContent>() {
            @Override public SASubsectionContent visitFormalItem(
              final @Nonnull SFormalItem formal)
              throws ConstraintError,
                Exception
            {
              final SAFormalItem pa =
                SAnnotator.transformSectionFormalItem(
                  ids,
                  formals,
                  footnotes,
                  number,
                  formal,
                  formal_no.getAndIncrement());
              return pa;
            }

            @Override public SASubsectionContent visitParagraph(
              final @Nonnull SParagraph paragraph)
              throws ConstraintError,
                Exception
            {
              final SAParagraph pa =
                SAnnotator.transformSectionParagraph(
                  ids,
                  footnotes,
                  number,
                  paragraph,
                  paragraph_no.getAndIncrement());
              return pa;
            }
          });

      content_r.add(r);
    }

    final Option<SAID> id = s.getID().mapPartial(new SAIDMapper());

    final SNonEmptyList<SASubsectionContent> content =
      SNonEmptyList.newList(content_r);
    return new SASectionWithParagraphs(
      number,
      s.getType(),
      id,
      title,
      s.getContents(),
      content);
  }

  private static @Nonnull SASection transformSectionWithSubsections(
    final @Nonnull SAIDMap ids,
    final @Nonnull SAFormalItemsByKind formals,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASectionNumber number,
    final @Nonnull SSectionWithSubsections s)
    throws ConstraintError,
      Exception
  {
    final SASectionTitle title =
      new SASectionTitle(number, s.getTitle().getActual());

    final List<SASubsection> subsections_r = new ArrayList<SASubsection>();
    for (final SSubsection ss : s.getSubsections().getElements()) {
      final SASubsection sa =
        SAnnotator.transformSubsection(
          ids,
          formals,
          footnotes,
          number,
          ss,
          subsections_r.size() + 1);
      sa.getID().mapPartial(new SAIDLinkCreator(ids, sa));
      subsections_r.add(sa);
    }
    final SNonEmptyList<SASubsection> subsections =
      SNonEmptyList.newList(subsections_r);

    final Option<SAID> id = s.getID().mapPartial(new SAIDMapper());
    return new SASectionWithSubsections(
      number,
      s.getType(),
      id,
      title,
      s.getContents(),
      subsections);
  }

  private static SASubsection transformSubsection(
    final @Nonnull SAIDMap ids,
    final @Nonnull SAFormalItemsByKind formals,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASectionNumber number,
    final @Nonnull SSubsection subsection,
    final int subsection_number)
    throws ConstraintError,
      Exception
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
      final SASubsectionContent ca =
        c
          .subsectionContentAccept(new SSubsectionContentVisitor<SASubsectionContent>() {
            @Override public SASubsectionContent visitFormalItem(
              final SFormalItem formal)
              throws ConstraintError,
                Exception
            {
              return SAnnotator.transformSubsectionFormalItem(
                ids,
                formals,
                footnotes,
                s_number,
                formal,
                formal_no.getAndIncrement());
            }

            @Override public SASubsectionContent visitParagraph(
              final SParagraph paragraph)
              throws ConstraintError,
                Exception
            {
              final SAParagraph pa =
                SAnnotator.transformSubsectionParagraph(
                  ids,
                  footnotes,
                  s_number,
                  paragraph,
                  paragraph_no.getAndIncrement());
              return pa;
            }
          });
      results_r.add(ca);
    }

    final SNonEmptyList<SASubsectionContent> in_content =
      SNonEmptyList.newList(results_r);

    final Option<SAID> id = subsection.getID().mapPartial(new SAIDMapper());
    return new SASubsection(
      s_number,
      subsection.getType(),
      id,
      title,
      in_content);
  }

  private static @Nonnull SAParagraph transformSubsectionParagraph(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionNumber number,
    final @Nonnull SParagraph paragraph,
    final int paragraph_number)
    throws ConstraintError,
      Exception
  {
    final Option<SAID> id = paragraph.getID().mapPartial(new SAIDMapper());

    final SAParagraph sap =
      number
        .subsectionNumberAccept(new SASubsectionNumberVisitor<SAParagraph>() {
          @Override public SAParagraph visitSubsectionNumberPSS(
            final SASubsectionNumberPSS ssn)
            throws ConstraintError,
              Exception
          {
            final SAParagraphNumber p_number =
              new SAParagraphNumberPSSP(ssn.getPart(), ssn.getSection(), ssn
                .getSubsection(), paragraph_number);

            final SNonEmptyList<SAParagraphContent> in_content =
              SAnnotator.transformParagraphContent(
                ids,
                footnotes,
                paragraph,
                p_number);

            return new SAParagraph(
              p_number,
              paragraph.getType(),
              in_content,
              id);
          }

          @Override public SAParagraph visitSubsectionNumberSS(
            final SASubsectionNumberSS ssn)
            throws ConstraintError,
              Exception
          {
            final SAParagraphNumber p_number =
              new SAParagraphNumberSSP(
                ssn.getSection(),
                ssn.getSubsection(),
                paragraph_number);

            final SNonEmptyList<SAParagraphContent> in_content =
              SAnnotator.transformParagraphContent(
                ids,
                footnotes,
                paragraph,
                p_number);

            return new SAParagraph(
              p_number,
              paragraph.getType(),
              in_content,
              id);
          }
        });

    id.mapPartial(new SAIDLinkCreator(ids, sap));
    return sap;
  }

  private static @Nonnull SAFormalItem transformSubsectionFormalItem(
    final @Nonnull SAIDMap ids,
    final @Nonnull SAFormalItemsByKind formals,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionNumber s_number,
    final @Nonnull SFormalItem formal,
    final int formal_number)
    throws ConstraintError,
      Exception
  {
    final SAFormalItem saf =
      s_number
        .subsectionNumberAccept(new SASubsectionNumberVisitor<SAFormalItem>() {
          @Override public SAFormalItem visitSubsectionNumberPSS(
            final @Nonnull SASubsectionNumberPSS p)
            throws ConstraintError,
              Exception
          {
            final SAFormalItemNumber f_number =
              new SAFormalItemNumberPSSF(p.getPart(), p.getSection(), p
                .getSubsection(), formal_number);
            final SAFormalItemTitle title =
              new SAFormalItemTitle(f_number, formal.getTitle().getActual());

            final SAFormalItemContent in_content =
              SAnnotator.transformFormalItemContent(
                ids,
                footnotes,
                formal,
                f_number);

            return new SAFormalItem(f_number, title, formal.getKind(), formal
              .getType(), in_content, formal_number);
          }

          @Override public SAFormalItem visitSubsectionNumberSS(
            final @Nonnull SASubsectionNumberSS p)
            throws ConstraintError,
              Exception
          {
            final SAFormalItemNumber f_number =
              new SAFormalItemNumberSSF(
                p.getSection(),
                p.getSubsection(),
                formal_number);
            final SAFormalItemTitle title =
              new SAFormalItemTitle(f_number, formal.getTitle().getActual());

            final SAFormalItemContent in_content =
              SAnnotator.transformFormalItemContent(
                ids,
                footnotes,
                formal,
                f_number);

            return new SAFormalItem(f_number, title, formal.getKind(), formal
              .getType(), in_content, formal_number);
          }
        });

    formals.put(saf.getKind(), saf);
    return saf;
  }

  private static @Nonnull SASubsectionNumber transformSubsectionNumber(
    final @Nonnull SASectionNumber number,
    final int subsection_number)
    throws ConstraintError,
      Exception
  {
    return number
      .sectionNumberAccept(new SASectionNumberVisitor<SASubsectionNumber>() {
        @Override public SASubsectionNumber visitSectionNumberWithoutPart(
          final SASectionNumberS p)
          throws ConstraintError,
            Exception
        {
          return new SASubsectionNumberSS(p.getSection(), subsection_number);
        }

        @Override public SASubsectionNumber visitSectionNumberWithPart(
          final SASectionNumberPS p)
          throws ConstraintError,
            Exception
        {
          return new SASubsectionNumberPSS(
            p.getPart(),
            p.getSection(),
            subsection_number);
        }
      });
  }

  private static @Nonnull SATable transformTable(
    final @Nonnull SAIDMap ids,
    final @Nonnull List<SAFootnote> footnotes,
    final @Nonnull SASubsectionContentNumber number,
    final @Nonnull STable t)
    throws ConstraintError,
      Exception
  {
    final SATableSummary in_summary =
      SAnnotator.transformTableSummary(t.getSummary());

    final Option<SATableHead> in_header =
      t.getHeader().mapPartial(
        new PartialFunction<STableHead, SATableHead, Exception>() {
          @Override public SATableHead call(
            final STableHead x)
            throws Exception
          {
            try {
              return SAnnotator.transformTableHead(x);
            } catch (final ConstraintError e) {
              throw new UnreachableCodeException(e);
            }
          }
        });

    final SATableBody in_body =
      SAnnotator.transformTableBody(ids, footnotes, number, t.getBody());
    return new SATable(in_summary, in_header, in_body);
  }

  private static @Nonnull SATableBody transformTableBody(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SASubsectionContentNumber in_number,
    final @Nonnull STableBody body)
    throws ConstraintError,
      Exception
  {
    final List<SATableRow> rows_r = new ArrayList<SATableRow>();
    for (final STableRow r : body.getRows().getElements()) {
      rows_r.add(SAnnotator.transformTableRow(
        in_ids,
        in_footnotes,
        in_number,
        r));
    }

    final SNonEmptyList<SATableRow> rows = SNonEmptyList.newList(rows_r);
    return new SATableBody(rows);
  }

  private static @Nonnull SATableCell transformTableCell(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SASubsectionContentNumber in_number,
    final @Nonnull STableCell c)
    throws ConstraintError,
      Exception
  {
    final List<SATableCellContent> content_r =
      new ArrayList<SATableCellContent>();
    for (final STableCellContent cc : c.getContent().getElements()) {
      content_r.add(SAnnotator.transformTableCellContent(
        in_ids,
        in_footnotes,
        in_number,
        cc));
    }

    final SNonEmptyList<SATableCellContent> content =
      SNonEmptyList.newList(content_r);
    return new SATableCell(content);
  }

  private static @Nonnull SATableCellContent transformTableCellContent(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SASubsectionContentNumber in_number,
    final @Nonnull STableCellContent cc)
    throws ConstraintError,
      Exception
  {
    return cc.tableCellContentAccept(new TableCellContentAnnotator(
      in_ids,
      in_footnotes,
      in_number));
  }

  private static @Nonnull SATableColumnName transformTableColumnName(
    final @Nonnull STableColumnName n)
    throws ConstraintError
  {
    return new SATableColumnName(n.getText());
  }

  private static @Nonnull SATableHead transformTableHead(
    final @Nonnull STableHead x)
    throws ConstraintError
  {
    final List<SATableColumnName> names_r =
      new ArrayList<SATableColumnName>();
    for (final STableColumnName n : x.getHeader().getElements()) {
      names_r.add(SAnnotator.transformTableColumnName(n));
    }

    final SNonEmptyList<SATableColumnName> names =
      SNonEmptyList.newList(names_r);
    return new SATableHead(names);
  }

  private static @Nonnull SATableRow transformTableRow(
    final @Nonnull SAIDMap in_ids,
    final @Nonnull List<SAFootnote> in_footnotes,
    final @Nonnull SASubsectionContentNumber in_number,
    final @Nonnull STableRow r)
    throws ConstraintError,
      Exception
  {
    final List<SATableCell> cells_r = new ArrayList<SATableCell>();
    for (final STableCell c : r.getColumns().getElements()) {
      cells_r.add(SAnnotator.transformTableCell(
        in_ids,
        in_footnotes,
        in_number,
        c));
    }

    final SNonEmptyList<SATableCell> cells = SNonEmptyList.newList(cells_r);
    return new SATableRow(cells);
  }

  private static @Nonnull SATableSummary transformTableSummary(
    final @Nonnull STableSummary summary)
    throws ConstraintError
  {
    return new SATableSummary(summary.getText());
  }

  private static @Nonnull SATerm transformTerm(
    final @Nonnull STerm term)
    throws ConstraintError
  {
    return new SATerm(new SAText(term.getText().getText()), term.getType());
  }

  private static @Nonnull SAText transformText(
    final @Nonnull SText text)
    throws ConstraintError
  {
    return new SAText(text.getText());
  }

  private static @Nonnull SAVerbatim transformVerbatim(
    final @Nonnull SVerbatim text)
    throws ConstraintError
  {
    return new SAVerbatim(text.getText(), text.getType());
  }

  private final @Nonnull SDocument document;
  private final @Nonnull Log       log;

  private SAnnotator(
    final @Nonnull Log in_log,
    final @Nonnull SDocument d)
    throws ConstraintError
  {
    this.document = Constraints.constrainNotNull(d, "Document");
    this.log = new Log(in_log, "annotator");
  }

  private @Nonnull SADocument process()
  {
    try {
      return this.document.documentAccept(new DocumentAnnotator(this.log));
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    }
  }
}
