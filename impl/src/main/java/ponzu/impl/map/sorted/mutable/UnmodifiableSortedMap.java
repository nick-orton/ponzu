/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ponzu.impl.map.sorted.mutable;

import java.util.Comparator;
import java.util.SortedMap;

import ponzu.impl.UnmodifiableMap;

/**
 * An unmodifiable view of a SortedMap.
 */
public class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V>
{
    private static final long serialVersionUID = 1L;

    public UnmodifiableSortedMap(SortedMap<K, V> delegate)
    {
        super(delegate);
    }

    protected SortedMap<K, V> getSortedMap()
    {
        return (SortedMap<K, V>) this.delegate;
    }

    public Comparator<? super K> comparator()
    {
        return this.getSortedMap().comparator();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
        return UnmodifiableTreeMap.of(this.getSortedMap().subMap(fromKey, toKey));
    }

    public SortedMap<K, V> headMap(K toKey)
    {
        return UnmodifiableTreeMap.of(this.getSortedMap().headMap(toKey));
    }

    public SortedMap<K, V> tailMap(K fromKey)
    {
        return UnmodifiableTreeMap.of(this.getSortedMap().tailMap(fromKey));
    }

    public K firstKey()
    {
        return this.getSortedMap().firstKey();
    }

    public K lastKey()
    {
        return this.getSortedMap().lastKey();
    }
}
