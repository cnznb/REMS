public class old{
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        checkState(elements != null);
        checkState(messager != null);
        checkState(steps != null);
    
        // First, collect all of the deferred elements and clear out the state from the previous rounds
        ImmutableMap.Builder<String, Optional<? extends Element>> deferredElementsBuilder =
            ImmutableMap.builder();
        for (String deferredTypeName : deferredTypeNames) {
          deferredElementsBuilder.put(deferredTypeName,
              Optional.fromNullable(elements.getTypeElement(deferredTypeName)));
        }
        for (String deferredPackageName : deferredPackageNames) {
          deferredElementsBuilder.put(deferredPackageName,
              Optional.fromNullable(elements.getPackageElement(deferredPackageName)));
        }
        ImmutableMap<String, Optional<? extends Element>> deferredElements =
            deferredElementsBuilder.build();
    
        deferredTypeNames.clear();
        deferredPackageNames.clear();
        // If this is the last round, report all of the missing elements
        if (roundEnv.processingOver()) {
          reportMissingElements(deferredElements);
          return false;
        }
    
        // For all of the elements that were deferred, find the annotated elements therein.  If we don't
        // find any, something is messed up and we just defer them again.
        ImmutableSetMultimap.Builder<Class<? extends Annotation>, Element>
            deferredElementsByAnnotationBuilder = ImmutableSetMultimap.builder();
        for (Entry<String, Optional<? extends Element>> deferredTypeElementEntry :
            deferredElements.entrySet()) {
          Optional<? extends Element> deferredElement = deferredTypeElementEntry.getValue();
          if (deferredElement.isPresent()) {
            findAnnotatedElements(deferredElement.get(), getSupportedAnnotationClasses(),
                deferredElementsByAnnotationBuilder);
          } else {
            deferredTypeNames.add(deferredTypeElementEntry.getKey());
          }
        }
        ImmutableSetMultimap<Class<? extends Annotation>, Element> deferredElementsByAnnotation =
            deferredElementsByAnnotationBuilder.build();
    
        ImmutableSetMultimap.Builder<Class<? extends Annotation>, Element> elementsByAnnotationBuilder =
            ImmutableSetMultimap.builder();
    
        Set<String> validPackageNames = Sets.newLinkedHashSet();
        Set<String> validTypeNames = Sets.newLinkedHashSet();
        // Look at the elements we've found and the new elements from this round and validate them.
        for (Class<? extends Annotation> annotationClass : getSupportedAnnotationClasses()) {
          // This should just call roundEnv.getElementsAnnotatedWith(Class) directly, but there is a bug
          // in some versions of eclipse that cause that method to crash.
          TypeElement annotationType = elements.getTypeElement(annotationClass.getCanonicalName());
          Set<? extends Element> elementsAnnotatedWith = (annotationType == null)
              ? ImmutableSet.<Element>of()
              : roundEnv.getElementsAnnotatedWith(annotationType);
          for (Element annotatedElement : Sets.union(
              elementsAnnotatedWith,
              deferredElementsByAnnotation.get(annotationClass))) {
            if (annotatedElement.getKind().equals(PACKAGE)) {
              PackageElement annotatedPackageElement = (PackageElement) annotatedElement;
              String annotatedPackageName = annotatedPackageElement.getQualifiedName().toString();
              boolean validPackage = validPackageNames.contains(annotatedPackageName)
                  || (!deferredPackageNames.contains(annotatedPackageName)
                      && validateElement(annotatedPackageElement));
              if (validPackage) {
                elementsByAnnotationBuilder.put(annotationClass, annotatedPackageElement);
                validPackageNames.add(annotatedPackageName);
              } else {
                deferredPackageNames.add(annotatedPackageName);
              }
            } else {
              TypeElement enclosingType = getEnclosingType(annotatedElement);
              String enclosingTypeName = enclosingType.getQualifiedName().toString();
              boolean validEnclosingType = validTypeNames.contains(enclosingTypeName)
                  || (!deferredTypeNames.contains(enclosingTypeName)
                      && validateElement(enclosingType));
              if (validEnclosingType) {
                elementsByAnnotationBuilder.put(annotationClass, annotatedElement);
                validTypeNames.add(enclosingTypeName);
              } else {
                deferredTypeNames.add(enclosingTypeName);
              }
            }
          }
        }
    
        ImmutableSetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation =
            elementsByAnnotationBuilder.build();
    
        // Finally, process the valid elements
        for (ProcessingStep step : steps) {
          SetMultimap<Class<? extends Annotation>, Element> filteredMap =
              Multimaps.filterKeys(elementsByAnnotation, Predicates.in(step.annotations()));
          if (!filteredMap.isEmpty()) {
            step.process(filteredMap);
          }
        }
    
        postProcess();
    
        return false;
    }
}
