public class old{
    public void testMissingClass() {
        // Test that referring to an undefined annotation does not trigger @AutoAnnotation processing.
        // The class Erroneous references an undefined annotation @NotAutoAnnotation. If we didn't have
        // any special treatment of undefined types then we could run into a compiler bug where
        // AutoAnnotationProcessor would think that a method annotated with @NotAutoAnnotation was in
        // fact annotated with @AutoAnnotation. As it is, we do get an error about @NotAutoAnnotation
        // being undefined, and we do not get an error complaining that this supposed @AutoAnnotation
        // method is not static. We do need to have @AutoAnnotation appear somewhere so that the
        // processor will run.
        JavaFileObject erroneousJavaFileObject = JavaFileObjects.forSourceLines(
            "com.example.annotations.Erroneous",
            "package com.example.annotations;",
            "",
            "import com.google.auto.value.AutoAnnotation;",
            "",
            "public class Erroneous {",
            "  @interface Empty {}",
            "  @AutoAnnotation static Empty newEmpty() {}",
            "  @NotAutoAnnotation Empty notNewEmpty() {}",
            "}"
        );
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector =
            new DiagnosticCollector<JavaFileObject>();
        JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(
            (Writer) null, (JavaFileManager) null, diagnosticCollector, (Iterable<String>) null,
            (Iterable<String>) null, ImmutableList.of(erroneousJavaFileObject));
        compilationTask.setProcessors(ImmutableList.of(new AutoAnnotationProcessor()));
        boolean result = compilationTask.call();
        assertThat(result).isFalse();
        List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticCollector.getDiagnostics();
        assertThat(diagnostics).isNotEmpty();
        assertThat(diagnostics.get(0).getMessage(null)).contains("NotAutoAnnotation");
        assertThat(diagnostics.get(0).getMessage(null)).doesNotContain("static");
    }
}
