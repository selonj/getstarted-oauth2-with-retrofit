package com.selonj.gson.enclosed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Administrator on 2016-03-31.
 */
public class EnclosingTypeAdapterFactory implements TypeAdapterFactory, com.selonj.gson.enclosed.dsl.GsonClause {
  private static final String DEFAULT_ENCLOSED_DATA_NAME = new String("data");

  private Class enclosedType;
  private String enclosedName = DEFAULT_ENCLOSED_DATA_NAME;

  public void setEnclosingType(Class enclosedType) {
    if (enclosedType == null) {
      throw new IllegalArgumentException("Enclosing type can't be null !");
    }
    this.enclosedType = enclosedType;
  }

  public void setEnclosingName(String enclosedName) {
    if (enclosedName == null) {
      throw new IllegalArgumentException("Enclosing property can't be null !");
    }
    this.enclosedName = enclosedName;
  }

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    checkingIfReady();
    if (isEnclosingWith(type.getRawType())) {
      return new EnclosingTypeAdapter<>(gson, TypeToken.get(enclosedType), this);
    }
    return null;
  }

  private void checkingIfReady() {
    if (enclosedType == null) {
      throw new IllegalStateException("You have not been configure enclosing adapter!");
    }
  }

  private <T> boolean isEnclosingWith(Class type) {
    return enclosedType.equals(type) || isArray(type);
  }

  private boolean isArray(Class type) {
    return Collection.class.isAssignableFrom(type);
  }

  @Override
  public Gson slight() {
    return to(new GsonBuilder()).create();
  }

  @Override
  public GsonBuilder to(GsonBuilder target) {
    return target.registerTypeAdapterFactory(this);
  }

  @Override
  public TypeAdapterFactory bare() {
    return this;
  }

  private class EnclosingTypeAdapter<T> extends TypeAdapter<T> {
    private TypeToken<T> type;
    private TypeAdapter<T> kernel;

    public EnclosingTypeAdapter(Gson gson, TypeToken<T> type, TypeAdapterFactory skippedFactory) {
      this.type = type;
      kernel = gson.getDelegateAdapter(skippedFactory, type);
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
      if (value == null) {
        return;
      }
      startingEnclosing(out);
      stringify(value, out);
      endingEnclosing(out);
    }

    private void startingEnclosing(JsonWriter out) throws IOException {
      out.beginObject();
      out.name(enclosedName);
    }

    private void stringify(T value, JsonWriter out) throws IOException {
      if (isArray(value.getClass())) {
        out.beginArray();
        Collection<T> group = (Collection<T>) value;
        for (T item : group) {
          stringify(item, out);
        }
        out.endArray();
      } else {
        kernel.write(out, value);
      }
    }

    private void endingEnclosing(JsonWriter out) throws IOException {
      out.endObject();
    }

    @Override
    public T read(JsonReader in) throws IOException {
      switch (in.peek()) {
        case BEGIN_OBJECT:
          return expand(in);
        case NAME:
          return fetch(in);
        default:
          if (skip(in)) return read(in);
          return null;
      }
    }

    private T expand(JsonReader in) throws IOException {
      in.beginObject();
      T result = read(in);
      in.endObject();
      return result;
    }

    private T fetch(JsonReader in) throws IOException {
      String name = in.nextName();
      if (name.equals(enclosedName)) {
        T result = extractEnclosedObject(in);
        skipRestOfTokens(in);
        return result;
      } else {
        return read(in);
      }
    }

    private void skipRestOfTokens(JsonReader in) throws IOException {
      while (skip(in)) ;
    }

    private boolean skip(JsonReader in) throws IOException {
      if (in.hasNext()) {
        in.skipValue();
        return in.peek() != JsonToken.END_DOCUMENT;
      }
      return false;
    }

    private T extractEnclosedObject(JsonReader in) throws IOException {
      switch (in.peek()) {
        case BEGIN_OBJECT:
          return parseEnclosingObject(in);
        case BEGIN_ARRAY:
          return (T) parseGroupOfEnclosingObject(in);
        default:
          throw mismatchEnclosingTypeException();
      }
    }

    private IllegalArgumentException mismatchEnclosingTypeException() {
      return new IllegalArgumentException(format("Bad enclosing json `%s` for <%s> !", enclosedName, type.toString()));
    }

    private T parseEnclosingObject(JsonReader in) throws IOException {
      return kernel.read(in);
    }

    private List<T> parseGroupOfEnclosingObject(JsonReader in) throws IOException {
      List<T> group = new ArrayList<>();
      in.beginArray();
      while (in.hasNext()) {
        group.add(extractEnclosedObject(in));
      }
      in.endArray();
      return group;
    }
  }
}
