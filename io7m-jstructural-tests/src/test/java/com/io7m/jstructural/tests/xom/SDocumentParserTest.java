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

import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SDocumentWithSections;
import com.io7m.jstructural.core.SID;
import com.io7m.jstructural.core.SImage;
import com.io7m.jstructural.core.SLink;
import com.io7m.jstructural.core.SLinkExternal;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SParagraph;
import com.io7m.jstructural.core.SParagraphContent;
import com.io7m.jstructural.core.SSection;
import com.io7m.jstructural.core.SSectionWithParagraphs;
import com.io7m.jstructural.core.SSubsectionContent;
import com.io7m.jstructural.core.STerm;
import com.io7m.jstructural.core.SText;
import com.io7m.jstructural.core.SVerbatim;
import com.io7m.jstructural.core.SXML;
import com.io7m.jstructural.xom.SDocumentParser;
import com.io7m.jstructural.xom.SDocumentSerializer;
import com.io7m.junreachable.UnreachableCodeException;
import nu.xom.Attribute;
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
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@SuppressWarnings("static-method") public final class SDocumentParserTest
{
  public static SDocument parse(
    final String name)
    throws ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    final String file = "/com/io7m/jstructural/tests/" + name;
    final URL url = SDocumentParserTest.class.getResource(file);
    final URI uri = url.toURI();

    return SDocumentParser.fromStream(
      SDocumentParserTest.class.getResourceAsStream(file),
      uri);
  }

  public static SDocument roundTripParse(
    final String name)
  {
    try {
      final SDocument d0 = SDocumentParserTest.parse(name);
      final Element root = SDocumentSerializer.document(d0);
      final Document out_doc = new Document(root);

      final ByteArrayOutputStream bao = new ByteArrayOutputStream();
      final Serializer s = new Serializer(bao, "UTF-8");
      s.write(out_doc);
      s.flush();

      final ByteArrayInputStream bai =
        new ByteArrayInputStream(bao.toByteArray());

      final URI uri = new URI("/temporary");
      final SDocument d1 =
        SDocumentParser.fromStream(bai, uri);

      Assert.assertEquals(d0, d1);
      return d1;
    } catch (final ValidityException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final SAXException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final ParserConfigurationException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final ParsingException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final IOException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final URISyntaxException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final BadParseAttributeException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final InclusionLoopException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final NoIncludeLocationException e1) {
      throw new UnreachableCodeException(e1);
    } catch (final XIncludeException e1) {
      throw new UnreachableCodeException(e1);
    }
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

  @Test public void testBasic0()
    throws ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    final SDocumentWithSections doc =
      (SDocumentWithSections) SDocumentParserTest.parse("basic-0.xml");

    final List<SSection> sections = doc.getSections().getElements();
    Assert.assertEquals(1, sections.size());

    final SSectionWithParagraphs section =
      (SSectionWithParagraphs) sections.get(0);

    final List<SSubsectionContent> section_content =
      section.getSectionContent().getElements();
    Assert.assertEquals(1, section_content.size());

    final SParagraph paragraph = (SParagraph) section_content.get(0);
    final SText text = (SText) paragraph.getContent().getElements().get(0);

    Assert.assertEquals("Paragraph.", text.getText().trim());
  }

  @Test public void testBasic1()
    throws ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    final SDocumentWithSections doc =
      (SDocumentWithSections) SDocumentParserTest.parse("basic-1.xml");

    final List<SSection> sections = doc.getSections().getElements();
    Assert.assertEquals(1, sections.size());

    final SSectionWithParagraphs section =
      (SSectionWithParagraphs) sections.get(0);

    final List<SSubsectionContent> section_content =
      section.getSectionContent().getElements();
    Assert.assertEquals(3, section_content.size());

    final SParagraph paragraph = (SParagraph) section_content.get(0);
    final SText text = (SText) paragraph.getContent().getElements().get(0);

    Assert.assertEquals("Paragraph.", text.getText().trim());
  }

  @Test(expected = SAXParseException.class) public void testEmpty()
    throws ValidityException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    SDocumentParserTest.parse("empty.xml");
  }

  @Test public void testImage0()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);

    final SImage i = SDocumentParser.image(e);
    Assert.assertTrue(i.getType().isNone());
    Assert.assertTrue(i.getWidth().isNone());
    Assert.assertTrue(i.getHeight().isNone());
    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage0t()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(at);

    final SImage i = SDocumentParser.image(e);

    {
      final Some<String> some = (Some<String>) i.getType();
      Assert.assertEquals("a_type", some.get());
    }

    Assert.assertTrue(i.getWidth().isNone());
    Assert.assertTrue(i.getHeight().isNone());
    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage1()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute ah =
      new Attribute("s:height", SXML.XML_URI.toString(), "23");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(ah);

    final SImage i = SDocumentParser.image(e);
    Assert.assertTrue(i.getType().isNone());
    Assert.assertTrue(i.getWidth().isNone());

    {
      final Some<Integer> some = (Some<Integer>) i.getHeight();
      Assert.assertEquals(23, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage1t()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute ah =
      new Attribute("s:height", SXML.XML_URI.toString(), "23");
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(ah);
    e.addAttribute(at);

    final SImage i = SDocumentParser.image(e);

    {
      final Some<String> some = (Some<String>) i.getType();
      Assert.assertEquals("a_type", some.get());
    }

    Assert.assertTrue(i.getWidth().isNone());

    {
      final Some<Integer> some = (Some<Integer>) i.getHeight();
      Assert.assertEquals(23, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage2()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute aw =
      new Attribute("s:width", SXML.XML_URI.toString(), "42");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(aw);

    final SImage i = SDocumentParser.image(e);
    Assert.assertTrue(i.getType().isNone());
    Assert.assertTrue(i.getHeight().isNone());

    {
      final Some<Integer> some = (Some<Integer>) i.getWidth();
      Assert.assertEquals(42, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage2t()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute aw =
      new Attribute("s:width", SXML.XML_URI.toString(), "42");
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(aw);
    e.addAttribute(at);

    final SImage i = SDocumentParser.image(e);

    {
      final Some<String> some = (Some<String>) i.getType();
      Assert.assertEquals("a_type", some.get());
    }

    Assert.assertTrue(i.getHeight().isNone());

    {
      final Some<Integer> some = (Some<Integer>) i.getWidth();
      Assert.assertEquals(42, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage3()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute aw =
      new Attribute("s:width", SXML.XML_URI.toString(), "42");
    final Attribute ah =
      new Attribute("s:height", SXML.XML_URI.toString(), "23");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(aw);
    e.addAttribute(ah);

    final SImage i = SDocumentParser.image(e);
    Assert.assertTrue(i.getType().isNone());

    {
      final Some<Integer> some = (Some<Integer>) i.getHeight();
      Assert.assertEquals(23, some.get().intValue());
    }

    {
      final Some<Integer> some = (Some<Integer>) i.getWidth();
      Assert.assertEquals(42, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImage3t()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute aw =
      new Attribute("s:width", SXML.XML_URI.toString(), "42");
    final Attribute ah =
      new Attribute("s:height", SXML.XML_URI.toString(), "23");
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(aw);
    e.addAttribute(ah);
    e.addAttribute(at);

    final SImage i = SDocumentParser.image(e);

    {
      final Some<String> some = (Some<String>) i.getType();
      Assert.assertEquals("a_type", some.get());
    }

    {
      final Some<Integer> some = (Some<Integer>) i.getHeight();
      Assert.assertEquals(23, some.get().intValue());
    }

    {
      final Some<Integer> some = (Some<Integer>) i.getWidth();
      Assert.assertEquals(42, some.get().intValue());
    }

    Assert.assertEquals("Image.", i.getText());
    Assert.assertEquals(uri, i.getURI());
  }

  @Test public void testImageRoundTrip()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Attribute aw =
      new Attribute("s:width", SXML.XML_URI.toString(), "42");
    final Attribute ah =
      new Attribute("s:height", SXML.XML_URI.toString(), "23");
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    e.appendChild("Image.");
    e.addAttribute(as);
    e.addAttribute(aw);
    e.addAttribute(ah);
    e.addAttribute(at);

    final SImage i0 = SDocumentParser.image(e);
    final Element ie = SDocumentSerializer.image(i0);
    final SImage i1 = SDocumentParser.image(ie);
    Assert.assertEquals(i0, i1);
  }

  @Test public void testLink0()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Element ei = new Element("s:image", SXML.XML_URI.toString());
    ei.appendChild("Image.");
    ei.addAttribute(as);

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild(ei);
    el.addAttribute(at);

    final SLink i = SDocumentParser.link(el);
    Assert.assertEquals(uri.toString(), i.getTarget());

    final SImage ii = (SImage) i.getContent().getElements().get(0);
    Assert.assertEquals("Image.", ii.getText());
  }

  @Test public void testLink1()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild("Link.");
    el.addAttribute(at);

    final SLink i = SDocumentParser.link(el);
    Assert.assertEquals(uri.toString(), i.getTarget());

    final SText ii = (SText) i.getContent().getElements().get(0);
    Assert.assertEquals("Link.", ii.getText());
  }

  @Test public void testLinkExternal0()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Element ei = new Element("s:image", SXML.XML_URI.toString());
    ei.appendChild("Image.");
    ei.addAttribute(as);

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild(ei);
    el.addAttribute(at);

    final SLinkExternal i = SDocumentParser.linkExternal(el);
    Assert.assertEquals(uri, i.getTarget());

    final SImage ii = (SImage) i.getContent().getElements().get(0);
    Assert.assertEquals("Image.", ii.getText());
  }

  @Test public void testLinkExternal1()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild("LinkExternal.");
    el.addAttribute(at);

    final SLinkExternal i = SDocumentParser.linkExternal(el);
    Assert.assertEquals(uri, i.getTarget());

    final SText ii = (SText) i.getContent().getElements().get(0);
    Assert.assertEquals("LinkExternal.", ii.getText());
  }

  @Test public void testLinkExternalRoundTrip_0()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Element ei = new Element("s:image", SXML.XML_URI.toString());
    ei.appendChild("Image.");
    ei.addAttribute(as);

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el =
      new Element("s:link-external", SXML.XML_URI.toString());
    el.appendChild(ei);
    el.addAttribute(at);

    final SLinkExternal i0 = SDocumentParser.linkExternal(el);
    final Element ie = SDocumentSerializer.linkExternal(i0);
    final SLinkExternal i1 = SDocumentParser.linkExternal(ie);
    Assert.assertEquals(i0, i1);
  }

  @Test public void testLinkExternalRoundTrip_1()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el =
      new Element("s:link-external", SXML.XML_URI.toString());
    el.appendChild("LinkExternal.");
    el.addAttribute(at);

    final SLinkExternal i0 = SDocumentParser.linkExternal(el);
    final Element ie = SDocumentSerializer.linkExternal(i0);
    final SLinkExternal i1 = SDocumentParser.linkExternal(ie);
    Assert.assertEquals(i0, i1);
  }

  @Test public void testLinkRoundTrip_0()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute as =
      new Attribute("s:source", SXML.XML_URI.toString(), uri.toString());
    final Element ei = new Element("s:image", SXML.XML_URI.toString());
    ei.appendChild("Image.");
    ei.addAttribute(as);

    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild(ei);
    el.addAttribute(at);

    final SLink i0 = SDocumentParser.link(el);
    final Element ie = SDocumentSerializer.link(i0);
    final SLink i1 = SDocumentParser.link(ie);
    Assert.assertEquals(i0, i1);
  }

  @Test public void testLinkRoundTrip_1()
    throws URISyntaxException
  {
    final URI uri = new URI("http://io7m.com");
    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), uri.toString());
    final Element el = new Element("s:link", SXML.XML_URI.toString());
    el.appendChild("Link.");
    el.addAttribute(at);

    final SLink i0 = SDocumentParser.link(el);
    final Element ie = SDocumentSerializer.link(i0);
    final SLink i1 = SDocumentParser.link(ie);
    Assert.assertEquals(i0, i1);
  }

  @Test public void testParagraph0()
    throws URISyntaxException
  {
    final Element e = new Element("s:paragraph", SXML.XML_URI.toString());
    e.appendChild("Paragraph.");

    final SParagraph p = SDocumentParser.paragraph(e);
    final SNonEmptyList<SParagraphContent> content = p.getContent();

    Assert.assertTrue(p.getID().isNone());
    Assert.assertTrue(p.getType().isNone());
    Assert.assertEquals(1, content.getElements().size());
    Assert.assertEquals(SText.text("Paragraph."), content
      .getElements()
      .get(0));
  }

  @Test public void testParagraph1()
    throws URISyntaxException
  {
    final Element e = new Element("s:paragraph", SXML.XML_URI.toString());
    final Attribute a =
      new Attribute(
        "xml:id",
        "http://www.w3.org/XML/1998/namespace",
        "paragraph_0");

    e.addAttribute(a);
    e.appendChild("Paragraph.");

    final SParagraph p = SDocumentParser.paragraph(e);
    final SNonEmptyList<SParagraphContent> content = p.getContent();

    Assert.assertTrue(p.getID().isSome());
    p.getID().map(new FunctionType<SID, Unit>() {
      @Override public Unit call(
        final SID x)
      {
        Assert.assertEquals("paragraph_0", x.getActual());
        return Unit.unit();
      }
    });

    Assert.assertTrue(p.getType().isNone());
    Assert.assertEquals(1, content.getElements().size());
    Assert.assertEquals(SText.text("Paragraph."), content
      .getElements()
      .get(0));
  }

  @Test public void testParagraph2()
    throws URISyntaxException
  {
    final Element e = new Element("s:paragraph", SXML.XML_URI.toString());
    final Attribute ai =
      new Attribute(
        "xml:id",
        "http://www.w3.org/XML/1998/namespace",
        "paragraph_0");
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    e.addAttribute(at);
    e.addAttribute(ai);
    e.appendChild("Paragraph.");

    final SParagraph p = SDocumentParser.paragraph(e);
    final SNonEmptyList<SParagraphContent> content = p.getContent();

    Assert.assertTrue(p.getID().isSome());
    p.getID().map(new FunctionType<SID, Unit>() {
      @Override public Unit call(
        final SID x)
      {
        Assert.assertEquals("paragraph_0", x.getActual());
        return Unit.unit();
      }
    });

    Assert.assertTrue(p.getType().isSome());
    p.getType().map(new FunctionType<String, Unit>() {

      @Override public Unit call(
        final String x)
      {
        Assert.assertEquals("a_type", x);
        return Unit.unit();
      }
    });

    Assert.assertEquals(1, content.getElements().size());
    Assert.assertEquals(SText.text("Paragraph."), content
      .getElements()
      .get(0));
  }

  @Test public void testParagraph3()
    throws URISyntaxException
  {
    final Element e = new Element("s:paragraph", SXML.XML_URI.toString());
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");

    e.addAttribute(at);
    e.appendChild("Paragraph.");

    final SParagraph p = SDocumentParser.paragraph(e);
    final SNonEmptyList<SParagraphContent> content = p.getContent();

    Assert.assertTrue(p.getID().isNone());
    Assert.assertTrue(p.getType().isSome());
    p.getType().map(new FunctionType<String, Unit>() {

      @Override public Unit call(
        final String x)
      {
        Assert.assertEquals("a_type", x);
        return Unit.unit();
      }
    });

    Assert.assertEquals(1, content.getElements().size());
    Assert.assertEquals(SText.text("Paragraph."), content
      .getElements()
      .get(0));
  }

  @Test public void testResolve_0()
  {
    SDocumentParserTest.roundTripParse("resolve-0.xml");
  }

  @Test public void testRoundTrip_0()
  {
    SDocumentParserTest.roundTripParse("basic-0.xml");
  }

  @Test public void testRoundTrip_1()
  {
    SDocumentParserTest.roundTripParse("basic-1.xml");
  }

  @Test public void testRoundTrip_2()
  {
    SDocumentParserTest.roundTripParse("jaux-documentation.xml");
  }

  @Test public void testRoundTrip_Bug_ebbe752c4e()
  {
    SDocumentParserTest.roundTripParse("bug-ebbe752c4e.xml");
  }

  @Test public void testRoundTrip_Bug_c1c7919467()
  {
    SDocumentParserTest.roundTripParse("lightweight-static-exceptions.xml");
  }

  @Test public void testTerm0()
  {
    final Element e = new Element("s:term", SXML.XML_URI.toString());
    e.appendChild("Term.");

    final STerm t = SDocumentParser.term(e);
    final SText text = t.getText();

    Assert.assertTrue(t.getType().isNone());
    Assert.assertEquals(SText.text("Term."), text);
  }

  @Test public void testTerm1()
  {
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");
    final Element e = new Element("s:term", SXML.XML_URI.toString());
    e.appendChild("Term.");
    e.addAttribute(at);

    final STerm t = SDocumentParser.term(e);
    final SText text = t.getText();

    Assert.assertTrue(t.getType().isSome());

    t.getType().map(new FunctionType<String, Unit>() {
      @Override public Unit call(
        final String x)
      {
        Assert.assertEquals("a_type", x);
        return Unit.unit();
      }
    });

    Assert.assertEquals(SText.text("Term."), text);
  }

  @Test public void testTermRoundTrip()
  {
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");
    final Element e = new Element("s:term", SXML.XML_URI.toString());
    e.appendChild("Term.");
    e.addAttribute(at);

    final STerm t0 = SDocumentParser.term(e);
    final Element t1 = SDocumentSerializer.term(t0);
    final STerm t2 = SDocumentParser.term(t1);
    Assert.assertEquals(t0, t2);
  }

  @Test public void testVerbatim0()
  {
    final Element e = new Element("s:verbatim", SXML.XML_URI.toString());
    e.appendChild("Verbatim.");

    final SVerbatim t = SDocumentParser.verbatim(e);
    final String text = t.getText();

    Assert.assertTrue(t.getType().isNone());
    Assert.assertEquals("Verbatim.", text);
  }

  @Test public void testVerbatim1()
  {
    final Element e = new Element("s:verbatim", SXML.XML_URI.toString());
    e.appendChild("Verbatim.");

    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");
    e.addAttribute(at);

    final SVerbatim t = SDocumentParser.verbatim(e);
    final String text = t.getText();

    Assert.assertTrue(t.getType().isSome());

    t.getType().map(new FunctionType<String, Unit>() {
      @Override public Unit call(
        final String x)
      {
        Assert.assertEquals("a_type", x);
        return Unit.unit();
      }
    });

    Assert.assertEquals("Verbatim.", text);
  }

  @Test public void testVerbatimRoundTrip()
  {
    final Attribute at =
      new Attribute("s:type", SXML.XML_URI.toString(), "a_type");
    final Element e = new Element("s:verbatim", SXML.XML_URI.toString());
    e.appendChild("Verbatim.");
    e.addAttribute(at);

    final SVerbatim t0 = SDocumentParser.verbatim(e);
    final Element t1 = SDocumentSerializer.verbatim(t0);
    final SVerbatim t2 = SDocumentParser.verbatim(t1);
    Assert.assertEquals(t0, t2);
  }
}
