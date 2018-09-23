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

package com.io7m.jstructural.writer.xml;

import com.io7m.jstructural.ast.SBlockContentType;
import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SImageType;
import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SInlineLinkContentType;
import com.io7m.jstructural.ast.SLinkExternalType;
import com.io7m.jstructural.ast.SLinkType;
import com.io7m.jstructural.ast.SListItemType;
import com.io7m.jstructural.ast.SListOrderedType;
import com.io7m.jstructural.ast.SListUnorderedType;
import com.io7m.jstructural.ast.SParagraphType;
import com.io7m.jstructural.ast.SSubsectionContentType;
import com.io7m.jstructural.ast.STermType;
import com.io7m.jstructural.ast.STextType;
import com.io7m.jstructural.ast.STypeableType;
import com.io7m.jstructural.ast.SVerbatimType;
import com.io7m.jstructural.compiler.api.SCompiledGlobalType;
import com.io7m.jstructural.compiler.api.SCompiledLocalType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A builder of XHTML elements.
 */

public final class SXHTMLBuilder
{
  /**
   * The XHTML namespace URI.
   */

  public static final URI XHTML_NAMESPACE_URI =
    URI.create("http://www.w3.org/1999/xhtml");

  /**
   * The XHTML namespace URI text.
   */

  public static final String XHTML_NAMESPACE =
    XHTML_NAMESPACE_URI.toString();

  private final Document document;

  private SXHTMLBuilder(
    final Document in_document)
  {
    this.document = Objects.requireNonNull(in_document, "document");
  }

  /**
   * @return A new XHTML builder
   */

  public static SXHTMLBuilder create()
  {
    try {
      final DocumentBuilderFactory document_builders =
        DocumentBuilderFactory.newDefaultInstance();
      final DocumentBuilder document_builder =
        document_builders.newDocumentBuilder();
      final Document document =
        document_builder.newDocument();

      return new SXHTMLBuilder(document);
    } catch (final ParserConfigurationException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void addClassAttribute(
    final Element element,
    final STypeableType<SCompiledLocalType> typeable)
  {
    typeable.type().ifPresent(type -> {
      element.setAttribute(
        "class",
        new StringBuilder(64)
          .append(typeNameForCSSClass(typeable))
          .append(' ')
          .append(type.value())
          .toString());
    });
  }

  private static String typeNameForCSSClass(
    final STypeableType<SCompiledLocalType> typeable)
  {
    final StringBuilder text = new StringBuilder(32);
    text.append("st");

    final String base = typeable.getClass().getSimpleName().substring(1);
    base.codePoints().forEach(c -> {
      if (Character.isUpperCase(c)) {
        text.append("_");
      }
      text.appendCodePoint(Character.toLowerCase(c));
    });

    return text.toString();
  }

  /**
   * Construct an XHTML element for the given text.
   *
   * @param text The text
   *
   * @return An XHTML element
   */

  public Text text(
    final STextType<SCompiledLocalType> text)
  {
    Objects.requireNonNull(text, "text");

    return this.document.createTextNode(text.text());
  }

  /**
   * Construct an XHTML element for the given term.
   *
   * @param term The term
   *
   * @return An XHTML element
   */

  public Element term(
    final STermType<SCompiledLocalType> term)
  {
    Objects.requireNonNull(term, "term");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "span");
    addClassAttribute(element, term);
    term.text().forEach(text -> element.appendChild(this.text(text)));
    return element;
  }

  /**
   * Construct an XHTML element for the given image.
   *
   * @param image The image
   *
   * @return An XHTML element
   */

  public Element image(
    final SImageType<SCompiledLocalType> image)
  {
    Objects.requireNonNull(image, "image");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "img");
    element.setAttribute("href", image.source().toString());

    image.size().ifPresent(size -> {
      element.setAttribute("width", Integer.toUnsignedString(size.width()));
      element.setAttribute("height", Integer.toUnsignedString(size.height()));
    });
    addClassAttribute(element, image);
    image.text().forEach(text -> element.appendChild(this.text(text)));
    return element;
  }

  /**
   * Construct an XHTML element for the given link.
   *
   * @param link The link
   *
   * @return An XHTML element
   */

  public Element linkExternal(
    final SLinkExternalType<SCompiledLocalType> link)
  {
    Objects.requireNonNull(link, "link");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "a");
    element.setAttribute("href", link.target().toString());

