public class old{
    private CodeBlock defaultValue(WireField field) {
        if (field.isRepeated()) {
          return codeBlock("$T.emptyList()", Collections.class);
        }
    
        WireOption fieldDefault = field.getDefault();
        TypeName javaType = fieldType(field);
    
        if (field.type().isScalar()) {
          Object initialValue = fieldDefault != null ? fieldDefault.value() : null;
          return fieldInitializer(javaType, initialValue);
        }
    
        if (fieldDefault != null) {
          return codeBlock("$T.$L", javaType, fieldDefault.value());
        }
    
        if (javaGenerator.isEnum(field.type())) {
          WireEnumConstant defaultValue = javaGenerator.enumDefault(field.type());
          return codeBlock("$T.$L", javaType, defaultValue.name());
        }
    
        throw new WireCompilerException("Field " + field + " cannot have default value");
    }
}
