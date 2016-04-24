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

import com.io7m.jstructural.core.SImage
import com.io7m.jstructural.core.SLink
import com.io7m.jstructural.core.SLinkContent
import com.io7m.jstructural.core.SNonEmptyList
import com.io7m.jstructural.core.SParagraphContent
import com.io7m.jstructural.core.STerm
import com.io7m.jstructural.core.SText
import com.io7m.jstructural.core.SVerbatim
import com.io7m.junreachable.UnimplementedCodeException

object SCInlineContent {

  fun link(x : SCLink.SCLinkInternal) : SLink {
    return SLink.link(x.target, SNonEmptyList.newList(x.content.elements.map { linkContent(it) }))
  }

  fun paraContent(e : SCInline) : SParagraphContent {
    return when (e) {
      is SCInline.SCInlineLink     ->
        when (e.actual) {
          is SCLink.SCLinkExternal -> throw UnimplementedCodeException()
          is SCLink.SCLinkInternal -> link(e.actual)
        }
      is SCInline.SCInlineVerbatim -> verbatim(e)
      is SCInline.SCInlineText     -> text(e)
      is SCInline.SCInlineInclude  -> throw UnimplementedCodeException()
      is SCInline.SCInlineTerm     -> term(e)
      is SCInline.SCInlineImage    -> image(e)
    }
  }

  fun term(e : SCInline.SCInlineTerm) : STerm =
    when (e.type.isPresent) {
      true -> STerm.termTyped(SText.text(SCText.concatenate(e.content)), e.type.get())
      false -> STerm.term(SText.text(SCText.concatenate(e.content)))
    }

  fun verbatim(e : SCInline.SCInlineVerbatim) : SVerbatim =
    when (e.type.isPresent) {
      true -> SVerbatim.verbatimTyped(e.text, e.type.get())
      false -> SVerbatim.verbatim(e.text)
    }

  fun linkContent(e : SCLinkContent) : SLinkContent {
    return when (e) {
      is SCLinkContent.SCLinkText    -> return text(e.actual)
      is SCLinkContent.SCLinkInclude -> throw UnsupportedOperationException()
      is SCLinkContent.SCLinkImage   -> return image(e.actual)
    }
  }

  fun image(image : SCInline.SCInlineImage) : SImage {
    return when (image.type.isPresent) {
      true  -> {
        throw UnimplementedCodeException()
      }
      false -> {
        throw UnimplementedCodeException()
      }
    }
  }

  fun text(e : SCInline.SCInlineText) =
    SText.text(e.text)

}
