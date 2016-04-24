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

import com.io7m.jlexing.core.LexicalPositionType
import com.io7m.jstructural.core.SNonEmptyList
import java.net.URI
import java.nio.file.Path
import java.util.Optional
import java.util.OptionalInt

/**
 * The type of elements that may have lexical information.
 */

interface SCLexicalType {
  val lexical : Optional<LexicalPositionType<Path>>
}

/**
 * The type of optional type arguments.
 */

interface SCTypeableType {
  val type : Optional<String>
}

/**
 * The type of optional ID assignments.
 */

interface SCIDableType {
  val id : Optional<SCID>
}

/**
 * The type of unique IDs for document elements.
 */

class SCID(
  override val lexical : Optional<LexicalPositionType<Path>>,
  val id : String) : SCLexicalType {

  override fun equals(other : Any?) : Boolean{
    if (this === other) return true
    if (other?.javaClass != javaClass) return false
    other as SCID
    return id == other.id
  }

  override fun hashCode() : Int{
    return id.hashCode()
  }
}

/**
 * The type of links.
 */

sealed class SCLink : SCLexicalType {

  class SCLinkExternal(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val target : URI,
    val content : SNonEmptyList<SCLinkContent>)
  : SCLink() {
    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[link-ext [target ")
      sb.append(target)
      sb.append("] ")

      val max = content.elements.size - 1
      for (i in 0 .. max) {
        sb.append(content.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }

      sb.append("]")
      return sb.toString()
    }
  }

  class SCLinkInternal(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val target : String,
    val content : SNonEmptyList<SCLinkContent>)
  : SCLink() {
    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[link [target ")
      sb.append(target)
      sb.append("] ")

      val max = content.elements.size - 1
      for (i in 0 .. max) {
        sb.append(content.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }

      sb.append("]")
      return sb.toString()
    }
  }
}

/**
 * The type of block commands.
 */

sealed class SCBlock : SCLexicalType {

  class SCBlockImport(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val file : String) : SCBlock(), SCLexicalType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[import \"")
      sb.append(file)
      sb.append("\"]")
      return sb.toString()
    }
  }

  class SCBlockDocument(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val title : SNonEmptyList<SCInline.SCInlineText>)
  : SCBlock() {
    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[document")
      sb.append(" [title ")
      val max = title.elements.size - 1
      for (i in 0 .. max) {
        sb.append(title.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }
      sb.append("]]")
      return sb.toString()
    }
  }

  class SCBlockParagraph(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    override val id : Optional<SCID>)
  : SCBlock(), SCTypeableType, SCIDableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[para")
      if (type.isPresent) {
        sb.append(" [type ")
        sb.append(type.get())
        sb.append("]")
      }
      if (id.isPresent) {
        sb.append(" [id ")
        sb.append(id.get())
        sb.append("]")
      }
      sb.append("]")
      return sb.toString()
    }
  }

  class SCBlockPart(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    override val id : Optional<SCID>,
    val title : SNonEmptyList<SCInline.SCInlineText>)
  : SCBlock(), SCTypeableType, SCIDableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[part")
      sb.append(" [title ")
      val max = title.elements.size - 1
      for (i in 0 .. max) {
        sb.append(title.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }
      sb.append("]")
      if (type.isPresent) {
        sb.append(" [type ")
        sb.append(type.get())
        sb.append("]")
      }
      if (id.isPresent) {
        sb.append(" [id ")
        sb.append(id.get())
        sb.append("]")
      }
      sb.append("]")
      return sb.toString()
    }
  }

  class SCBlockSection(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    override val id : Optional<SCID>,
    val title : SNonEmptyList<SCInline.SCInlineText>)
  : SCBlock(), SCTypeableType, SCIDableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[section")
      sb.append(" [title ")
      val max = title.elements.size - 1
      for (i in 0 .. max) {
        sb.append(title.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }
      sb.append("]")
      if (type.isPresent) {
        sb.append(" [type ")
        sb.append(type.get())
        sb.append("]")
      }
      if (id.isPresent) {
        sb.append(" [id ")
        sb.append(id.get())
        sb.append("]")
      }
      sb.append("]")
      return sb.toString()
    }
  }

  class SCBlockSubsection(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    override val id : Optional<SCID>,
    val title : SNonEmptyList<SCInline.SCInlineText>)
  : SCBlock(), SCTypeableType, SCIDableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[subsection")
      sb.append(" [title ")
      val max = title.elements.size - 1
      for (i in 0 .. max) {
        sb.append(title.elements[i])
        if (i < max) {
          sb.append(" ")
        }
      }
      sb.append("]")
      if (type.isPresent) {
        sb.append(" [type ")
        sb.append(type.get())
        sb.append("]")
      }
      if (id.isPresent) {
        sb.append(" [id ")
        sb.append(id.get())
        sb.append("]")
      }
      sb.append("]")
      return sb.toString()
    }
  }
}

/**
 * The type of inline commands.
 */

sealed class SCInline : SCLexicalType {

