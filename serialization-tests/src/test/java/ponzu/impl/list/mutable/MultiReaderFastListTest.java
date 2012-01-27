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

package ponzu.impl.list.mutable;

import ponzu.impl.test.Verify;
import org.junit.Test;

public class MultiReaderFastListTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUuTXVsdGlSZWFkZXJG\n"
                        + "YXN0TGlzdAAAAAAAAAABDAAAeHBzcgAtY29tLmdzLmNvbGxlY3Rpb25zLmltcGwubGlzdC5tdXRh\n"
                        + "YmxlLkZhc3RMaXN0AAAAAAAAAAEMAAB4cHcEAAAAAHh4",
                MultiReaderFastList.newList());
    }
}