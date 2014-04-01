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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
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

public final class SDocumentXHTMLWriterMultiTest
{
  private static final int DOCUMENTATION_PAGES = 47;

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

    @Override public Element onBodyStart(
      final Element body)
    {
      this.on_body_start_called++;
      return null;
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
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
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
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(1, cb.on_head_called);
    Assert.assertEquals(1, cb.on_body_start_called);
    Assert.assertEquals(1, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
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
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, da);
    Assert.assertEquals(
      SDocumentXHTMLWriterMultiTest.DOCUMENTATION_PAGES,
      cb.on_head_called);
    Assert.assertEquals(
      SDocumentXHTMLWriterMultiTest.DOCUMENTATION_PAGES,
      cb.on_body_start_called);
    Assert.assertEquals(
      SDocumentXHTMLWriterMultiTest.DOCUMENTATION_PAGES,
      cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
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
    SDocumentXHTMLWriterMultiTest
      .checkDocument(new Document(new Element("z")));
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
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(9, cb.on_head_called);
    Assert.assertEquals(9, cb.on_body_start_called);
    Assert.assertEquals(9, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
    }
  }

  private static class BodyReplacerTrivial implements
    SDocumentXHTMLWriterCallbacks
  {
    public BodyReplacerTrivial()
    {
      // Nothing
    }

    @Override public void onBodyEnd(
      final Element body)
    {
      // Nothing
    }

    @Override public Element onBodyStart(
      final Element body)
    {
      final Element e = new Element("div", SXHTML.XHTML_URI.toString());
      e.addAttribute(new Attribute("class", null, "new_parent"));
      return e;
    }

    @Override public void onHead(
      final Element head)
    {
      // Nothing
    }
  }

  private static class BodyReplacerExtra implements
    SDocumentXHTMLWriterCallbacks
  {
    public BodyReplacerExtra()
    {
      // Nothing
    }

    @Override public void onBodyEnd(
      final Element body)
    {
      // Nothing
    }

    @Override public Element onBodyStart(
      final Element body)
    {
      final Element ea = new Element("div", SXHTML.XHTML_URI.toString());
      ea.addAttribute(new Attribute("class", null, "new_ancestor"));

      final Element ep = new Element("div", SXHTML.XHTML_URI.toString());
      ep.addAttribute(new Attribute("class", null, "new_parent"));

      ea.appendChild(ep);
      return ep;
    }

    @Override public void onHead(
      final Element head)
    {
      // Nothing
    }
  }

  @SuppressWarnings("static-method") @Test public void testBasicReparent_0()
    throws ConstraintError,
      ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    final URI uri = SDocumentation.getDocumentationXMLLocation();
    final InputStream s = uri.toURL().openStream();
    final Log log = TestUtilities.getLog();
    final SDocument d = SDocumentParser.fromStream(s, uri, log);
    s.close();

    final SADocument da = SAnnotator.document(log, d);
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final BodyReplacerTrivial cb = new BodyReplacerTrivial();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, da);

    for (final String name : dr.keySet()) {
      final Document doc = dr.get(name);

      final XPathContext ns = new XPathContext();
      ns.addNamespace("h", SXHTML.XHTML_URI.toString());
      final Nodes nodes =
        doc
          .query(
            "/h:html/h:body/h:div[@class='new_parent']/h:div[@class='st200_body']",
            ns);
      Assert.assertEquals(1, nodes.size());

      SDocumentXHTMLWriterMultiTest.checkDocument(doc);

      final Serializer sr = new Serializer(System.out);
      sr.setIndent(2);
      sr.setMaxLength(80);
      sr.write(doc);
    }
  }

  @SuppressWarnings("static-method") @Test public void testBasicReparent_1()
    throws ConstraintError,
      ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    final URI uri = SDocumentation.getDocumentationXMLLocation();
    final InputStream s = uri.toURL().openStream();
    final Log log = TestUtilities.getLog();
    final SDocument d = SDocumentParser.fromStream(s, uri, log);
    s.close();

    final SADocument da = SAnnotator.document(log, d);
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final BodyReplacerExtra cb = new BodyReplacerExtra();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, da);

    for (final String name : dr.keySet()) {
      final Document doc = dr.get(name);
      SDocumentXHTMLWriterMultiTest.checkDocument(doc);

      final XPathContext ns = new XPathContext();
      ns.addNamespace("h", SXHTML.XHTML_URI.toString());
      final Nodes nodes =
        doc
          .query(
            "/h:html/h:body/h:div[@class='new_ancestor']/h:div[@class='new_parent']/h:div[@class='st200_body']",
            ns);
      Assert.assertEquals(1, nodes.size());

      final Serializer sr = new Serializer(System.out);
      sr.setIndent(2);
      sr.setMaxLength(80);
      sr.write(doc);
    }
  }
}
