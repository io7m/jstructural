/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.compact

import com.io7m.jstructural.core.SNonEmptyList
import com.io7m.junreachable.UnreachableCodeException
import org.valid4j.Assertive
import java.net.URI
import java.net.URISyntaxException
import java.util.Deque
import java.util.HashMap
import java.util.Optional
import java.util.OptionalInt

class SCParser : SCParserType {

  private object CommandMatchers {

    val symbol =
      SCExpressionMatch.anySymbol()
    val string =
      SCExpressionMatch.anyString()
    val symbol_or_string =
      SCExpressionMatch.oneOf(listOf(symbol, string))

    val id_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.ID)
    val id =
      SCExpressionMatch.allOfList(listOf(id_name, symbol))

    val type_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.TYPE)
    val type =
      SCExpressionMatch.allOfList(listOf(type_name, symbol))

    val target_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.TARGET)
    val target =
      SCExpressionMatch.allOfList(listOf(target_name, symbol_or_string))

    val size_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.SIZE)
    val size =
      SCExpressionMatch.allOfList(listOf(size_name, symbol, symbol))

    val image_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.IMAGE)
    val image =
      SCExpressionMatch.prefixOfList(listOf(image_name, target, symbol_or_string))
    val image_with_type =
      SCExpressionMatch.prefixOfList(listOf(image_name, target, type, symbol_or_string))
    val image_with_size =
      SCExpressionMatch.prefixOfList(listOf(image_name, target, size, symbol_or_string))
    val image_with_type_size =
      SCExpressionMatch.prefixOfList(listOf(image_name, target, type, size, symbol_or_string))

    val para_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.PARA)
    val para_none =
      SCExpressionMatch.allOfList(listOf(para_name))
    val para_with_id =
      SCExpressionMatch.allOfList(listOf(para_name, id))
    val para_with_id_type =
      SCExpressionMatch.allOfList(listOf(para_name, id, type))
    val para_with_type_id =
      SCExpressionMatch.allOfList(listOf(para_name, type, id))
    val para_with_type =
      SCExpressionMatch.allOfList(listOf(para_name, type))

    val title_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.TITLE)
    val title =
      SCExpressionMatch.prefixOfList(listOf(title_name, symbol_or_string))

    val part_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.PART)
    val part_none =
      SCExpressionMatch.allOfList(listOf(part_name, title))
    val part_with_id =
      SCExpressionMatch.allOfList(listOf(part_name, title, id))
    val part_with_id_type =
      SCExpressionMatch.allOfList(listOf(part_name, title, id, type))
    val part_with_type_id =
      SCExpressionMatch.allOfList(listOf(part_name, title, type, id))
    val part_with_type =
      SCExpressionMatch.allOfList(listOf(part_name, title, type))

    val section_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.SECTION)
    val section_none =
      SCExpressionMatch.allOfList(listOf(section_name, title))
    val section_with_id =
      SCExpressionMatch.allOfList(listOf(section_name, title, id))
    val section_with_id_type =
      SCExpressionMatch.allOfList(listOf(section_name, title, id, type))
    val section_with_type_id =
      SCExpressionMatch.allOfList(listOf(section_name, title, type, id))
    val section_with_type =
      SCExpressionMatch.allOfList(listOf(section_name, title, type))

    val subsection_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.SUBSECTION)
    val subsection_none =
      SCExpressionMatch.allOfList(listOf(subsection_name, title))
    val subsection_with_id =
      SCExpressionMatch.allOfList(listOf(subsection_name, title, id))
    val subsection_with_id_type =
      SCExpressionMatch.allOfList(listOf(subsection_name, title, id, type))
    val subsection_with_type_id =
      SCExpressionMatch.allOfList(listOf(subsection_name, title, type, id))
    val subsection_with_type =
      SCExpressionMatch.allOfList(listOf(subsection_name, title, type))

    val document_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.DOCUMENT)
    val document_none =
      SCExpressionMatch.allOfList(listOf(document_name, title))

    val term_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.TERM)
    val term_type =
      SCExpressionMatch.prefixOfList(listOf(term_name, type, symbol_or_string))
    val term =
      SCExpressionMatch.prefixOfList(listOf(term_name, symbol_or_string))

    val verbatim_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.VERBATIM)
    val verbatim =
      SCExpressionMatch.prefixOfList(listOf(verbatim_name, SCExpressionMatch.anyString()))
    val verbatim_type =
      SCExpressionMatch.prefixOfList(listOf(verbatim_name, type, SCExpressionMatch.anyString()))

    val import_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.IMPORT)
    val import =
      SCExpressionMatch.allOfList(listOf(import_name, string))

    val include_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.INCLUDE)
    val include =
      SCExpressionMatch.allOfList(listOf(include_name, string))

    val link_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.LINK)
    val link =
      SCExpressionMatch.prefixOfList(listOf(link_name, target))

    val link_ext_name =
      SCExpressionMatch.exactSymbol(SCCommandNames.LINK_EXT)
    val link_ext =
      SCExpressionMatch.prefixOfList(listOf(link_ext_name, target))
  }

  private fun parseBlockImport(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockImport {

    if (SCExpressionMatch.matches(e, CommandMatchers.import)) {
      Assertive.require(e.elements.size == 2)
      val content = (e.elements[1] as SCExpression.EQuoted).text
      return SCBlock.SCBlockImport(e.lexical, content)
    }

    eq.add(failedToMatch(e, listOf(CommandMatchers.import)))
    throw SCException.SCParseException()
  }

  private fun parseBlockPara(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockParagraph {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)

    when {
      SCExpressionMatch.matches(e, CommandMatchers.para_none)         -> {
        Assertive.require(e.elements.size == 1)
        return SCBlock.SCBlockParagraph(
          e.lexical,
          id = Optional.empty(),
          type = Optional.empty())
      }
      SCExpressionMatch.matches(e, CommandMatchers.para_with_id)      -> {
        Assertive.require(e.elements.size == 2)
        val id = parseAttributeID(e.elements[1] as SCExpression.EList)
        return SCBlock.SCBlockParagraph(
          e.lexical,
          id = Optional.of(id),
          type = Optional.empty())
      }
      SCExpressionMatch.matches(e, CommandMatchers.para_with_id_type) -> {
        Assertive.require(e.elements.size == 3)
        val id = parseAttributeID(e.elements[1] as SCExpression.EList)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockParagraph(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type))
      }
      SCExpressionMatch.matches(e, CommandMatchers.para_with_type_id) -> {
        Assertive.require(e.elements.size == 3)
        val type = parseAttributeType(e.elements[1] as SCExpression.EList)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockParagraph(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type))
      }
      SCExpressionMatch.matches(e, CommandMatchers.para_with_type)    -> {
        Assertive.require(e.elements.size == 2)
        val type = parseAttributeType(e.elements[1] as SCExpression.EList)
        return SCBlock.SCBlockParagraph(
          e.lexical,
          id = Optional.empty(),
          type = Optional.of(type))
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.para_none,
      CommandMatchers.para_with_id,
      CommandMatchers.para_with_id_type,
      CommandMatchers.para_with_type_id,
      CommandMatchers.para_with_type)))
    throw SCException.SCParseException()
  }

  private fun parseBlockDocument(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockDocument {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)

    when {
      SCExpressionMatch.matches(e, CommandMatchers.document_none) -> {
        Assertive.require(e.elements.size == 2)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        return SCBlock.SCBlockDocument(
          e.lexical,
          title = title)
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.document_none)))
    throw SCException.SCParseException()
  }

  private fun parseBlockPart(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockPart {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)

    when {
      SCExpressionMatch.matches(e, CommandMatchers.part_none)         -> {
        Assertive.require(e.elements.size == 2)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        return SCBlock.SCBlockPart(
          e.lexical,
          id = Optional.empty(),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.part_with_id)      -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockPart(
          e.lexical,
          id = Optional.of(id),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.part_with_id_type) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        val type = parseAttributeType(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockPart(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.part_with_type_id) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        val id = parseAttributeID(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockPart(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.part_with_type)    -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockPart(
          e.lexical,
          id = Optional.empty(),
          type = Optional.of(type),
          title = title)
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.part_none,
      CommandMatchers.part_with_id,
      CommandMatchers.part_with_id_type,
      CommandMatchers.part_with_type_id,
      CommandMatchers.part_with_type)))
    throw SCException.SCParseException()
  }

  private fun parseBlockSection(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockSection {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)

    when {
      SCExpressionMatch.matches(e, CommandMatchers.section_none)         -> {
        Assertive.require(e.elements.size == 2)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        return SCBlock.SCBlockSection(
          e.lexical,
          id = Optional.empty(),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.section_with_id)      -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockSection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.section_with_id_type) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        val type = parseAttributeType(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockSection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.section_with_type_id) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        val id = parseAttributeID(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockSection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.section_with_type)    -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockSection(
          e.lexical,
          id = Optional.empty(),
          type = Optional.of(type),
          title = title)
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.section_none,
      CommandMatchers.section_with_id,
      CommandMatchers.section_with_id_type,
      CommandMatchers.section_with_type_id,
      CommandMatchers.section_with_type)))
    throw SCException.SCParseException()
  }

  private fun parseBlockSubsection(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCBlock.SCBlockSubsection {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)

    when {
      SCExpressionMatch.matches(e, CommandMatchers.subsection_none)         -> {
        Assertive.require(e.elements.size == 2)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        return SCBlock.SCBlockSubsection(
          e.lexical,
          id = Optional.empty(),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.subsection_with_id)      -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockSubsection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.empty(),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.subsection_with_id_type) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val id = parseAttributeID(e.elements[2] as SCExpression.EList)
        val type = parseAttributeType(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockSubsection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.subsection_with_type_id) -> {
        Assertive.require(e.elements.size == 4)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        val id = parseAttributeID(e.elements[3] as SCExpression.EList)
        return SCBlock.SCBlockSubsection(
          e.lexical,
          id = Optional.of(id),
          type = Optional.of(type),
          title = title)
      }
      SCExpressionMatch.matches(e, CommandMatchers.subsection_with_type)    -> {
        Assertive.require(e.elements.size == 3)
        val title = parseAttributeTitle(e.elements[1] as SCExpression.EList, eq)
        val type = parseAttributeType(e.elements[2] as SCExpression.EList)
        return SCBlock.SCBlockSubsection(
          e.lexical,
          id = Optional.empty(),
          type = Optional.of(type),
          title = title)
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.subsection_none,
      CommandMatchers.subsection_with_id,
      CommandMatchers.subsection_with_id_type,
      CommandMatchers.subsection_with_type_id,
      CommandMatchers.subsection_with_type)))
    throw SCException.SCParseException()
  }

  private fun parseAttributeType(e : SCExpression.EList) : String {
    Assertive.require(e.elements.size == 2)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    Assertive.require(e.elements[1] is SCExpression.ESymbol)
    return (e.elements[1] as SCExpression.ESymbol).text
  }

  private fun parseAttributeID(e : SCExpression.EList) : SCID {
    Assertive.require(e.elements.size == 2)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    Assertive.require(e.elements[1] is SCExpression.ESymbol)
    val sym = e.elements[1] as SCExpression.ESymbol
    return SCID(sym.lexical, sym.text)
  }

  private fun parseAttributeTarget(e : SCExpression.EList) : String {
    Assertive.require(e.elements.size == 2)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    val target = e.elements[1]
    return when (target) {
      is SCExpression.EList   -> throw UnreachableCodeException()
      is SCExpression.ESymbol -> target.text
      is SCExpression.EQuoted -> target.text
    }
  }

  private fun parseAttributeTitle(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SNonEmptyList<SCInline.SCInlineText> {
    Assertive.require(e.elements.size >= 2)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    val texts = e.elements.subList(1, e.elements.size)
    return SNonEmptyList.newList(texts.map { e -> parseInlineText(e, eq) })
  }

  private fun parseAttributeTargetAsURI(
    e : SCExpression.EList,
    eq : Deque<SCError>) : URI {
    val text = parseAttributeTarget(e)
    return try {
      URI(text)
    } catch (x : URISyntaxException) {
      eq.add(SCError.SCParseError(e.lexical, "Invalid URI: " + x.message))
      throw SCException.SCParseException()
    }
  }

  private fun parseInlineTerm(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineTerm {

    when {
      SCExpressionMatch.matches(e, CommandMatchers.term_type) -> {
        Assertive.require(e.elements.size >= 3)
        val texts = e.elements.subList(2, e.elements.size)
        Assertive.require(texts.size >= 1)
        val type = parseAttributeType(e.elements[1] as SCExpression.EList)
        val content = texts.map { et -> parseInlineText(et, eq) }
        return SCInline.SCInlineTerm(e.lexical, Optional.of(type), content)
      }

      SCExpressionMatch.matches(e, CommandMatchers.term)      -> {
        Assertive.require(e.elements.size >= 2)
        val texts = e.elements.subList(1, e.elements.size)
        Assertive.require(texts.size >= 1)
        val content = texts.map { et -> parseInlineText(et, eq) }
        return SCInline.SCInlineTerm(e.lexical, Optional.empty(), content)
      }
    }

    eq.add(failedToMatch(e, listOf(CommandMatchers.term_type, CommandMatchers.term)))
    throw SCException.SCParseException()
  }

  private data class Size(
    val width : Int,
    val height : Int)

  private fun parseAttributeSize(
    e : SCExpression.EList,
    eq : Deque<SCError>) : Size {
    Assertive.require(e.elements.size == 3)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    Assertive.require(e.elements[1] is SCExpression.ESymbol)
    Assertive.require(e.elements[2] is SCExpression.ESymbol)

    return try {
      val w = Integer.parseUnsignedInt((e.elements[1] as SCExpression.ESymbol).text)
      val h = Integer.parseUnsignedInt((e.elements[2] as SCExpression.ESymbol).text)
      Size(w, h)
    } catch (x : NumberFormatException) {
      eq.add(SCError.SCParseError(e.lexical, "Invalid width or height: " + x.message))
      throw SCException.SCParseException()
    }
  }

  private fun parseInlineImage(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineImage {

    when {
      SCExpressionMatch.matches(e, CommandMatchers.image_with_type_size) -> {
        Assertive.require(e.elements.size >= 5)
        val target = parseAttributeTargetAsURI(e.elements[1] as SCExpression.EList, eq)
        val type = Optional.of(parseAttributeType(e.elements[2] as SCExpression.EList))
        val size = parseAttributeSize(e.elements[3] as SCExpression.EList, eq)
        val width = OptionalInt.of(size.width)
        val height = OptionalInt.of(size.height)
        val texts = e.elements.subList(4, e.elements.size)
        val content = texts.map { e -> parseInlineText(e, eq) }
        return SCInline.SCInlineImage(e.lexical, type, target, width, height, content)
      }
      SCExpressionMatch.matches(e, CommandMatchers.image_with_type)      -> {
        Assertive.require(e.elements.size >= 4)
        val target = parseAttributeTargetAsURI(e.elements[1] as SCExpression.EList, eq)
        val type = Optional.of(parseAttributeType(e.elements[2] as SCExpression.EList))
        val width = OptionalInt.empty()
        val height = OptionalInt.empty()
        val texts = e.elements.subList(3, e.elements.size)
        val content = texts.map { e -> parseInlineText(e, eq) }
        return SCInline.SCInlineImage(e.lexical, type, target, width, height, content)
      }
      SCExpressionMatch.matches(e, CommandMatchers.image_with_size)      -> {
        Assertive.require(e.elements.size >= 4)
        val target = parseAttributeTargetAsURI(e.elements[1] as SCExpression.EList, eq)
        val type = Optional.empty<String>()
        val size = parseAttributeSize(e.elements[2] as SCExpression.EList, eq)
        val width = OptionalInt.of(size.width)
        val height = OptionalInt.of(size.height)
        val texts = e.elements.subList(3, e.elements.size)
        val content = texts.map { e -> parseInlineText(e, eq) }
        return SCInline.SCInlineImage(e.lexical, type, target, width, height, content)
      }
      SCExpressionMatch.matches(e, CommandMatchers.image)                -> {
        Assertive.require(e.elements.size >= 3)
        val target = parseAttributeTargetAsURI(e.elements[1] as SCExpression.EList, eq)
        val type = Optional.empty<String>()
        val width = OptionalInt.empty()
        val height = OptionalInt.empty()
        val texts = e.elements.subList(2, e.elements.size)
        val content = texts.map { e -> parseInlineText(e, eq) }
        return SCInline.SCInlineImage(e.lexical, type, target, width, height, content)
      }
    }

    eq.add(failedToMatch(e, listOf(
      CommandMatchers.image,
      CommandMatchers.image_with_size,
      CommandMatchers.image_with_type,
      CommandMatchers.image_with_type_size)))
    throw SCException.SCParseException()
  }

  private fun parseInlineInclude(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineInclude {

    if (SCExpressionMatch.matches(e, CommandMatchers.include)) {
      Assertive.require(e.elements.size == 2)
      val content = (e.elements[1] as SCExpression.EQuoted).text
      return SCInline.SCInlineInclude(e.lexical, content)
    }

    eq.add(failedToMatch(e, listOf(CommandMatchers.include)))
    throw SCException.SCParseException()
  }

  private fun parseInlineVerbatim(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineVerbatim {

    when {
      SCExpressionMatch.matches(e, CommandMatchers.verbatim_type) -> {
        Assertive.require(e.elements.size == 3)
        val type = parseAttributeType(e.elements[1] as SCExpression.EList)
        val content = (e.elements[2] as SCExpression.EQuoted).text
        return SCInline.SCInlineVerbatim(e.lexical, Optional.of(type), content)
      }
      SCExpressionMatch.matches(e, CommandMatchers.verbatim)      -> {
        Assertive.require(e.elements.size == 2)
        val content = (e.elements[1] as SCExpression.EQuoted).text
        return SCInline.SCInlineVerbatim(e.lexical, Optional.empty(), content)
      }
    }

    eq.add(failedToMatch(e, listOf(CommandMatchers.verbatim_type, CommandMatchers.verbatim)))
    throw SCException.SCParseException()
  }

  private fun parseInlineLinkInternal(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineLink {
    return SCInline.SCInlineLink(e.lexical, parseLinkInternal(e, eq))
  }

  private fun parseInlineLinkExternal(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCInline.SCInlineLink {
    return SCInline.SCInlineLink(e.lexical, parseLinkExternal(e, eq))
  }

  private fun parseInlineText(
    e : SCExpression,
    eq : Deque<SCError>) : SCInline.SCInlineText {
    when (e) {
      is SCExpression.EList   -> {
        val sb = StringBuilder()
        sb.append("Expected text, but received an inline command.")
        sb.append(System.lineSeparator())
        sb.append("  Expected: Text")
        sb.append(System.lineSeparator())
        sb.append("  Received: ")
        sb.append(e)
        sb.append(System.lineSeparator())
        eq.add(SCError.SCParseError(e.lexical, sb.toString()))
        throw SCException.SCParseException()
      }
      is SCExpression.ESymbol ->
        return SCInline.SCInlineText(e.lexical, e.text)
      is SCExpression.EQuoted ->
        return SCInline.SCInlineText(e.lexical, e.text)
    }
  }

  private fun parseInlineAny(
    e : SCExpression,
    eq : Deque<SCError>) : SCInline {
    return when (e) {
      is SCExpression.EQuoted ->
        SCInline.SCInlineText(e.lexical, e.text)
      is SCExpression.ESymbol ->
        SCInline.SCInlineText(e.lexical, e.text)
      is SCExpression.EList   -> {
        if (!SCExpressionMatch.matches(e, isInlineCommand)) {
          val sb = StringBuilder()
          sb.append("Expected an inline command.")
          sb.append(System.lineSeparator())
          sb.append("  Expected: ")
          sb.append(isInlineCommand)
          sb.append(System.lineSeparator())
          sb.append("  Received: ")
          sb.append(e)
          sb.append(System.lineSeparator())
          eq.add(SCError.SCParseError(e.lexical, sb.toString()))
          throw SCException.SCParseException()
        }

        val name = commandName(e)
        Assertive.require(inlineCommands.containsKey(name))
        val ic = inlineCommands.get(name)!!
        Assertive.require(ic.name == name)
        return ic.parser.invoke(e, eq)
      }
    }
  }

  private fun parseLinkInternal(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCLink.SCLinkInternal {

    if (SCExpressionMatch.matches(e, CommandMatchers.link)) {
      Assertive.require(e.elements.size >= 3)
      val texts = e.elements.subList(2, e.elements.size)
      Assertive.require(texts.size >= 1)

      val target = parseAttributeTarget(e.elements[1] as SCExpression.EList)
      val content = SNonEmptyList.newList(texts.map { e -> parseLinkContent(e, eq) })
      return SCLink.SCLinkInternal(e.lexical, SCID(e.elements[1].lexical, target), content)
    } else {
      eq.add(failedToMatch(e, listOf(CommandMatchers.link)))
      throw SCException.SCParseException()
    }
  }

  private fun parseLinkExternal(
    e : SCExpression.EList,
    eq : Deque<SCError>) : SCLink.SCLinkExternal {

    if (SCExpressionMatch.matches(e, CommandMatchers.link_ext)) {
      Assertive.require(e.elements.size >= 3)
      val texts = e.elements.subList(2, e.elements.size)
      Assertive.require(texts.size >= 1)

      val target = parseAttributeTarget(e.elements[1] as SCExpression.EList)
      try {
        val uri = URI(target)
        val content = SNonEmptyList.newList(texts.map { e -> parseLinkContent(e, eq) })
        return SCLink.SCLinkExternal(e.lexical, uri, content)
      } catch (x : URISyntaxException) {
        eq.add(SCError.SCParseError(e.lexical, "Invalid URI: " + x.message))
        throw SCException.SCParseException()
      }

    } else {
      eq.add(failedToMatch(e, listOf(CommandMatchers.link_ext)))
      throw SCException.SCParseException()
    }
  }

  private fun parseLinkContent(
    e : SCExpression,
    eq : Deque<SCError>) : SCLinkContent {
    return when (e) {
      is SCExpression.EList   -> {
        val ii = parseInlineAny(e, eq)
        when (ii) {
          is SCInline.SCInlineInclude  ->
            SCLinkContent.SCLinkInclude(ii)
          is SCInline.SCInlineImage    ->
            SCLinkContent.SCLinkImage(ii)
          is SCInline.SCInlineText     -> {
            // A list cannot be parsed as text
            throw UnreachableCodeException()
          }
          is SCInline.SCInlineLink     -> {
            eq.add(SCError.SCParseError(e.lexical, "Link commands cannot appear inside link commands"))
            throw SCException.SCParseException()
          }
          is SCInline.SCInlineVerbatim -> {
            eq.add(SCError.SCParseError(e.lexical, "Verbatim commands cannot appear inside link commands"))
            throw SCException.SCParseException()
          }
          is SCInline.SCInlineTerm     -> {
            eq.add(SCError.SCParseError(e.lexical, "Term commands cannot appear inside link commands"))
            throw SCException.SCParseException()
          }
        }
      }
      is SCExpression.ESymbol ->
        SCLinkContent.SCLinkText(SCInline.SCInlineText(e.lexical, e.text))
      is SCExpression.EQuoted ->
        SCLinkContent.SCLinkText(SCInline.SCInlineText(e.lexical, e.text))
    }
  }

  private fun failedToMatch(
    e : SCExpression.EList,
    m : List<SCExpressionMatch>) : SCError.SCParseError {

    val sb = StringBuilder()
    sb.append("Input did not match expected form.")
    sb.append(System.lineSeparator())
    sb.append("  Expected one of: ")
    sb.append(System.lineSeparator())

    for (i in 0 .. m.size - 1) {
      sb.append("    ")
      sb.append(m[i])
      sb.append(System.lineSeparator())
    }

    sb.append("  Received: ")
    sb.append(System.lineSeparator())
    sb.append("    ")
    sb.append(e)
    sb.append(System.lineSeparator())
    return SCError.SCParseError(e.lexical, sb.toString())
  }

  private val blockCommands : Map<String, BlockCommand> =
    makeBlockCommands()
  private val blockCommandNamesDescription : String =
    makeMapDescription(blockCommands)
  private val inlineCommands : Map<String, InlineCommand> =
    makeInlineCommands()
  private val inlineCommandNamesDescription : String =
    makeMapDescription(inlineCommands)

  private fun makeInlineCommands() : Map<String, InlineCommand> {
    val m = HashMap<String, InlineCommand>()
    m.put(SCCommandNames.TERM,
      InlineCommand(SCCommandNames.TERM, { e, eq -> parseInlineTerm(e, eq) }))
    m.put(SCCommandNames.VERBATIM,
      InlineCommand(SCCommandNames.VERBATIM, { e, eq -> parseInlineVerbatim(e, eq) }))
    m.put(SCCommandNames.LINK_EXT,
      InlineCommand(SCCommandNames.LINK_EXT, { e, eq -> parseInlineLinkExternal(e, eq) }))
    m.put(SCCommandNames.LINK,
      InlineCommand(SCCommandNames.LINK, { e, eq -> parseInlineLinkInternal(e, eq) }))
    m.put(SCCommandNames.IMAGE,
      InlineCommand(SCCommandNames.IMAGE, { e, eq -> parseInlineImage(e, eq) }))
    m.put(SCCommandNames.INCLUDE,
      InlineCommand(SCCommandNames.INCLUDE, { e, eq -> parseInlineInclude(e, eq) }))
    return m
  }

  private data class BlockCommand(
    val name : String,
    val parser : (SCExpression.EList, Deque<SCError>) -> SCBlock)

  private data class InlineCommand(
    val name : String,
    val parser : (SCExpression.EList, Deque<SCError>) -> SCInline)

  private fun makeBlockCommands() : Map<String, BlockCommand> {
    val m = HashMap<String, BlockCommand>()
    m.put(SCCommandNames.DOCUMENT,
      BlockCommand(SCCommandNames.DOCUMENT, { e, eq -> parseBlockDocument(e, eq) }))
    m.put(SCCommandNames.PARA,
      BlockCommand(SCCommandNames.PARA, { e, eq -> parseBlockPara(e, eq) }))
    m.put(SCCommandNames.PART,
      BlockCommand(SCCommandNames.PART, { e, eq -> parseBlockPart(e, eq) }))
    m.put(SCCommandNames.SECTION,
      BlockCommand(SCCommandNames.SECTION, { e, eq -> parseBlockSection(e, eq) }))
    m.put(SCCommandNames.SUBSECTION,
      BlockCommand(SCCommandNames.SUBSECTION, { e, eq -> parseBlockSubsection(e, eq) }))
    m.put(SCCommandNames.IMPORT,
      BlockCommand(SCCommandNames.IMPORT, { e, eq -> parseBlockImport(e, eq) }))
    return m
  }

  private fun makeMapDescription(m : Map<String, Any>) : String {
    val sb = StringBuilder()
    sb.append("{")
    val iter = m.keys.iterator()
    while (iter.hasNext()) {
      sb.append(iter.next())
      if (iter.hasNext()) {
        sb.append(" | ")
      }
    }
    sb.append("}")
    return sb.toString()
  }

  private fun commandName(e : SCExpression.EList) : String {
    Assertive.require(e.elements.size > 0)
    Assertive.require(e.elements[0] is SCExpression.ESymbol)
    return (e.elements[0] as SCExpression.ESymbol).text
  }

  private val isBlockCommand =
    SCExpressionMatch.prefixOfList(
      listOf(SCExpressionMatch.MatchSymbol(
        { s -> blockCommands.containsKey(s) },
        blockCommandNamesDescription)))

  private val isInlineCommand =
    SCExpressionMatch.prefixOfList(
      listOf(SCExpressionMatch.MatchSymbol(
        { s -> inlineCommands.containsKey(s) },
        inlineCommandNamesDescription)))

  private fun parseBlockAny(
    e : SCExpression,
    eq : Deque<SCError>) : SCBlock {
    if (!SCExpressionMatch.matches(e, isBlockCommand)) {
      val sb = StringBuilder()
      sb.append("Expected a block command.")
      sb.append(System.lineSeparator())
      sb.append("  Expected: ")
      sb.append(isInlineCommand)
      sb.append(System.lineSeparator())
      sb.append("  Received: ")
      sb.append(e)
      sb.append(System.lineSeparator())
      eq.add(SCError.SCParseError(e.lexical, sb.toString()))
      throw SCException.SCParseException()
    }

    val el = e as SCExpression.EList
    val name = commandName(el)
    Assertive.require(blockCommands.containsKey(name))
    val bc = blockCommands.get(name)!!
    Assertive.require(bc.name == name)
    return bc.parser.invoke(el, eq)
  }

  override fun parseInline(
    e : SCExpression,
    error_queue : Deque<SCError>) : SCInline {
    return parseInlineAny(e, error_queue)
  }

  override fun parseBlock(
    e : SCExpression.EList,
    error_queue : Deque<SCError>) : SCBlock {
    return parseBlockAny(e, error_queue)
  }

  override fun parse(
    e : SCExpression,
    error_queue : Deque<SCError>) : SCElement {
    return when (e) {
      is SCExpression.ESymbol ->
        SCElement.SCElementInline(parseInlineAny(e, error_queue))
      is SCExpression.EQuoted ->
        SCElement.SCElementInline(parseInlineAny(e, error_queue))
      is SCExpression.EList   -> {
        if (SCExpressionMatch.matches(e, isBlockCommand)) {
          SCElement.SCElementBlock(parseBlockAny(e, error_queue))
        } else {
          SCElement.SCElementInline(parseInlineAny(e, error_queue))
        }
      }
    }
  }
}
