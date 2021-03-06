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

package ponzu.impl.partition.bag;

import ponzu.api.bag.Bag;
import ponzu.api.bag.MutableBag;
import ponzu.api.block.predicate.Predicate;
import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.partition.bag.PartitionImmutableBag;
import ponzu.api.partition.bag.PartitionMutableBag;
import ponzu.impl.bag.mutable.HashBag;
import ponzu.impl.partition.AbstractPartitionMutableCollection;

public class PartitionHashBag<T> extends AbstractPartitionMutableCollection<T> implements PartitionMutableBag<T>
{
    private final MutableBag<T> selected = HashBag.newBag();
    private final MutableBag<T> rejected = HashBag.newBag();

    public PartitionHashBag(Predicate<? super T> predicate)
    {
        super(predicate);
    }

    public static <V> PartitionMutableBag<V> of(Bag<V> bag, Predicate<? super V> predicate)
    {
        final PartitionHashBag<V> partitionHashBag = new PartitionHashBag<V>(predicate);
        bag.forEachWithOccurrences(new ObjectIntProcedure<V>()
        {
            public void value(V each, int occurrences)
            {
                partitionHashBag.addOccurrences(each, occurrences);
            }
        });
        return partitionHashBag;
    }

    public MutableBag<T> getSelected()
    {
        return this.selected;
    }

    public MutableBag<T> getRejected()
    {
        return this.rejected;
    }

    public PartitionImmutableBag<T> toImmutable()
    {
        return new PartitionImmutableBagImpl<T>(this);
    }

    public void addOccurrences(T each, int occurrences)
    {
        MutableBag<T> bag = this.predicate.accept(each) ? this.selected : this.rejected;
        bag.addOccurrences(each, occurrences);
    }
}
