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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SDocumentTitle;
import com.io7m.jstructural.core.SDocumentWithParts;
import com.io7m.jstructural.core.SDocumentWithSections;
import com.io7m.jstructural.core.SFootnote;
import com.io7m.jstructural.core.SFootnoteContent;
import com.io7m.jstructural.core.SFormalItem;
import com.io7m.jstructural.core.SFormalItemContent;
import com.io7m.jstructural.core.SFormalItemList;
import com.io7m.jstructural.core.SFormalItemTitle;
import com.io7m.jstructural.core.SID;
import com.io7m.jstructural.core.SImage;
import com.io7m.jstructural.core.SLink;
import com.io7m.jstructural.core.SLinkContent;
import com.io7m.jstructural.core.SLinkExternal;
import com.io7m.jstructural.core.SListItem;
import com.io7m.jstructural.core.SListItemContent;
import com.io7m.jstructural.core.SListOrdered;
import com.io7m.jstructural.core.SListUnordered;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SParagraph;
import com.io7m.jstructural.core.SParagraphContent;
import com.io7m.jstructural.core.SPart;
import com.io7m.jstructural.core.SPartTitle;
import com.io7m.jstructural.core.SSection;
import com.io7m.jstructural.core.SSectionTitle;
import com.io7m.jstructural.core.SSectionWithParagraphs;
import com.io7m.jstructural.core.SSectionWithSubsections;
import com.io7m.jstructural.core.SSubsection;
import com.io7m.jstructural.core.SSubsectionContent;
import com.io7m.jstructural.core.SSubsectionTitle;
import com.io7m.jstructural.core.STable;
import com.io7m.jstructural.core.STableBody;
import com.io7m.jstructural.core.STableCell;
import com.io7m.jstructural.core.STableCellContent;
import com.io7m.jstructural.core.STableColumnName;
import com.io7m.jstructural.core.STableHead;
import com.io7m.jstructural.core.STableRow;
import com.io7m.jstructural.core.STableSummary;
import com.io7m.jstructural.core.STerm;
import com.io7m.jstructural.core.SText;
import com.io7m.jstructural.core.SVerbatim;
import com.io7m.jstructural.core.SXML;
import com.io7m.jstructural.schema.SSchema;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Text;
import nu.xom.ValidityException;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.InclusionLoopException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A document parser that uses XOM to process documents.</p>
 *
 * <p>Note: This is not a public API and is subject to change without
 * warning.</p>
 */

