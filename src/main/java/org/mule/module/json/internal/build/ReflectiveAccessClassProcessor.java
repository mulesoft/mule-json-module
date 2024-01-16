/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal.build;

import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.github.fge.msgsimple.provider.LoadingMessageSourceProvider;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

import java.io.IOException;

/**
 * Changes the internals of certain dependencies so that they are accessible via reflection at runtime.
 * Relies on bytecode manipulation and can be done at compile time.
 */
public class ReflectiveAccessClassProcessor {

  public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Invalid class file output directory arguments provided. " +
          "Expected argument format: <argument>${project.build.outputDirectory}</argument>");
    }

    String buildOutputDir = args[0];
    setFieldPublic(JsonNodeReader.class, "BUNDLE", buildOutputDir);
    setFieldPublic(ProcessingMessage.class, "BUNDLE", buildOutputDir);
    setFieldPublic(MessageBundles.class, "BUNDLES", buildOutputDir);
    setFieldPublic(MessageBundle.class, "providers", buildOutputDir);
    setFieldPublic(LoadingMessageSourceProvider.class, "service", buildOutputDir);
  }

  /**
   * Changes visibility to public for the specified field of the target class.
   * Bytecode manipulation can be done at compile time.
   *
   * @param fromClass the class that the field belongs to
   * @param fieldName the name of the field to make public
   * @param buildOutputDir the build output directory where the modified class will be written to
   * @throws NotFoundException when unable to load the class
   * @throws IOException when unable to write the modified class file
   * @throws CannotCompileException when unable to write the modified class file
   */
  private static void setFieldPublic(Class<?> fromClass, String fieldName, String buildOutputDir)
      throws NotFoundException, IOException, CannotCompileException {
    ClassPool classPool = ClassPool.getDefault();
    LoaderClassPath classPath = new LoaderClassPath(fromClass.getClassLoader());
    classPool.insertClassPath(classPath);
    CtClass ctClass = classPool.get(fromClass.getCanonicalName());
    CtField ctField = ctClass.getDeclaredField(fieldName);
    ctField.setModifiers(Modifier.setPublic(ctField.getModifiers()));
    ctClass.writeFile(buildOutputDir);
  }
}
