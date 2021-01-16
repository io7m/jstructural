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

import java.util.Optional;

/**
 * The type of sections
 *
 * @param <T> The type of data associated with the AST
 */

public interface SSectionType<T> extends SBlockContentType<T>, STypeableType<T>
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
   * @return The unique identifier
   */

  Optional<SBlockIDType<T>> id();

  /**
   * @return The section title
   */

  String title();

  /**
   * @return {@code true} iff this section should get a generated table of contents
   */

  boolean tableOfContents();

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