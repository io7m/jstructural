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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;

/**
 * The set of formal items organized by kind.
 */

public final class SAFormalItemsByKind implements
  SAFormalItemsByKindReadable,
  SAFormalItemsByKindWritable
{
  private static final SortedMap<SAFormalItemNumber, SAFormalItem> EMPTY;
  static {
    final SortedMap<SAFormalItemNumber, SAFormalItem> um =
      Collections
        .unmodifiableSortedMap(new TreeMap<SAFormalItemNumber, SAFormalItem>());
    assert um != null;
    EMPTY = um;
  }

  private final LogUsableType                                      log;
  private final Map<String, Set<SAFormalItem>>                     map;

  /**
   * Construct a new empty map.
   * 
   * @param in_log
   *          A log handle
   */

  public SAFormalItemsByKind(
    final LogUsableType in_log)
  {
    this.log = NullCheck.notNull(in_log, "Log").with("formal-items");
    this.map = new HashMap<String, Set<SAFormalItem>>();
  }

  @Override public SortedMap<SAFormalItemNumber, SAFormalItem> get(
    final String kind)
  {
    final Set<SAFormalItem> set;
    if (this.map.containsKey(kind)) {
      set = this.map.get(kind);
    } else {
      return SAFormalItemsByKind.EMPTY;
    }

    final SortedMap<SAFormalItemNumber, SAFormalItem> r =
      new TreeMap<SAFormalItemNumber, SAFormalItem>();
    for (final SAFormalItem f : set) {
      r.put(f.getNumber(), f);
    }

    final SortedMap<SAFormalItemNumber, SAFormalItem> rm =
      Collections.unmodifiableSortedMap(r);
    assert rm != null;
    return rm;
  }

  @Override public void put(
    final String kind,
    final SAFormalItem item)
  {
    if (this.log.wouldLog(LogLevel.LOG_DEBUG)) {
      final StringBuilder b = new StringBuilder();
      b.append("new: ");
      b.append(item.getNumber().formalItemNumberFormat());
      b.append(" ");
      b.append(item.getTitle().getActual());
      b.append(" (kind ");
      b.append(item.getKind());
      b.append(")");
      final String r = b.toString();
      assert r != null;
      this.log.debug(r);
    }

    Set<SAFormalItem> set = null;
    if (this.map.containsKey(kind)) {
      set = this.map.get(kind);
    } else {
      set = new HashSet<SAFormalItem>();
    }

    if (set.contains(item)) {
      throw new IllegalArgumentException("Item already added to set");
    }

    set.add(item);
    this.map.put(kind, set);
  }
}
