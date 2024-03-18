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

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarOutputStream;

/**
 * The transformer configuration needed by the shader to enable reflective access for the specified class members.
 */
public class ReflectiveAccessShaderTransformer implements ResourceTransformer {

  private List<ClassMember> members;

  @Override
  public boolean canTransformResource(String resource) {
    return false;
  }

  @Override
  public boolean hasTransformedResource() {
    return false;
  }

  @Override
  public void processResource(String resource, InputStream is, List<Relocator> relocators) {}

  @Override
  public void modifyOutputStream(JarOutputStream os) {}

  public List<ClassMember> getMembers() {
    return members;
  }

  public void setMembers(List<ClassMember> members) {
    this.members = members;
  }

  public static class ClassMember {

    private String className;
    private String memberName;

    public boolean equals(String className, String memberName) {
      return Objects.equals(this.className, className) && Objects.equals(this.memberName, memberName);
    }

    public String getClassName() {
      return className;
    }

    public void setClassName(String className) {
      this.className = className;
    }

    public String getMemberName() {
      return memberName;
    }

    public void setMemberName(String memberName) {
      this.memberName = memberName;
    }
  }
}
