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

package ponzu.impl.multimap;

import java.util.Collection;
import java.util.Map;

import ponzu.api.RichIterable;
import ponzu.api.bag.Bag;
import ponzu.api.bag.MutableBag;
import ponzu.api.block.function.Function;
import ponzu.api.block.function.Generator;
import ponzu.api.block.predicate.Predicate;
import ponzu.api.block.procedure.Procedure;
import ponzu.api.block.procedure.Procedure2;
import ponzu.api.map.MutableMap;
import ponzu.api.multimap.Multimap;
import ponzu.api.tuple.Pair;
import ponzu.impl.UnmodifiableRichIterable;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.factory.Bags;
import ponzu.impl.map.mutable.UnifiedMap;
import ponzu.impl.tuple.Tuples;
import ponzu.impl.utility.Iterate;
import ponzu.impl.utility.MapIterate;

public abstract class AbstractMultimap<K, V, C extends RichIterable<V>>
        implements Multimap<K, V>
{
    protected abstract MutableMap<K, C> getMap();

    /**
     * Creates the collection of values for a single key.
     * <p/>
     * Collections with weak, soft, or phantom references are not supported.
     * Each call to {@code createCollection} should create a new instance.
     * <p/>
     * The returned collection class determines whether duplicate key-value
     * pairs are allowed.
     *
     * @return an empty collection of values
     */
    protected abstract C createCollection();

    protected Generator<C> createCollectionBlock()
    {
        return new Generator<C>()
        {
            public C value()
            {
                return AbstractMultimap.this.createCollection();
            }
        };
    }

    // Query Operations

    public boolean containsKey(Object key)
    {
        return this.getMap().containsKey(key);
    }

    public boolean containsValue(final Object value)
    {
        return MapIterate.anySatisfy(this.getMap(), new Predicate<C>()
        {
            public boolean accept(C collection)
            {
                return collection.contains(value);
            }
        });
    }

    public boolean containsKeyAndValue(Object key, Object value)
    {
        C collection = this.getMap().get(key);
        return collection != null && collection.contains(value);
    }

    // Views

    public RichIterable<K> keysView()
    {
        return this.getMap().keysView();
    }

    public RichIterable<RichIterable<V>> multiValuesView()
    {
        return this.getMap().valuesView().transform(new Function<C, RichIterable<V>>()
        {
            public RichIterable<V> valueOf(C multiValue)
            {
                return UnmodifiableRichIterable.of(multiValue);
            }
        });
    }

    public Bag<K> keyBag()
    {
        final MutableBag<K> bag = Bags.mutable.of();
        this.getMap().forEachKeyValue(new Procedure2<K, C>()
        {
            public void value(K key, C value)
            {
                bag.addOccurrences(key, value.size());
            }
        });
        return bag;
    }

    public RichIterable<V> valuesView()
    {
        return this.getMap().valuesView().flatTransform(Functions.<Iterable<V>>getPassThru());
    }

    public RichIterable<Pair<K, RichIterable<V>>> keyMultiValuePairsView()
    {
        return this.getMap().keyValuesView().transform(new Function<Pair<K, C>, Pair<K, RichIterable<V>>>()
        {
            public Pair<K, RichIterable<V>> valueOf(Pair<K, C> pair)
            {
                return Tuples.<K, RichIterable<V>>pair(pair.getOne(), UnmodifiableRichIterable.of(pair.getTwo()));
            }
        });
    }

    public RichIterable<Pair<K, V>> keyValuePairsView()
    {
        return this.keyMultiValuePairsView().flatTransform(new Function<Pair<K, RichIterable<V>>, Iterable<Pair<K, V>>>()
        {
            public Iterable<Pair<K, V>> valueOf(Pair<K, RichIterable<V>> pair)
            {
                return pair.getTwo().transform(new KeyValuePairFunction<V, K>(pair.getOne()));
            }
        });
    }

    // Comparison and hashing

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof Multimap)
        {
            Multimap<?, ?> that = (Multimap<?, ?>) object;
            return this.getMap().equals(that.toMap());
        }
        return false;
    }

    /**
     * Returns the hash code for this multimap.
     * <p/>
     * The hash code of a multimap is defined as the hash code of the map view,
     * as returned by {@link Multimap#toMap()}.
     *
     * @see Map#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.getMap().hashCode();
    }

    /**
     * Returns a string representation of the multimap, generated by calling
     * {@code toString} on the map returned by {@link Multimap#toMap()}.
     *
     * @return a string representation of the multimap
     */
    @Override
    public String toString()
    {
        return this.getMap().toString();
    }

    public boolean notEmpty()
    {
        return !this.isEmpty();
    }

    public void forEachValue(final Procedure<? super V> procedure)
    {
        this.getMap().forEachValue(new Procedure<C>()
        {
            public void value(C collection)
            {
                collection.forEach(procedure);
            }
        });
    }

    public void forEachKey(Procedure<? super K> procedure)
    {
        this.getMap().forEachKey(procedure);
    }

    public void forEachKeyValue(final Procedure2<K, V> procedure)
    {
        final Procedure2<V, K> innerProcedure = new Procedure2<V, K>()
        {
            public void value(V value, K key)
            {
                procedure.value(key, value);
            }
        };

        this.getMap().forEachKeyValue(new Procedure2<K, C>()
        {
            public void value(K key, C collection)
            {
                collection.forEachWith(innerProcedure, key);
            }
        });
    }

    public <R extends Collection<V>> MutableMap<K, R> toMap(final Generator<R> collectionFactory)
    {
        final MutableMap<K, R> result = UnifiedMap.newMap();
        this.getMap().forEachKeyValue(new Procedure2<K, C>()
        {
            public void value(K key, C iterable)
            {
                R newCollection = collectionFactory.value();
                Iterate.addAllTo(iterable, newCollection);
                result.put(key, newCollection);
            }
        });

        return result;
    }

    private static final class KeyValuePairFunction<V, K> implements Function<V, Pair<K, V>>
    {
        private static final long serialVersionUID = 1L;

        private final K key;

        private KeyValuePairFunction(K key)
        {
            this.key = key;
        }

        public Pair<K, V> valueOf(V value)
        {
            return Tuples.pair(this.key, value);
        }
    }
}