  class SCInlineLink(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val actual : SCLink)
  : SCInline() {
    override fun toString() : String = actual.toString()
  }

  class SCInlineVerbatim(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    val text : String)
  : SCInline(), SCTypeableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[verbatim ")
      if (type.isPresent) {
        sb.append("[type ")
        sb.append(type.get())
        sb.append("] ")
      }
      sb.append("\"")

      val max = text.length - 1
      for (i in 0 .. max) {
        val c = text.get(i)
        if (c == '"') {
          sb.append("\\\"")
        } else {
          sb.append(c)
        }
      }

      sb.append("\"")
      sb.append("]")
      return sb.toString()
    }
  }

  class SCInlineText(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val text : String)
  : SCInline() {
    override fun toString() = text
  }

  class SCInlineInclude(
    override val lexical : Optional<LexicalPositionType<Path>>,
    val file : String) : SCInline(), SCLexicalType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[include \"")
      sb.append(file)
      sb.append("\"]")
      return sb.toString()
    }
  }

  class SCInlineTerm(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    val content : List<SCInlineText>)
  : SCInline(), SCTypeableType {

    override fun toString() : String {
      val sb = StringBuilder()
      sb.append("[term ")
      if (type.isPresent) {
        sb.append("[type ")
        sb.append(type.get())
        sb.append("] ")
      }

      val max = content.size - 1
      for (i in 0 .. max) {
        sb.append(content[i])
        if (i < max) {
          sb.append(" ")
        }
      }

      sb.append("]")
      return sb.toString()
    }
  }

  class SCInlineImage(
    override val lexical : Optional<LexicalPositionType<Path>>,
    override val type : Optional<String>,
    val target : URI,
    val width : OptionalInt,
    val height : OptionalInt,
    val content : List<SCInlineText>)
  : SCInline(), SCTypeableType
}

/**
 * The type of content that can appear in links.
 */

sealed class SCLinkContent {

  class SCLinkText(
    val actual : SCInline.SCInlineText)
  : SCLinkContent() {
    override fun toString() = actual.toString()
  }

  class SCLinkInclude(
    val actual : SCInline.SCInlineInclude)
  : SCLinkContent() {
    override fun toString() = actual.toString()
  }

  class SCLinkImage(
    val actual : SCInline.SCInlineImage)
  : SCLinkContent() {
    override fun toString() = actual.toString()
  }

}

sealed class SCElement : SCLexicalType {

  class SCElementInline(
    val actual : SCInline) : SCElement() {
    override val lexical : Optional<LexicalPositionType<Path>> get() = actual.lexical
    override fun toString() = actual.toString()
  }

  class SCElementBlock(
    val actual : SCBlock) : SCElement() {
    override val lexical : Optional<LexicalPositionType<Path>> get() = actual.lexical
    override fun toString() = actual.toString()
  }

}
