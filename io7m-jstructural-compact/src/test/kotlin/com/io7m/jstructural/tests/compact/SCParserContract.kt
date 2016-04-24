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

package com.io7m.jstructural.tests.compact

import com.io7m.jstructural.compact.SCBlock
import com.io7m.jstructural.compact.SCError
import com.io7m.jstructural.compact.SCException
import com.io7m.jstructural.compact.SCExpression
import com.io7m.jstructural.compact.SCID
import com.io7m.jstructural.compact.SCInline
import com.io7m.jstructural.compact.SCLink
import com.io7m.jstructural.compact.SCLinkContent
import com.io7m.jstructural.compact.SCParserType
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.ArrayDeque
import java.util.Optional
import java.util.OptionalInt

abstract class SCParserContract {

  protected abstract fun newParserForString(text : String) : Parser

  @Rule @JvmField val expected = ExpectedExceptionWith.none()

  data class Parser(
    val p : SCParserType,
    val s : () -> SCExpression)

  @Test fun testInlineText() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("x")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineText
    Assert.assertEquals("x", i.text)
  }

  @Test fun testInlineTextQuoted() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("\"x\"")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineText
    Assert.assertEquals("x", i.text)
  }

  @Test fun testInlineTermError() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineTermTypeError() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term [type]]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineTermNestedError() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term x [term y]]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineTerm() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term x]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineTerm
    Assert.assertEquals("x", i.content[0].text)
    Assert.assertEquals(Optional.empty<String>(), i.type)
  }

  @Test fun testInlineTermType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term [type y] x]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineTerm
    Assert.assertEquals("x", i.content[0].text)
    Assert.assertEquals(Optional.of("y"), i.type)
  }

  @Test fun testInlineTermQuoted() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term \"x\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineTerm
    Assert.assertEquals("x", i.content[0].text)
    Assert.assertEquals(Optional.empty<String>(), i.type)
  }

  @Test fun testInlineTermQuotedType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[term [type y] \"x\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineTerm
    Assert.assertEquals("x", i.content[0].text)
    Assert.assertEquals(Optional.of("y"), i.type)
  }

  @Test fun testInlineVerbatim() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[verbatim \"x\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineVerbatim
    Assert.assertEquals("x", i.text)
  }

  @Test fun testInlineVerbatimType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[verbatim [type y] \"x\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineVerbatim
    Assert.assertEquals("x", i.text)
    Assert.assertEquals("y", i.type.get())
  }

  @Test fun testInlineVerbatimError() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[verbatim [x]]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternal() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link [target \"x\"] y]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkInternal

    val lt = l.content.elements[0] as SCLinkContent.SCLinkText
    Assert.assertEquals("x", l.target)
    Assert.assertEquals("y", lt.actual.text)
  }

  @Test fun testInlineLinkInternalQuoted() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link [target \"x\"] \"y\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkInternal

    val lt = l.content.elements[0] as SCLinkContent.SCLinkText
    Assert.assertEquals("x", l.target)
    Assert.assertEquals("y", lt.actual.text)
  }

  @Test fun testInlineLinkInternalImage() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link [target \"x\"] (image [target \"q\"] y)]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkInternal

    val lt = l.content.elements[0] as SCLinkContent.SCLinkImage
    Assert.assertEquals("x", l.target)
    Assert.assertEquals("q", lt.actual.target.toString())
  }

  @Test fun testInlineLinkInternalInclude() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link [target \"x\"] (include \"z\")]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkInternal

    val inc = l.content.elements[0] as SCLinkContent.SCLinkInclude
    Assert.assertEquals("x", l.target)
    Assert.assertEquals("z", inc.actual.file)
  }

  @Test fun testInlineLinkInternalError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternalError1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link x y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternalError2() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link [target \"x\"] [x]]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternalErrorNestedLink() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link (target \"x\") q (link [target \"y\"] z)]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternalErrorNestedVerbatim() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link (target \"x\") q (verbatim \"x\")]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkInternalErrorNestedTerm() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link (target \"x\") q (term \"x\")]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternal() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext [target \"http://example.com\"] y]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkExternal

    val lt = l.content.elements[0] as SCLinkContent.SCLinkText
    Assert.assertEquals("http://example.com", l.target.toString())
    Assert.assertEquals("y", lt.actual.text)
  }

  @Test fun testInlineLinkExternalQuoted() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext [target \"http://example.com\"] \"y\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineLink
    val l = i.actual as SCLink.SCLinkExternal

    val lt = l.content.elements[0] as SCLinkContent.SCLinkText
    Assert.assertEquals("http://example.com", l.target.toString())
    Assert.assertEquals("y", lt.actual.text)
  }

  @Test fun testInlineLinkExternalError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext [target \" \"] x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternalError1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext [target \"http://example.com\"] [x]]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternalErrorNestedLink() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext (target \"x\") q (link [target \"y\"] z)]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternalErrorEmpty() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternalErrorNestedVerbatim() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext (target \"x\") q (verbatim \"x\")]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineLinkExternalErrorNestedTerm() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[link-ext (target \"x\") q (term \"x\")]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImage() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] y]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineImage
    Assert.assertEquals("x", i.target.toString())
    Assert.assertEquals(Optional.empty<String>(), i.type)
    Assert.assertEquals(OptionalInt.empty(), i.width)
    Assert.assertEquals(OptionalInt.empty(), i.height)
    Assert.assertEquals("y", i.content[0].text)
  }

  @Test fun testInlineImageType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [type y] z]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineImage
    Assert.assertEquals("x", i.target.toString())
    Assert.assertEquals("y", i.type.get())
    Assert.assertEquals(OptionalInt.empty(), i.width)
    Assert.assertEquals(OptionalInt.empty(), i.height)
    Assert.assertEquals("z", i.content[0].text)
  }

  @Test fun testInlineImageTypeSize() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [type y] [size 100 200] z]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineImage
    Assert.assertEquals("x", i.target.toString())
    Assert.assertEquals("y", i.type.get())
    Assert.assertEquals(OptionalInt.of(100), i.width)
    Assert.assertEquals(OptionalInt.of(200), i.height)
    Assert.assertEquals("z", i.content[0].text)
  }

  @Test fun testInlineImageSize() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [size 100 200] z]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineImage
    Assert.assertEquals("x", i.target.toString())
    Assert.assertEquals(Optional.empty<String>(), i.type)
    Assert.assertEquals(OptionalInt.of(100), i.width)
    Assert.assertEquals(OptionalInt.of(200), i.height)
    Assert.assertEquals("z", i.content[0].text)
  }

  @Test fun testInlineImageError() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImageErrorBadTarget() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \" \"] z]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImageErrorBadWidth() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [size x 100] y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImageErrorBadWidthNegative() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [size -100 100] y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImageErrorBadHeight() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [size 100 x] y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineImageErrorBadHeightNegative() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[image [target \"x\"] [size 100 -100] y]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineInclude() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[include \"file.txt\"]")
    val i = pp.p.parseInline(pp.s(), eq) as SCInline.SCInlineInclude
    Assert.assertEquals("file.txt", i.file)
  }

  @Test fun testInlineIncludeError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[include x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testInlineIncludeError1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[include]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseInline(pp.s(), eq)
  }

  @Test fun testBlockPara() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockParagraph
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
  }

  @Test fun testBlockParaID() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockParagraph
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
  }

  @Test fun testBlockParaType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para [type x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockParagraph
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.of("x"), b.type)
  }

  @Test fun testBlockParaIDType0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para [id x] [type y]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockParagraph
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
  }

  @Test fun testBlockParaIDType1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para [type y] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockParagraph
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
  }

  @Test fun testBlockParaError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[para x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockPart() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part [title t]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockPart
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockPartID() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part [title t] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockPart
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockPartType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part [title t] [type x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockPart
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.of("x"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockPartIDType0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part [title t] [id x] [type y]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockPart
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockPartIDType1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part [title t] [type y] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockPart
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockPartError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[part x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockSection() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section [title t]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSection
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSectionID() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section [title t] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSectionType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section [title t] [type x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSection
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.of("x"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSectionIDType0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section [title t] [id x] [type y]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSectionIDType1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section [title t] [type y] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSectionError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[section x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockSubsection() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection [title t]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSubsection
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSubsectionID() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection [title t] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSubsection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.empty<String>(), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSubsectionType() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection [title t] [type x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSubsection
    Assert.assertEquals(Optional.empty<SCID>(), b.id)
    Assert.assertEquals(Optional.of("x"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSubsectionIDType0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection [title t] [id x] [type y]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSubsection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSubsectionIDType1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection [title t] [type y] [id x]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockSubsection
    Assert.assertEquals(Optional.of(SCID(Optional.empty(), "x")), b.id)
    Assert.assertEquals(Optional.of("y"), b.type)
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockSubsectionError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[subsection x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockImport() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[import \"file.txt\"]")
    val i = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockImport
    Assert.assertEquals("file.txt", i.file)
  }

  @Test fun testBlockImportError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[import x]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockImportError1() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[import]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  @Test fun testBlockDocument() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[document [title t]]")
    val b = pp.p.parseBlock(pp.s() as SCExpression.EList, eq) as SCBlock.SCBlockDocument
    Assert.assertEquals("t", b.title.elements[0].text)
  }

  @Test fun testBlockDocumentError0() {
    val eq = ArrayDeque<SCError>()
    val pp = newParserForString("[document]")

    expected.expectWith(SCException.SCParseException::class.java, {
      Assert.assertEquals(1, eq.size)
      showErrors(eq)
    })
    pp.p.parseBlock(pp.s() as SCExpression.EList, eq)
  }

  private fun showErrors(eq : ArrayDeque<SCError>) =
    eq.map { e -> System.out.println(e) }
}
