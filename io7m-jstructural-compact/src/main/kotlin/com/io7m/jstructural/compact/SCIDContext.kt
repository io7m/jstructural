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

import com.io7m.jstructural.core.SID
import org.valid4j.Assertive
import java.util.HashMap
import java.util.Optional

class SCIDContext private constructor(
  private val scid_to_sid : MutableMap<SCID, SID>,
  private val sid_to_scid : MutableMap<SID, SCID>) : SCIDContextType {

  companion object {
    fun create() : SCIDContextType =
      SCIDContext(HashMap(), HashMap())
  }

  override fun check(id : SCID) : Optional<SID> {
    return when (scid_to_sid.containsKey(id)) {
      true -> Optional.of(scid_to_sid.get(id))
      false -> Optional.empty()
    }
  }

  override fun declare(scid : SCID) : SCIDContextDeclaration {
    return when (scid_to_sid.containsKey(scid)) {
      false -> {
        val sid = SID.newID(scid.id)
        scid_to_sid.put(scid, sid)
        sid_to_scid.put(sid, scid)
        SCIDContextDeclaration.SCIDCreated(sid)
      }
      true -> {
        val sid = scid_to_sid.get(scid)
        Assertive.require(sid_to_scid.containsKey(sid))
        val orig = sid_to_scid.get(sid)!!
        SCIDContextDeclaration.SCIDCollision(orig)
      }
    }
  }

}
