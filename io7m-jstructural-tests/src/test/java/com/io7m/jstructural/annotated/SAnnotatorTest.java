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

import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SAFootnote;
import com.io7m.jstructural.annotated.SAFootnoteContent;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAIDMapReadable;
import com.io7m.jstructural.annotated.SALinkExternal;
import com.io7m.jstructural.annotated.SAParagraph;
import com.io7m.jstructural.annotated.SASectionWithParagraphs;
import com.io7m.jstructural.annotated.SASectionWithSubsections;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SAText;
import com.io7m.jstructural.annotated.SAnnotator;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.xom.SDocumentParserTest;
import com.io7m.jstructural.xom.TestUtilities;

public final class SAnnotatorTest
{
  /**
   * Enable a custom URL handler so that XIncludes can use a structuraltest://
   * URL scheme in order to include other files in the test resources.
   */

  @SuppressWarnings("static-method") @Before public void before()
  {
    System.setProperty(
      "java.protocol.handler.pkgs",
      "com.io7m.jstructural.xom");
  }

  public static @Nonnull SADocument annotate(
    final @Nonnull String name)
    throws ConstraintError
  {
    final SDocument d = SDocumentParserTest.roundTripParse(name);
    return SAnnotator.document(TestUtilities.getLog(), d);
  }

  @SuppressWarnings("static-method") @Test public void testAnnotate_0()
    throws ConstraintError
  {
    final SADocument a = SAnnotatorTest.annotate("basic-0.xml");
    final SAIDMapReadable m = a.getIDMappings();
    Assert.assertEquals(2, m.size());
    Assert
      .assertTrue(m.get(new SAID("section_0")) instanceof SASectionWithParagraphs);
    Assert.assertTrue(m.get(new SAID("paragraph_0")) instanceof SAParagraph);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testAnnotateResolved_0()
      throws ConstraintError
  {
    final SADocument a = SAnnotatorTest.annotate("resolve-0.xml");
    final SAIDMapReadable m = a.getIDMappings();
    Assert.assertEquals(2, m.size());
    Assert
      .assertTrue(m.get(new SAID("section_0")) instanceof SASectionWithParagraphs);
    Assert.assertTrue(m.get(new SAID("paragraph_0")) instanceof SAParagraph);
  }

  @SuppressWarnings("static-method") @Test public void testAnnotate_1()
    throws ConstraintError
  {
    final SADocument a = SAnnotatorTest.annotate("basic-1.xml");
    final SAIDMapReadable m = a.getIDMappings();
    Assert.assertEquals(4, m.size());
    Assert
      .assertTrue(m.get(new SAID("section_0")) instanceof SASectionWithParagraphs);
    Assert.assertTrue(m.get(new SAID("paragraph_0")) instanceof SAParagraph);
    Assert.assertTrue(m.get(new SAID("paragraph_1")) instanceof SAParagraph);
    Assert.assertTrue(m.get(new SAID("paragraph_2")) instanceof SAParagraph);
  }

  @SuppressWarnings("static-method") @Test public void testAnnotate_2()
    throws ConstraintError
  {
    final SADocument a = SAnnotatorTest.annotate("basic-2.xml");
    final SAIDMapReadable m = a.getIDMappings();
    Assert.assertEquals(5, m.size());
    Assert
      .assertTrue(m.get(new SAID("section_0")) instanceof SASectionWithSubsections);
    Assert
      .assertTrue(m.get(new SAID("subsection_0")) instanceof SASubsection);
    Assert.assertTrue(m.get(new SAID("paragraph_0")) instanceof SAParagraph);
    Assert.assertTrue(m.get(new SAID("paragraph_1")) instanceof SAParagraph);
    Assert.assertTrue(m.get(new SAID("paragraph_2")) instanceof SAParagraph);
  }

  @SuppressWarnings("static-method") @Test public void testAnnotateLarge_0()
    throws ConstraintError
  {
    final SADocument a = SAnnotatorTest.annotate("jaux-documentation.xml");
    final SAIDMapReadable m = a.getIDMappings();
    final List<SAFootnote> f = a.getFootnotes();
    Assert.assertEquals(0, m.size());
    Assert.assertEquals(1, f.size());

    final SAFootnote f0 = f.get(0);
    final SNonEmptyList<SAFootnoteContent> f0c = f0.getContent();
    Assert.assertEquals(3, f0c.getElements().size());
    Assert.assertTrue(f0c.getElements().get(0) instanceof SAText);
    Assert.assertTrue(f0c.getElements().get(1) instanceof SALinkExternal);
    Assert.assertTrue(f0c.getElements().get(2) instanceof SAText);
  }
}
