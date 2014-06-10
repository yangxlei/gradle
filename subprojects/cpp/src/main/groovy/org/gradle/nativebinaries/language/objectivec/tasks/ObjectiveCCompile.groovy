/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativebinaries.language.objectivec.tasks
import org.gradle.api.Incubating
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.nativebinaries.language.c.tasks.AbstractNativeCompileTask
import org.gradle.nativebinaries.language.objectivec.internal.DefaultObjectiveCCompileSpec
import org.gradle.nativebinaries.toolchain.internal.NativeCompileSpec
import org.gradle.nativebinaries.toolchain.internal.PlatformToolChain
/**
 * Compiles Objective-C source files into object files.
 */
@Incubating
class ObjectiveCCompile extends AbstractNativeCompileTask {
    @Override
    protected NativeCompileSpec createCompileSpec() {
        new DefaultObjectiveCCompileSpec()
    }

    @Override
    protected Compiler<NativeCompileSpec> createCompiler(PlatformToolChain toolChain) {
        return toolChain.createObjectiveCCompiler()
    }
}
