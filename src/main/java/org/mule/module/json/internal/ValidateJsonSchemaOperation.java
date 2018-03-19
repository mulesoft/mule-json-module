package org.mule.module.json.internal;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toMap;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static org.mule.runtime.api.meta.model.display.PathModel.Type.FILE;
import static org.mule.runtime.api.meta.model.operation.ExecutionType.CPU_INTENSIVE;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.module.json.api.SchemaRedirect;
import org.mule.module.json.internal.error.SchemaValidatorErrorTypeProvider;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.execution.Execution;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Path;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.stereotype.Validator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Operation to validate an XML document against a schema
 *
 * @since 1.0
 */
public class ValidateJsonSchemaOperation implements Startable, Stoppable {

  private static final int MIN_IDLE_POOL_COUNT = 1;
  private static final int MAX_IDLE_POOL_COUNT = 32;

  private LoadingCache<ValidatorKey, GenericObjectPool<JsonSchemaValidator>> validatorPool;

  @Override
  public void start() {
    validatorPool = CacheBuilder.newBuilder()
        .expireAfterAccess(5, MINUTES)
        .removalListener(
            (RemovalListener<ValidatorKey, GenericObjectPool<JsonSchemaValidator>>) notification -> notification.getValue()
                .close())
        .build(new CacheLoader<ValidatorKey, GenericObjectPool<JsonSchemaValidator>>() {

          @Override
          public GenericObjectPool<JsonSchemaValidator> load(ValidatorKey key) throws Exception {
            return createPool(createPooledObjectFactory(key));
          }
        });
  }

  @Override
  public void stop() {
    validatorPool.invalidateAll();
  }

  /**
   * Validates that the input content is compliant with a given schema. This operation supports referencing many schemas
   * (using comma as a separator) which include each other.
   *
   * @param schemas         The location in which the schema to validate against is to be found. This attribute supports URI
   *                        representations such as "http://org.mule/schema.json" or "resource:/schema.json".
   *                        It also supports a most common classpath reference such as simply "schema.json". Multiple schemas
   *                        can be provided using comma as a separator
   * @param content         the json document to be validated
   * @param schemaRedirects Allows to redirect any given URI in the Schema (or even the schema location itself) to any other
   *                        specific URI. The most common use case for this feature is to map external namespace URIs without the
   *                        need to a local resource
   * @param dereferencing   Draft v4 defines two dereferencing modes: canonical and inline. CANONICAL will be the default option
   *                        but INLINE can also be specified. When validating a v3 draft this attribute is ignored.
   */
  @Validator
  @Execution(CPU_INTENSIVE)
  @Throws(SchemaValidatorErrorTypeProvider.class)
  public void validateSchema(@Summary("The schema location") @Path(type = FILE, acceptedFileExtensions = "json") String schemas,
                             @Content InputStream content,
                             @NullSafe @Optional Collection<SchemaRedirect> schemaRedirects,
                             @Optional(defaultValue = "CANONICAL") JsonSchemaDereferencingMode dereferencing) {

    JsonSchemaValidator validator;
    GenericObjectPool<JsonSchemaValidator> pool =
        validatorPool.getUnchecked(new ValidatorKey(schemas, dereferencing, asMap(schemaRedirects)));

    try {
      validator = pool.borrowObject();
    } catch (Exception e) {
      throw new MuleRuntimeException(createStaticMessage("Could not obtain schema validator"), e);
    }

    try {
      validator.validate(content);
    } finally {
      pool.returnObject(validator);
    }
  }

  class ValidatorKey {

    private String schemas;
    private JsonSchemaDereferencingMode dereferencingType;
    private Map<String, String> schemaRedirects;

    public ValidatorKey(String schemas, JsonSchemaDereferencingMode dereferencingType, Map<String, String> schemaRedirects) {
      this.schemas = schemas;
      this.dereferencingType = dereferencingType;
      this.schemaRedirects = schemaRedirects;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ValidatorKey) {
        ValidatorKey key = (ValidatorKey) obj;
        return Objects.equals(schemas, key.schemas)
            && dereferencingType == key.dereferencingType
            && Objects.equals(schemaRedirects, key.schemaRedirects);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder().append(schemas).append(dereferencingType).append(schemaRedirects).toHashCode();
    }
  }

  private BasePooledObjectFactory<JsonSchemaValidator> createPooledObjectFactory(ValidatorKey key) {
    return new BasePooledObjectFactory<JsonSchemaValidator>() {

      @Override
      public JsonSchemaValidator create() throws Exception {
        return JsonSchemaValidator.builder()
            .addSchemaRedirects(key.schemaRedirects)
            .setDereferencing(key.dereferencingType)
            .setSchemaLocation(key.schemas)
            .build();
      }

      @Override
      public void passivateObject(PooledObject<JsonSchemaValidator> p) throws Exception {
      }

      @Override
      public PooledObject<JsonSchemaValidator> wrap(JsonSchemaValidator validator) {
        return new DefaultPooledObject<>(validator);
      }
    };
  }

  private Map<String, String> asMap(Collection<SchemaRedirect> redirects) {
    return redirects.stream().collect(toMap(SchemaRedirect::getFrom, SchemaRedirect::getTo));
  }

  private GenericObjectPool<JsonSchemaValidator> createPool(BasePooledObjectFactory<JsonSchemaValidator> factory) {
    return new GenericObjectPool<>(factory, defaultPoolConfig());
  }

  private GenericObjectPoolConfig defaultPoolConfig() {
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    config.setMinIdle(MIN_IDLE_POOL_COUNT);
    config.setMaxIdle(MAX_IDLE_POOL_COUNT);
    config.setMaxTotal(MAX_IDLE_POOL_COUNT);
    config.setBlockWhenExhausted(true);
    config.setTimeBetweenEvictionRunsMillis(MINUTES.toMillis(5));
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    config.setTestWhileIdle(false);
    config.setTestOnCreate(false);
    config.setJmxEnabled(false);

    return config;
  }


}
