/*
 * Copyright © 2018 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.compiler.main;

import com.io7m.jaffirm.core.Postconditions;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jstructural.ast.SBlockContentType;
import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SBlockIDType;
import com.io7m.jstructural.ast.SContentNumber;
import com.io7m.jstructural.ast.SDocument;
import com.io7m.jstructural.ast.SFootnote;
import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFormalItem;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SImageSize;
import com.io7m.jstructural.ast.SImageSizeType;
import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SInlineLinkContentType;
import com.io7m.jstructural.ast.SInlineTableContentType;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SLinkExternal;
import com.io7m.jstructural.ast.SListItem;
import com.io7m.jstructural.ast.SListItemType;
import com.io7m.jstructural.ast.SListOrdered;
import com.io7m.jstructural.ast.SListUnordered;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParagraph;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SSectionType;
import com.io7m.jstructural.ast.SSectionWithSections;
import com.io7m.jstructural.ast.SSectionWithSectionsType;
import com.io7m.jstructural.ast.SSectionWithSubsectionContent;
import com.io7m.jstructural.ast.SSectionWithSubsectionContentType;
import com.io7m.jstructural.ast.SSectionWithSubsections;
import com.io7m.jstructural.ast.SSectionWithSubsectionsType;
import com.io7m.jstructural.ast.SSubsection;
import com.io7m.jstructural.ast.SSubsectionContentType;
import com.io7m.jstructural.ast.SSubsectionType;
import com.io7m.jstructural.ast.STable;
import com.io7m.jstructural.ast.STableBody;
import com.io7m.jstructural.ast.STableBodyType;
import com.io7m.jstructural.ast.STableCell;
import com.io7m.jstructural.ast.STableCellType;
import com.io7m.jstructural.ast.STableColumnName;
import com.io7m.jstructural.ast.STableColumnNameType;
import com.io7m.jstructural.ast.STableHeader;
import com.io7m.jstructural.ast.STableHeaderType;
import com.io7m.jstructural.ast.STableRow;
import com.io7m.jstructural.ast.STableRowType;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STextType;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.jstructural.ast.STypeNameType;
import com.io7m.jstructural.ast.SVerbatim;
import com.io7m.jstructural.compiler.api.SCompilationTaskType;
import com.io7m.jstructural.compiler.api.SCompileError;
import com.io7m.jstructural.compiler.api.SCompiledGlobalType;
import com.io7m.jstructural.compiler.api.SCompiledLocalType;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.Value;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import static com.io7m.jstructural.compiler.api.SCompileErrorType.Severity.ERROR;

final class SCompilationTask implements SCompilationTaskType
{
  private final SDocument<SParsed> document;

  SCompilationTask(final SDocument<SParsed> in_document)
  {
    this.document = Objects.requireNonNull(in_document, "document");
  }

  private static Validation<Seq<SCompileError>, SDocument<CompiledLocal>> compileDocument(
    final CompiledGlobal global,
    final SDocument<SParsed> doc)
  {
    try {
      final CompiledLocal local = new CompiledLocal(global, global.numbering.current());
      final Vector<SSectionType<SParsed>> root_sections = doc.sections();

      return Validation.sequence(root_sections.map(s -> compileSection(global, s)))
        .map(Value::toVector)
        .map(cs -> compileDocumentFinish(doc, local, cs));
    } finally {
      Postconditions.checkPostconditionI(
        global.numbering.components(),
        global.numbering.components() == 1,
        i -> "Numbering stack must be empty");
    }
  }

  private static SDocument<CompiledLocal> compileDocumentFinish(
    final SDocument<SParsed> document,
    final CompiledLocal local,
    final Vector<SSectionType<CompiledLocal>> sections)
  {
    final SDocument<CompiledLocal> result = SDocument.of(local, sections, document.title());
    setElementParentTo(result, result);
    assignAllParents(sections, result);
    return result;
  }

  private static <T extends SModelType<CompiledLocal>> void assignAllParents(
    final Vector<T> child_elements,
    final SBlockContentType<CompiledLocal> parent)
  {
    child_elements.forEach(child_element -> setElementParentTo(child_element, parent));
  }

  @SuppressWarnings("unchecked")
  private static <T extends SInlineAnyContentType<CompiledLocal>> void assignAllInlineParents(
    final Vector<T> child_elements,
    final SBlockContentType<CompiledLocal> parent)
  {
    child_elements.forEach(child_element -> {
      switch (child_element.inlineKind()) {
        case INLINE_TEXT: {
          final SText<CompiledLocal> element =
            (SText<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          return;
        }

        case INLINE_TERM: {
          final STerm<CompiledLocal> element =
            (STerm<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          assignAllInlineParents(element.text(), parent);
          return;
        }

        case INLINE_IMAGE: {
          final SImage<CompiledLocal> element =
            (SImage<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          assignAllInlineParents(element.text(), parent);
          return;
        }

        case INLINE_LINK: {
          final SLink<CompiledLocal> element =
            (SLink<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          assignAllInlineParents(element.content(), parent);
          return;
        }

        case INLINE_LINK_EXTERNAL: {
          final SLinkExternal<CompiledLocal> element =
            (SLinkExternal<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          assignAllInlineParents(element.content(), parent);
          return;
        }

        case INLINE_FOOTNOTE_REFERENCE: {
          final SFootnoteReference<CompiledLocal> element =
            (SFootnoteReference<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          return;
        }

        case INLINE_FORMAL_ITEM_REFERENCE: {
          final SFormalItemReference<CompiledLocal> element =
            (SFormalItemReference<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          return;
        }

        case INLINE_VERBATIM: {
          final SVerbatim<CompiledLocal> element = (SVerbatim<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          assignAllInlineParents(Vector.of(element.text()), parent);
          return;
        }

        case INLINE_LIST_ORDERED: {
          final SListOrdered<CompiledLocal> element = (SListOrdered<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          element.items().forEach(item -> assignAllInlineParents(item.content(), parent));
          return;
        }

        case INLINE_LIST_UNORDERED: {
          final SListUnordered<CompiledLocal> element =
            (SListUnordered<CompiledLocal>) child_element;
          setElementParentTo(element, parent);
          element.items().forEach(item -> assignAllInlineParents(item.content(), parent));
          return;
        }

        case INLINE_TABLE: {
          final STable<CompiledLocal> element = (STable<CompiledLocal>) child_element;
          setElementParentTo(element, parent);

          element.header().ifPresent(header -> {
            setElementParentTo(header, parent);
            header.names().forEach(name -> setElementParentTo(name, parent));
          });

          setElementParentTo(element.body(), parent);
          element.body().rows().forEach(row -> {
            setElementParentTo(row, parent);
            row.cells().forEach(cell -> {
              setElementParentTo(cell, parent);
              assignAllInlineParents(cell.content(), parent);
            });
          });
        }
      }
    });
  }

  private static void setElementParentTo(
    final SModelType<CompiledLocal> element,
    final SBlockContentType<CompiledLocal> parent)
  {
    element.data().setParent(parent);
  }

  @SuppressWarnings("unchecked")
  private static <A, B, C extends SSectionType<B>>
  Validation<A, SSectionType<CompiledLocal>>
  widenSectionResult(final Validation<A, C> v)
  {
    return (Validation<A, SSectionType<CompiledLocal>>) v;
  }

  @SuppressWarnings("unchecked")
  private static <A, B, C extends SSubsectionContentType<B>>
  Validation<A, SSubsectionContentType<CompiledLocal>>
  widenSubsectionContentResult(final Validation<A, C> v)
  {
    return (Validation<A, SSubsectionContentType<CompiledLocal>>) v;
  }

  @SuppressWarnings("unchecked")
  private static <A, B, C extends SInlineAnyContentType<B>>
  Validation<A, SInlineAnyContentType<CompiledLocal>>
  widenInlineAnyContentResult(final Validation<A, C> v)
  {
    return (Validation<A, SInlineAnyContentType<CompiledLocal>>) v;
  }

  @SuppressWarnings("unchecked")
  private static <A, B, C extends SInlineLinkContentType<B>>
  Validation<A, SInlineLinkContentType<CompiledLocal>>
  widenInlineLinkContentResult(final Validation<A, C> v)
  {
    return (Validation<A, SInlineLinkContentType<CompiledLocal>>) v;
  }

  @SuppressWarnings("unchecked")
  private static <A, B, C extends SInlineTableContentType<B>>
  Validation<A, SInlineTableContentType<CompiledLocal>>
  widenInlineTableContentResult(final Validation<A, C> v)
  {
    return (Validation<A, SInlineTableContentType<CompiledLocal>>) v;
  }

  private static Validation<Seq<SCompileError>, SSectionType<CompiledLocal>> compileSection(
    final CompiledGlobal global,
    final SSectionType<SParsed> section)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      switch (section.sectionKind()) {
        case SECTION_WITH_SECTIONS:
          return widenSectionResult(compileSectionWithSections(
            global, local, (SSectionWithSectionsType<SParsed>) section));
        case SECTION_WITH_SUBSECTIONS:
          return widenSectionResult(compileSectionWithSubsections(
            global, local, (SSectionWithSubsectionsType<SParsed>) section));
        case SECTION_WITH_SUBSECTION_CONTENT:
          return widenSectionResult(compileSectionWithSubsectionContent(
            global, local, (SSectionWithSubsectionContentType<SParsed>) section));
      }
      throw new UnreachableCodeException();
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SSectionWithSubsectionContentType<CompiledLocal>>
  compileSectionWithSubsectionContent(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SSectionWithSubsectionContentType<SParsed> section)
  {
    return
      Validation.sequence(section.content().map(child -> compileSubsectionContent(global, child)))
        .map(Value::toVector)
        .map(cs -> compileSectionWithSubsectionContentFinish(local, section, cs));
  }

  private static SSectionWithSubsectionContentType<CompiledLocal>
  compileSectionWithSubsectionContentFinish(
    final CompiledLocal local,
    final SSectionWithSubsectionContentType<SParsed> section,
    final Vector<SSubsectionContentType<CompiledLocal>> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      section.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      section.id().map(block_id -> compileBlockId(local, block_id));

    final SSectionWithSubsectionContent<CompiledLocal> result =
      SSectionWithSubsectionContent.of(
        local,
        type,
        id,
        section.title(),
        section.tableOfContents(),
        content);

    assignAllParents(content, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SSectionWithSubsectionsType<CompiledLocal>>
  compileSectionWithSubsections(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SSectionWithSubsectionsType<SParsed> section)
  {
    return Validation.sequence(section.subsections().map(child -> compileSubsection(global, child)))
      .map(Value::toVector)
      .map(cs -> compileSectionWithSubsectionsFinish(local, section, cs));
  }

  private static SSectionWithSubsectionsType<CompiledLocal>
  compileSectionWithSubsectionsFinish(
    final CompiledLocal local,
    final SSectionWithSubsectionsType<SParsed> section,
    final Vector<SSubsectionType<CompiledLocal>> subsections)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      section.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      section.id().map(block_id -> compileBlockId(local, block_id));

    final SSectionWithSubsections<CompiledLocal> result =
      SSectionWithSubsections.of(
        local,
        type,
        id,
        section.title(),
        section.tableOfContents(),
        subsections);

    assignAllParents(subsections, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SSubsectionType<CompiledLocal>>
  compileSubsection(
    final CompiledGlobal global,
    final SSubsectionType<SParsed> subsection)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Vector<SSubsectionContentType<SParsed>> content = subsection.content();
      return Validation.sequence(content.map(cs -> compileSubsectionContent(global, cs)))
        .map(Value::toVector)
        .map(cs -> compileSubsectionFinish(local, subsection, cs));
    } finally {
      global.numbering.pop();
    }
  }

  private static CompiledLocal createNewLocal(
    final CompiledGlobal global)
  {
    global.numbering.increment();
    final SContentNumber number = global.numbering.current();
    return new CompiledLocal(global, number);
  }

  private static SSubsectionType<CompiledLocal> compileSubsectionFinish(
    final CompiledLocal local,
    final SSubsectionType<SParsed> subsection,
    final Vector<SSubsectionContentType<CompiledLocal>> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      subsection.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      subsection.id().map(block_id -> compileBlockId(local, block_id));

    final SSubsection<CompiledLocal> result =
      SSubsection.of(local, type, id, subsection.title(), content);
    assignAllParents(content, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SSubsectionContentType<CompiledLocal>>
  compileSubsectionContent(
    final CompiledGlobal global,
    final SSubsectionContentType<SParsed> content)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      switch (content.subsectionContentKind()) {
        case SUBSECTION_PARAGRAPH:
          return widenSubsectionContentResult(
            compileParagraph(global, local, (SParagraph<SParsed>) content));
        case SUBSECTION_FORMAL_ITEM:
          return widenSubsectionContentResult(
            compileFormalItem(global, local, (SFormalItem<SParsed>) content));
        case SUBSECTION_FOOTNOTE:
          return widenSubsectionContentResult(
            compileFootnote(global, local, (SFootnote<SParsed>) content));
      }

      throw new UnreachableCodeException();
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SFormalItem<CompiledLocal>>
  compileFormalItem(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SFormalItem<SParsed> formal_item)
  {
    return Validation.sequence(formal_item.content().map(child -> compileInlineAny(global, child)))
      .map(Value::toVector)
      .map(cs -> compileFormalItemFinish(local, formal_item, cs));
  }

  private static SFormalItem<CompiledLocal> compileFormalItemFinish(
    final CompiledLocal local,
    final SFormalItem<SParsed> formal_item,
    final Vector<SInlineAnyContentType<CompiledLocal>> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      formal_item.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      formal_item.id().map(block_id -> compileBlockId(local, block_id));

    final SFormalItem<CompiledLocal> result =
      SFormalItem.of(local, type, id, formal_item.title(), content);
    assignAllInlineParents(content, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SFootnote<CompiledLocal>>
  compileFootnote(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SFootnote<SParsed> footnote)
  {
    return Validation.sequence(footnote.content().map(child -> compileInlineAny(global, child)))
      .map(Value::toVector)
      .map(cs -> compileFootnoteFinish(local, footnote, cs));
  }

  private static SFootnote<CompiledLocal> compileFootnoteFinish(
    final CompiledLocal local,
    final SFootnote<SParsed> footnote,
    final Vector<SInlineAnyContentType<CompiledLocal>> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      footnote.type().map(name -> compileTypeName(local, name));
    final SBlockIDType<CompiledLocal> id =
      compileBlockId(local, footnote.id());

    final SFootnote<CompiledLocal> result = SFootnote.of(local, type, id, content);
    assignAllInlineParents(content, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SParagraph<CompiledLocal>>
  compileParagraph(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SParagraph<SParsed> paragraph)
  {
    return Validation.sequence(paragraph.content().map(child -> compileInlineAny(global, child)))
      .map(Value::toVector)
      .map(cs -> compileParagraphFinish(local, paragraph, cs));
  }

  private static Validation<Seq<SCompileError>, SInlineAnyContentType<CompiledLocal>>
  compileInlineAny(
    final CompiledGlobal global,
    final SInlineAnyContentType<SParsed> content)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      switch (content.inlineKind()) {
        case INLINE_TEXT:
          return widenInlineAnyContentResult(
            compileInlineText(local, (SText<SParsed>) content));

        case INLINE_TERM:
          return widenInlineAnyContentResult(
            compileInlineTerm(global, local, (STerm<SParsed>) content));

        case INLINE_IMAGE:
          return widenInlineAnyContentResult(
            compileInlineImage(global, local, (SImage<SParsed>) content));

        case INLINE_LINK:
          return widenInlineAnyContentResult(
            compileInlineLink(global, local, (SLink<SParsed>) content));

        case INLINE_LINK_EXTERNAL:
          return widenInlineAnyContentResult(
            compileInlineLinkExternal(global, local, (SLinkExternal<SParsed>) content));

        case INLINE_FOOTNOTE_REFERENCE:
          return widenInlineAnyContentResult(
            compileInlineFootnoteReference(local, (SFootnoteReference<SParsed>) content));

        case INLINE_FORMAL_ITEM_REFERENCE:
          return widenInlineAnyContentResult(
            compileInlineFormalItemReference(local, (SFormalItemReference<SParsed>) content));

        case INLINE_VERBATIM:
          return widenInlineAnyContentResult(
            compileInlineVerbatim(global, local, (SVerbatim<SParsed>) content));

        case INLINE_LIST_ORDERED:
          return widenInlineAnyContentResult(
            compileInlineListOrdered(global, local, (SListOrdered<SParsed>) content));

        case INLINE_LIST_UNORDERED:
          return widenInlineAnyContentResult(
            compileInlineListUnordered(global, local, (SListUnordered<SParsed>) content));

        case INLINE_TABLE:
          return widenInlineAnyContentResult(
            compileInlineTable(global, local, (STable<SParsed>) content));
      }

      throw new UnreachableCodeException();
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SInlineTableContentType<CompiledLocal>>
  compileInlineTableContent(
    final CompiledGlobal global,
    final SInlineTableContentType<SParsed> content)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      switch (content.inlineTableKind()) {
        case INLINE_TABLE_TEXT:
          return widenInlineTableContentResult(
            compileInlineText(local, (SText<SParsed>) content));

        case INLINE_TABLE_TERM:
          return widenInlineTableContentResult(
            compileInlineTerm(global, local, (STerm<SParsed>) content));

        case INLINE_TABLE_IMAGE:
          return widenInlineTableContentResult(
            compileInlineImage(global, local, (SImage<SParsed>) content));

        case INLINE_TABLE_LINK:
          return widenInlineTableContentResult(
            compileInlineLink(global, local, (SLink<SParsed>) content));

        case INLINE_TABLE_LINK_EXTERNAL:
          return widenInlineTableContentResult(
            compileInlineLinkExternal(global, local, (SLinkExternal<SParsed>) content));

        case INLINE_TABLE_FOOTNOTE_REFERENCE:
          return widenInlineTableContentResult(
            compileInlineFootnoteReference(local, (SFootnoteReference<SParsed>) content));

        case INLINE_TABLE_FORMAL_ITEM_REFERENCE:
          return widenInlineTableContentResult(
            compileInlineFormalItemReference(local, (SFormalItemReference<SParsed>) content));

        case INLINE_TABLE_VERBATIM:
          return widenInlineTableContentResult(
            compileInlineVerbatim(global, local, (SVerbatim<SParsed>) content));

        case INLINE_TABLE_LIST_ORDERED:
          return widenInlineTableContentResult(
            compileInlineListOrdered(global, local, (SListOrdered<SParsed>) content));

        case INLINE_TABLE_LIST_UNORDERED:
          return widenInlineTableContentResult(
            compileInlineListUnordered(global, local, (SListUnordered<SParsed>) content));
      }

      throw new UnreachableCodeException();
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SInlineLinkContentType<CompiledLocal>>
  compileLinkContent(
    final CompiledGlobal global,
    final SInlineLinkContentType<SParsed> content)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      switch (content.inlineLinkKind()) {
        case INLINE_LINK_TEXT:
          return widenInlineLinkContentResult(
            compileInlineText(local, (STextType<SParsed>) content));

        case INLINE_LINK_IMAGE:
          return widenInlineLinkContentResult(
            compileInlineImage(global, local, (SImage<SParsed>) content));
      }

      throw new UnreachableCodeException();
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SText<CompiledLocal>>
  compileInlineText(
    final CompiledLocal local,
    final STextType<SParsed> content)
  {
    return Validation.valid(SText.of(local, content.text()));
  }

  private static Validation<Seq<SCompileError>, STable<CompiledLocal>>
  compileInlineTable(
    final CompiledGlobal global,
    final CompiledLocal local,
    final STable<SParsed> table)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      table.type().map(name -> compileTypeName(local, name));

    final OptionalInt column_count =
      compileInlineTableGetColumnCount(table);

    final Validation<Seq<SCompileError>, Optional<STableHeader<CompiledLocal>>> header =
      compileInlineTableHeaderOptional(global, table.header());
    final Validation<Seq<SCompileError>, STableBody<CompiledLocal>> body =
      compileInlineTableBody(global, table.body(), column_count);

    return flattenErrors(
      Validation.combine(header, body)
        .ap((c_header, c_body) -> STable.of(local, type, c_header, c_body)));
  }

  private static OptionalInt
  compileInlineTableGetColumnCount(
    final STable<SParsed> table)
  {
    final OptionalInt column_count;
    final Optional<STableHeaderType<SParsed>> header_opt = table.header();
    if (header_opt.isPresent()) {
      final STableHeaderType<SParsed> header = header_opt.get();
      column_count = OptionalInt.of(header.names().size());
    } else {
      column_count = OptionalInt.empty();
    }
    return column_count;
  }

  private static <E, T> Validation<Seq<E>, T> flattenErrors(
    final Validation<Seq<Seq<E>>, T> v)
  {
    return v.mapError(xs -> xs.fold(Vector.empty(), Seq::appendAll));
  }

  private static Validation<Seq<SCompileError>, Optional<STableHeader<CompiledLocal>>>
  compileInlineTableHeaderOptional(
    final CompiledGlobal global,
    final Optional<STableHeaderType<SParsed>> header_opt)
  {
    if (header_opt.isPresent()) {
      final STableHeaderType<SParsed> header = header_opt.get();
      return compileInlineTableHeader(global, header).map(Optional::of);
    }
    return Validation.valid(Optional.empty());
  }

  private static Validation<Seq<SCompileError>, STableHeader<CompiledLocal>>
  compileInlineTableHeader(
    final CompiledGlobal global,
    final STableHeaderType<SParsed> header)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Optional<STypeNameType<CompiledLocal>> type =
        header.type().map(name -> compileTypeName(local, name));

      final Vector<STableColumnNameType<SParsed>> names = header.names();
      return Validation.sequence(names.map(
        name -> compileInlineTableHeaderName(global, name)))
        .map(Value::toVector)
        .map(c_names -> STableHeader.of(local, type, c_names.map(Function.identity())));
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, STableColumnNameType<CompiledLocal>>
  compileInlineTableHeaderName(
    final CompiledGlobal global,
    final STableColumnNameType<SParsed> column_name)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Optional<STypeNameType<CompiledLocal>> type =
        column_name.type().map(name -> compileTypeName(local, name));

      return Validation.valid(STableColumnName.of(local, type, column_name.name()));
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, STableBody<CompiledLocal>>
  compileInlineTableBody(
    final CompiledGlobal global,
    final STableBodyType<SParsed> body,
    final OptionalInt expected_row_size)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Optional<STypeNameType<CompiledLocal>> type =
        body.type().map(name -> compileTypeName(local, name));

      final Vector<STableRowType<SParsed>> rows = body.rows();
      return Validation.sequence(rows.map(
        row -> compileInlineTableBodyRow(global, row, expected_row_size)))
        .map(Value::toVector)
        .map(c_rows -> STableBody.of(local, type, c_rows.map(Function.identity())));
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, STableRow<CompiledLocal>>
  compileInlineTableBodyRow(
    final CompiledGlobal global,
    final STableRowType<SParsed> row,
    final OptionalInt column_count)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Optional<STypeNameType<CompiledLocal>> type =
        row.type().map(name -> compileTypeName(local, name));

      final Vector<STableCellType<SParsed>> cells = row.cells();
      if (column_count.isPresent()) {
        final int expected_count = column_count.getAsInt();
        final int received_count = cells.size();
        if (received_count != expected_count) {
          return Validation.invalid(
            Vector.of(SCompileError.of(
              row.lexical(),
              ERROR,
              new StringBuilder(128)
                .append(
                  "Number of columns in table row does not match the number declared in the table header.")
                .append(System.lineSeparator())
                .append("  Expected: ")
                .append(expected_count)
                .append(" columns")
                .append(System.lineSeparator())
                .append("  Received: ")
                .append(received_count)
                .append(" columns")
                .append(System.lineSeparator())
                .toString(),
              Optional.empty())));
        }
      }

      return Validation.sequence(cells.map(cell -> compileInlineTableCell(global, cell)))
        .map(Value::toVector)
        .map(c_cells -> STableRow.of(local, type, c_cells.map(Function.identity())));

    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, STableCell<CompiledLocal>>
  compileInlineTableCell(
    final CompiledGlobal global,
    final STableCellType<SParsed> cell)
  {
    final CompiledLocal local = createNewLocal(global);
    global.numbering.push();

    try {
      final Optional<STypeNameType<CompiledLocal>> type =
        cell.type().map(name -> compileTypeName(local, name));

      final Vector<SInlineTableContentType<SParsed>> contents = cell.content();
      return Validation.sequence(contents.map(c -> compileInlineTableContent(global, c)))
        .map(Value::toVector)
        .map(content -> STableCell.of(local, type, content));
    } finally {
      global.numbering.pop();
    }
  }

  private static Validation<Seq<SCompileError>, SListOrdered<CompiledLocal>>
  compileInlineListOrdered(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SListOrdered<SParsed> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      content.type().map(name -> compileTypeName(local, name));

    return Validation.sequence(content.items().map(item -> compileInlineListItem(global, item)))
      .map(Value::toVector)
      .map(items -> SListOrdered.of(local, type, items));
  }

  private static Validation<Seq<SCompileError>, SListUnordered<CompiledLocal>>
  compileInlineListUnordered(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SListUnordered<SParsed> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      content.type().map(name -> compileTypeName(local, name));

    return Validation.sequence(content.items().map(item -> compileInlineListItem(global, item)))
      .map(Value::toVector)
      .map(items -> SListUnordered.of(local, type, items));
  }

  private static Validation<Seq<SCompileError>, SListItemType<CompiledLocal>>
  compileInlineListItem(
    final CompiledGlobal global,
    final SListItemType<SParsed> item)
  {
    final CompiledLocal local = createNewLocal(global);

    return Validation.sequence(item.content().map(content -> compileInlineAny(global, content)))
      .map(Value::toVector)
      .map(items -> SListItem.of(local, items));
  }

  private static Validation<Seq<SCompileError>, STerm<CompiledLocal>>
  compileInlineTerm(
    final CompiledGlobal global,
    final CompiledLocal local,
    final STerm<SParsed> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      content.type().map(name -> compileTypeName(local, name));

    return Validation.sequence(content.text().map(text -> compileInlineTextNewLocal(global, text)))
      .map(Value::toVector)
      .map(texts -> STerm.of(local, type, texts.map(Function.identity())));
  }

  private static Validation<Seq<SCompileError>, SImage<CompiledLocal>>
  compileInlineImage(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SImage<SParsed> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      content.type().map(name -> compileTypeName(local, name));
    final Optional<SImageSizeType<CompiledLocal>> size =
      content.size().map(ssize -> compileSize(global, ssize));

    return Validation.sequence(content.text().map(text -> compileInlineTextNewLocal(global, text)))
      .map(Value::toVector)
      .map(texts -> SImage.of(local, type, content.source(), size, texts.map(Function.identity())));
  }

  private static SImageSizeType<CompiledLocal> compileSize(
    final CompiledGlobal global,
    final SImageSizeType<SParsed> ssize)
  {
    final CompiledLocal local = createNewLocal(global);
    return SImageSize.of(local, ssize.width(), ssize.height());
  }

  private static Validation<Seq<SCompileError>, SText<CompiledLocal>>
  compileInlineTextNewLocal(
    final CompiledGlobal global,
    final STextType<SParsed> text)
  {
    final CompiledLocal text_local = createNewLocal(global);
    return compileInlineText(text_local, text);
  }

  private static Validation<Seq<SCompileError>, SFootnoteReference<CompiledLocal>>
  compileInlineFootnoteReference(
    final CompiledLocal local,
    final SFootnoteReference<SParsed> reference)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      reference.type().map(name -> compileTypeName(local, name));

    return Validation.valid(SFootnoteReference.of(local, type, reference.target()));
  }

  private static Validation<Seq<SCompileError>, SFormalItemReference<CompiledLocal>>
  compileInlineFormalItemReference(
    final CompiledLocal local,
    final SFormalItemReference<SParsed> reference)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      reference.type().map(name -> compileTypeName(local, name));

    return Validation.valid(SFormalItemReference.of(local, type, reference.target()));
  }

  private static Validation<Seq<SCompileError>, SLink<CompiledLocal>>
  compileInlineLink(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SLink<SParsed> link)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      link.type().map(name -> compileTypeName(local, name));

    return Validation.sequence(link.content().map(content -> compileLinkContent(global, content)))
      .map(Value::toVector)
      .map(contents -> SLink.of(local, type, link.target(), contents));
  }

  private static Validation<Seq<SCompileError>, SLinkExternal<CompiledLocal>>
  compileInlineLinkExternal(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SLinkExternal<SParsed> link)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      link.type().map(name -> compileTypeName(local, name));

    return Validation.sequence(link.content().map(content -> compileLinkContent(global, content)))
      .map(Value::toVector)
      .map(contents -> SLinkExternal.of(local, type, link.target(), contents));
  }

  private static Validation<Seq<SCompileError>, SVerbatim<CompiledLocal>>
  compileInlineVerbatim(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SVerbatim<SParsed> verbatim)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      verbatim.type().map(name -> compileTypeName(local, name));

    return compileInlineTextNewLocal(global, verbatim.text())
      .map(c_text -> SVerbatim.of(local, type, c_text));
  }

  private static SParagraph<CompiledLocal>
  compileParagraphFinish(
    final CompiledLocal local,
    final SParagraph<SParsed> paragraph,
    final Vector<SInlineAnyContentType<CompiledLocal>> content)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      paragraph.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      paragraph.id().map(block_id -> compileBlockId(local, block_id));

    final SParagraph<CompiledLocal> result = SParagraph.of(local, type, id, content);
    assignAllInlineParents(content, result);
    return result;
  }

  private static Validation<Seq<SCompileError>, SSectionWithSectionsType<CompiledLocal>>
  compileSectionWithSections(
    final CompiledGlobal global,
    final CompiledLocal local,
    final SSectionWithSectionsType<SParsed> section)
  {
    return Validation.sequence(section.sections().map(child -> compileSection(global, child)))
      .map(Value::toVector)
      .map(cs -> compileSectionWithSectionsFinish(local, section, cs));
  }

  private static SSectionWithSectionsType<CompiledLocal> compileSectionWithSectionsFinish(
    final CompiledLocal local,
    final SSectionWithSectionsType<SParsed> section,
    final Vector<SSectionType<CompiledLocal>> sections)
  {
    final Optional<STypeNameType<CompiledLocal>> type =
      section.type().map(name -> compileTypeName(local, name));
    final Optional<SBlockIDType<CompiledLocal>> id =
      section.id().map(block_id -> compileBlockId(local, block_id));

    final SSectionWithSections<CompiledLocal> result =
      SSectionWithSections.of(
        local,
        type,
        id,
        section.title(),
        section.tableOfContents(),
        sections);

    assignAllParents(sections, result);
    return result;
  }

  private static SBlockIDType<CompiledLocal> compileBlockId(
    final CompiledLocal local,
    final SBlockIDType<SParsed> id)
  {
    return SBlockID.<CompiledLocal>builder()
      .setData(local)
      .setLexical(id.lexical())
      .setValue(id.value())
      .build();
  }

  private static STypeNameType<CompiledLocal> compileTypeName(
    final CompiledLocal local,
    final STypeNameType<SParsed> type)
  {
    return STypeName.<CompiledLocal>builder()
      .setData(local)
      .setLexical(type.lexical())
      .setValue(type.value())
      .build();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> run()
  {
    final CompiledGlobal global = new CompiledGlobal();
    return compileDocument(global, this.document)
      .map(doc -> (SDocument<SCompiledLocalType>) (Object) doc);
  }

  private static final class CompiledGlobal implements SCompiledGlobalType
  {
    private final SContentNumbering numbering;

    CompiledGlobal()
    {
      this.numbering = SContentNumbering.create();
    }
  }

  private static final class CompiledLocal implements SCompiledLocalType
  {
    private final CompiledGlobal global;
    private final SContentNumber number;
    private SBlockContentType<CompiledLocal> parent;

    CompiledLocal(
      final CompiledGlobal in_global,
      final SContentNumber in_number)
    {
      this.global = Objects.requireNonNull(in_global, "global");
      this.number = Objects.requireNonNull(in_number, "number");
    }

    void setParent(
      final SBlockContentType<CompiledLocal> in_parent)
    {
      Preconditions.checkPrecondition(
        this.parent,
        this.parent == null,
        p -> "Parent must be assigned exactly once");

      this.parent = Objects.requireNonNull(in_parent, "parent");
    }

    @Override
    public SContentNumber number()
    {
      return this.number;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SBlockContentType<SCompiledLocalType> parent()
    {
      return
        (SBlockContentType<SCompiledLocalType>)
          (Object)
            Objects.requireNonNull(this.parent, "parent");
    }
  }
}
