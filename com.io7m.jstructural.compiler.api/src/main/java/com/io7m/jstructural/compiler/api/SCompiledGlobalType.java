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

package com.io7m.jstructural.compiler.api;

import com.io7m.jstructural.ast.SBlockContentType;
import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SBlockIDType;
import com.io7m.jstructural.ast.SFootnoteType;
import com.io7m.jstructural.ast.SFormalItemType;
import com.io7m.jstructural.ast.SParagraphType;
import com.io7m.jstructural.ast.SSectionType;
import com.io7m.jstructural.ast.SSubsectionType;

import java.math.BigInteger;
import java.util.Objects;

/**
 * The type of compiled context global to the document.
 */

public interface SCompiledGlobalType
{
  /**
   * @param footnote The footnote
   *
   * @return The index of the given footnote
   */

  BigInteger footnoteIndexOf(
    SFootnoteType<SCompiledLocalType> footnote);

  /**
   * Find the block for the given ID.
   *
   * @param block_id The block ID
   *
   * @return The block
   */

  default SBlockContentType<SCompiledLocalType> findBlockForID(
    final SBlockID<?> block_id)
  {
    return this.findBlockFor(Objects.requireNonNull(block_id, "block_id").value());
  }

  /**
   * Find the block for the given ID.
   *
   * @param block_id The block ID
   *
   * @return The block
   */

  SBlockContentType<SCompiledLocalType> findBlockFor(
    String block_id);

  /**
   * Find the section with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The section
   */

  default SSectionType<SCompiledLocalType> findSectionFor(
    final String block_id)
  {
    final SBlockContentType<SCompiledLocalType> block = this.findBlockFor(block_id);
    if (block instanceof SSectionType) {
      return (SSectionType<SCompiledLocalType>) block;
    }

    throw new IllegalArgumentException(unexpectedType(block, SSectionType.class));
  }

  /**
   * Find the subsection with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The subsection
   */

  default SSubsectionType<SCompiledLocalType> findSubsectionFor(
    final String block_id)
  {
    final SBlockContentType<SCompiledLocalType> block = this.findBlockFor(block_id);
    if (block instanceof SSubsectionType) {
      return (SSubsectionType<SCompiledLocalType>) block;
    }

    throw new IllegalArgumentException(unexpectedType(block, SSubsectionType.class));
  }

  /**
   * Find the paragraph with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The paragraph
   */

  default SParagraphType<SCompiledLocalType> findParagraphFor(
    final String block_id)
  {
    final SBlockContentType<SCompiledLocalType> block = this.findBlockFor(block_id);
    if (block instanceof SParagraphType) {
      return (SParagraphType<SCompiledLocalType>) block;
    }

    throw new IllegalArgumentException(unexpectedType(block, SParagraphType.class));
  }

  /**
   * Find the footnote with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The footnote
   */

  default SFootnoteType<SCompiledLocalType> findFootnoteFor(
    final String block_id)
  {
    final SBlockContentType<SCompiledLocalType> block = this.findBlockFor(block_id);
    if (block instanceof SFootnoteType) {
      return (SFootnoteType<SCompiledLocalType>) block;
    }

    throw new IllegalArgumentException(unexpectedType(block, SFootnoteType.class));
  }

  /**
   * Find the formal item with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The formal item
   */

  default SFormalItemType<SCompiledLocalType> findFormalItemFor(
    final String block_id)
  {
    final SBlockContentType<SCompiledLocalType> block = this.findBlockFor(block_id);
    if (block instanceof SFormalItemType) {
      return (SFormalItemType<SCompiledLocalType>) block;
    }

    throw new IllegalArgumentException(unexpectedType(block, SFormalItemType.class));
  }

  /**
   * Find the section with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The section
   */

  default SSectionType<SCompiledLocalType> findSectionForID(
    final SBlockIDType<SCompiledLocalType> block_id)
  {
    return this.findSectionFor(block_id.value());
  }

  /**
   * Find the subsection with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The subsection
   */

  default SSubsectionType<SCompiledLocalType> findSubsectionForID(
    final SBlockIDType<SCompiledLocalType> block_id)
  {
    return this.findSubsectionFor(block_id.value());
  }

  /**
   * Find the paragraph with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The paragraph
   */

  default SParagraphType<SCompiledLocalType> findParagraphForID(
    final SBlockIDType<SCompiledLocalType> block_id)
  {
    return this.findParagraphFor(block_id.value());
  }

  /**
   * Find the footnote with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The footnote
   */

  default SFootnoteType<SCompiledLocalType> findFootnoteForID(
    final SBlockIDType<SCompiledLocalType> block_id)
  {
    return this.findFootnoteFor(block_id.value());
  }

  /**
   * Find the formal item with the given ID.
   *
   * @param block_id The block ID
   *
   * @return The formal item
   */

  default SFormalItemType<SCompiledLocalType> findFormalItemForID(
    final SBlockIDType<SCompiledLocalType> block_id)
  {
    return this.findFormalItemFor(block_id.value());
  }

  /**
   * Produce an error message indicating an unexpected type.
   *
   * @param block The block
   * @param type  The type
   *
   * @return An error message
   */

  private static String unexpectedType(
    final SBlockContentType<SCompiledLocalType> block,
    final Class<?> type)
  {
    return new StringBuilder(128)
      .append("Block does not have the expected type.")
      .append(System.lineSeparator())
      .append("  Expected: ")
      .append(type.getCanonicalName())
      .append(System.lineSeparator())
      .append("  Received: ")
      .append(block.getClass().getCanonicalName())
      .append(System.lineSeparator())
      .toString();
  }
}
