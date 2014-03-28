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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnimplementedCodeException;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;
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

/**
 * A document parser that uses XOM to process documents.
 */

public final class SDocumentParser
{
  private static class TrivialErrorHandler implements ErrorHandler
  {
    private @CheckForNull SAXParseException exception;
    private final @Nonnull Log              log;

    public TrivialErrorHandler(
      final @Nonnull Log in_log)
    {
      this.log = in_log;
    }

    @Override public void error(
      final SAXParseException e)
      throws SAXException
    {
      this.log.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    @Override public void fatalError(
      final SAXParseException e)
      throws SAXException
    {
      this.log.critical(e + ": " + e.getMessage());
      this.exception = e;
    }

    public @CheckForNull SAXParseException getException()
    {
      return this.exception;
    }

    @Override public void warning(
      final SAXParseException e)
      throws SAXException
    {
      this.log.warn(e + ": " + e.getMessage());
      this.exception = e;
    }
  }

  /**
   * Attempt to parse a document from the given element. The element is
   * assumed to have been validated with the <code>structural</code> schema.
   * 
   * @param log
   *          A log handle
   * @param e
   *          The element
   * @return A document
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   * @throws URISyntaxException
   *           If parsing a URI fails, internally
   */

  static @Nonnull SDocument document(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    Constraints.constrainNotNull(log, "Log");
    Constraints.constrainNotNull(e, "Element");

    log.debug("document: starting");

    final SDocumentTitle title = SDocumentParser.documentTitleRoot(e);
    final SDocumentStyle style = SDocumentParser.documentStyleRoot(e);
    final boolean contents = SDocumentParser.documentContentsRoot(e);
    final Element esect = SDocumentParser.getElement(e, "section");
    if (esect != null) {
      return SDocumentParser.documentWithSections(
        log,
        e,
        title,
        style,
        contents);
    }
    return SDocumentParser.documentWithParts(log, e, title, style, contents);
  }

  private static boolean documentContentsRoot(
    final @Nonnull Element root)
  {
    final Element e = SDocumentParser.getElement(root, "document-contents");
    return e != null;
  }

  private static @CheckForNull SDocumentStyle documentStyleRoot(
    final @Nonnull Element root)
    throws ConstraintError,
      URISyntaxException
  {
    final Element e = SDocumentParser.getElement(root, "document-style");
    if (e == null) {
      return null;
    }
    return SDocumentStyle.documentStyle(new URI(e.getValue()));
  }

  private static @Nonnull SDocumentTitle documentTitleRoot(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final Element e = SDocumentParser.getElement(root, "document-title");
    return SDocumentTitle.documentTitle(e.getValue());
  }

  private static SDocument documentWithParts(
    final @Nonnull Log log,
    final @Nonnull Element root,
    final @Nonnull SDocumentTitle title,
    final @CheckForNull SDocumentStyle style,
    final boolean contents)
    throws ConstraintError,
      URISyntaxException
  {
    final Elements parts = SDocumentParser.getElements(root, "part");
    assert parts != null;
    assert parts.size() > 0;

    final List<SPart> elements = new ArrayList<SPart>();
    for (int index = 0; index < parts.size(); ++index) {
      final SPart part = SDocumentParser.part(log, parts.get(index));
      elements.add(part);
    }

    final SNonEmptyList<SPart> content = SNonEmptyList.newList(elements);
    return SDocumentParser.documentWithPartsMake(
      title,
      style,
      contents,
      content);
  }

  private static SDocument documentWithPartsMake(
    final @Nonnull SDocumentTitle title,
    final @CheckForNull SDocumentStyle style,
    final boolean contents,
    final @Nonnull SNonEmptyList<SPart> content)
    throws ConstraintError
  {
    if (contents) {
      if (style != null) {
        return SDocumentWithParts
          .documentStyleContents(title, style, content);
      }
      return SDocumentWithParts.documentContents(title, content);
    }

    if (style != null) {
      return SDocumentWithParts.documentStyle(title, style, content);
    }
    return SDocumentWithParts.document(title, content);
  }

