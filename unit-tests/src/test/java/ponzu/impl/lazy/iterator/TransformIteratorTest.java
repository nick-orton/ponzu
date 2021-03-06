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

package ponzu.impl.lazy.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ponzu.impl.block.factory.Functions;
import ponzu.impl.factory.Lists;
import org.junit.Assert;
import org.junit.Test;

import static ponzu.impl.factory.Iterables.*;

public class TransformIteratorTest
{
    @Test
    public void iterator()
    {
        Iterator<String> iterator = new TransformIterator<Boolean, String>(iList(Boolean.TRUE), Functions.getToString());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("true", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorWithFunctionName()
    {
        Iterator<String> iterator = new TransformIterator<Boolean, String>(iList(Boolean.TRUE), Functions.getToString());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("true", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorWithFunctionNameAndIterator()
    {
        Iterator<String> iterator = new TransformIterator<Boolean, String>(iList(Boolean.TRUE).iterator(), Functions.getToString());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("true", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void noSuchElementException()
    {
        new TransformIterator<Boolean, String>(Lists.mutable.<Boolean>of(), Functions.getToString()).next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove()
    {
        new TransformIterator<Boolean, String>(Lists.mutable.<Boolean>of(), Functions.getToString()).remove();
    }
}
