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

package ponzu.impl.multimap.list;

import ponzu.api.collection.MutableCollection;
import ponzu.api.multimap.Multimap;
import ponzu.api.multimap.MutableMultimap;
import ponzu.api.tuple.Pair;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.multimap.AbstractMutableMultimapTestCase;
import ponzu.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test of {@link SynchronizedPutFastListMultimap}.
 */
public class SynchronizedPutFastListMultimapTest extends AbstractMutableMultimapTestCase
{
    @Override
    public <K, V> SynchronizedPutFastListMultimap<K, V> newMultimap()
    {
        return SynchronizedPutFastListMultimap.newMultimap();
    }

    @Override
    public <K, V> SynchronizedPutFastListMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        SynchronizedPutFastListMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key, value);
        return mutableMultimap;
    }

    @Override
    public <K, V> SynchronizedPutFastListMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        SynchronizedPutFastListMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        return mutableMultimap;
    }

    @Override
    protected <V> MutableCollection<V> createCollection(V... args)
    {
        return FastList.newListWith(args);
    }

    @Override
    public <K, V> Multimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return SynchronizedPutFastListMultimap.newMultimap(pairs);
    }

    @Override
    public <K, V> SynchronizedPutFastListMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        SynchronizedPutFastListMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        return mutableMultimap;
    }

    @Override
    public <K, V> SynchronizedPutFastListMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        SynchronizedPutFastListMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        mutableMultimap.put(key4, value4);
        return mutableMultimap;
    }

    @Test
    @Override
    public void testClear()
    {
        MutableMultimap<Integer, Object> multimap =
                this.<Integer, Object>newMultimapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        multimap.clear();
        Verify.assertEmpty(multimap);
    }

    @Test
    @Override
    public void testToString()
    {
        MutableMultimap<String, Integer> multimap =
                this.newMultimapWithKeysValues("One", 1, "One", 2);
        Assert.assertEquals("{One=[1, 2]}", multimap.toString());
    }
}