public final class SDocumentParser
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SDocumentParser.class);
  }

  private SDocumentParser()
  {

  }

  /**
   * Attempt to parse a document from the given element. The element is assumed
   * to have been validated with the {@code structural} schema.
   *
   * @param e The element
   *
   * @return A document
   *
   * @throws URISyntaxException If parsing a URI fails, internally
   */

  static SDocument document(
    final Element e)
    throws URISyntaxException
  {
    NullCheck.notNull(e, "Element");

    SDocumentParser.LOG.debug("document: starting");

    final SDocumentTitle title = SDocumentParser.documentTitleRoot(e);
    final SDocumentStyle style = SDocumentParser.documentStyleRoot(e);
    final boolean contents = SDocumentParser.documentContentsRoot(e);
    final Element esect = SDocumentParser.getElement(e, "section");
    if (esect != null) {
      return SDocumentParser.documentWithSections(
        e, title, style, contents);
    }
    return SDocumentParser.documentWithParts(e, title, style, contents);
  }

  private static boolean documentContentsRoot(
    final Element root)
  {
    final Element e = SDocumentParser.getElement(root, "document-contents");
    return e != null;
  }

  private static
  @Nullable
  SDocumentStyle documentStyleRoot(
    final Element root)
    throws URISyntaxException
  {
    final Element e = SDocumentParser.getElement(root, "document-style");
    if (e == null) {
      return null;
    }
    return SDocumentStyle.documentStyle(new URI(e.getValue()));
  }

  private static SDocumentTitle documentTitleRoot(
    final Element root)
  {
    final Element e = SDocumentParser.getElement(root, "document-title");
    assert e != null;
    return SDocumentTitle.documentTitle(e.getValue());
  }

  private static SDocument documentWithParts(
    final Element root,
    final SDocumentTitle title,
    final @Nullable SDocumentStyle style,
    final boolean contents)
    throws URISyntaxException
  {
    final Elements parts = SDocumentParser.getElements(root, "part");
    assert parts != null;
    assert parts.size() > 0;

    final List<SPart> elements = new ArrayList<SPart>();
    for (int index = 0; index < parts.size(); ++index) {
      final SPart part = SDocumentParser.part(parts.get(index));
      elements.add(part);
    }

    final SNonEmptyList<SPart> content = SNonEmptyList.newList(elements);
    return SDocumentParser.documentWithPartsMake(
      title, style, contents, content);
  }

  private static SDocument documentWithPartsMake(
    final SDocumentTitle title,
    final @Nullable SDocumentStyle style,
    final boolean contents,
    final SNonEmptyList<SPart> content)
  {
    if (contents) {
      if (style != null) {
        return SDocumentWithParts.documentStyleContents(title, style, content);
      }
      return SDocumentWithParts.documentContents(title, content);
    }

    if (style != null) {
      return SDocumentWithParts.documentStyle(title, style, content);
    }
    return SDocumentWithParts.document(title, content);
  }

  private static SDocument documentWithSections(
    final Element root,
    final SDocumentTitle title,
    final @Nullable SDocumentStyle style,
    final boolean contents)
    throws URISyntaxException
  {
    final Elements sections = SDocumentParser.getElements(root, "section");
    assert sections != null;
    assert sections.size() > 0;

    final List<SSection> elements = new ArrayList<SSection>();
    for (int index = 0; index < sections.size(); ++index) {
      final SSection section =
        SDocumentParser.section(sections.get(index));
      elements.add(section);
    }

    final SNonEmptyList<SSection> content = SNonEmptyList.newList(elements);
    return SDocumentParser.documentWithSectionsMake(
      title, style, contents, content);
  }

  private static SDocumentWithSections documentWithSectionsMake(
    final SDocumentTitle title,
    final @Nullable SDocumentStyle style,
    final boolean contents,
    final SNonEmptyList<SSection> content)
  {
    if (contents) {
      if (style != null) {
        return SDocumentWithSections.documentStyleContents(
          title, style, content);
      }
      return SDocumentWithSections.documentContents(title, content);
    }

    if (style != null) {
      return SDocumentWithSections.documentStyle(title, style, content);
    }
    return SDocumentWithSections.document(title, content);
  }

  private static SFootnote footnote(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("footnote: starting");

    final List<SFootnoteContent> content_nodes =
      new ArrayList<SFootnoteContent>();
    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node ec = e.getChild(index);
      content_nodes.add(SDocumentParser.footnoteContent(ec));
    }

    final SNonEmptyList<SFootnoteContent> content =
      SNonEmptyList.newList(content_nodes);
    return SFootnote.footnote(content);
  }

  private static SFootnoteContent footnoteContent(
    final Node c)
    throws URISyntaxException
  {
    if (c instanceof Text) {
      final Text et = (Text) c;
      return SText.text(et.getValue());
    }

    if (c instanceof Element) {
      final Element ecc = (Element) c;
      if ("image".equals(ecc.getLocalName())) {
        return SDocumentParser.image(ecc);
      }
      if ("link".equals(ecc.getLocalName())) {
        return SDocumentParser.link(ecc);
      }
      if ("link-external".equals(ecc.getLocalName())) {
        return SDocumentParser.linkExternal(ecc);
      }
      if ("term".equals(ecc.getLocalName())) {
        return SDocumentParser.term(ecc);
      }
      if ("verbatim".equals(ecc.getLocalName())) {
        return SDocumentParser.verbatim(ecc);
      }
      if ("footnote".equals(ecc.getLocalName())) {
        return SDocumentParser.footnote(ecc);
      }
    }

    throw new UnreachableCodeException();
  }

  private static SFormalItem formalItem(
    final Element e)
    throws URISyntaxException
  {
    final String type = SDocumentParser.typeAttribute(e);
    final String kind = SDocumentParser.kindAttribute(e);
    assert kind != null;
    final SID id = SDocumentParser.idAttribute(e);
    final SFormalItemTitle title = SDocumentParser.formalItemTitleRoot(e);

    final Elements children = e.getChildElements();
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      if ("formal-item-title".equals(ec.getLocalName())) {
        continue;
      }
      return SDocumentParser.formalItemMake(
        type, kind, id, title, SDocumentParser.formalItemContent(ec));
    }

    throw new UnreachableCodeException();
  }

  private static SFormalItemContent formalItemContent(
    final Element ec)
    throws URISyntaxException
  {
    if ("formal-item-list".equals(ec.getLocalName())) {
      return SDocumentParser.formalItemList(ec);
    }
    if ("image".equals(ec.getLocalName())) {
      return SDocumentParser.image(ec);
    }
    if ("list-ordered".equals(ec.getLocalName())) {
      return SDocumentParser.listOrdered(ec);
    }
    if ("list-unordered".equals(ec.getLocalName())) {
      return SDocumentParser.listUnordered(ec);
    }
    if ("table".equals(ec.getLocalName())) {
      return SDocumentParser.table(ec);
    }
    if ("verbatim".equals(ec.getLocalName())) {
      return SDocumentParser.verbatim(ec);
    }

    throw new UnreachableCodeException();
  }

  private static SFormalItemList formalItemList(
    final Element e)
  {
    final String kind = SDocumentParser.kindAttribute(e);
    assert kind != null;
    return SFormalItemList.formalItemList(kind);
  }

  private static SFormalItem formalItemMake(
    final @Nullable String type,
    final String kind,
    final @Nullable SID id,
    final SFormalItemTitle title,
    final SFormalItemContent content)
  {
    if (type != null) {
      if (id != null) {
        return SFormalItem.formalItemTypedWithID(
          title, kind, type, content, id);
      }
      return SFormalItem.formalItemTyped(title, kind, type, content);
    }

    if (id != null) {
      return SFormalItem.formalItemWithID(title, kind, content, id);
    }

    return SFormalItem.formalItem(title, kind, content);
  }

  private static SFormalItemTitle formalItemTitleRoot(
    final Element e)
  {
    final Element ec = SDocumentParser.getElement(e, "formal-item-title");
    assert ec != null;
    return SFormalItemTitle.formalItemTitle(ec.getValue());
  }

  /**
   * Parse a document from a validated stream.
   *
   * @param uri    The base URI of the document
   * @param stream The stream
   *
   * @return A document
   *
   * @throws SAXException                 On XML parse errors
   * @throws ParserConfigurationException On parser configuration errors
   * @throws ValidityException            On XML validation errors
   * @throws ParsingException             On parser errors
   * @throws IOException                  On I/O errors
   * @throws URISyntaxException           On failing to parse a URI
   * @throws XIncludeException            If an xinclude fails
   * @throws NoIncludeLocationException   If an xinclude fails
   * @throws InclusionLoopException       If an xinclude fails
   * @throws BadParseAttributeException   If an xinclude fails
   */

  public static SDocument fromStream(
    final InputStream stream,
    final URI uri)
    throws
    ValidityException,
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
    final Document doc = SDocumentParser.fromStreamValidate(stream, uri);
    final Element root = doc.getRootElement();

    if ("document".equals(root.getLocalName())) {
      return SDocumentParser.document(root);
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Parse and validate a document from the given stream.
   *
   * @param stream An input stream
   * @param uri    The document URI
   *
   * @return A parsed and validated document
   *
   * @throws SAXException                 On XML parse errors
   * @throws ParserConfigurationException On parser configuration errors
   * @throws ValidityException            On XML validation errors
   * @throws ParsingException             On parser errors
   * @throws IOException                  On I/O errors
   * @throws XIncludeException            If an xinclude fails
   * @throws NoIncludeLocationException   If an xinclude fails
   * @throws InclusionLoopException       If an xinclude fails
   * @throws BadParseAttributeException   If an xinclude fails
   */

  static Document fromStreamValidate(
    final InputStream stream,
    final URI uri)
    throws
    SAXException,

    ParserConfigurationException,
    ValidityException,
    ParsingException,
    IOException,
    BadParseAttributeException,
    InclusionLoopException,
    NoIncludeLocationException,
    XIncludeException
  {
    NullCheck.notNull(stream, "Stream");

    SDocumentParser.LOG.debug("xml: creating sax parser");

    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    factory.setXIncludeAware(true);
    factory.setFeature("http://apache.org/xml/features/xinclude", true);

    SDocumentParser.LOG.debug("xml: opening xml.xsd");

    final InputStream xml_xsd =
      new URL(SSchema.getSchemaXMLXSDLocation().toString()).openStream();

    try {
      SDocumentParser.LOG.debug("xml: opening schema.xsd");

      final InputStream schema_xsd =
        new URL(SSchema.getSchemaXSDLocation().toString()).openStream();

      try {
        SDocumentParser.LOG.debug("xml: creating schema handler");

        final SchemaFactory schema_factory =
          SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        final Source[] sources = new Source[2];
        sources[0] = new StreamSource(xml_xsd);
        sources[1] = new StreamSource(schema_xsd);
        factory.setSchema(schema_factory.newSchema(sources));

        final TrivialErrorHandler handler = new TrivialErrorHandler();
        final SAXParser parser = factory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(handler);

        SDocumentParser.LOG.debug("xml: parsing and validating");
        final Builder builder = new Builder(reader);
        final Document doc = builder.build(stream, uri.toString());

        final SAXParseException ex = handler.getException();
        if (ex != null) {
          throw ex;
        }

        return doc;
      } finally {
        schema_xsd.close();
      }
    } finally {
      xml_xsd.close();
    }
  }

  private static
  @Nullable
  Element getElement(
    final Element element,
    final String name)
  {
    return element.getFirstChildElement(name, SXML.XML_URI.toString());
  }

  private static
  @Nullable
  Elements getElements(
    final Element element,
    final String name)
  {
    return element.getChildElements(name, SXML.XML_URI.toString());
  }

  private static
  @Nullable
  Integer heightAttribute(
    final Element ec)
  {
    final Attribute a = ec.getAttribute("height", SXML.XML_URI.toString());
    if (a == null) {
      return null;
    }
    return Integer.valueOf(a.getValue());
  }

  private static
  @Nullable
  SID idAttribute(
    final Element e)
  {
    final Attribute eid =
      e.getAttribute("id", "http://www.w3.org/XML/1998/namespace");
    if (eid == null) {
      return null;
    }
    return SID.newID(eid.getValue());
  }

  /**
   * Parse an image element.
   *
   * @param ec The raw element
   *
   * @return An image element
   *
   * @throws URISyntaxException On malformed URIs
   */

  public static SImage image(
    final Element ec)
    throws URISyntaxException
  {
    NullCheck.notNull(ec);

    final String type = SDocumentParser.typeAttribute(ec);
    final URI source = SDocumentParser.sourceAttribute(ec);
    final Integer width = SDocumentParser.widthAttribute(ec);
    final Integer height = SDocumentParser.heightAttribute(ec);
    final String text = ec.getValue();

    if (type != null) {
      if (width != null) {
        if (height != null) {
          return SImage.imageTypedWidthHeight(
            source, type, width.intValue(), height.intValue(), text);
        }
        return SImage.imageTypedWidth(source, type, width.intValue(), text);
      }
      if (height != null) {
        return SImage.imageTypedHeight(source, type, height.intValue(), text);
      }
      return SImage.imageTyped(source, type, text);
    }

    if (width != null) {
      if (height != null) {
        return SImage.imageWidthHeight(
          source, width.intValue(), height.intValue(), text);
      }
      return SImage.imageWidth(source, width.intValue(), text);
    }

    if (height != null) {
      return SImage.imageHeight(source, height.intValue(), text);
    }

    return SImage.image(source, text);
  }

  private static String kindAttribute(
    final Element e)
  {
    final Attribute et = e.getAttribute("kind", SXML.XML_URI.toString());
    assert et != null;
    final String r = et.getValue();
    assert r != null;
    return r;
  }

  /**
   * Parse a link element.
   *
   * @param ec The raw element
   *
   * @return A link element
   *
   * @throws URISyntaxException On malformed URIs
   */

  public static SLink link(
    final Element ec)
    throws URISyntaxException
  {
    NullCheck.notNull(ec);

    final String target = SDocumentParser.targetAttribute(ec);
    final SNonEmptyList<SLinkContent> content = SDocumentParser.linkContent(ec);
    return SLink.link(target, content);
  }

  private static SNonEmptyList<SLinkContent> linkContent(
    final Element ec)
    throws URISyntaxException
  {
    final List<SLinkContent> elements = new ArrayList<SLinkContent>();

    for (int index = 0; index < ec.getChildCount(); ++index) {
      final Node child = ec.getChild(index);
      if (child instanceof Text) {
        final Text et = (Text) child;
        elements.add(SText.text(et.getValue()));
        continue;
      } else if (child instanceof Element) {
        final Element ecc = (Element) child;
        if ("image".equals(ecc.getLocalName())) {
          elements.add(SDocumentParser.image(ecc));
          continue;
        }
        throw new UnreachableCodeException();
      } else {
        throw new UnreachableCodeException();
      }
    }

    return SNonEmptyList.newList(elements);
  }

  /**
   * Parse an external link.
   *
   * @param ec The raw element
   *
   * @return An external link
   *
   * @throws URISyntaxException On malformed URIs
   */

  public static SLinkExternal linkExternal(
    final Element ec)
    throws URISyntaxException
  {
    final URI target = new URI(SDocumentParser.targetAttribute(ec));
    final SNonEmptyList<SLinkContent> content = SDocumentParser.linkContent(ec);
    return SLinkExternal.link(target, content);
  }

  private static SListItem listItem(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("list-item: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItemContent> items = new ArrayList<SListItemContent>();
    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node ec = e.getChild(index);
      items.add(SDocumentParser.listItemContent(ec));
    }

    final SNonEmptyList<SListItemContent> content =
      SNonEmptyList.newList(items);
    if (type != null) {
      return SListItem.listItemTyped(type, content);
    }
    return SListItem.listItem(content);
  }

  private static SListItemContent listItemContent(
    final Node e)
    throws URISyntaxException
  {
    if (e instanceof Text) {
      return SText.text(e.getValue());
    }

    if (e instanceof Element) {
      final Element ee = (Element) e;

      if ("footnote".equals(ee.getLocalName())) {
        return SDocumentParser.footnote(ee);
      }
      if ("image".equals(ee.getLocalName())) {
        return SDocumentParser.image(ee);
      }
      if ("link".equals(ee.getLocalName())) {
        return SDocumentParser.link(ee);
      }
      if ("link-external".equals(ee.getLocalName())) {
        return SDocumentParser.linkExternal(ee);
      }
      if ("list-ordered".equals(ee.getLocalName())) {
        return SDocumentParser.listOrdered(ee);
      }
      if ("list-unordered".equals(ee.getLocalName())) {
        return SDocumentParser.listUnordered(ee);
      }
      if ("term".equals(ee.getLocalName())) {
        return SDocumentParser.term(ee);
      }
      if ("verbatim".equals(ee.getLocalName())) {
        return SDocumentParser.verbatim(ee);
      }
    }

    throw new UnreachableCodeException();
  }

  private static SListOrdered listOrdered(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("list-ordered: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItem> items = new ArrayList<SListItem>();
    final Elements children =
      e.getChildElements("list-item", SXML.XML_URI.toString());
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      items.add(SDocumentParser.listItem(ec));
    }

    final SNonEmptyList<SListItem> content = SNonEmptyList.newList(items);
    if (type != null) {
      return SListOrdered.listTyped(type, content);
    }
    return SListOrdered.list(content);
  }

  private static SListUnordered listUnordered(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("list-unordered: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItem> items = new ArrayList<SListItem>();
    final Elements children =
      e.getChildElements("list-item", SXML.XML_URI.toString());
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      items.add(SDocumentParser.listItem(ec));
    }

    final SNonEmptyList<SListItem> content = SNonEmptyList.newList(items);
    if (type != null) {
      return SListUnordered.listTyped(type, content);
    }
    return SListUnordered.list(content);
  }

  /**
   * Parse a paragraph element.
   *
   * @param e The raw element
   *
   * @return A paragraph element
   *
   * @throws URISyntaxException On malformed URIs
   */

  public static SParagraph paragraph(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("paragraph: starting");

    final SID id = SDocumentParser.idAttribute(e);
    final String type = SDocumentParser.typeAttribute(e);

    final List<SParagraphContent> elements = new ArrayList<SParagraphContent>();

    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node child = e.getChild(index);
      elements.add(SDocumentParser.paragraphContent(child));
    }

    final SNonEmptyList<SParagraphContent> content =
      SNonEmptyList.newList(elements);
    return SDocumentParser.paragraphMake(id, type, content);
  }

  private static SParagraphContent paragraphContent(
    final Node child)
    throws URISyntaxException
  {
    if (child instanceof Text) {
      final Text et = (Text) child;
      return SText.text(et.getValue());
    }

    if (child instanceof Element) {
      final Element ec = (Element) child;

      if ("footnote".equals(ec.getLocalName())) {
        return SDocumentParser.footnote(ec);
      }
      if ("formal-item-list".equals(ec.getLocalName())) {
        return SDocumentParser.formalItemList(ec);
      }
      if ("image".equals(ec.getLocalName())) {
        return SDocumentParser.image(ec);
      }
      if ("link".equals(ec.getLocalName())) {
        return SDocumentParser.link(ec);
      }
      if ("link-external".equals(ec.getLocalName())) {
        return SDocumentParser.linkExternal(ec);
      }
      if ("list-ordered".equals(ec.getLocalName())) {
        return SDocumentParser.listOrdered(ec);
      }
      if ("list-unordered".equals(ec.getLocalName())) {
        return SDocumentParser.listUnordered(ec);
      }
      if ("term".equals(ec.getLocalName())) {
        return SDocumentParser.term(ec);
      }
      if ("table".equals(ec.getLocalName())) {
        return SDocumentParser.table(ec);
      }
      if ("verbatim".equals(ec.getLocalName())) {
        return SDocumentParser.verbatim(ec);
      }
    }

    throw new UnreachableCodeException();
  }

  private static SParagraph paragraphMake(
    final @Nullable SID id,
    final @Nullable String type,
    final SNonEmptyList<SParagraphContent> content)
  {
    if (SDocumentParser.LOG.isDebugEnabled()) {
      SDocumentParser.LOG.debug(
        "paragraph: (id: {}) (type: {})",
        id != null ? id.getActual() : id,
        type);
    }

    if (id != null) {
      if (type != null) {
        return SParagraph.paragraphTypedID(type, id, content);
      }
      return SParagraph.paragraphID(id, content);
    }
    if (type != null) {
      return SParagraph.paragraphTyped(type, content);
    }
    return SParagraph.paragraph(content);
  }

  private static SPart part(
    final Element pe)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("part: starting");

    final SPartTitle title = SDocumentParser.partTitleRoot(pe);
    final boolean contents = SDocumentParser.partContentsRoot(pe);
    final SID id = SDocumentParser.idAttribute(pe);
    final String type = SDocumentParser.typeAttribute(pe);

    final Elements children = pe.getChildElements();

    final List<SSection> elements = new ArrayList<SSection>();

    for (int index = 0; index < children.size(); ++index) {
      final Element e = children.get(index);

      if ("part-title".equals(e.getLocalName())) {
        continue;
      }
      if ("part-contents".equals(e.getLocalName())) {
        continue;
      }

      if ("section".equals(e.getLocalName())) {
        elements.add(SDocumentParser.section(e));
      }
    }

    final SNonEmptyList<SSection> content = SNonEmptyList.newList(elements);
    return SDocumentParser.partMake(title, contents, id, type, content);
  }

  private static boolean partContentsRoot(
    final Element r)
  {
    final Element e = SDocumentParser.getElement(r, "part-contents");
    return e != null;
  }

  private static SPart partMake(
    final SPartTitle title,
    final boolean contents,
    final @Nullable SID id,
    final @Nullable String type,
    final SNonEmptyList<SSection> sections)
  {
    if (SDocumentParser.LOG.isDebugEnabled()) {
      SDocumentParser.LOG.debug(
        "part: (title: {}) (id: {}) (type: {}) ({})",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents");
    }

    if (contents) {
      if (id != null) {
        if (type != null) {
          return SPart.partWithContentsTypedID(type, id, title, sections);
        }
        return SPart.partWithContentsID(id, title, sections);
      }

      if (type != null) {
        return SPart.partWithContentsTyped(type, title, sections);
      }

      return SPart.partWithContents(title, sections);
    }

    if (id != null) {
      if (type != null) {
        return SPart.partTypedID(type, id, title, sections);
      }
      return SPart.partID(id, title, sections);
    }

    if (type != null) {
      return SPart.partTyped(type, title, sections);
    }

    return SPart.part(title, sections);
  }

  private static SPartTitle partTitleRoot(
    final Element e)
  {
    final Element r = SDocumentParser.getElement(e, "part-title");
    assert r != null;
    return SPartTitle.partTitle(r.getValue());
  }

  private static SSection section(
    final Element section)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("section: starting");

    final SSectionTitle title = SDocumentParser.sectionTitleRoot(section);
    final boolean contents = SDocumentParser.sectionContentsRoot(section);
    final SID id = SDocumentParser.idAttribute(section);
    final String type = SDocumentParser.typeAttribute(section);

    final Element esect = SDocumentParser.getElement(section, "subsection");
    if (esect != null) {
      return SDocumentParser.sectionWithSubsections(
        section, title, id, type, contents);
    }
    return SDocumentParser.sectionWithParagraphs(
      section, title, id, type, contents);
  }

  private static boolean sectionContentsRoot(
    final Element root)
  {
    final Element e = SDocumentParser.getElement(root, "section-contents");
    return e != null;
  }

  private static SSectionTitle sectionTitleRoot(
    final Element root)
  {
    final Element e = SDocumentParser.getElement(root, "section-title");
    assert e != null;
    return SSectionTitle.sectionTitle(e.getValue());
  }

  private static SSectionWithParagraphs sectionWithParagraphs(
    final Element section,
    final SSectionTitle title,
    final @Nullable SID id,
    final @Nullable String type,
    final boolean contents)
    throws URISyntaxException
  {
    final Elements children = section.getChildElements();

    final List<SSubsectionContent> elements =
      new ArrayList<SSubsectionContent>();

    for (int index = 0; index < children.size(); ++index) {
      final Element e = children.get(index);

      if ("section-title".equals(e.getLocalName())) {
        continue;
      }
      if ("section-contents".equals(e.getLocalName())) {
        continue;
      }

      elements.add(SDocumentParser.subsectionContent(e));
    }

    final SNonEmptyList<SSubsectionContent> content =
      SNonEmptyList.newList(elements);

    return SDocumentParser.sectionWithParagraphsMake(
      title, contents, id, type, content);
  }

  private static SSectionWithParagraphs sectionWithParagraphsMake(
    final SSectionTitle title,
    final boolean contents,
    final @Nullable SID id,
    final @Nullable String type,
    final SNonEmptyList<SSubsectionContent> content)
  {
    if (SDocumentParser.LOG.isDebugEnabled()) {
      SDocumentParser.LOG.debug(
        "section: (paragraphs) (title: {}) (id: {}) (type: {}) ({})",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents");
    }

    if (contents) {
      if (id != null) {
        if (type != null) {
          return SSectionWithParagraphs.sectionWithContentsTypedID(
            type, id, title, content);
        }
        return SSectionWithParagraphs.sectionWithContentsID(
          id, title, content);
      }

      if (type != null) {
        return SSectionWithParagraphs.sectionWithContentsTyped(
          type, title, content);
      }

      return SSectionWithParagraphs.sectionWithContents(title, content);
    }

    if (id != null) {
      if (type != null) {
        return SSectionWithParagraphs.sectionTypedID(type, id, title, content);
      }
      return SSectionWithParagraphs.sectionID(id, title, content);
    }

    if (type != null) {
      return SSectionWithParagraphs.sectionTyped(type, title, content);
    }

    return SSectionWithParagraphs.section(title, content);
  }

  private static SSectionWithSubsections sectionWithSubsections(
    final Element section,
    final SSectionTitle title,
    final @Nullable SID id,
    final @Nullable String type,
    final boolean contents)
    throws URISyntaxException
  {
    final Elements children = section.getChildElements();

    final List<SSubsection> elements = new ArrayList<SSubsection>();

    for (int index = 0; index < children.size(); ++index) {
      final Element e = children.get(index);

      if ("section-title".equals(e.getLocalName())) {
        continue;
      } else if ("section-contents".equals(e.getLocalName())) {
        continue;
      } else if ("subsection".equals(e.getLocalName())) {
        elements.add(SDocumentParser.subsection(e));
        continue;
      }

      throw new UnreachableCodeException();
    }

    final SNonEmptyList<SSubsection> content = SNonEmptyList.newList(elements);

    return SDocumentParser.sectionWithSubsectionsMake(
      title, contents, id, type, content);
  }

  private static SSectionWithSubsections sectionWithSubsectionsMake(
    final SSectionTitle title,
    final boolean contents,
    final @Nullable SID id,
    final @Nullable String type,
    final SNonEmptyList<SSubsection> content)
  {
    if (SDocumentParser.LOG.isDebugEnabled()) {
      SDocumentParser.LOG.debug(
        "section: (subsections) (title: {}) (id: {}) (type: {}) ({})",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents");
    }

    if (contents) {
      if (id != null) {
        if (type != null) {
          return SSectionWithSubsections.sectionWithContentsTypedID(
            type, id, title, content);
        }
        return SSectionWithSubsections.sectionWithContentsID(
          id, title, content);
      }

      if (type != null) {
        return SSectionWithSubsections.sectionWithContentsTyped(
          type, title, content);
      }

      return SSectionWithSubsections.sectionWithContents(title, content);
    }

    if (id != null) {
      if (type != null) {
        return SSectionWithSubsections.sectionTypedID(
          type, id, title, content);
      }
      return SSectionWithSubsections.sectionID(id, title, content);
    }

    if (type != null) {
      return SSectionWithSubsections.sectionTyped(type, title, content);
    }

    return SSectionWithSubsections.section(title, content);
  }

  private static URI sourceAttribute(
    final Element ec)
    throws URISyntaxException
  {
    final Attribute a = ec.getAttribute("source", SXML.XML_URI.toString());
    return new URI(a.getValue());
  }

  private static SSubsection subsection(
    final Element e)
    throws URISyntaxException
  {
    SDocumentParser.LOG.debug("subsection: starting");

    final SSubsectionTitle title = SDocumentParser.subsectionTitleRoot(e);
    final SID id = SDocumentParser.idAttribute(e);
    final String type = SDocumentParser.typeAttribute(e);

    final Elements children = e.getChildElements();

    final List<SSubsectionContent> elements =
      new ArrayList<SSubsectionContent>();

    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);

      if ("subsection-title".equals(ec.getLocalName())) {
        continue;
      }

      elements.add(SDocumentParser.subsectionContent(ec));
    }

    final SNonEmptyList<SSubsectionContent> content =
      SNonEmptyList.newList(elements);

    return SDocumentParser.subsectionMake(title, id, type, content);
  }

  private static SSubsectionContent subsectionContent(
    final Element e)
    throws URISyntaxException
  {
    if ("paragraph".equals(e.getLocalName())) {
      return SDocumentParser.paragraph(e);
    } else if ("formal-item".equals(e.getLocalName())) {
      return SDocumentParser.formalItem(e);
    }

    throw new UnreachableCodeException();
  }

  private static SSubsection subsectionMake(
    final SSubsectionTitle title,
    final @Nullable SID id,
    final @Nullable String type,
    final SNonEmptyList<SSubsectionContent> content)
  {
    if (SDocumentParser.LOG.isDebugEnabled()) {
      SDocumentParser.LOG.debug(
        "subsection: (title: {}) (id: {}) (type: {})",
        title.getActual(),
        id != null ? id.getActual() : id,
        type);
    }

    if (id != null) {
      if (type != null) {
        return SSubsection.subsectionTypedID(type, id, title, content);
      }
      return SSubsection.subsectionID(id, title, content);
    }

    if (type != null) {
      return SSubsection.subsectionTyped(type, title, content);
    }

    return SSubsection.subsection(title, content);
  }

  private static SSubsectionTitle subsectionTitleRoot(
    final Element e)
  {
    final Element ec = SDocumentParser.getElement(e, "subsection-title");
    assert ec != null;
    return SSubsectionTitle.subsectionTitle(ec.getValue());
  }

  private static STable table(
    final Element ec)
    throws URISyntaxException
  {
    final STableSummary summary = SDocumentParser.tableSummary(ec);
    final STableHead head = SDocumentParser.tableHead(ec);
    final STableBody body = SDocumentParser.tableBody(ec);
    if (head != null) {
      return STable.tableHeader(summary, head, body);
    }
    return STable.table(summary, body);
  }

  private static STableBody tableBody(
    final Element e)
    throws URISyntaxException
  {
    final Element ec = SDocumentParser.getElement(e, "table-body");
    assert ec != null;

    final List<STableRow> rows = new ArrayList<STableRow>();
    final Elements ecs =
      ec.getChildElements("table-row", SXML.XML_URI.toString());
    for (int index = 0; index < ecs.size(); ++index) {
      final Element ecc = ecs.get(index);
      rows.add(SDocumentParser.tableRow(ecc));
    }

    return STableBody.tableBody(SNonEmptyList.newList(rows));
  }

  private static STableCell tableCell(
    final Element e)
    throws URISyntaxException
  {
    final List<STableCellContent> content = new ArrayList<STableCellContent>();

    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node n = e.getChild(index);
      content.add(SDocumentParser.tableCellContent(n));
    }

    return STableCell.tableCell(content);
  }

  private static STableCellContent tableCellContent(
    final Node n)
    throws URISyntaxException
  {
    if (n instanceof Text) {
      final Text et = (Text) n;
      return SText.text(et.getValue());
    }

    if (n instanceof Element) {
      final Element ecc = (Element) n;
      if ("footnote".equals(ecc.getLocalName())) {
        return SDocumentParser.footnote(ecc);
      }
      if ("image".equals(ecc.getLocalName())) {
        return SDocumentParser.image(ecc);
      }
      if ("link".equals(ecc.getLocalName())) {
        return SDocumentParser.link(ecc);
      }
      if ("link-external".equals(ecc.getLocalName())) {
        return SDocumentParser.linkExternal(ecc);
      }
      if ("list-ordered".equals(ecc.getLocalName())) {
        return SDocumentParser.listOrdered(ecc);
      }
      if ("list-unordered".equals(ecc.getLocalName())) {
        return SDocumentParser.listUnordered(ecc);
      }
      if ("term".equals(ecc.getLocalName())) {
        return SDocumentParser.term(ecc);
      }
      if ("verbatim".equals(ecc.getLocalName())) {
        return SDocumentParser.verbatim(ecc);
      }
    }

    throw new UnreachableCodeException();
  }

  private static
  @Nullable
  STableHead tableHead(
    final Element e)
  {
    final Element ec = SDocumentParser.getElement(e, "table-head");
    if (ec != null) {

      final List<STableColumnName> names = new ArrayList<STableColumnName>();
      final Elements ecs =
        ec.getChildElements("table-column-name", SXML.XML_URI.toString());
      for (int index = 0; index < ecs.size(); ++index) {
        final Element ecc = ecs.get(index);
        names.add(STableColumnName.tableColumnName(ecc.getValue()));
      }

      return STableHead.tableHead(SNonEmptyList.newList(names));
    }
    return null;
  }

  private static STableRow tableRow(
    final Element e)
    throws URISyntaxException
  {
    final List<STableCell> cells = new ArrayList<STableCell>();
    final Elements ecs =
      e.getChildElements("table-cell", SXML.XML_URI.toString());
    for (int index = 0; index < ecs.size(); ++index) {
      final Element ecc = ecs.get(index);
      cells.add(SDocumentParser.tableCell(ecc));
    }

    return STableRow.tableRow(SNonEmptyList.newList(cells));
  }

  private static STableSummary tableSummary(
    final Element e)
  {
    final Element ec = SDocumentParser.getElement(e, "table-summary");
    assert ec != null;
    return STableSummary.tableSummary(ec.getValue());
  }

  private static String targetAttribute(
    final Element ec)
  {
    final Attribute a = ec.getAttribute("target", SXML.XML_URI.toString());
    return a.getValue();
  }

  /**
   * Parse a term element.
   *
   * @param ec The raw element
   *
   * @return A term element
   */

  public static STerm term(
    final Element ec)
  {
    NullCheck.notNull(ec);

    final String type = SDocumentParser.typeAttribute(ec);
    final SText text = SText.text(ec.getValue());
    if (type != null) {
      return STerm.termTyped(text, type);
    }
    return STerm.term(text);
  }

  private static
  @Nullable
  String typeAttribute(
    final Element e)
  {
    final Attribute et = e.getAttribute("type", SXML.XML_URI.toString());
    if (et == null) {
      return null;
    }
    return et.getValue();
  }

  /**
   * Parse a verbatim element.
   *
   * @param ec The raw element
   *
   * @return A verbatim element
   */

  public static SVerbatim verbatim(
    final Element ec)
  {
    NullCheck.notNull(ec);

    final String type = SDocumentParser.typeAttribute(ec);
    final String text = ec.getValue();
    if (type != null) {
      return SVerbatim.verbatimTyped(text, type);
    }
    return SVerbatim.verbatim(text);
  }

  private static
  @Nullable
  Integer widthAttribute(
    final Element ec)
  {
    final Attribute a = ec.getAttribute("width", SXML.XML_URI.toString());
    if (a == null) {
      return null;
    }
    return Integer.valueOf(a.getValue());
  }

  private static final class TrivialErrorHandler implements ErrorHandler
  {
    private @Nullable SAXParseException exception;

    private TrivialErrorHandler()
    {

    }

    @Override
    public void error(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SDocumentParser.LOG.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    @Override
    public void fatalError(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SDocumentParser.LOG.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    public
    @Nullable
    SAXParseException getException()
    {
      return this.exception;
    }

    @Override
    public void warning(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SDocumentParser.LOG.warn(e + ": " + e.getMessage());
      this.exception = e;
    }
  }
}