  private static @Nonnull SDocument documentWithSections(
    final @Nonnull Log log,
    final @Nonnull Element root,
    final @Nonnull SDocumentTitle title,
    final @CheckForNull SDocumentStyle style,
    final boolean contents)
    throws ConstraintError,
      URISyntaxException
  {
    final Elements sections = SDocumentParser.getElements(root, "section");
    assert sections != null;
    assert sections.size() > 0;

    final List<SSection> elements = new ArrayList<SSection>();
    for (int index = 0; index < sections.size(); ++index) {
      final SSection section =
        SDocumentParser.section(log, sections.get(index));
      elements.add(section);
    }

    final SNonEmptyList<SSection> content = SNonEmptyList.newList(elements);
    return SDocumentParser.documentWithSectionsMake(
      title,
      style,
      contents,
      content);
  }

  private static @Nonnull SDocumentWithSections documentWithSectionsMake(
    final @Nonnull SDocumentTitle title,
    final @CheckForNull SDocumentStyle style,
    final boolean contents,
    final @Nonnull SNonEmptyList<SSection> content)
    throws ConstraintError
  {
    if (contents) {
      if (style != null) {
        return SDocumentWithSections.documentStyleContents(
          title,
          style,
          content);
      }
      return SDocumentWithSections.documentContents(title, content);
    }

    if (style != null) {
      return SDocumentWithSections.documentStyle(title, style, content);
    }
    return SDocumentWithSections.document(title, content);
  }

  private static @Nonnull SFootnote footnote(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws URISyntaxException,
      ConstraintError
  {
    log.debug("footnote: starting");

    final List<SFootnoteContent> content_nodes =
      new ArrayList<SFootnoteContent>();
    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node ec = e.getChild(index);
      content_nodes.add(SDocumentParser.footnoteContent(log, ec));
    }

    final SNonEmptyList<SFootnoteContent> content =
      SNonEmptyList.newList(content_nodes);
    return SFootnote.footnote(content);
  }

