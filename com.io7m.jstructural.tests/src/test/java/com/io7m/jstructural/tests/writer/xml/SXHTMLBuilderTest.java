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

package com.io7m.jstructural.tests.writer.xml;

import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SContentNumbers;
import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFootnoteType;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SFormalItemType;
import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SImageSize;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SLinkExternal;
import com.io7m.jstructural.ast.SListItem;
import com.io7m.jstructural.ast.SListOrdered;
import com.io7m.jstructural.ast.SListUnordered;
import com.io7m.jstructural.ast.SParagraph;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.jstructural.ast.SVerbatim;
import com.io7m.jstructural.compiler.api.SCompiledGlobalType;
import com.io7m.jstructural.compiler.api.SCompiledLocalType;
import com.io7m.jstructural.writer.xml.SXHTMLBuilder;
import com.io7m.jstructural.writer.xml.SXHTMLLink;
import com.io7m.jstructural.writer.xml.SXHTMLLinkProviderType;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class SXHTMLBuilderTest
{
  private static final Logger LOG = LoggerFactory.getLogger(SXHTMLBuilderTest.class);

  @Test
  public void testText()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SXHTMLBuilder xhtml = SXHTMLBuilder.create();
    final SText<SCompiledLocalType> text = SText.of(local, "Hello.");
    final Text xt = xhtml.text(text);

    LOG.debug("xhtml: {}", xt);
    Assertions.assertEquals("Hello.", xt.getWholeText());
  }

  @Test
  public void testTerm()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final STerm<SCompiledLocalType> term =
      STerm.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        Vector.of(SText.of(local, "Hello.")));

    final Element xt = xhtml.term(term);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("span", xt.getLocalName());
    Assertions.assertEquals("st_term t", xt.getAttribute("class"));
    Assertions.assertEquals("Hello.", xt.getTextContent());
  }

  @Test
  public void testImage()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SImage<SCompiledLocalType> image =
      SImage.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        URI.create("http://www.example.com"),
        Optional.of(SImageSize.of(local, 300, 400)),
        Vector.of(SText.of(local, "Hello.")));

    final Element xt = xhtml.image(image);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("img", xt.getLocalName());
    Assertions.assertEquals("http://www.example.com", xt.getAttribute("href"));
    Assertions.assertEquals("300", xt.getAttribute("width"));
    Assertions.assertEquals("400", xt.getAttribute("height"));
    Assertions.assertEquals("st_image t", xt.getAttribute("class"));
    Assertions.assertEquals("Hello.", xt.getTextContent());
  }

  @Test
  public void testLinkExternal()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SLinkExternal<SCompiledLocalType> link =
      SLinkExternal.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        URI.create("http://www.example.com"),
        Vector.of(SText.of(local, "Hello.")));

    final Element xt = xhtml.linkExternal(link);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("a", xt.getLocalName());
    Assertions.assertEquals("http://www.example.com", xt.getAttribute("href"));
    Assertions.assertEquals("st_link_external t", xt.getAttribute("class"));
    Assertions.assertEquals("Hello.", xt.getTextContent());
  }

  @Test
  public void testLink()
  {
    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SBlockID<SCompiledLocalType> id = SBlockID.of(local, "a");

    Mockito.when(links.linkOf(id))
      .thenReturn(SXHTMLLink.of("index.xhtml", "a"));

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SLink<SCompiledLocalType> link =
      SLink.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        id,
        Vector.of(SText.of(local, "Hello.")));

    final Element xt = xhtml.link(links, link);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("a", xt.getLocalName());
    Assertions.assertEquals("index.xhtml#a", xt.getAttribute("href"));
    Assertions.assertEquals("st_link t", xt.getAttribute("class"));
    Assertions.assertEquals("Hello.", xt.getTextContent());
  }

  @Test
  public void testFootnoteReference()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);
    final SCompiledGlobalType global =
      Mockito.mock(SCompiledGlobalType.class);
    final SFootnoteType<SCompiledLocalType> footnote =
      Mockito.mock(SFootnoteType.class);

    final SBlockID<SCompiledLocalType> id =
      SBlockID.of(local, "a");

    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);

    Mockito.when(local.global())
      .thenReturn(global);

    Mockito.when(links.linkOf(id))
      .thenReturn(SXHTMLLink.of("index.xhtml", "a"));

    Mockito.when(global.findFootnoteForID(id))
      .thenReturn(footnote);

    Mockito.when(global.footnoteIndexOf(footnote))
      .thenReturn(BigInteger.ONE);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SFootnoteReference<SCompiledLocalType> ref =
      SFootnoteReference.of(local, Optional.of(STypeName.of(local, "t")), id);

    final Element xt = xhtml.footnoteReference(links, ref);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("span", xt.getLocalName());
    Assertions.assertEquals("st_footnote_reference t", xt.getAttribute("class"));

    final Text xtc0 = (Text) xt.getFirstChild();
    Assertions.assertEquals("[", xtc0.getTextContent());

    final Element xtc1 = (Element) xtc0.getNextSibling();
    Assertions.assertEquals("a", xtc1.getLocalName());
    Assertions.assertEquals("index.xhtml#a", xtc1.getAttribute("href"));
    Assertions.assertEquals("1", xtc1.getTextContent());

    final Text xtc2 = (Text) xtc1.getNextSibling();
    Assertions.assertEquals("]", xtc2.getTextContent());
  }

  @Test
  public void testFormalItemReference()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);
    final SCompiledGlobalType global =
      Mockito.mock(SCompiledGlobalType.class);
    final SFormalItemType<SCompiledLocalType> formal =
      Mockito.mock(SFormalItemType.class);

    final SBlockID<SCompiledLocalType> id =
      SBlockID.of(local, "a");

    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);

    Mockito.when(local.global())
      .thenReturn(global);

    Mockito.when(formal.data())
      .thenReturn(local);

    Mockito.when(local.number())
      .thenReturn(SContentNumbers.parse("1.2.3"));

    Mockito.when(links.linkOf(id))
      .thenReturn(SXHTMLLink.of("index.xhtml", "a"));

    Mockito.when(global.findFormalItemForID(id))
      .thenReturn(formal);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SFormalItemReference<SCompiledLocalType> ref =
      SFormalItemReference.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        SBlockID.of(local, "a"));

    final Element xt = xhtml.formalItemReference(links, ref);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("span", xt.getLocalName());
    Assertions.assertEquals("st_formal_item_reference t", xt.getAttribute("class"));

    final Text xtc0 = (Text) xt.getFirstChild();
    Assertions.assertEquals("[", xtc0.getTextContent());

    final Element xtc1 = (Element) xtc0.getNextSibling();
    Assertions.assertEquals("a", xtc1.getLocalName());
    Assertions.assertEquals("index.xhtml#a", xtc1.getAttribute("href"));
    Assertions.assertEquals("1.2.3", xtc1.getTextContent());

    final Text xtc2 = (Text) xtc1.getNextSibling();
    Assertions.assertEquals("]", xtc2.getTextContent());
  }

  @Test
  public void testVerbatim()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SVerbatim<SCompiledLocalType> verbatim =
      SVerbatim.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        SText.of(local, "Hello."));

    final Element xt = xhtml.verbatim(verbatim);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("pre", xt.getLocalName());
    Assertions.assertEquals("st_verbatim t", xt.getAttribute("class"));
    Assertions.assertEquals("Hello.", xt.getTextContent());
  }

  @Test
  public void testListOrdered()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);
    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SListOrdered<SCompiledLocalType> list =
      SListOrdered.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        Vector.of(
          SListItem.of(
            local,
            Vector.of(SText.of(local, "1")),
            Optional.of(STypeName.of(local, "x"))),
          SListItem.of(
            local,
            Vector.of(SText.of(local, "2")),
            Optional.of(STypeName.of(local, "y"))),
          SListItem.of(
            local,
            Vector.of(SText.of(local, "3")),
            Optional.of(STypeName.of(local, "z")))));

    final Element xt = xhtml.listOrdered(links, list);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("ol", xt.getLocalName());
    Assertions.assertEquals("st_list_ordered t", xt.getAttribute("class"));

    final Element e0 = (Element) xt.getFirstChild();
    Assertions.assertEquals("li", e0.getLocalName());
    Assertions.assertEquals("1", e0.getTextContent());
    Assertions.assertEquals("st_list_item x", e0.getAttribute("class"));

    final Element e1 = (Element) e0.getNextSibling();
    Assertions.assertEquals("li", e1.getLocalName());
    Assertions.assertEquals("2", e1.getTextContent());
    Assertions.assertEquals("st_list_item y", e1.getAttribute("class"));

    final Element e2 = (Element) e1.getNextSibling();
    Assertions.assertEquals("li", e2.getLocalName());
    Assertions.assertEquals("3", e2.getTextContent());
    Assertions.assertEquals("st_list_item z", e2.getAttribute("class"));
  }

  @Test
  public void testListUnordered()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);
    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SListUnordered<SCompiledLocalType> list =
      SListUnordered.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        Vector.of(
          SListItem.of(
            local,
            Vector.of(SText.of(local, "1")),
            Optional.of(STypeName.of(local, "x"))),
          SListItem.of(
            local,
            Vector.of(SText.of(local, "2")),
            Optional.of(STypeName.of(local, "y"))),
          SListItem.of(
            local,
            Vector.of(SText.of(local, "3")),
            Optional.of(STypeName.of(local, "z")))));

    final Element xt = xhtml.listUnordered(links, list);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("ul", xt.getLocalName());
    Assertions.assertEquals("st_list_unordered t", xt.getAttribute("class"));

    final Element e0 = (Element) xt.getFirstChild();
    Assertions.assertEquals("li", e0.getLocalName());
    Assertions.assertEquals("1", e0.getTextContent());
    Assertions.assertEquals("st_list_item x", e0.getAttribute("class"));

    final Element e1 = (Element) e0.getNextSibling();
    Assertions.assertEquals("li", e1.getLocalName());
    Assertions.assertEquals("2", e1.getTextContent());
    Assertions.assertEquals("st_list_item y", e1.getAttribute("class"));

    final Element e2 = (Element) e1.getNextSibling();
    Assertions.assertEquals("li", e2.getLocalName());
    Assertions.assertEquals("3", e2.getTextContent());
    Assertions.assertEquals("st_list_item z", e2.getAttribute("class"));
  }

  @Test
  public void testParagraph()
  {
    final SCompiledLocalType local =
      Mockito.mock(SCompiledLocalType.class);
    final SXHTMLLinkProviderType links =
      Mockito.mock(SXHTMLLinkProviderType.class);

    Mockito.when(local.number())
      .thenReturn(SContentNumbers.parse("1.2.3"));

    final SXHTMLBuilder xhtml =
      SXHTMLBuilder.create();

    final SParagraph<SCompiledLocalType> para =
      SParagraph.of(
        local,
        Optional.of(STypeName.of(local, "t")),
        Optional.of(SBlockID.of(local, "x")),
        Vector.of(SText.of(local, "Hello")));

    final Element xt = xhtml.paragraph(links, para);
    LOG.debug("xhtml: {}", xt);

    Assertions.assertEquals(SXHTMLBuilder.XHTML_NAMESPACE, xt.getNamespaceURI());
    Assertions.assertEquals("div", xt.getLocalName());
    Assertions.assertEquals("st_paragraph t", xt.getAttribute("class"));

    final Element number_container = (Element) xt.getFirstChild();
    Assertions.assertEquals("3", number_container.getTextContent());
    Assertions.assertEquals("st_paragraph_number", number_container.getAttribute("class"));

    final Element number_link = (Element) number_container.getFirstChild();
    Assertions.assertEquals("a", number_link.getLocalName());
    Assertions.assertEquals("st_paragraph_1_2_3", number_link.getAttribute("id"));
    Assertions.assertEquals("#st_paragraph_1_2_3", number_link.getAttribute("href"));

    final Element content_container = (Element) number_container.getNextSibling();
    Assertions.assertEquals("Hello", content_container.getTextContent());
    Assertions.assertEquals("st_paragraph_content", content_container.getAttribute("class"));

    dump(xt);
  }

  private static void dump(final Element element)
  {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
      final DocumentBuilderFactory document_builders =
        DocumentBuilderFactory.newDefaultInstance();
      final DocumentBuilder document_builder =
        document_builders.newDocumentBuilder();
      final Document document =
        document_builder.newDocument();

      final TransformerFactory transformer_factory =
        TransformerFactory.newInstance();
      final Transformer transformer =
        transformer_factory.newTransformer();

      transformer.setOutputProperty(
        OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
        OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
      transformer.setOutputProperty(
        "{http://xml.apache.org/xslt}indent-amount",
        "2");

      document.adoptNode(element);
      document.appendChild(element);

      transformer.transform(new DOMSource(document), new StreamResult(stream));

      stream.flush();

      LOG.debug("document: {}", stream.toString("UTF-8"));
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }
}
