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

package ponzu.impl.map.immutable;

import org.junit.Assert;
import org.junit.Test;
import ponzu.api.block.predicate.Predicate2;
import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.block.procedure.Procedure2;
import ponzu.api.list.MutableList;
import ponzu.api.map.ImmutableMap;
import ponzu.api.tuple.Pair;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.block.factory.Predicates2;
import ponzu.impl.block.function.Constant;
import ponzu.impl.block.procedure.CollectionAddProcedure;
import ponzu.impl.factory.Lists;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.test.Verify;
import ponzu.impl.tuple.Tuples;

/**
 * JUnit test for {@link ImmutableTripletonMap}.
 */
public class ImmutableTripletonMapTest extends ImmutableMemoryEfficientMapTestCase
{
    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableTripletonMap<Integer, String>(1, "1", 2, "2", 3, "3");
    }

    @Override
    protected int size()
    {
        return 3;
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableMap<Integer, String> map1 = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        ImmutableMap<Integer, String> map2 = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Override
    @Test
    public void forEachValue()
    {
        super.forEachValue();
        MutableList<String> collection = Lists.mutable.of();
        this.classUnderTest().forEachValue(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), collection);
    }

    @Override
    @Test
    public void forEachKey()
    {
        super.forEachKey();
        MutableList<Integer> collection = Lists.mutable.of();
        this.classUnderTest().forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), collection);
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsent(4, new Constant<String>("4")));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        super.ifPresentApply();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.ifPresentApply(4, Functions.<String>getPassThru()));
        Assert.assertEquals("1", map.ifPresentApply(1, Functions.<String>getPassThru()));
        Assert.assertEquals("2", map.ifPresentApply(2, Functions.<String>getPassThru()));
        Assert.assertEquals("3", map.ifPresentApply(3, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        super.notEmpty();
        Assert.assertTrue(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();
        final MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, Integer> map = new ImmutableTripletonMap<Integer, Integer>(1, 1, 2, 2, 3, 3);
        map.forEachWith(new Procedure2<Integer, Integer>()
        {
            public void value(Integer argument1, Integer argument2)
            {
                result.add(argument1 + argument2);
            }
        }, 10);
        Assert.assertEquals(FastList.newListWith(11, 12, 13), result);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        final MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        map.forEachWithIndex(new ObjectIntProcedure<String>()
        {
            public void value(String value, int index)
            {
                result.add(value);
                result.add(String.valueOf(index));
            }
        });
        Assert.assertEquals(FastList.newListWith("One", "0", "Two", "1", "Three", "2"), result);
    }

    @Override
    @Test
    public void keyValuesView()
    {
        super.keyValuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        for (Pair<Integer, String> keyValue : map.keyValuesView())
        {
            result.add(keyValue.getTwo());
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three"), result);
    }

    @Override
    @Test
    public void valuesView()
    {
        super.valuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        for (String value : map.valuesView())
        {
            result.add(value);
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three"), result);
    }

    @Override
    @Test
    public void keysView()
    {
        super.keysView();
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        for (Integer key : map.keysView())
        {
            result.add(key);
        }
        Assert.assertEquals(FastList.newListWith(1, 2, 3), result);
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableMap<Integer, String> map = new ImmutableTripletonMap<Integer, String>(1, "One", 2, "Two", 3, "Three");
        Assert.assertEquals("{1=One, 2=Two, 3=Three}", map.toString());
    }

    @Override
    public void select()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.filter(Predicates2.alwaysFalse());
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.filter(Predicates2.alwaysTrue());
        Verify.assertInstanceOf(ImmutableTripletonMap.class, full);
        Assert.assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "2".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(2, "2"), two);

        ImmutableMap<Integer, String> three = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, three);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(3, "3"), three);

        ImmutableMap<Integer, String> oneAndThree = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2) || "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(1, "1", 3, "3"), oneAndThree);

        ImmutableMap<Integer, String> oneAndTwo = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2) || "2".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(1, "1", 2, "2"), oneAndTwo);

        ImmutableMap<Integer, String> twoAndThree = map.filter(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "2".equals(argument2) || "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(2, "2", 3, "3"), twoAndThree);
    }

    @Override
    public void reject()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.filterNot(Predicates2.alwaysTrue());
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.filterNot(Predicates2.alwaysFalse());
        Verify.assertInstanceOf(ImmutableTripletonMap.class, full);
        Assert.assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "2".equals(argument2) || "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2) || "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(2, "2"), two);

        ImmutableMap<Integer, String> three = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2) || "2".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableSingletonMap.class, three);
        Assert.assertEquals(new ImmutableSingletonMap<Integer, String>(3, "3"), three);

        ImmutableMap<Integer, String> oneAndThree = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "2".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(1, "1", 3, "3"), oneAndThree);

        ImmutableMap<Integer, String> oneAndTwo = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "3".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(1, "1", 2, "2"), oneAndTwo);

        ImmutableMap<Integer, String> twoAndThree = map.filterNot(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "1".equals(argument2);
            }
        });
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<Integer, String>(2, "2", 3, "3"), twoAndThree);
    }

    @Override
    public void detect()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Pair<Integer, String> one = map.find(Predicates2.alwaysTrue());
        Assert.assertEquals(Tuples.pair(1, "1"), one);

        Pair<Integer, String> two = map.find(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "2".equals(argument2);
            }
        });
        Assert.assertEquals(Tuples.pair(2, "2"), two);

        Pair<Integer, String> three = map.find(new Predicate2<Integer, String>()
        {
            public boolean accept(Integer argument1, String argument2)
            {
                return "3".equals(argument2);
            }
        });
        Assert.assertEquals(Tuples.pair(3, "3"), three);

        Assert.assertNull(map.find(Predicates2.alwaysFalse()));
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return new ImmutableTripletonMap<K, V>(key1, value1, key2, value2, null, null);
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return new ImmutableTripletonMap<K, V>(key1, value1, key2, value2, key3, value3);
    }
}
