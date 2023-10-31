public class old{
    public SymbolTableAssert isEmpty() {
        // check that actual SymbolTable we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format(
                "Expected actual SymbolTable to be empty but was not.", actual);

        // check
        if (!actual.isEmpty())
            throw new AssertionError(errorMessage);

        // return the current assertion for method chaining
        return this;
    }
}
