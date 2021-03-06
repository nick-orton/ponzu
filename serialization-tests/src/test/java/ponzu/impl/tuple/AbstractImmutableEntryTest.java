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

package ponzu.impl.tuple;

import ponzu.impl.test.Verify;
import org.junit.Test;

public class AbstractImmutableEntryTest
{
    @Test
    public void getKeyFunction()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5BYnN0cmFjdEltbXV0YWJsZUVu\n"
                        + "dHJ5JEtleUZ1bmN0aW9uAAAAAAAAAAECAAB4cA==",
                AbstractImmutableEntry.getKeyFunction());
    }

    @Test
    public void getValueFunction()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5BYnN0cmFjdEltbXV0YWJsZUVu\n"
                        + "dHJ5JFZhbHVlRnVuY3Rpb24AAAAAAAAAAQIAAHhw",
                AbstractImmutableEntry.getValueFunction());
    }

    @Test
    public void getPairFunction()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5BYnN0cmFjdEltbXV0YWJsZUVu\n"
                        + "dHJ5JFBhaXJGdW5jdGlvbgAAAAAAAAABAgAAeHA=",
                AbstractImmutableEntry.getPairFunction());
    }
}
