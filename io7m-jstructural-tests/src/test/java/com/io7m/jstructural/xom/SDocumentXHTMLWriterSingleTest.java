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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SortedMap;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.InclusionLoopException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SAnnotator;
import com.io7m.jstructural.annotated.SAnnotatorTest;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.documentation.SDocumentation;

public final class SDocumentXHTMLWriterSingleTest
{
  private static final class Callbacks implements
    SDocumentXHTMLWriterCallbacks
  {
    int on_body_end_called;
    int on_body_start_called;
    int on_head_called;

    public Callbacks()
    {
      this.on_head_called = 0;
      this.on_body_end_called = 0;
      this.on_body_start_called = 0;
    }

    @Override public void onBodyEnd(
      final Element body)
    {
      this.on_body_end_called++;
    }

    @Override public void onBodyStart(
      final Element body)
    {
      this.on_body_start_called++;
    }

    @Override public void onHead(
      final @Nonnull Element head)
    {
      this.on_head_called++;
    }
  }

  private static void checkDocument(
    final @Nonnull Document dd)
    throws IOException,
      ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException,
      ConstraintError
  {
    final ByteArrayOutputStream o = new ByteArrayOutputStream();
    final Serializer s = new Serializer(o, "UTF-8");
    s.write(dd);
    s.flush();

    final ByteArrayInputStream i = new ByteArrayInputStream(o.toByteArray());
    SXHTML10StrictValidator.fromStreamValidate(
      i,
      new URI(dd.getBaseURI()),
      TestUtilities.getLog());
  }

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

  @SuppressWarnings("static-method") @Test public void testBasic_0()
    throws ConstraintError,
      ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument d = SAnnotatorTest.annotate("basic-0.xml");
    final SDocumentXHTMLWriterSingle writer =
      new SDocumentXHTMLWriterSingle();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterSingleTest.checkDocument(dr.get(name));
    }
  }

  @SuppressWarnings("static-method") @Test public void testBasic_2()
    throws ConstraintError,
      ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument d = SAnnotatorTest.annotate("basic-2.xml");
    final SDocumentXHTMLWriterSingle writer =
      new SDocumentXHTMLWriterSingle();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterSingleTest.checkDocument(dr.get(name));
    }
  }

  @SuppressWarnings("static-method") @Test public void testDocumentation_0()
    throws ConstraintError,
      IOException,
      ValidityException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException,
      XIncludeException
  {
    final URI uri = SDocumentation.getDocumentationXMLLocation();
    final InputStream s = uri.toURL().openStream();

    final Log log = TestUtilities.getLog();
    final SDocument d = SDocumentParser.fromStream(s, uri, log);
    s.close();

    final SADocument da = SAnnotator.document(log, d);
    final SDocumentXHTMLWriterSingle writer =
      new SDocumentXHTMLWriterSingle();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, da);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterSingleTest.checkDocument(dr.get(name));
    }
  }

  /**
   * Ensure that validation is working in the test suite. Try to validate
   * something that is certainly not XHTML 1.0 Strict.
   */

  @SuppressWarnings("static-method") @Test(expected = SAXException.class) public
    void
    testFailure()
      throws ValidityException,
        IOException,
        SAXException,
        ParserConfigurationException,
        ParsingException,
        URISyntaxException,
        ConstraintError
  {
    SDocumentXHTMLWriterSingleTest.checkDocument(new Document(
      new Element("z")));
  }

  @SuppressWarnings("static-method") @Test public void testLarge_0()
    throws ConstraintError,
      ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument d = SAnnotatorTest.annotate("jaux-documentation.xml");
    final SDocumentXHTMLWriterSingle writer =
      new SDocumentXHTMLWriterSingle();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterSingleTest.checkDocument(dr.get(name));
    }
  }
}
