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

package ponzu.impl.block.procedure;

import ponzu.impl.test.Verify;
import org.junit.Test;

public class FlatCollectProcedureTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuRmxhdENvbGxl\n"
                        + "Y3RQcm9jZWR1cmUAAAAAAAAAAQIAAkwACmNvbGxlY3Rpb250ABZMamF2YS91dGlsL0NvbGxlY3Rp\n"
                        + "b247TAAIZnVuY3Rpb250ADBMY29tL2dzL2NvbGxlY3Rpb25zL2FwaS9ibG9jay9mdW5jdGlvbi9G\n"
                        + "dW5jdGlvbjt4cHBw",
                new FlatCollectProcedure<Object, Object>(null, null));
    }
}
