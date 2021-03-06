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

package ponzu.impl.block.function;

import ponzu.api.list.MutableList;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.block.factory.IntegerPredicates;
import ponzu.impl.block.factory.Predicates;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.map.mutable.UnifiedMap;
import org.junit.Assert;
import org.junit.Test;

public class IfFunctionTest
{
    @Test
    public void iterate()
    {
        UnifiedMap<Integer, Integer> map = UnifiedMap.newMap(5);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);
        map.put(5, 5);

        IfFunction<Integer, Integer> function = new IfFunction<Integer, Integer>(
                IntegerPredicates.isEven(),
                Functions.getFixedValue(1),
                Functions.getFixedValue(0));
        MutableList<Integer> result = map.valuesView().transform(function).toList();

        Assert.assertEquals(FastList.newListWith(0, 1, 0, 1, 0), result);
    }

    @Test
    public void testIf()
    {
        IfFunction<Integer, Boolean> function = new IfFunction<Integer, Boolean>(
                Predicates.greaterThan(5),
                Functions.getFixedValue(true));

        Assert.assertTrue(function.valueOf(10));
    }

    @Test
    public void ifElse()
    {
        IfFunction<Integer, Boolean> function = new IfFunction<Integer, Boolean>(
                Predicates.greaterThan(5),
                Functions.getFixedValue(true),
                Functions.getFixedValue(false));

        Assert.assertFalse(function.valueOf(1));
    }
}