    addClassAttribute(element, link);
    link.content().forEach(content -> element.appendChild(this.linkContent(content)));
    return element;
  }

  /**
   * Construct an XHTML element for the given link.
   *
   * @param links The link provider
   * @param link  The link
   *
   * @return An XHTML element
   */

  public Element link(
    final SXHTMLLinkProviderType links,
    final SLinkType<SCompiledLocalType> link)
  {
    Objects.requireNonNull(links, "links");
    Objects.requireNonNull(link, "link");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "a");
    element.setAttribute("href", links.linkOf(link.target()).formatted());

    addClassAttribute(element, link);
    link.content().forEach(content -> element.appendChild(this.linkContent(content)));
    return element;
  }

  /**
   * Construct an XHTML element for the given footnote reference.
   *
   * @param links The link provider
   * @param ref   The reference
   *
   * @return An XHTML element
   */

  public Element footnoteReference(
    final SXHTMLLinkProviderType links,
    final SFootnoteReference<SCompiledLocalType> ref)
  {
    Objects.requireNonNull(links, "links");
    Objects.requireNonNull(ref, "ref");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "span");
    addClassAttribute(element, ref);

    final Element link = this.document.createElementNS(XHTML_NAMESPACE, "a");
    link.setAttribute("href", links.linkOf(ref.target()).formatted());

    final SCompiledGlobalType global = ref.data().global();
    link.setTextContent(global.footnoteIndexOf(global.findFootnoteForID(ref.target())).toString());

    element.appendChild(this.document.createTextNode("["));
    element.appendChild(link);
    element.appendChild(this.document.createTextNode("]"));
    return element;
  }

  /**
   * Construct an XHTML element for the given footnote reference.
   *
   * @param links The link provider
   * @param ref   The reference
   *
   * @return An XHTML element
   */

  public Element formalItemReference(
    final SXHTMLLinkProviderType links,
    final SFormalItemReference<SCompiledLocalType> ref)
  {
    Objects.requireNonNull(links, "links");
    Objects.requireNonNull(ref, "ref");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "span");
    addClassAttribute(element, ref);

    final Element link = this.document.createElementNS(XHTML_NAMESPACE, "a");
    link.setAttribute("href", links.linkOf(ref.target()).formatted());

    final SCompiledGlobalType global = ref.data().global();
    link.setTextContent(global.findFormalItemForID(ref.target()).data().number().toHumanString());

    element.appendChild(this.document.createTextNode("["));
    element.appendChild(link);
    element.appendChild(this.document.createTextNode("]"));
    return element;
  }

  /**
   * Construct an XHTML element for the given verbatim.
   *
   * @param verbatim The verbatim
   *
   * @return An XHTML element
   */

  public Element verbatim(
    final SVerbatimType<SCompiledLocalType> verbatim)
  {
    Objects.requireNonNull(verbatim, "verbatim");

    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "pre");
    addClassAttribute(element, verbatim);
    element.setTextContent(verbatim.text().text());
    return element;
  }

  /**
   * Construct an XHTML element for the given ordered list.
   *
   * @param links A link provider
   * @param list  The list
   *
   * @return An XHTML element
   */

  public Element listOrdered(
    final SXHTMLLinkProviderType links,
    final SListOrderedType<SCompiledLocalType> list)
  {
    Objects.requireNonNull(list, "list");
    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "ol");
    addClassAttribute(element, list);
    return this.listItems(links, element, list.items());
  }

  /**
   * Construct an XHTML element for the given ordered list.
   *
   * @param links A link provider
   * @param list  The list
   *
   * @return An XHTML element
   */

  public Element listUnordered(
    final SXHTMLLinkProviderType links,
    final SListUnorderedType<SCompiledLocalType> list)
  {
    Objects.requireNonNull(list, "list");
    final Element element = this.document.createElementNS(XHTML_NAMESPACE, "ul");
    addClassAttribute(element, list);
    return this.listItems(links, element, list.items());
  }

  private Element listItems(
    final SXHTMLLinkProviderType links,
    final Element element,
    final Vector<SListItemType<SCompiledLocalType>> items)
  {
    items.forEach(item -> {
      final Element item_element = this.document.createElementNS(XHTML_NAMESPACE, "li");
      addClassAttribute(item_element, item);
      item.content().forEach(content -> item_element.appendChild(this.inlineAny(links, content)));
      element.appendChild(item_element);
    });
    return element;
  }

  /**
   * Construct an XHTML element for the given link content.
   *
   * @param content The link content
   *
   * @return An XHTML element
   */

  public Node linkContent(
    final SInlineLinkContentType<SCompiledLocalType> content)
  {
    Objects.requireNonNull(content, "content");

    switch (content.inlineLinkKind()) {
      case INLINE_LINK_TEXT:
        return this.text((STextType<SCompiledLocalType>) content);
      case INLINE_LINK_IMAGE:
        return this.image((SImageType<SCompiledLocalType>) content);
    }

    throw new UnreachableCodeException();
  }

  /**
   * Construct an XHTML element for the given verbatim.
   *
   * @param links   A link provider
   * @param content The content
   *
   * @return An XHTML element
   */

  public Node inlineAny(
    final SXHTMLLinkProviderType links,
    final SInlineAnyContentType<SCompiledLocalType> content)
  {
    Objects.requireNonNull(content, "content");

    switch (content.inlineKind()) {
      case INLINE_TEXT:
        return this.text((STextType<SCompiledLocalType>) content);
      case INLINE_TERM:
        return this.term((STermType<SCompiledLocalType>) content);
      case INLINE_IMAGE:
        return this.image((SImageType<SCompiledLocalType>) content);
      case INLINE_LINK:
        return this.link(links, (SLinkType<SCompiledLocalType>) content);
      case INLINE_LINK_EXTERNAL:
        return this.linkExternal((SLinkExternalType<SCompiledLocalType>) content);
      case INLINE_FOOTNOTE_REFERENCE:
        return this.footnoteReference(links, (SFootnoteReference<SCompiledLocalType>) content);
      case INLINE_FORMAL_ITEM_REFERENCE:
        return this.formalItemReference(links, (SFormalItemReference<SCompiledLocalType>) content);
      case INLINE_VERBATIM:
        return this.verbatim((SVerbatimType<SCompiledLocalType>) content);
      case INLINE_LIST_ORDERED:
        return this.listOrdered(links, (SListOrderedType<SCompiledLocalType>) content);
      case INLINE_LIST_UNORDERED:
        return this.listUnordered(links, (SListUnorderedType<SCompiledLocalType>) content);
      case INLINE_TABLE:
        throw new UnimplementedCodeException();
    }

    throw new UnreachableCodeException();
  }

  /**
   * Construct an XHTML element for the given paragraph.
   *
   * @param links A link provider
   * @param paragraph  The paragraph
   *
   * @return An XHTML element
   */

  public Element paragraph(
    final SXHTMLLinkProviderType links,
    final SParagraphType<SCompiledLocalType> paragraph)
  {
    Objects.requireNonNull(paragraph, "paragraph");

    final Element container =
      this.document.createElementNS(XHTML_NAMESPACE, "div");
    addClassAttribute(container, paragraph);

    final Element number_container =
      this.document.createElementNS(XHTML_NAMESPACE, "div");

    final Element number_link = this.document.createElementNS(XHTML_NAMESPACE, "a");
    number_link.setTextContent(paragraph.data().number().components().last().toString());
    number_link.setAttribute("id", anchorOf(paragraph));
    number_container.appendChild(number_link);

    final Element content_container =
      this.document.createElementNS(XHTML_NAMESPACE, "div");

    paragraph.content().forEach(
      paragraph_content -> content_container.appendChild(this.inlineAny(links, paragraph_content)));

    container.appendChild(number_container);
    container.appendChild(content_container);
    return container;
  }

  private static String anchorOf(
    final SBlockContentType<SCompiledLocalType> block)
  {
    switch (block.blockKind()) {
      case BLOCK_SUBSECTION_CONTENT:
        final SSubsectionContentType<SCompiledLocalType> ssc =
          (SSubsectionContentType<SCompiledLocalType>) block;
        switch (ssc.subsectionContentKind()) {
          case SUBSECTION_PARAGRAPH:
            return "st_paragraph_" + underscoredNumberOf(ssc.data());
          case SUBSECTION_FORMAL_ITEM:
            return "st_formal_" + underscoredNumberOf(ssc.data());
          case SUBSECTION_FOOTNOTE:
            return "st_footnote" + underscoredNumberOf(ssc.data());
        }
        throw new UnreachableCodeException();

      case BLOCK_SUBSECTION:
        return "st_subsection_" + underscoredNumberOf(block.data());
      case BLOCK_SECTION:
        return "st_section_" + underscoredNumberOf(block.data());
      case BLOCK_DOCUMENT:
        return "st_document_" + underscoredNumberOf(block.data());
    }

    throw new UnreachableCodeException();
  }

  private static String underscoredNumberOf(
    final SCompiledLocalType data)
  {
    return data.number()
      .components()
      .map(BigInteger::toString)
      .collect(Collectors.joining("_"));
  }
}