  private static @Nonnull SFootnoteContent footnoteContent(
    final @Nonnull Log log,
    final @Nonnull Node c)
    throws ConstraintError,
      URISyntaxException
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
        return SDocumentParser.footnote(log, ecc);
      }
    }

    throw new UnreachableCodeException();
  }

  private static @Nonnull SFormalItem formalItem(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    final String type = SDocumentParser.typeAttribute(e);
    final String kind = SDocumentParser.kindAttribute(e);
    final SFormalItemTitle title = SDocumentParser.formalItemTitleRoot(e);

    final Elements children = e.getChildElements();
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      if ("formal-item-title".equals(ec.getLocalName())) {
        continue;
      }
      return SDocumentParser.formalItemMake(
        type,
        kind,
        title,
        SDocumentParser.formalItemContent(log, ec));
    }

    throw new UnreachableCodeException();
  }

  private static @Nonnull SFormalItemContent formalItemContent(
    final @Nonnull Log log,
    final @Nonnull Element ec)
    throws URISyntaxException,
      ConstraintError
  {
    if ("formal-item-list".equals(ec.getLocalName())) {
      return SDocumentParser.formalItemList(ec);
    }
    if ("image".equals(ec.getLocalName())) {
      return SDocumentParser.image(ec);
    }
    if ("list-ordered".equals(ec.getLocalName())) {
      return SDocumentParser.listOrdered(log, ec);
    }
    if ("list-unordered".equals(ec.getLocalName())) {
      return SDocumentParser.listUnordered(log, ec);
    }
    if ("table".equals(ec.getLocalName())) {
      return SDocumentParser.table(log, ec);
    }
    if ("verbatim".equals(ec.getLocalName())) {
      return SDocumentParser.verbatim(ec);
    }

    throw new UnreachableCodeException();
  }

  private static SFormalItemList formalItemList(
    final @Nonnull Element e)
    throws ConstraintError
  {
    final String kind = SDocumentParser.kindAttribute(e);
    return SFormalItemList.formalItemList(kind);
  }

  private static @Nonnull SFormalItem formalItemMake(
    final @CheckForNull String type,
    final @Nonnull String kind,
    final @Nonnull SFormalItemTitle title,
    final @Nonnull SFormalItemContent content)
    throws ConstraintError
  {
    if (type != null) {
      return SFormalItem.formalItemTyped(title, kind, type, content);
    }
    return SFormalItem.formalItem(title, kind, content);
  }

  private static SFormalItemTitle formalItemTitleRoot(
    final Element e)
    throws ConstraintError
  {
    final Element ec = SDocumentParser.getElement(e, "formal-item-title");
    return SFormalItemTitle.formalItemTitle(ec.getValue());
  }

  /**
   * Parse a document from a validated stream.
   * 
   * @param uri
   *          The base URI of the document
   * @param stream
   *          The stream
   * @param log
   *          A log handle
   * @return A document
   * 
   * @throws SAXException
   *           On XML parse errors
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   * @throws ParserConfigurationException
   *           On parser configuration errors
   * @throws ValidityException
   *           On XML validation errors
   * @throws ParsingException
   *           On parser errors
   * @throws IOException
   *           On I/O errors
   * @throws URISyntaxException
   *           On failing to parse a URI
   * @throws XIncludeException
   *           If an xinclude fails
   * @throws NoIncludeLocationException
   *           If an xinclude fails
   * @throws InclusionLoopException
   *           If an xinclude fails
   * @throws BadParseAttributeException
   *           If an xinclude fails
   */

  public static @Nonnull SDocument fromStream(
    final @Nonnull InputStream stream,
    final @Nonnull URI uri,
    final @Nonnull Log log)
    throws ConstraintError,
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
    final Log lp = new Log(log, "parser");
    final Document doc = SDocumentParser.fromStreamValidate(stream, uri, log);
    final Element root = doc.getRootElement();

    if ("document".equals(root.getLocalName())) {
      return SDocumentParser.document(lp, root);
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Parse and validate a document from the given stream.
   * 
   * @param stream
   *          An input stream
   * @param uri
   * @param log
   *          A log handle
   * @return A parsed and validated document
   * 
   * @throws SAXException
   *           On XML parse errors
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   * @throws ParserConfigurationException
   *           On parser configuration errors
   * @throws ValidityException
   *           On XML validation errors
   * @throws ParsingException
   *           On parser errors
   * @throws IOException
   *           On I/O errors
   * @throws XIncludeException
   *           If an xinclude fails
   * @throws NoIncludeLocationException
   *           If an xinclude fails
   * @throws InclusionLoopException
   *           If an xinclude fails
   * @throws BadParseAttributeException
   *           If an xinclude fails
   */

  static @Nonnull Document fromStreamValidate(
    final @Nonnull InputStream stream,
    final @Nonnull URI uri,
    final @Nonnull Log log)
    throws SAXException,
      ConstraintError,
      ParserConfigurationException,
      ValidityException,
      ParsingException,
      IOException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      XIncludeException
  {
    Constraints.constrainNotNull(stream, "Stream");
    Constraints.constrainNotNull(log, "Log");

    final Log log_xml = new Log(log, "xml");

    log_xml.debug("creating sax parser");

    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    factory.setXIncludeAware(true);
    factory.setFeature("http://apache.org/xml/features/xinclude", true);

    log_xml.debug("opening xml.xsd");

    final InputStream xml_xsd =
      new URL(SSchema.getSchemaXMLXSDLocation().toString()).openStream();

    try {
      log_xml.debug("opening schema.xsd");

      final InputStream schema_xsd =
        new URL(SSchema.getSchemaXSDLocation().toString()).openStream();

      try {
        log_xml.debug("creating schema handler");

        final SchemaFactory schema_factory =
          SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        final Source[] sources = new Source[2];
        sources[0] = new StreamSource(xml_xsd);
        sources[1] = new StreamSource(schema_xsd);
        factory.setSchema(schema_factory.newSchema(sources));

        final TrivialErrorHandler handler = new TrivialErrorHandler(log_xml);
        final SAXParser parser = factory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(handler);

        log_xml.debug("parsing and validating");
        final Builder builder = new Builder(reader);
        final Document doc = builder.build(stream, uri.toString());

        if (handler.getException() != null) {
          throw handler.getException();
        }

        return doc;
      } finally {
        schema_xsd.close();
      }
    } finally {
      xml_xsd.close();
    }
  }

  private static @CheckForNull Element getElement(
    final @Nonnull Element element,
    final @Nonnull String name)
  {
    return element.getFirstChildElement(name, SXML.XML_URI.toString());
  }

  private static @CheckForNull Elements getElements(
    final @Nonnull Element element,
    final @Nonnull String name)
  {
    return element.getChildElements(name, SXML.XML_URI.toString());
  }

  private static @CheckForNull Integer heightAttribute(
    final @Nonnull Element ec)
  {
    final Attribute a = ec.getAttribute("height", SXML.XML_URI.toString());
    if (a == null) {
      return null;
    }
    return Integer.valueOf(a.getValue());
  }

  private static @CheckForNull SID idAttribute(
    final @Nonnull Element e)
    throws ConstraintError
  {
    final Attribute eid =
      e.getAttribute("id", "http://www.w3.org/XML/1998/namespace");
    if (eid == null) {
      return null;
    }
    return SID.newID(eid.getValue());
  }

  static @Nonnull SImage image(
    final @Nonnull Element ec)
    throws ConstraintError,
      URISyntaxException
  {
    final String type = SDocumentParser.typeAttribute(ec);
    final URI source = SDocumentParser.sourceAttribute(ec);
    final Integer width = SDocumentParser.widthAttribute(ec);
    final Integer height = SDocumentParser.heightAttribute(ec);
    final String text = ec.getValue();

    if (type != null) {
      if (width != null) {
        if (height != null) {
          return SImage.imageTypedWidthHeight(
            source,
            type,
            width.intValue(),
            height.intValue(),
            text);
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
          source,
          width.intValue(),
          height.intValue(),
          text);
      }
      return SImage.imageWidth(source, width.intValue(), text);
    }

    if (height != null) {
      return SImage.imageHeight(source, height.intValue(), text);
    }

    return SImage.image(source, text);
  }

  private static @CheckForNull String kindAttribute(
    final @Nonnull Element e)
  {
    final Attribute et = e.getAttribute("kind", SXML.XML_URI.toString());
    if (et == null) {
      return null;
    }
    return et.getValue();
  }

  static @Nonnull SLink link(
    final @Nonnull Element ec)
    throws URISyntaxException,
      ConstraintError
  {
    final String target = SDocumentParser.targetAttribute(ec);
    final SNonEmptyList<SLinkContent> content =
      SDocumentParser.linkContent(ec);
    return SLink.link(target, content);
  }

  private static @Nonnull SNonEmptyList<SLinkContent> linkContent(
    final @Nonnull Element ec)
    throws ConstraintError,
      URISyntaxException
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

  static @Nonnull SLinkExternal linkExternal(
    final @Nonnull Element ec)
    throws URISyntaxException,
      ConstraintError
  {
    final URI target = new URI(SDocumentParser.targetAttribute(ec));
    final SNonEmptyList<SLinkContent> content =
      SDocumentParser.linkContent(ec);
    return SLinkExternal.link(target, content);
  }

  private static @Nonnull SListItem listItem(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("list-item: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItemContent> items = new ArrayList<SListItemContent>();
    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node ec = e.getChild(index);
      items.add(SDocumentParser.listItemContent(log, ec));
    }

    final SNonEmptyList<SListItemContent> content =
      SNonEmptyList.newList(items);
    if (type != null) {
      return SListItem.listItemTyped(type, content);
    }
    return SListItem.listItem(content);
  }

  private static @Nonnull SListItemContent listItemContent(
    final @Nonnull Log log,
    final @Nonnull Node e)
    throws ConstraintError,
      URISyntaxException
  {
    if (e instanceof Text) {
      return SText.text(e.getValue());
    }

    if (e instanceof Element) {
      final Element ee = (Element) e;

      if ("footnote".equals(ee.getLocalName())) {
        return SDocumentParser.footnote(log, ee);
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
        return SDocumentParser.listOrdered(log, ee);
      }
      if ("list-unordered".equals(ee.getLocalName())) {
        return SDocumentParser.listUnordered(log, ee);
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

  private static @Nonnull SListOrdered listOrdered(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("list-ordered: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItem> items = new ArrayList<SListItem>();
    final Elements children =
      e.getChildElements("list-item", SXML.XML_URI.toString());
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      items.add(SDocumentParser.listItem(log, ec));
    }

    final SNonEmptyList<SListItem> content = SNonEmptyList.newList(items);
    if (type != null) {
      return SListOrdered.listTyped(type, content);
    }
    return SListOrdered.list(content);
  }

  private static @Nonnull SListUnordered listUnordered(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("list-unordered: starting");

    final String type = SDocumentParser.typeAttribute(e);
    final List<SListItem> items = new ArrayList<SListItem>();
    final Elements children =
      e.getChildElements("list-item", SXML.XML_URI.toString());
    for (int index = 0; index < children.size(); ++index) {
      final Element ec = children.get(index);
      items.add(SDocumentParser.listItem(log, ec));
    }

    final SNonEmptyList<SListItem> content = SNonEmptyList.newList(items);
    if (type != null) {
      return SListUnordered.listTyped(type, content);
    }
    return SListUnordered.list(content);
  }

  static @Nonnull SParagraph paragraph(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("paragraph: starting");

    final SID id = SDocumentParser.idAttribute(e);
    final String type = SDocumentParser.typeAttribute(e);

    final List<SParagraphContent> elements =
      new ArrayList<SParagraphContent>();

    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node child = e.getChild(index);
      elements.add(SDocumentParser.paragraphContent(log, child));
    }

    final SNonEmptyList<SParagraphContent> content =
      SNonEmptyList.newList(elements);
    return SDocumentParser.paragraphMake(log, id, type, content);
  }

  private static SParagraphContent paragraphContent(
    final @Nonnull Log log,
    final @Nonnull Node child)
    throws URISyntaxException,
      ConstraintError
  {
    if (child instanceof Text) {
      final Text et = (Text) child;
      return SText.text(et.getValue());
    }

    if (child instanceof Element) {
      final Element ec = (Element) child;

      if ("footnote".equals(ec.getLocalName())) {
        return SDocumentParser.footnote(log, ec);
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
        return SDocumentParser.listOrdered(log, ec);
      }
      if ("list-unordered".equals(ec.getLocalName())) {
        return SDocumentParser.listUnordered(log, ec);
      }
      if ("term".equals(ec.getLocalName())) {
        return SDocumentParser.term(ec);
      }
      if ("table".equals(ec.getLocalName())) {
        return SDocumentParser.table(log, ec);
      }
      if ("verbatim".equals(ec.getLocalName())) {
        return SDocumentParser.verbatim(ec);
      }
    }

    throw new UnreachableCodeException();
  }

  private static SParagraph paragraphMake(
    final @Nonnull Log log,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final @Nonnull SNonEmptyList<SParagraphContent> content)
    throws ConstraintError
  {
    if (log.enabled(Level.LOG_DEBUG)) {
      log.debug(String.format("paragraph: (id: %s) (type: %s)", id != null
        ? id.getActual()
        : id, type));
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
    final @Nonnull Log log,
    final @Nonnull Element pe)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("part: starting");

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
        elements.add(SDocumentParser.section(log, e));
      }
    }

    final SNonEmptyList<SSection> content = SNonEmptyList.newList(elements);
    return SDocumentParser.partMake(log, title, contents, id, type, content);
  }

  private static boolean partContentsRoot(
    final @Nonnull Element r)
  {
    final Element e = SDocumentParser.getElement(r, "part-contents");
    return e != null;
  }

  private static SPart partMake(
    final @Nonnull Log log,
    final @Nonnull SPartTitle title,
    final boolean contents,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final @Nonnull SNonEmptyList<SSection> sections)
    throws ConstraintError
  {
    if (log.enabled(Level.LOG_DEBUG)) {
      log.debug(String.format(
        "part: (title: %s) (id: %s) (type: %s) (%s)",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents"));
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
    final @Nonnull Element e)
    throws ConstraintError
  {
    final Element r = SDocumentParser.getElement(e, "part-title");
    return SPartTitle.partTitle(r.getValue());
  }

  private static @Nonnull SSection section(
    final @Nonnull Log log,
    final @Nonnull Element section)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("section: starting");

    final SSectionTitle title = SDocumentParser.sectionTitleRoot(section);
    final boolean contents = SDocumentParser.sectionContentsRoot(section);
    final SID id = SDocumentParser.idAttribute(section);
    final String type = SDocumentParser.typeAttribute(section);

    final Element esect = SDocumentParser.getElement(section, "subsection");
    if (esect != null) {
      return SDocumentParser.sectionWithSubsections(
        log,
        section,
        title,
        id,
        type,
        contents);
    }
    return SDocumentParser.sectionWithParagraphs(
      log,
      section,
      title,
      id,
      type,
      contents);
  }

  private static boolean sectionContentsRoot(
    final @Nonnull Element root)
  {
    final Element e = SDocumentParser.getElement(root, "section-contents");
    return e != null;
  }

  private static @Nonnull SSectionTitle sectionTitleRoot(
    final @Nonnull Element root)
    throws ConstraintError
  {
    final Element e = SDocumentParser.getElement(root, "section-title");
    return SSectionTitle.sectionTitle(e.getValue());
  }

  private static SSectionWithParagraphs sectionWithParagraphs(
    final @Nonnull Log log,
    final @Nonnull Element section,
    final @Nonnull SSectionTitle title,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final boolean contents)
    throws ConstraintError,
      URISyntaxException
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

      elements.add(SDocumentParser.subsectionContent(log, e));
    }

    final SNonEmptyList<SSubsectionContent> content =
      SNonEmptyList.newList(elements);

    return SDocumentParser.sectionWithParagraphsMake(
      log,
      title,
      contents,
      id,
      type,
      content);
  }

  private static SSectionWithParagraphs sectionWithParagraphsMake(
    final @Nonnull Log log,
    final @Nonnull SSectionTitle title,
    final boolean contents,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final @Nonnull SNonEmptyList<SSubsectionContent> content)
    throws ConstraintError
  {
    if (log.enabled(Level.LOG_DEBUG)) {
      log.debug(String.format(
        "section: (paragraphs) (title: %s) (id: %s) (type: %s) (%s)",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents"));
    }

    if (contents) {
      if (id != null) {
        if (type != null) {
          return SSectionWithParagraphs.sectionWithContentsTypedID(
            type,
            id,
            title,
            content);
        }
        return SSectionWithParagraphs.sectionWithContentsID(
          id,
          title,
          content);
      }

      if (type != null) {
        return SSectionWithParagraphs.sectionWithContentsTyped(
          type,
          title,
          content);
      }

      return SSectionWithParagraphs.sectionWithContents(title, content);
    }

    if (id != null) {
      if (type != null) {
        return SSectionWithParagraphs
          .sectionTypedID(type, id, title, content);
      }
      return SSectionWithParagraphs.sectionID(id, title, content);
    }

    if (type != null) {
      return SSectionWithParagraphs.sectionTyped(type, title, content);
    }

    return SSectionWithParagraphs.section(title, content);
  }

  private static SSectionWithSubsections sectionWithSubsections(
    final @Nonnull Log log,
    final @Nonnull Element section,
    final @Nonnull SSectionTitle title,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final boolean contents)
    throws ConstraintError,
      URISyntaxException
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
        elements.add(SDocumentParser.subsection(log, e));
        continue;
      }

      throw new UnreachableCodeException();
    }

    final SNonEmptyList<SSubsection> content =
      SNonEmptyList.newList(elements);

    return SDocumentParser.sectionWithSubsectionsMake(
      log,
      title,
      contents,
      id,
      type,
      content);
  }

  private static SSectionWithSubsections sectionWithSubsectionsMake(
    final @Nonnull Log log,
    final @Nonnull SSectionTitle title,
    final boolean contents,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final @Nonnull SNonEmptyList<SSubsection> content)
    throws ConstraintError
  {
    if (log.enabled(Level.LOG_DEBUG)) {
      log.debug(String.format(
        "section: (subsections) (title: %s) (id: %s) (type: %s) (%s)",
        title.getActual(),
        id != null ? id.getActual() : id,
        type,
        contents ? "contents" : "no contents"));
    }

    if (contents) {
      if (id != null) {
        if (type != null) {
          return SSectionWithSubsections.sectionWithContentsTypedID(
            type,
            id,
            title,
            content);
        }
        return SSectionWithSubsections.sectionWithContentsID(
          id,
          title,
          content);
      }

      if (type != null) {
        return SSectionWithSubsections.sectionWithContentsTyped(
          type,
          title,
          content);
      }

      return SSectionWithSubsections.sectionWithContents(title, content);
    }

    if (id != null) {
      if (type != null) {
        return SSectionWithSubsections.sectionTypedID(
          type,
          id,
          title,
          content);
      }
      return SSectionWithSubsections.sectionID(id, title, content);
    }

    if (type != null) {
      return SSectionWithSubsections.sectionTyped(type, title, content);
    }

    return SSectionWithSubsections.section(title, content);
  }

  private static @Nonnull URI sourceAttribute(
    final @Nonnull Element ec)
    throws URISyntaxException
  {
    final Attribute a = ec.getAttribute("source", SXML.XML_URI.toString());
    return new URI(a.getValue());
  }

  private static SSubsection subsection(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    log.debug("subsection: starting");

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

      elements.add(SDocumentParser.subsectionContent(log, ec));
    }

    final SNonEmptyList<SSubsectionContent> content =
      SNonEmptyList.newList(elements);

    return SDocumentParser.subsectionMake(log, title, id, type, content);
  }

  private static SSubsectionContent subsectionContent(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws URISyntaxException,
      ConstraintError
  {
    if ("paragraph".equals(e.getLocalName())) {
      return SDocumentParser.paragraph(log, e);
    } else if ("formal-item".equals(e.getLocalName())) {
      return SDocumentParser.formalItem(log, e);
    }

    throw new UnreachableCodeException();
  }

  private static @Nonnull SSubsection subsectionMake(
    final @Nonnull Log log,
    final @Nonnull SSubsectionTitle title,
    final @CheckForNull SID id,
    final @CheckForNull String type,
    final @Nonnull SNonEmptyList<SSubsectionContent> content)
    throws ConstraintError
  {
    if (log.enabled(Level.LOG_DEBUG)) {
      log.debug(String.format(
        "subsection: (title: %s) (id: %s) (type: %s)",
        title.getActual(),
        id != null ? id.getActual() : id,
        type));
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
    throws ConstraintError
  {
    final Element ec = SDocumentParser.getElement(e, "subsection-title");
    return SSubsectionTitle.subsectionTitle(ec.getValue());
  }

  private static @Nonnull STable table(
    final @Nonnull Log log,
    final @Nonnull Element ec)
    throws ConstraintError,
      URISyntaxException
  {
    final STableSummary summary = SDocumentParser.tableSummary(ec);
    final STableHead head = SDocumentParser.tableHead(ec);
    final STableBody body = SDocumentParser.tableBody(log, ec);
    if (head != null) {
      return STable.tableHeader(summary, head, body);
    }
    return STable.table(summary, body);
  }

  private static @Nonnull STableBody tableBody(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    final Element ec = SDocumentParser.getElement(e, "table-body");

    final List<STableRow> rows = new ArrayList<STableRow>();
    final Elements ecs =
      ec.getChildElements("table-row", SXML.XML_URI.toString());
    for (int index = 0; index < ecs.size(); ++index) {
      final Element ecc = ecs.get(index);
      rows.add(SDocumentParser.tableRow(log, ecc));
    }

    return STableBody.tableBody(SNonEmptyList.newList(rows));
  }

  private static STableCell tableCell(
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    final List<STableCellContent> content =
      new ArrayList<STableCellContent>();

    for (int index = 0; index < e.getChildCount(); ++index) {
      final Node n = e.getChild(index);
      content.add(SDocumentParser.tableCellContent(log, n));
    }

    return STableCell.tableCell(SNonEmptyList.newList(content));
  }

  private static @Nonnull STableCellContent tableCellContent(
    final @Nonnull Log log,
    final @Nonnull Node n)
    throws ConstraintError,
      URISyntaxException
  {
    if (n instanceof Text) {
      final Text et = (Text) n;
      return SText.text(et.getValue());
    }

    if (n instanceof Element) {
      final Element ecc = (Element) n;
      if ("footnote".equals(ecc.getLocalName())) {
        return SDocumentParser.footnote(log, ecc);
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
        return SDocumentParser.listOrdered(log, ecc);
      }
      if ("list-unordered".equals(ecc.getLocalName())) {
        return SDocumentParser.listUnordered(log, ecc);
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

  private static @CheckForNull STableHead tableHead(
    final @Nonnull Element e)
    throws ConstraintError
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
    final @Nonnull Log log,
    final @Nonnull Element e)
    throws ConstraintError,
      URISyntaxException
  {
    final List<STableCell> cells = new ArrayList<STableCell>();
    final Elements ecs =
      e.getChildElements("table-cell", SXML.XML_URI.toString());
    for (int index = 0; index < ecs.size(); ++index) {
      final Element ecc = ecs.get(index);
      cells.add(SDocumentParser.tableCell(log, ecc));
    }

    return STableRow.tableRow(SNonEmptyList.newList(cells));
  }

  private static @Nonnull STableSummary tableSummary(
    final @Nonnull Element e)
    throws ConstraintError
  {
    final Element ec = SDocumentParser.getElement(e, "table-summary");
    return STableSummary.tableSummary(ec.getValue());
  }

  private static @Nonnull String targetAttribute(
    final @Nonnull Element ec)
  {
    final Attribute a = ec.getAttribute("target", SXML.XML_URI.toString());
    return a.getValue();
  }

  static @Nonnull STerm term(
    final @Nonnull Element ec)
    throws ConstraintError
  {
    final String type = SDocumentParser.typeAttribute(ec);
    final SText text = SText.text(ec.getValue());
    if (type != null) {
      return STerm.termTyped(text, type);
    }
    return STerm.term(text);
  }

  private static @CheckForNull String typeAttribute(
    final @Nonnull Element e)
  {
    final Attribute et = e.getAttribute("type", SXML.XML_URI.toString());
    if (et == null) {
      return null;
    }
    return et.getValue();
  }

  static @Nonnull SVerbatim verbatim(
    final @Nonnull Element ec)
    throws ConstraintError
  {
    final String type = SDocumentParser.typeAttribute(ec);
    final String text = ec.getValue();
    if (type != null) {
      return SVerbatim.verbatimTyped(text, type);
    }
    return SVerbatim.verbatim(text);
  }

  private static @CheckForNull Integer widthAttribute(
    final @Nonnull Element ec)
  {
    final Attribute a = ec.getAttribute("width", SXML.XML_URI.toString());
    if (a == null) {
      return null;
    }
    return Integer.valueOf(a.getValue());
  }

  private SDocumentParser()
  {

  }
}
