/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.ast;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;
import com.io7m.jstructural.annotations.SImmutableStyleType;
import io.vavr.collection.Vector;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.net.URI;
import java.util.Optional;

/**
 * The type of AST elements.
 *
 * @param <T> The type of data associated with the AST
 */

@SImmutableStyleType
@VavrEncodingEnabled
public interface SModelType<T> extends LexicalType<URI>
{
  /**
   * @return Data associated with the AST element
   */

  T data();

  /**
   * The type of type names.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STypeNameType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The actual name value
     */

    @Value.Parameter
    String value();

    /**
     * Check preconditions for the type.
     */

    @Value.Check
    default void checkPreconditions()
    {
      Preconditions.checkPrecondition(
        this.value(),
        STypeNames.isValid(this.value()),
        s -> "Type name must be valid");
    }
  }

  /**
   * The type of block IDs.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SBlockIDType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The actual name value
     */

    @Value.Parameter
    String value();

    /**
     * Check preconditions for the type.
     */

    @Value.Check
    default void checkPreconditions()
    {
      Preconditions.checkPrecondition(
        this.value(),
        SBlockIDs.isValid(this.value()),
        s -> "Block ID must be valid");
    }
  }

  /**
   * The type of inline link content.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SInlineLinkContentType<T> extends SInlineAnyContentType<T>
  {
    /**
     * @return The precise kind of inline link content
     */

    InlineLinkKind inlineLinkKind();

    /**
     * The precise kind of inline link content.
     */

    enum InlineLinkKind
    {
      /**
       * @see STextType
       */

      INLINE_LINK_TEXT,

      /**
       * @see SImageType
       */

      INLINE_LINK_IMAGE
    }
  }

  /**
   * The type of inline content.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SInlineAnyContentType<T> extends SContentType<T>
  {
    @Override
    default ContentKind contentKind()
    {
      return ContentKind.CONTENT_INLINE_ANY;
    }

    /**
     * @return The precise kind of inline content
     */

    InlineKind inlineKind();

    /**
     * The precise kind of inline content.
     */

    enum InlineKind
    {
      /**
       * @see STextType
       */

      INLINE_TEXT,

      /**
       * @see STermType
       */

      INLINE_TERM,

      /**
       * @see SImageType
       */

      INLINE_IMAGE,

      /**
       * @see SLinkType
       */

      INLINE_LINK,

      /**
       * @see SLinkExternalType
       */

      INLINE_LINK_EXTERNAL,

      /**
       * @see SFootnoteReferenceType
       */

      INLINE_FOOTNOTE_REFERENCE,

      /**
       * @see SFormalItemReferenceType
       */

      INLINE_FORMAL_ITEM_REFERENCE,

      /**
       * @see SVerbatimType
       */

      INLINE_VERBATIM,

      /**
       * @see SListOrderedType
       */

      INLINE_LIST_ORDERED,

      /**
       * @see SListUnorderedType
       */

      INLINE_LIST_UNORDERED,

      /**
       * @see STableType
       */

      INLINE_TABLE,
    }
  }

  /**
   * The type of inline content that may appear in table cells.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SInlineTableContentType<T> extends SInlineAnyContentType<T>
  {
    /**
     * @return The precise kind of inline content
     */

    InlineTableKind inlineTableKind();

    /**
     * The precise kind of inline content.
     */

    enum InlineTableKind
    {
      /**
       * @see STextType
       */

      INLINE_TABLE_TEXT,

      /**
       * @see STermType
       */

      INLINE_TABLE_TERM,

      /**
       * @see SImageType
       */

      INLINE_TABLE_IMAGE,

      /**
       * @see SLinkType
       */

      INLINE_TABLE_LINK,

      /**
       * @see SLinkExternalType
       */

      INLINE_TABLE_LINK_EXTERNAL,

      /**
       * @see SFootnoteReferenceType
       */

      INLINE_TABLE_FOOTNOTE_REFERENCE,

      /**
       * @see SFormalItemReferenceType
       */

      INLINE_TABLE_FORMAL_ITEM_REFERENCE,

      /**
       * @see SVerbatimType
       */

      INLINE_TABLE_VERBATIM,

      /**
       * @see SListOrderedType
       */

      INLINE_TABLE_LIST_ORDERED,

      /**
       * @see SListUnorderedType
       */

      INLINE_TABLE_LIST_UNORDERED
    }
  }

  /**
   * The type of plain inline text.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STextType<T>
    extends SInlineLinkContentType<T>, SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_TEXT;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_TEXT;
    }

    @Override
    default InlineLinkKind inlineLinkKind()
    {
      return InlineLinkKind.INLINE_LINK_TEXT;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The actual text
     */

    @Value.Parameter
    String text();
  }

  /**
   * The type of terms.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STermType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_TERM;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_TERM;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The actual text
     */

    @Value.Parameter
    Vector<STextType<T>> text();
  }

  /**
   * The type of image sizes.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SImageSizeType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The image width
     */

    @Value.Parameter
    int width();

    /**
     * @return The image height
     */

    @Value.Parameter
    int height();
  }

  /**
   * The type of images.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SImageType<T>
    extends SInlineLinkContentType<T>, SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_IMAGE;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_IMAGE;
    }

    @Override
    default InlineLinkKind inlineLinkKind()
    {
      return InlineLinkKind.INLINE_LINK_IMAGE;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The image URI
     */

    @Value.Parameter
    URI source();

    /**
     * @return The image size
     */

    @Value.Parameter
    Optional<SImageSizeType<T>> size();

    /**
     * @return The actual text
     */

    @Value.Parameter
    Vector<STextType<T>> text();
  }

  /**
   * The type of verbatim sections.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SVerbatimType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_VERBATIM;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_VERBATIM;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The actual text
     */

    @Value.Parameter
    STextType<T> text();
  }

  /**
   * The type of internal links.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SLinkType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_LINK;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_LINK;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The link target
     */

    @Value.Parameter
    String target();

    /**
     * @return The link content
     */

    @Value.Parameter
    Vector<SInlineLinkContentType<T>> content();
  }

  /**
   * The type of external links.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SLinkExternalType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_LINK_EXTERNAL;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_LINK_EXTERNAL;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The link target
     */

    @Value.Parameter
    URI target();

    /**
     * @return The link content
     */

    @Value.Parameter
    Vector<SInlineLinkContentType<T>> content();
  }

  /**
   * The type of footnote references.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SFootnoteReferenceType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_FOOTNOTE_REFERENCE;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_FOOTNOTE_REFERENCE;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The target footnote
     */

    @Value.Parameter
    String target();
  }

  /**
   * The type of formal item references.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SFormalItemReferenceType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_FORMAL_ITEM_REFERENCE;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_FORMAL_ITEM_REFERENCE;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The target footnote
     */

    @Value.Parameter
    String target();
  }

  /**
   * The type of ordered lists.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SListOrderedType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_LIST_ORDERED;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_LIST_ORDERED;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The target footnote
     */

    @Value.Parameter
    Vector<SListItemType<T>> items();
  }

  /**
   * The type of unordered lists.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SListUnorderedType<T> extends SInlineTableContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineTableKind inlineTableKind()
    {
      return InlineTableKind.INLINE_TABLE_LIST_UNORDERED;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_LIST_UNORDERED;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The target footnote
     */

    @Value.Parameter
    Vector<SListItemType<T>> items();
  }

  /**
   * The type of list items.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SListItemType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The content
     */

    @Value.Parameter
    Vector<SInlineAnyContentType<T>> content();
  }

  /**
   * The type of table column names.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableColumnNameType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The column name
     */

    @Value.Parameter
    String name();
  }

  /**
   * The type of table headers.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableHeaderType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The column names
     */

    @Value.Parameter
    Vector<STableColumnNameType<T>> names();
  }

  /**
   * The type of table cells.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableCellType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The cells
     */

    @Value.Parameter
    Vector<SInlineTableContentType<T>> content();
  }

  /**
   * The type of table rows.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableRowType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The cells
     */

    @Value.Parameter
    Vector<STableCellType<T>> cells();
  }

  /**
   * The type of table bodies.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableBodyType<T> extends SModelType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The column names
     */

    @Value.Parameter
    Vector<STableRowType<T>> rows();
  }

  /**
   * The type of table bodies.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface STableType<T> extends SInlineAnyContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default InlineKind inlineKind()
    {
      return InlineKind.INLINE_TABLE;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The header
     */

    @Value.Parameter
    Optional<STableHeaderType<T>> header();

    /**
     * @return The body
     */

    @Value.Parameter
    STableBodyType<T> body();

    /**
     * Check preconditions for the type.
     */

    @Value.Check
    default void checkPreconditions()
    {
      if (this.header().isPresent()) {
        final STableHeaderType<T> h = this.header().get();
        final int size = h.names().size();
        for (final STableRowType<T> row : this.body().rows()) {
          Preconditions.checkPreconditionI(
            row.cells().size(),
            row.cells().size() == size,
            s -> "All table rows must contain " + size + " columns");
        }
      }
    }
  }

  /**
   * The type of subsection content.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SSubsectionContentType<T> extends SBlockContentType<T>
  {
    /**
     * @return The precise kind of subsection content
     */

    SubsectionContentKind subsectionContentKind();

    @Override
    default BlockKind blockKind()
    {
      return BlockKind.BLOCK_SUBSECTION_CONTENT;
    }

    /**
     * The precise kind of subsection content.
     */

    enum SubsectionContentKind
    {
      /**
       * @see SParagraphType
       */

      SUBSECTION_PARAGRAPH,

      /**
       * @see SFormalItemType
       */

      SUBSECTION_FORMAL_ITEM,

      /**
       * @see SFootnoteType
       */

      SUBSECTION_FOOTNOTE
    }
  }

  /**
   * The type of paragraphs.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SParagraphType<T> extends SSubsectionContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SubsectionContentKind subsectionContentKind()
    {
      return SubsectionContentKind.SUBSECTION_PARAGRAPH;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The unique identifier
     */

    @Value.Parameter
    Optional<SBlockIDType<T>> id();
  }

  /**
   * The type of formal items.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SFormalItemType<T> extends SSubsectionContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SubsectionContentKind subsectionContentKind()
    {
      return SubsectionContentKind.SUBSECTION_FORMAL_ITEM;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The unique identifier
     */

    @Value.Parameter
    Optional<SBlockIDType<T>> id();

    /**
     * @return The title
     */

    @Value.Parameter
    String title();
  }

  /**
   * The type of footnotes.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SFootnoteType<T> extends SSubsectionContentType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SubsectionContentKind subsectionContentKind()
    {
      return SubsectionContentKind.SUBSECTION_FOOTNOTE;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The unique identifier
     */

    @Value.Parameter
    String id();
  }

  /**
   * The type of subsections.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SSubsectionType<T> extends SBlockContentType<T>
  {
    @Override
    default BlockKind blockKind()
    {
      return BlockKind.BLOCK_SUBSECTION;
    }

    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The type
     */

    @Value.Parameter
    Optional<STypeNameType<T>> type();

    /**
     * @return The unique identifier
     */

    @Value.Parameter
    Optional<SBlockIDType<T>> id();

    /**
     * @return The title
     */

    @Value.Parameter
    String title();

    /**
     * @return The subsection content
     */

    @Value.Parameter
    Vector<SSubsectionContentType<T>> content();
  }

  /**
   * The type of sections
   *
   * @param <T> The type of data associated with the AST
   */

  interface SSectionType<T> extends SBlockContentType<T>
  {
    /**
     * @return The precise kind of section
     */

    SectionKind sectionKind();

    @Override
    default BlockKind blockKind()
    {
      return BlockKind.BLOCK_SECTION;
    }

    /**
     * @return The type
     */

    Optional<STypeNameType<T>> type();

    /**
     * @return The unique identifier
     */

    Optional<SBlockIDType<T>> id();

    /**
     * @return The section title
     */

    String title();

    /**
     * The precise kind of section.
     */

    enum SectionKind
    {
      /**
       * @see SSectionWithSectionsType
       */

      SECTION_WITH_SECTIONS,

      /**
       * @see SSectionWithSubsectionsType
       */

      SECTION_WITH_SUBSECTIONS,

      /**
       * @see SSectionWithSubsectionContentType
       */

      SECTION_WITH_SUBSECTION_CONTENT
    }
  }

  /**
   * The type of sections that contain nested sections.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SSectionWithSectionsType<T> extends SSectionType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SectionKind sectionKind()
    {
      return SectionKind.SECTION_WITH_SECTIONS;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    @Override
    @Value.Parameter
    Optional<STypeNameType<T>> type();

    @Override
    @Value.Parameter
    Optional<SBlockIDType<T>> id();

    @Override
    @Value.Parameter
    String title();

    /**
     * @return The nested sections
     */

    @Value.Parameter
    Vector<SSectionType<T>> sections();
  }

  /**
   * The type of sections that contain nested subsections.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SSectionWithSubsectionsType<T> extends SSectionType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SectionKind sectionKind()
    {
      return SectionKind.SECTION_WITH_SUBSECTIONS;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    @Override
    @Value.Parameter
    Optional<STypeNameType<T>> type();

    @Override
    @Value.Parameter
    Optional<SBlockIDType<T>> id();

    @Override
    @Value.Parameter
    String title();

    /**
     * @return The nested subsections
     */

    @Value.Parameter
    Vector<SSubsectionType<T>> subsections();
  }

  /**
   * The type of sections that contain nested subsection content.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SSectionWithSubsectionContentType<T> extends SSectionType<T>
  {
    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    default SectionKind sectionKind()
    {
      return SectionKind.SECTION_WITH_SUBSECTION_CONTENT;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    @Override
    @Value.Parameter
    Optional<STypeNameType<T>> type();

    @Override
    @Value.Parameter
    Optional<SBlockIDType<T>> id();

    @Override
    @Value.Parameter
    String title();

    /**
     * @return The nested subsection content
     */

    @Value.Parameter
    Vector<SSubsectionContentType<T>> content();
  }

  /**
   * The type of sections that contain nested subsection content.
   *
   * @param <T> The type of data associated with the AST
   */

  @SImmutableStyleType
  @Value.Immutable
  interface SDocumentType<T> extends SBlockContentType<T>
  {
    @Override
    default BlockKind blockKind()
    {
      return BlockKind.BLOCK_DOCUMENT;
    }

    @Value.Auxiliary
    @Value.Default
    @Override
    default LexicalPosition<URI> lexical()
    {
      return SLexicalDefaults.DEFAULT_POSITION;
    }

    @Override
    @Value.Auxiliary
    @Value.Parameter
    T data();

    /**
     * @return The document's sections
     */

    @Value.Parameter
    Vector<SSectionType<T>> sections();

    /**
     * @return The document title
     */

    @Value.Parameter
    String title();
  }

  /**
   * The type of block content.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SBlockContentType<T> extends SContentType<T>
  {
    @Override
    default ContentKind contentKind()
    {
      return ContentKind.CONTENT_BLOCK;
    }

    /**
     * @return The precise kind of block content
     */

    BlockKind blockKind();

    /**
     * The precise kind of block content.
     */

    enum BlockKind
    {
      /**
       * @see SSubsectionContentType
       */

      BLOCK_SUBSECTION_CONTENT,

      /**
       * @see SSubsectionType
       */

      BLOCK_SUBSECTION,

      /**
       * @see SSectionType
       */

      BLOCK_SECTION,

      /**
       * @see SDocumentType
       */

      BLOCK_DOCUMENT,
    }
  }

  /**
   * The type of block content.
   *
   * @param <T> The type of data associated with the AST
   */

  interface SContentType<T> extends SModelType<T>
  {
    /**
     * @return The precise kind of content
     */

    ContentKind contentKind();

    /**
     * The precise kind of content.
     */

    enum ContentKind
    {
      /**
       * @see SInlineAnyContentType
       */

      CONTENT_INLINE_ANY,

      /**
       * @see SBlockContentType
       */

      CONTENT_BLOCK
    }
  }
}
