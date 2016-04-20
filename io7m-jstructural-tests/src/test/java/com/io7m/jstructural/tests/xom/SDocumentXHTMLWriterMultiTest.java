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

package com.io7m.jstructural.tests.xom;

import com.io7m.jnull.Nullable;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.tests.annotated.SAnnotatorTest;
import com.io7m.jstructural.xom.SDocumentXHTMLWriterCallbacks;
import com.io7m.jstructural.xom.SDocumentXHTMLWriterMulti;
import com.io7m.jstructural.xom.SXHTML;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SortedMap;

@SuppressWarnings("static-method") public final class SDocumentXHTMLWriterMultiTest
{
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

    @Override public @Nullable Element onBodyStart(
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

    @Override public @Nullable Element onBodyStart(
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

    @Override public @Nullable Element onBodyStart(
      final Element body)
    {
      this.on_body_start_called++;
      return null;
    }

    @Override public void onHead(
      final Element head)
    {
      this.on_head_called++;
    }
  }

  private static final int DOCUMENTATION_PAGES = 46;

  private static void checkDocument(
    final Document dd)
    throws IOException,
      ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final ByteArrayOutputStream o = new ByteArrayOutputStream();
    final Serializer s = new Serializer(o, "UTF-8");
    s.write(dd);
    s.flush();

    final ByteArrayInputStream i = new ByteArrayInputStream(o.toByteArray());
    SXHTML10StrictValidator.fromStreamValidate(i, new URI(dd.getBaseURI()));
  }

  /**
   * Enable a custom URL handler so that XIncludes can use a structuraltest://
   * URL scheme in order to include other files in the test resources.
   */

  @Before public void before()
  {
    System.setProperty(
      "java.protocol.handler.pkgs",
      "com.io7m.jstructural.tests.xom");
  }

  @Test public void testBasic_0()
    throws ValidityException,
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
    Assert.assertEquals(2, cb.on_head_called);
    Assert.assertEquals(2, cb.on_body_start_called);
    Assert.assertEquals(2, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
    }
  }

  @Test public void testBasic_0_frontPage()
    throws ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument d = SAnnotatorTest.annotate("basic-0.xml");
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    writer.setFrontPageName("CUSTOM.XHTML");
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);
    Assert.assertEquals(2, cb.on_head_called);
    Assert.assertEquals(2, cb.on_body_start_called);
    Assert.assertEquals(2, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
    }

    Assert.assertTrue(dr.keySet().contains("CUSTOM.XHTML"));
  }

  @Test public void testBasic_2()
    throws ValidityException,
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
    Assert.assertEquals(2, cb.on_head_called);
    Assert.assertEquals(2, cb.on_body_start_called);
    Assert.assertEquals(2, cb.on_body_end_called);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
    }
  }

  @Test public void testBasicReparent_0()
    throws ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument da = SAnnotatorTest.annotate("documentation.xml");
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

  @Test public void testBasicReparent_1()
    throws ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument da = SAnnotatorTest.annotate("documentation.xml");
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

  @Test public void testBug_151733738209()
    throws Exception
  {
    final SADocument d = SAnnotatorTest.annotate("bug-151733738209.xml");
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);

    for (final String name : dr.keySet()) {
      SDocumentXHTMLWriterMultiTest.checkDocument(dr.get(name));
    }
  }

  @Test public void testBug_cd7bc9304c()
  {
    final SADocument d = SAnnotatorTest.annotate("glowmaps.xml");
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();
    final Callbacks cb = new Callbacks();
    final SortedMap<String, Document> dr = writer.writeDocuments(cb, d);

    Assert.assertEquals(4, dr.size());
  }

  @Test public void testDocumentation_0()
    throws IOException,
      ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    final SADocument da = SAnnotatorTest.annotate("documentation.xml");
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

  @Test(expected = SAXException.class) public void testFailure()
    throws ValidityException,
      IOException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      URISyntaxException
  {
    SDocumentXHTMLWriterMultiTest
      .checkDocument(new Document(new Element("z")));
  }

  @Test public void testLarge_0()
    throws ValidityException,
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
}
