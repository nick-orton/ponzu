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

package ponzu.impl.set.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.collection.MutableCollection;
import ponzu.api.list.MutableList;
import ponzu.api.set.sorted.MutableSortedSet;
import ponzu.impl.block.factory.Comparators;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.block.factory.Predicates;
import ponzu.impl.block.procedure.CollectionAddProcedure;
import ponzu.impl.factory.Lists;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.set.mutable.UnifiedSet;
import ponzu.impl.test.SerializeTestHelper;
import ponzu.impl.test.Verify;
import ponzu.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link SortedSetAdapter}.
 */
public class SortedSetAdapterTest extends AbstractSortedSetTestCase
{
    @Override
    protected <T> SortedSetAdapter<T> classUnderTest()
    {
        return new SortedSetAdapter<T>(new TreeSet<T>());
    }

    @Override
    protected <T> SortedSetAdapter<T> classUnderTest(T... elements)
    {
        TreeSet<T> set = new TreeSet<T>();
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(set));
        return new SortedSetAdapter<T>(set);
    }

    @Override
    protected <T> SortedSetAdapter<T> classUnderTest(Comparator<? super T> comparator, T... elements)
    {
        TreeSet<T> set = new TreeSet<T>(comparator);
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(set));
        return new SortedSetAdapter<T>(set);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedSet.class, SortedSetAdapter.adapt(new TreeSet<Integer>()).asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.classUnderTest().asUnmodifiable());
    }

    @Override
    @Test
    public void testClone()
    {
        super.testClone();
        MutableSortedSet<Integer> set = this.<Integer>classUnderTest(Collections.<Integer>reverseOrder()).with(1, 2, 3);
        MutableSortedSet<Integer> list2 = set.clone();
        Verify.assertSortedSetsEqual(set, list2);
    }

    @Test
    public void adapt()
    {
        TreeSet<Integer> integers = new TreeSet<Integer>(FastList.newListWith(1, 2, 3, 4));
        MutableSortedSet<Integer> adapter1 = SortedSetAdapter.adapt(integers);
        MutableSortedSet<Integer> adapter2 = new SortedSetAdapter<Integer>(new TreeSet<Integer>()).with(1, 2, 3, 4);
        Verify.assertEqualsAndHashCode(adapter1, adapter2);
        Verify.assertSortedSetsEqual(adapter1, adapter2);
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        SortedSetAdapter<Integer> integers = this.<Integer>classUnderTest(1, 2, 3, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.<Integer>newSetWith(1, 2), integers.filter(Predicates.lessThan(3)));
        Verify.assertInstanceOf(MutableSortedSet.class, this.<Integer>classUnderTest().filter(Predicates.alwaysTrue()));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), this.<Object>classUnderTest().filter(Predicates.alwaysTrue()));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        SortedSetAdapter<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Object>reverseNaturalOrder(), 1, 2), integers.filterNot(Predicates.greaterThan(2)));
        Verify.assertInstanceOf(MutableSortedSet.class, this.<Integer>classUnderTest().filter(Predicates.alwaysTrue()));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), this.<Object>classUnderTest().filterNot(Predicates.alwaysTrue()));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4"),
                this.<Integer>classUnderTest(1, 2, 3, 4).transform(Functions.getToString()));
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4"), this.<Integer>classUnderTest(1, 2, 3, 4).transform(Functions.getToString(),
                FastList.<String>newList()));
        Verify.assertInstanceOf(FastList.class, this.<Object>classUnderTest().transform(Functions.getToString()));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        MutableCollection<Integer> set1 = this.<Integer>classUnderTest(1, 2, 3);
        SortedSetAdapter<Integer> set2 = this.<Integer>classUnderTest(Collections.<Integer>reverseOrder(), 1, 2, 3);
        MutableCollection<Integer> set3 = this.<Integer>classUnderTest(2, 3, 4);
        MutableSortedSet<Integer> set4 = TreeSortedSet.newSetWith(2, 3, 4);

        Verify.assertEqualsAndHashCode(set1, set1);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), set1);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), set2);

        Assert.assertEquals(set1, set2);
        Verify.assertNotEquals(set2, set3);
        Verify.assertEqualsAndHashCode(set3, set4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 1, 2, 3), set2);
    }

    @Test
    public void serialization()
    {
        MutableSortedSet<Integer> collection = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3);
        MutableSortedSet<Integer> deserialized = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertPostSerializedEqualsAndHashCode(collection);
        deserialized.add(4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Object>reverseNaturalOrder(), 1, 2, 3, 4), deserialized);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        final MutableList<Integer> result = Lists.mutable.of();
        MutableCollection<Integer> collection = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4);
        collection.forEachWithIndex(new ObjectIntProcedure<Integer>()
        {
            public void value(Integer object, int index)
            {
                result.add(object);
            }
        });
        Verify.assertListsEqual(FastList.newListWith(4, 3, 2, 1), result);
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();
        Assert.assertEquals(Integer.valueOf(1), this.<Integer>classUnderTest(1, 2, 3).getFirst());
        Assert.assertEquals(Integer.valueOf(3), this.<Integer>classUnderTest(Collections.<Integer>reverseOrder(), 1, 2, 3).getFirst());
        Verify.assertThrows(NoSuchElementException.class, new Runnable()
        {
            public void run()
            {
                new SortedSetAdapter(new TreeSet<Object>()).getFirst();
            }
        });
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();
        Assert.assertNotNull(this.<Object>classUnderTest(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), this.<Integer>classUnderTest(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(1), this.<Integer>classUnderTest(Collections.<Integer>reverseOrder(), 1, 2, 3).getLast());
        Verify.assertThrows(NoSuchElementException.class, new Runnable()
        {
            public void run()
            {
                new SortedSetAdapter(new TreeSet<Object>()).getFirst();
            }
        });
    }

    @Override
    @Test
    public void iterator()
    {
        super.iterator();
        MutableCollection<Integer> objects = this.classUnderTest(2, 3, 1, 4, 5);
        MutableList<Integer> result = Lists.mutable.of();
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i > 0; i--)
        {
            Integer integer = iterator.next();
            result.add(integer);
        }
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 5), result);
    }

    @Test
    public void withMethods()
    {
        Verify.assertContainsAll(this.classUnderTest().with(1), 1);
        Verify.assertContainsAll(this.classUnderTest().with(1, 2), 1, 2);
        Verify.assertContainsAll(this.classUnderTest().with(1, 2, 3), 1, 2, 3);
        Verify.assertContainsAll(this.classUnderTest().with(1, 2, 3, 4), 1, 2, 3, 4);
    }

    @Test
    public void returnType()
    {
        //Type TreeSet is important here because it's not a MutableSet
        SortedSet<Integer> set = new TreeSet<Integer>();
        MutableSortedSet<Integer> integerSetAdapter = SortedSetAdapter.adapt(set);
        Verify.assertInstanceOf(MutableSortedSet.class, integerSetAdapter.filter(Predicates.alwaysTrue()));
    }

    @Test
    public void adaptNull()
    {
        Verify.assertThrows(
                NullPointerException.class,
                new Runnable()
                {
                    public void run()
                    {
                        new SortedSetAdapter<Object>(null);
                    }
                });

        Verify.assertThrows(
                NullPointerException.class,
                new Runnable()
                {
                    public void run()
                    {
                        SortedSetAdapter.adapt(null);
                    }
                });
    }
}
