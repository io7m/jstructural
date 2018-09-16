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

package com.io7m.jstructural.tests.ast;

import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SContentNumber;
import com.io7m.jstructural.ast.SDocument;
import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SImageSize;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SLinkExternal;
import com.io7m.jstructural.ast.SListOrdered;
import com.io7m.jstructural.ast.SListUnordered;
import com.io7m.jstructural.ast.SParagraph;
import com.io7m.jstructural.ast.SSectionWithSections;
import com.io7m.jstructural.ast.SSectionWithSubsectionContent;
import com.io7m.jstructural.ast.SSectionWithSubsections;
import com.io7m.jstructural.ast.STable;
import com.io7m.jstructural.ast.STableBody;
import com.io7m.jstructural.ast.STableCell;
import com.io7m.jstructural.ast.STableColumnName;
import com.io7m.jstructural.ast.STableHeader;
import com.io7m.jstructural.ast.STableRow;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.jstructural.ast.SVerbatim;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class SModelTest
{
  @Test
  public void testBlockID()
  {
    EqualsVerifier.forClass(SBlockID.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "value")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testContentNumber()
  {
    EqualsVerifier.forClass(SContentNumber.class)
      .withNonnullFields("components", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testDocument()
  {
    EqualsVerifier.forClass(SDocument.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "sections", "title")
      .verify();
  }

  @Test
  public void testFootnoteReference()
  {
    EqualsVerifier.forClass(SFootnoteReference.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "target")
      .verify();
  }

  @Test
  public void testFormalItemReference()
  {
    EqualsVerifier.forClass(SFormalItemReference.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "target")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testImage()
  {
    EqualsVerifier.forClass(SImage.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "initShim", "type", "source", "size", "text")
      .verify();
  }

  @Test
  public void testImageSize()
  {
    EqualsVerifier.forClass(SImageSize.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "width", "height")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testLinkExternal()
  {
    EqualsVerifier.forClass(SLinkExternal.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "target", "content", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testLink()
  {
    EqualsVerifier.forClass(SLink.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "target", "content", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testListOrdered()
  {
    EqualsVerifier.forClass(SListOrdered.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "items", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testListUnordered()
  {
    EqualsVerifier.forClass(SListUnordered.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "items", "initShim")
      .verify();
  }

  @Test
  public void testVerbatim()
  {
    EqualsVerifier.forClass(SVerbatim.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "text")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testParagraph()
  {
    EqualsVerifier.forClass(SParagraph.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "id", "content", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testSectionWithSections()
  {
    EqualsVerifier.forClass(SSectionWithSections.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "id", "title", "sections", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testSectionWithSubsections()
  {
    EqualsVerifier.forClass(SSectionWithSubsections.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "id", "title", "subsections", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testSectionWithSubsectionContent()
  {
    EqualsVerifier.forClass(SSectionWithSubsectionContent.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "id", "title", "content", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testTableBody()
  {
    EqualsVerifier.forClass(STableBody.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "rows", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testTableCell()
  {
    EqualsVerifier.forClass(STableCell.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "content", "initShim")
      .verify();
  }

  @Test
  public void testTableColumnName()
  {
    EqualsVerifier.forClass(STableColumnName.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "name")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testTableHeader()
  {
    EqualsVerifier.forClass(STableHeader.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "names", "initShim")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testTableRow()
  {
    EqualsVerifier.forClass(STableRow.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "cells", "initShim")
      .verify();
  }

  @Test
  public void testTable()
  {
    EqualsVerifier.forClass(STable.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "body")
      .verify();
  }

  @Test
  @Disabled("Disabled due to a problem with equalsverifier")
  public void testTerm()
  {
    EqualsVerifier.forClass(STerm.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "type", "text", "initShim")
      .verify();
  }

  @Test
  public void testText()
  {
    EqualsVerifier.forClass(SText.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "text")
      .verify();
  }

  @Test
  public void testTypeName()
  {
    EqualsVerifier.forClass(STypeName.class)
      .withIgnoredFields("lexical", "data")
      .withNonnullFields("lexical", "data", "value")
      .verify();
  }
}
