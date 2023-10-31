public class old{
    public void prepare(MappingManager manager, PreparedStatement ps) {
        this.session = manager.getSession();
        this.statement = ps;

        if (method.isVarArgs())
            throw new IllegalArgumentException(String.format("Invalid varargs method %s in @Accessor interface"));
        if (ps.getVariables().size() != method.getParameterTypes().length)
            throw new IllegalArgumentException(String.format("The number of arguments for method %s (%d) does not match the number of bind parameters in the @Query (%d)",
                                                              method.getName(), method.getParameterTypes().length, ps.getVariables().size()));
        // TODO: we should also validate the types of the parameters...

        Class<?> returnType = method.getReturnType();
        if (Void.TYPE.isAssignableFrom(returnType) || ResultSet.class.isAssignableFrom(returnType))
            return;
        if (Statement.class.isAssignableFrom(returnType)) {
            returnStatement = true;
            return;
        }
        if (ResultSetFuture.class.isAssignableFrom(returnType)) {
            this.async = true;
            return;
        }
        if (ListenableFuture.class.isAssignableFrom(returnType)) {
            this.async = true;
            Type k = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
            if (k instanceof Class && ResultSet.class.isAssignableFrom((Class<?>)k))
                return;
            mapType(manager, returnType, k);
        } else {
            mapType(manager, returnType, method.getGenericReturnType());
        }
    }
}
