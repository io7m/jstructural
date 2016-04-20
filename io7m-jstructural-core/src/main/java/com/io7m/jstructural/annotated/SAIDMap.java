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

package com.io7m.jstructural.annotated;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Mappings from IDs to content.
 */

@Immutable public final class SAIDMap
  implements SAIDMapWritable, SAIDMapReadable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SAIDMap.class);
  }

  private final Map<SAID, SAIDTargetContent> map;

  /**
   * Construct a new empty map.
   */

  public SAIDMap()
  {
    this.map = new HashMap<SAID, SAIDTargetContent>(128);
  }

  @Override public @Nullable SAIDTargetContent get(
    final SAID id)
  {
    NullCheck.notNull(id, "ID");

    if (SAIDMap.LOG.isDebugEnabled()) {
      SAIDMap.LOG.debug("get: {}", id.getActual());
    }

    return this.map.get(id);
  }

  @Override public void put(
    final SAID id,
    final SAIDTargetContent c)
  {
    NullCheck.notNull(id, "ID");
    NullCheck.notNull(c, "Content");

    if (this.map.containsKey(id)) {
      throw new IllegalArgumentException("ID already used");
    }

    if (SAIDMap.LOG.isDebugEnabled()) {
      SAIDMap.LOG.debug("new: {}", id.getActual());
    }

    this.map.put(id, c);
  }

  @Override public int size()
  {
    return this.map.size();
  }
}
