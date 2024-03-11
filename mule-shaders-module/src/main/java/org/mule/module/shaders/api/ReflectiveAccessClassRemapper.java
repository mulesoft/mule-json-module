/*
 * Copyright (c) 2024, Salesforce, Inc.
 * SPDX-License-Identifier: Apache-2
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
package org.mule.module.shaders.api;

import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.mule.module.shaders.api.ReflectiveAccessShaderTransformer.ClassMember;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Changes the specified class members to be accessible via reflection at runtime.
 */
public class ReflectiveAccessClassRemapper extends ClassRemapper {

  private static final char PACKAGE_SEPARATOR = '.';
  private static final char PATH_SEPARATOR = '/';
  private static final char INNER_CLASS_SEPARATOR = '$';
  private final List<ClassMember> configuredMembers;

  public ReflectiveAccessClassRemapper(ClassVisitor classVisitor, Remapper remapper, List<ResourceTransformer> transformers) {
    super(classVisitor, remapper);
    this.configuredMembers = transformers.stream()
        .filter(ReflectiveAccessShaderTransformer.class::isInstance)
        .map(ReflectiveAccessShaderTransformer.class::cast)
        .map(ReflectiveAccessShaderTransformer::getMembers)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
    int effectiveAccess = isFieldConfigured(name) ? toAccessible(access) : access;
    return super.visitField(effectiveAccess, name, descriptor, signature, value);
  }

  private boolean isFieldConfigured(String fieldName) {
    return this.configuredMembers.stream()
        .anyMatch(member -> member.equals(toCanonicalName(this.className), fieldName));
  }

  private static int toAccessible(int access) {
    return (access & ~Opcodes.ACC_FINAL & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
  }

  private static String toCanonicalName(String resourceName) {
    return Optional.ofNullable(resourceName)
        .map(name -> name.replace(PATH_SEPARATOR, PACKAGE_SEPARATOR))
        .map(name -> name.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR))
        .orElse(null);
  }
}
