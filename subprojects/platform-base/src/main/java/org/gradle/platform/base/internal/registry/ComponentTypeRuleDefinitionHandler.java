/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.platform.base.internal.registry;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.internal.project.ProjectIdentifier;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.reflect.JavaReflectionUtil;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.language.base.internal.DefaultFunctionalSourceSet;
import org.gradle.language.base.plugins.ComponentModelBasePlugin;
import org.gradle.model.internal.core.ModelMutator;
import org.gradle.model.internal.inspect.MethodRuleDefinition;
import org.gradle.model.internal.inspect.RuleSourceDependencies;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.model.internal.type.ModelType;
import org.gradle.platform.base.*;
import org.gradle.platform.base.component.BaseComponentSpec;
import org.gradle.platform.base.internal.DefaultComponentSpecIdentifier;
import org.gradle.platform.base.internal.builder.TypeBuilderInternal;

public class ComponentTypeRuleDefinitionHandler extends TypeRuleDefinitionHandler<ComponentType, ComponentSpec, BaseComponentSpec> {

    private Instantiator instantiator;

    public ComponentTypeRuleDefinitionHandler(final Instantiator instantiator) {
        super("component", ComponentSpec.class, BaseComponentSpec.class, ComponentTypeBuilder.class, JavaReflectionUtil.factory(new DirectInstantiator(), DefaultComponentTypeBuilder.class));
        this.instantiator = instantiator;
    }

    @Override
    <R> void doRegister(MethodRuleDefinition<R> ruleDefinition, ModelRegistry modelRegistry, RuleSourceDependencies dependencies, ModelType<? extends ComponentSpec> type, TypeBuilderInternal<ComponentSpec> builder) {
        ModelType<? extends BaseComponentSpec> implementation = determineImplementationType(type, builder);
        dependencies.add(ComponentModelBasePlugin.class);
        if (implementation != null) {
            ModelMutator<?> mutator = new RegisterTypeRule<ComponentSpec, BaseComponentSpec>(type, implementation, ruleDefinition.getDescriptor(), new RegistrationAction(instantiator));
            modelRegistry.mutate(mutator);
        }
    }

    public static class DefaultComponentTypeBuilder extends AbstractTypeBuilder<ComponentSpec> implements ComponentTypeBuilder<ComponentSpec> {
        public DefaultComponentTypeBuilder() {
            super(ComponentType.class);
        }
    }

    private static class RegistrationAction implements Action<RegistrationContext<ComponentSpec, BaseComponentSpec>> {
        private final Instantiator instantiator;

        public RegistrationAction(Instantiator instantiator) {
            this.instantiator = instantiator;
        }

        public void execute(final RegistrationContext<ComponentSpec, BaseComponentSpec> context) {
            ExtensionContainer extensions = context.getExtensions();
            ProjectSourceSet projectSourceSet = extensions.getByType(ProjectSourceSet.class);
            ComponentSpecContainer componentSpecs = extensions.getByType(ComponentSpecContainer.class);
            doRegister(context.getType(), context.getImplementation(), projectSourceSet, componentSpecs, context.getProjectIdentifier());
        }

        private <T extends ComponentSpec, I extends BaseComponentSpec> void doRegister(final ModelType<T> type, final ModelType<I> implementation, final ProjectSourceSet projectSourceSet, ComponentSpecContainer componentSpecs, final ProjectIdentifier projectIdentifier) {
            componentSpecs.registerFactory(type.getConcreteClass(), new NamedDomainObjectFactory<T>() {
                public T create(String name) {
                    FunctionalSourceSet componentSourceSet = instantiator.newInstance(DefaultFunctionalSourceSet.class, name, instantiator, projectSourceSet);
                    ComponentSpecIdentifier id = new DefaultComponentSpecIdentifier(projectIdentifier.getPath(), name);

                    // safe because we implicitly know that U extends V, but can't express this in the type system
                    @SuppressWarnings("unchecked")
                    T created = (T) BaseComponentSpec.create(implementation.getConcreteClass(), id, componentSourceSet, instantiator);

                    return created;
                }
            });
        }
    }
}
